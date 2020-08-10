package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 删除订单
     *
     * @param username
     */
    @Override
    public void closeOrder(String username) {

        //修改订单支付状态
        SeckillStatus seckillStatus = (SeckillStatus)JSONObject.parseObject(stringRedisTemplate.boundValueOps("SeckillStatus_" + username).get(),SeckillStatus.class);
        seckillStatus.setStatus(3);
        stringRedisTemplate.boundValueOps("SeckillStatus_" + username).set(JSONObject.toJSONString(seckillStatus));

        //删除redis订单
        SeckillOrder order = (SeckillOrder)redisTemplate.boundHashOps("Order").get(username);
        //判断是否还有订单存在
        if (order != null){
            String orderId = order.getId();
            redisTemplate.boundHashOps("Order").delete(username);
            System.out.println("开始删除用户" + username + "的订单" + orderId);
        }
    }

    /**
     * 抢单记录
     *
     * @param username
     * @param id
     * @param time
     */
    @Override
    public void add(String username, String id, String time) {
        //获取商品的信息
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);

        //判断用户是否重复排队:
        //increment默认值是从0开始,默认是每次加1
        //返回值,是加参数值之后的结果:
        Long increment = redisTemplate.boundValueOps("UserQueueCount_" + username).increment(1);
        if (increment > 1){
            throw new RuntimeException("重复排队");
        }

        //封装排队状态对象
        if (seckillGoods != null){
            //封装排队状态对象
            SeckillStatus seckillStatus = new SeckillStatus();
            seckillStatus.setUsername(username);//设置用户名
            seckillStatus.setCreateTime(new Date());//设置创建时间
            seckillStatus.setStatus(1);//设置排队状态
            seckillStatus.setGoodsId(id);//设置购买的商品id
            seckillStatus.setTime(time);

            String json = JSONObject.toJSONString(seckillStatus);
            //将状态对象存入redis中 想要lua脚本去redis取时只能用stringRedisTemplate存入redis
            stringRedisTemplate.boundValueOps("SeckillStatus_" + username).set(json);

            //往mq里面发送一条消息
            rabbitTemplate.convertAndSend("seckillOrderExchange","seckillOrderQueue",json);
        }
    }

    /**
     * 删除订单
     *
     * @param username
     */
    @Override
    public void deleteOrder(String username) {
        //获取排队的信息
        SeckillStatus seckillStatus = JSONObject.parseObject(stringRedisTemplate.boundValueOps("SeckillStatus_" + username).get(),
                SeckillStatus.class);
        seckillStatus.setStatus(3);
        stringRedisTemplate.boundValueOps("SeckillStatus_" + username).set(JSONObject.toJSONString(seckillStatus));

        //删除redis中的订单的消息
        redisTemplate
                .boundHashOps("Order")
                .delete(username);
        //用排队的计数器要不要清除
        redisTemplate.delete("UserQueueCount_" + username);

        //库存回滚
        //情况一
        //获取商品的信息
        SeckillGoods seckillGoods =
                (SeckillGoods)redisTemplate
                        .boundHashOps("SeckillGoods" + seckillStatus.getTime())
                        .get(seckillStatus.getGoodsId());
        //商品售罄
        if (seckillGoods == null){
            //查询数据库中的商品的数据
            seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
            seckillGoods.setStockCount(seckillGoods.getStockCount() +1);
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
        }else{
            //商品没有售罄的情况下
            //将商品的数组压入redis形成一个队列
            redisTemplate
                    .boundListOps("SeckillGoodsStockQueue_" + seckillStatus.getGoodsId())
                    .leftPush(seckillStatus.getGoodsId());
            //自增记录商品的库存
            redisTemplate
                    .boundHashOps("SeckillGoodsCount")
                    .increment(seckillStatus.getGoodsId(),1);
        }
    }

    /**
     * 修改订单
     *
     * @param username
     * @param transactionid
     */
    @Override
    public void updatePayStatus(String username, String transactionid) {
        //redis获取订单的信息
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate
                .boundHashOps("Order")
                .get(username);
        //修改订单的状态
        seckillOrder.setStatus("1");
        //设置交易流水号
        seckillOrder.setTransactionId(transactionid);
        //订单的信息存入数据库
        seckillOrderMapper.insertSelective(seckillOrder);

        //用排队的计数器要清除
        redisTemplate.delete("UserQueueCount_" + username);

        //用户排队的信息要不要更新或者删除:方案一:直接删除
        //stringRedisTemplate.delete("SeckillStatus" + username);

        //方案二
        SeckillStatus seckillStatus =
                JSONObject.parseObject(stringRedisTemplate.boundValueOps("SeckillStatus_" + username).get(),
                SeckillStatus.class);
        //删除redis中的订单的消息
        redisTemplate
                .boundHashOps("Order")
                .delete(username);

        seckillStatus.setStatus(4);
        stringRedisTemplate.boundValueOps("SeckillStatus_" +username).set(JSONObject.toJSONString(seckillStatus));
    }


    /**
     * SeckillOrder条件+分页查询
     * @param seckillOrder 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder){
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder){
        Example example=new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(seckillOrder!=null){
            // 主键
            if(!StringUtils.isEmpty(seckillOrder.getId())){
                    criteria.andEqualTo("id",seckillOrder.getId());
            }
            // 秒杀商品ID
            if(!StringUtils.isEmpty(seckillOrder.getSeckillId())){
                    criteria.andEqualTo("seckillId",seckillOrder.getSeckillId());
            }
            // 支付金额
            if(!StringUtils.isEmpty(seckillOrder.getMoney())){
                    criteria.andEqualTo("money",seckillOrder.getMoney());
            }
            // 用户
            if(!StringUtils.isEmpty(seckillOrder.getUserId())){
                    criteria.andEqualTo("userId",seckillOrder.getUserId());
            }
            // 创建时间
            if(!StringUtils.isEmpty(seckillOrder.getCreateTime())){
                    criteria.andEqualTo("createTime",seckillOrder.getCreateTime());
            }
            // 支付时间
            if(!StringUtils.isEmpty(seckillOrder.getPayTime())){
                    criteria.andEqualTo("payTime",seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if(!StringUtils.isEmpty(seckillOrder.getStatus())){
                    criteria.andEqualTo("status",seckillOrder.getStatus());
            }
            // 收货人地址
            if(!StringUtils.isEmpty(seckillOrder.getReceiverAddress())){
                    criteria.andEqualTo("receiverAddress",seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if(!StringUtils.isEmpty(seckillOrder.getReceiverMobile())){
                    criteria.andEqualTo("receiverMobile",seckillOrder.getReceiverMobile());
            }
            // 收货人
            if(!StringUtils.isEmpty(seckillOrder.getReceiver())){
                    criteria.andEqualTo("receiver",seckillOrder.getReceiver());
            }
            // 交易流水
            if(!StringUtils.isEmpty(seckillOrder.getTransactionId())){
                    criteria.andEqualTo("transactionId",seckillOrder.getTransactionId());
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
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder){
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 增加SeckillOrder
     * @param seckillOrder
     */
    @Override
    public void add(SeckillOrder seckillOrder){
        seckillOrderMapper.insert(seckillOrder);
    }

    /**
     * 根据ID查询SeckillOrder
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(String id){
        return  seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
