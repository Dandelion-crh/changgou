package com.changgou.seckill.time;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 类名: SeckillTimer
 * 作者: crh
 * 日期: 2020/6/17 0017下午 6:01
 */
@Component
public class SeckillTimer {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 将数据库的符合条件的商品的数据存入redis
     * 0/30:从项目启动开始,每30秒执行一次
     * 0-30：从项目启动开始,每小时每分钟的0至30秒执行
     * 1,2,5:从项目启动开始,第1秒第2秒第5秒执行
     * 了解一下其他的控制定时任务的几个参数
     */
    @Scheduled(cron = "0/15 * * * * ?")
    public void goodsPushToRedis(){
        //获取当前时间所在的对应的5个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();

        for (Date startTime : dateMenus) {
            //获取结束时间
            Date endTime = DateUtil.addDateHour(startTime, 2);
            //日期格式转换
            String start = DateUtil.data2str(startTime,DateUtil.PATTERN_YYYY_MM_DDHHMM);
            String end = DateUtil.data2str(endTime,DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //拼接查询的条件
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //状态为审核通过的
            criteria.andEqualTo("status", "1");
            //库存大于0
            criteria.andGreaterThan("stockCount" , 0);
            //开始时间
            criteria.andGreaterThanOrEqualTo("startTime" , start);
            //结束时间
            criteria.andLessThanOrEqualTo("endTime", end);
            //redis中获取所有的商品的id
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + DateUtil.data2str(startTime, DateUtil.PATTERN_YYYYMMDDHH)).keys();
            //非空判断
            if(keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }
            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //数据存入redis
            for (SeckillGoods seckillGood : seckillGoods) {
                //将商品的库存转换成一个数组
                String[] ids = pushIds(seckillGood.getStockCount(),seckillGood.getId());
                //将商品的数组压入redis形成一个队列
                redisTemplate.boundListOps("SeckillGoodsStockQueue_" + seckillGood.getId()).leftPushAll(ids);
                //自增记录商品的库存
                redisTemplate
                        .boundHashOps("SeckillGoodsCount")
                        .increment(seckillGood.getId(),seckillGood.getStockCount());
                redisTemplate.boundHashOps("SeckillGoods_" + DateUtil.data2str(startTime, DateUtil.PATTERN_YYYYMMDDHH)).put(seckillGood.getId(), seckillGood);
                System.out.println(seckillGood.getId());
            }
        }
    }


    /***
     * 将商品ID存入到数组中
     * @param len:长度
     * @param id :值
     * @return
     */
    public String[] pushIds(int len,String id){
        String[] ids = new String[len];
        for (int i = 0; i <ids.length ; i++) {
            ids[i]=id;
        }
        return ids;
    }
}
