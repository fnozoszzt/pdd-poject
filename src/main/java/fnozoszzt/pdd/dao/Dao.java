package fnozoszzt.pdd.dao;

import fnozoszzt.pdd.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface Dao {

    @Select({"select owner_name, access_token, refresh_token from user_login where owner_name = #{owner_name}"})
    User getUser(User user);

    @Insert({"insert into user_login (owner_name, access_token, refresh_token) values (#{owner_name}, #{access_token}, #{refresh_token})"})
    int insertUser(User user);

    @Update({"update agent set access_token=#{access_token}, refresh_token=#{refresh_token} where owner_name=#{owner_name}"})
    int updateUser(User user);

    @Insert({"insert into Login_history (owner_name, owner_id, access_token, refresh_token, str, login_timestamp) values " +
            "(#{owner_name}, #{owner_id}, #{access_token}), #{refresh_token}, #{str}, #{login_timestamp}"})
    int insertLoginHistory(User user);


}
