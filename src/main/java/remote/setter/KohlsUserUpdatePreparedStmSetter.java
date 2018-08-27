package remote.setter;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import remote.pojo.KohlsUser;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KohlsUserUpdatePreparedStmSetter implements ItemPreparedStatementSetter<KohlsUser> {

    @Override
    public void setValues(KohlsUser user, PreparedStatement ps) throws SQLException {
        ps.setString(1, user.getStatus());
        ps.setLong(2, user.getId());
    }
}
