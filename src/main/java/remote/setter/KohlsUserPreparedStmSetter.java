package remote.setter;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import remote.pojo.KohlsUser;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KohlsUserPreparedStmSetter implements ItemPreparedStatementSetter<KohlsUser> {

    @Override
    public void setValues(KohlsUser user, PreparedStatement ps) throws SQLException {
        ps.setLong(1, user.getId());
        ps.setString(2, user.getLoyaltyAccountEmail());
        ps.setString(3, user.getBarcode());
    }
}
