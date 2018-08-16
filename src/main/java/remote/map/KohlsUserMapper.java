package remote.map;


import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import remote.pojo.KohlsUser;

import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class KohlsUserMapper implements RowMapper<KohlsUser> {

    @Override
    public KohlsUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        KohlsUser user = new KohlsUser();
        user.setId(Long.valueOf(rs.getString("ID")));
        user.setStatus(rs.getString("STATUS"));
        user.setLoyaltyAccountEmail(rs.getString("LOYALTY_ACCOUNT_EMAIL"));
        return user;
    }
}
