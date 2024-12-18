package main.java.service;

import main.java.entity.User;
import main.java.mapper.UserMapper;
import main.java.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class UserService {
    public User login(String username, String password, String role) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.login(username, password, role);
            if (user != null && user.getRole().equals(role)) {
                return user;
            }
            return null;
        }
    }

    public List<User> getAllByRole(String role) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.getAllByRole(role);
        }
    }

    public void insert(User user) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            try {
                mapper.insert(user);
                session.commit();
            } catch (Exception e) {
                session.rollback();
                // 检查是否是用户名重复错误
                if (e.getMessage().contains("Duplicate entry") &&
                        e.getMessage().contains("users.username")) {
                    throw new RuntimeException("用户名已存在，请使用其他用户名");
                }
                // 其他数据库错误
                throw new RuntimeException("添加用户失败：" + getReadableErrorMessage(e));
            }
        }
    }

    // 添加一个工具方法来转换错误信息
    public static String getReadableErrorMessage(Exception e) {
            String message = e.getMessage();

            // 处理常见的数据库错误
            if (message.contains("Duplicate entry")) {
                return "数据已存在";
            }
            if (message.contains("cannot be null")) {
                return "必填字段不能为空";
            }
            if (message.contains("Data too long")) {
                return "输入数据过长";
            }
            if (message.contains("foreign key constraint")) {
                return "数据关联错误";
            }
            // 如果是其他错误，返回通用错误信息
            return "系统错误，请稍后重试";
            }

    public void update(User user) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.update(user);
            session.commit();
        }
    }

    public void delete(Integer id) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.delete(id);
            session.commit();
        }
    }

    public User getById(Integer id) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.getById(id);
        }
    }

    public List<User> getAllUsers() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.getAllUsers();
        }
    }

    public void updateStatus(Integer userId, String status) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);

            Map<String, Object> params = new HashMap<>();
            params.put("id", userId);
            params.put("status", status);

            mapper.updateStatus(params);
            session.commit();
        } catch (Exception e) {
            throw new RuntimeException("更新用户状态失败", e);
        }
    }

}
