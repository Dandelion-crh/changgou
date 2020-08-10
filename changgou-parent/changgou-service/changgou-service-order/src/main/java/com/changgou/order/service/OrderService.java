package com.changgou.order.service;
import com.changgou.order.pojo.Order;
import com.github.pagehelper.PageInfo;
import java.util.List;
/****
 * @Author:shenkunlin
 * @Description:Order业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface OrderService {

    /**
     * 通过用户名查找用户全部订单信息
     * @param username
     * @return
     */
    List<Order> findOrder(String username);

    /**
     * 通过用户名查询未支付订单信息
     * @param username
     * @return
     */
    List<Order> findUnpaidOrder(String username);

    /**
     * 通过用户名查询已支付待收货订单信息
     * @param username
     * @return
     */
    List<Order> findHarvestOrder(String username);

    /**
     * 通过用户名查询已收货订单信息
     * @param username
     * @return
     */
    List<Order> findReceiveOrder(String username);

    /**
     * 通过订单id修改发货状态
     * @param Id
     */
    void updateConsignStatus(String Id);

    /**
     * 删除数据库和redis订单信息
     * @param id
     */
    void deleteOrder(String id);

    /**
     * 根据订单ID修改订单状态
     * @param orderId
     * @param transactionid
     */
    void updateStatus(String orderId,String transactionid);

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(String id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);

    /***
     * 新增Order
     * @param order
     */
    void add(Order order);

    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
     Order findById(String id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();
}
