package com.changgou.order.service.impl;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.changgou.util.DateUtil;
import com.changgou.util.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private UserFeign userFeign;


    /**
     * 通过用户名查找用户全部订单信息
     *
     * @param username
     */
    @Override
    public List<Order> findOrder(String username) {
        //设置order的用户名,通过用户名查找所有订单信息
        Order orderExample = new Order();
        orderExample.setUsername(username);
        //返回所有订单信息
        return findAndPackageOrder(orderExample);
    }

    /**
     *通过用户名查询未支付订单信息
     * @param username
     * @return
     */
    @Override
    public List<Order> findUnpaidOrder(String username) {
        //设置order的用户名,通过用户名查找所有订单信息
        Order orderExample = new Order();
        orderExample.setUsername(username);
        orderExample.setPayStatus("0");
        //返回未支付订单信息
        return findAndPackageOrder(orderExample);
    }

    /**
     *通过用户名查询已支付待收货订单信息
     * @param username
     * @return
     */
    @Override
    public List<Order> findHarvestOrder(String username) {
        //设置order的用户名,通过用户名查找所有订单信息
        Order orderExample = new Order();
        orderExample.setUsername(username);
        orderExample.setPayStatus("1");
        orderExample.setOrderStatus("0");
        //返回待收货订单信息
        return findAndPackageOrder(orderExample);
    }

    /**
     *通过用户名查询已收货订单信息
     * @param username
     * @return
     */
    @Override
    public List<Order> findReceiveOrder(String username) {
        //设置order的用户名,通过用户名查找所有订单信息
        Order orderExample = new Order();
        orderExample.setUsername(username);
        orderExample.setPayStatus("1");
        orderExample.setOrderStatus("1");
        orderExample.setConsignStatus("2");
        //返回待收货订单信息
        return findAndPackageOrder(orderExample);
    }

    /**
     * 通过订单id修改发货状态
     * @param orderId
     */
    @Override
    public void updateConsignStatus(String orderId) {
        //通过订单ID查找订单信息
        Order order = findById(orderId);
        //设置订单发货状态
        order.setConsignStatus("2");
        //设置订单状态
        order.setOrderStatus("1");
        //修改订单已收货状态
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 通过用户名查找订单并封装到集合中
     * @return
     */
    public List<Order> findAndPackageOrder(Order orderExample){

        List<Order> orderList = orderMapper.select(orderExample);

        //判断是否有订单信息
        if (orderList != null && orderList.size() >0){
            //遍历订单,通过订单id查找每一个商品信息
            for (Order order : orderList) {
                //创建List集合存放每个商品的名字和数量
                ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
                String orderId = order.getId();

                //时间格式转换
                //获得订单创建时间
                Date createTime = order.getCreateTime();
                //调用dateUtil方法进行时间格式转换
                Date resultDate = DateUtil.dataToData(createTime, "yyyy-MM-dd HH:mm");
                order.setCreateTime(resultDate);

                //通过订单id找商品的信息
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                //通过订单id获得商品列表
                List<OrderItem> orderItemList = orderItemMapper.select(orderItem);

                if (orderList != null && orderItemList.size()>0){
                    //遍历商品列表
                    for (OrderItem item : orderItemList) {
                        Map<String,Object> map = new HashMap<String,Object>();
                        //将每一个商品的名字和数量,图片加到Map,再把它放到List中
                        map.put("skuid",item.getSkuId());
                        map.put("name",item.getName());
                        map.put("num",item.getNum());
                        map.put("image",item.getImage());
                        list.add(map);
                    }
                    //将每一个商品的名字和数量存到order的SkuNameAndNum对象中
                    order.setSkuNameAndNum(list);
                }
            }
        }
        return orderList;
    }


    /**
     * 增加Order
     * @param order
     */
    @Override
    public void add(Order order){

        order.setId("No" + idWorker.nextId());
        //总数量
        Integer num = 0;
        //总价格
        Integer money = 0;

        //商品购买记录的对象
        Map<String,Integer> dataMap = new HashMap<>();

        //获取选中的商品的列表
        String[] ids = order.getIds();
        for (String id : ids) {
            //获取到了选中购物车数据
            OrderItem orderItem = (OrderItem)redisTemplate.boundHashOps("Cart_" + order.getUsername()).get(id);
            //完善OrderItem信息
            orderItem.setId("No" + idWorker.nextId());
            //设置订单的id
            orderItem.setOrderId(order.getId());
            //存入数据库
            orderItemMapper.insertSelective(orderItem);
            //数量增加,价格增加
            num += orderItem.getNum();
            money += orderItem.getMoney();

            //从购物车删除数据
            redisTemplate.boundHashOps("Cart_" + order.getUsername()).delete(id);

            //记录商品的id和消费的数量
            dataMap.put(id,orderItem.getNum());
        }

        //完善Order的信息
        order.setTotalNum(num);//总数量
        order.setTotalMoney(money);//总价格
        order.setCreateTime(new Date());//创建时间
        order.setUpdateTime(new Date());//修改时间
        order.setOrderStatus("0");//订单状态未完成
        order.setPayStatus("0");//订单支付状态
        order.setConsignStatus("0");//订单发货状态
        order.setIsDelete("0");//订单逻辑删除状态

        orderMapper.insertSelective(order);

        //添加redis订单数据
        redisTemplate.boundHashOps("Order").put(order.getId(),order);

        //扣减商品库存
        skuFeign.descount(dataMap);

        //增加用户积分
        //userFeign.addPoints(10);
    }


    /**
     * 付款失败 删除redis订单信息 ,修改数据库订单支付状态
     * 删除数据库和redis订单信息
     *
     * @param id
     */
    @Override
    public void deleteOrder(String id) {
        //删除redis的订单数据
        redisTemplate.boundHashOps("Order").delete(id);
        //修改数据库的订单数据
        Order order = orderMapper.selectByPrimaryKey(id);
        order.setPayStatus("2");
        //如果是支付取消,订单取消,订单失败
        //order.setIsDelete("1");
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     * 付款成功
     * 根据订单ID修改订单状态
     *
     * @param orderId
     * @param transactionid 交易流水号
     */
    @Override
    public void updateStatus(String orderId, String transactionid) {
        //从redis中获取订单信息
        Order order = (Order)redisTemplate.boundHashOps("Order").get(orderId);
        //修改订单支付状态
        order.setPayStatus("1");
        //添加交易流水号
        order.setTransactionId(transactionid);
        //修改数据库的订单信息
        orderMapper.updateByPrimaryKeySelective(order);
        //修改redis的订单信息
        redisTemplate.boundHashOps("Order").put(orderId,order);
    }

    /**
     * Order条件+分页查询
     * @param order 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Order> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order){
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     * @param order
     * @return
     */
    public Example createExample(Order order){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(order!=null){
            // 订单id
            if(!StringUtils.isEmpty(order.getId())){
                    criteria.andEqualTo("id",order.getId());
            }
            // 数量合计
            if(!StringUtils.isEmpty(order.getTotalNum())){
                    criteria.andEqualTo("totalNum",order.getTotalNum());
            }
            // 金额合计
            if(!StringUtils.isEmpty(order.getTotalMoney())){
                    criteria.andEqualTo("totalMoney",order.getTotalMoney());
            }
            // 优惠金额
            if(!StringUtils.isEmpty(order.getPreMoney())){
                    criteria.andEqualTo("preMoney",order.getPreMoney());
            }
            // 邮费
            if(!StringUtils.isEmpty(order.getPostFee())){
                    criteria.andEqualTo("postFee",order.getPostFee());
            }
            // 实付金额
            if(!StringUtils.isEmpty(order.getPayMoney())){
                    criteria.andEqualTo("payMoney",order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if(!StringUtils.isEmpty(order.getPayType())){
                    criteria.andEqualTo("payType",order.getPayType());
            }
            // 订单创建时间
            if(!StringUtils.isEmpty(order.getCreateTime())){
                    criteria.andEqualTo("createTime",order.getCreateTime());
            }
            // 订单更新时间
            if(!StringUtils.isEmpty(order.getUpdateTime())){
                    criteria.andEqualTo("updateTime",order.getUpdateTime());
            }
            // 付款时间
            if(!StringUtils.isEmpty(order.getPayTime())){
                    criteria.andEqualTo("payTime",order.getPayTime());
            }
            // 发货时间
            if(!StringUtils.isEmpty(order.getConsignTime())){
                    criteria.andEqualTo("consignTime",order.getConsignTime());
            }
            // 交易完成时间
            if(!StringUtils.isEmpty(order.getEndTime())){
                    criteria.andEqualTo("endTime",order.getEndTime());
            }
            // 交易关闭时间
            if(!StringUtils.isEmpty(order.getCloseTime())){
                    criteria.andEqualTo("closeTime",order.getCloseTime());
            }
            // 物流名称
            if(!StringUtils.isEmpty(order.getShippingName())){
                    criteria.andEqualTo("shippingName",order.getShippingName());
            }
            // 物流单号
            if(!StringUtils.isEmpty(order.getShippingCode())){
                    criteria.andEqualTo("shippingCode",order.getShippingCode());
            }
            // 用户名称
            if(!StringUtils.isEmpty(order.getUsername())){
                    criteria.andLike("username","%"+order.getUsername()+"%");
            }
            // 买家留言
            if(!StringUtils.isEmpty(order.getBuyerMessage())){
                    criteria.andEqualTo("buyerMessage",order.getBuyerMessage());
            }
            // 是否评价
            if(!StringUtils.isEmpty(order.getBuyerRate())){
                    criteria.andEqualTo("buyerRate",order.getBuyerRate());
            }
            // 收货人
            if(!StringUtils.isEmpty(order.getReceiverContact())){
                    criteria.andEqualTo("receiverContact",order.getReceiverContact());
            }
            // 收货人手机
            if(!StringUtils.isEmpty(order.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",order.getReceiverMobile());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(order.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(!StringUtils.isEmpty(order.getSourceType())){
                    criteria.andEqualTo("sourceType",order.getSourceType());
            }
            // 交易流水号
            if(!StringUtils.isEmpty(order.getTransactionId())){
                    criteria.andEqualTo("transactionId",order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if(!StringUtils.isEmpty(order.getOrderStatus())){
                    criteria.andEqualTo("orderStatus",order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if(!StringUtils.isEmpty(order.getPayStatus())){
                    criteria.andEqualTo("payStatus",order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if(!StringUtils.isEmpty(order.getConsignStatus())){
                    criteria.andEqualTo("consignStatus",order.getConsignStatus());
            }
            // 是否删除
            if(!StringUtils.isEmpty(order.getIsDelete())){
                    criteria.andEqualTo("isDelete",order.getIsDelete());
            }
        }
        return example;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     * @param order
     */
    @Override
    public void update(Order order){
        orderMapper.updateByPrimaryKey(order);
    }


    /**
     * 根据ID查询Order
     * @param id
     * @return
     */
    @Override
    public Order findById(String id){
        return  orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }
}
