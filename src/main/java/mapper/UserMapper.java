package main.java.mapper;

import main.java.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    User login(@Param("username") String username,
               @Param("password") String password,
               @Param("role") String role);

    List<User> getAllByRole(String role);

    void insert(User user);

    void update(User user);

    void delete(Integer id);

    User getById(Integer id);

    User loginWithStatus(@Param("username") String username,
                         @Param("password") String password,
                         @Param("role") String role);

    void updateStatus(Map<String, Object> params);

    void updateRole(@Param("id") Integer id, @Param("role") String role);

    @Select("SELECT * FROM users ORDER BY id DESC")
    List<User> getAllUsers();
} 