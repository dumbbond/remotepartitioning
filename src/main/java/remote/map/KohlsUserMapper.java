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
        user.setId(rs.getString("ID"));
        user.setStatus(rs.getString("STATUS"));
        user.setName(rs.getString("NAME"));
        user.setEmail(rs.getString("EMAIL"));
        //System.out.println(user);
        return user;
    }
}
