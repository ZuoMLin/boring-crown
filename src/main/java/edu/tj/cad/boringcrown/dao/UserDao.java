package edu.tj.cad.boringcrown.dao;

import edu.tj.cad.boringcrown.domain.entity.UserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zuomlin
 */
@Mapper
@Component
public interface UserDao {

    @Select({
            "<script>",
            "SELECT * FROM USER WHERE username IN",
            "<foreach collection='usernameList' item='username' open='(' close=')' separator=','>",
            "#{username}",
            "</foreach>",
            "</script>"
    })
    List<UserEntity> findByUsernames(@Param("usernameList") List<String> usernameList);

    @Select("SELECT * FROM USER WHERE role = #{role} ORDER BY userid")
    List<UserEntity> findByRole(@Param("role") String role);

    @Select("SELECT * FROM USER WHERE status = #{status}")
    List<UserEntity> findByStatus(@Param("status") int status);

    @Select("SELECT * FROM USER WHERE userid = #{userId}")
    UserEntity findByUserId(@Param("userId") int userId);

    @Update("UPDATE USER SET status = #{status} WHERE userid = #{userId}")
    int updateUserStatus(@Param("userId") int userId, @Param("status") int status);

    @Insert("INSERT INTO USER(username, password, role, status) VALUES(#{username}, #{password}, #{role}, #{status})")
    int insertUser(@Param("username") String username, @Param("password") String password, @Param("role") String role, @Param("status") int status);
}
