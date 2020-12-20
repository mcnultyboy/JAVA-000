package com.yb.MultiDataSource1.mapper;

import com.yb.MultiDataSource1.entity.Order;
import com.yb.MultiDataSource1.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {

    @Insert("insert into order_info values (#{order_id}, #{user_id},#{price}, #{create_time}, #{update_time})")
    int save(Order order);

    @Select("select * from order_info")
    List<Order> selectAll();

    @Select("select * from order_info where order_id=#{orderId}")
    Order selectByOrderId(@Param("orderId")long orderId);

    // 如果有多个参数，则需要使用@Param 进行映射，Mybatis不会通过参数名进行自动映射
    @Select("select * from order_info where user_id=#{userId} and order_id=#{orderId}")
    Order selectByUserIdAndOrderId(@Param("userId") long userId, @Param("orderId") long orderId);
}
