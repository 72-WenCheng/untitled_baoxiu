package main.java.service;

import main.java.entity.RepairOrder;
import main.java.mapper.RepairOrderMapper;
import main.java.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import java.util.Date;
import java.util.List;
import static main.java.service.UserService.getReadableErrorMessage;

public class RepairOrderService {
    public void createOrder(RepairOrder order) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            try {
                order.setReportTime(new Date());
                mapper.insert(order);
                session.commit();
            } catch (Exception e) {
            session.rollback();
            throw new RuntimeException("创建报修单失败：" + getReadableErrorMessage(e));
        }

        }
    }

    public void updateOrder(RepairOrder order) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            mapper.update(order);
            session.commit();
        }
    }

    public RepairOrder getById(Integer id) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.getById(id);
        }
    }

    public List<RepairOrder> getByReporterId(Integer reporterId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.getByReporterId(reporterId);
        }
    }

    public List<RepairOrder> getByStatus(String status) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.getByStatus(status);
        }
    }

    public List<RepairOrder> getAllOrderByTime() {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            return mapper.getAllOrderByTime();
        }
    }
}