package remote.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import remote.pojo.KohlsUser;

import java.sql.Connection;
import java.util.List;

public class TransactionalCompositeItemWriter extends CompositeItemWriter<KohlsUser> {

    private static Logger LOG = LoggerFactory.getLogger(TransactionalCompositeItemWriter.class);

    private List<ItemWriter<? super KohlsUser>> delegates;

    private PlatformTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Override
    public void write(final List<? extends KohlsUser> user) throws Exception {

        Connection con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());

        if (con.getAutoCommit()) {

            LOG.debug("Switching JDBC Connection [" + con + "] to manual commit");

            con.setAutoCommit(false);
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("TransactionalCompositeItemWriter Transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            for (ItemWriter<? super KohlsUser> writer : delegates) {
                writer.write(user);
            }
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }

        transactionManager.commit(status);

        if (!con.getAutoCommit()) {

            LOG.debug("Switching JDBC Connection [" + con + "] to auto commit");

            con.setAutoCommit(true);
        }

    }

    public void setDelegates(List<ItemWriter<? super KohlsUser>> delegates) {
        super.setDelegates(delegates);
        this.delegates = delegates;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
