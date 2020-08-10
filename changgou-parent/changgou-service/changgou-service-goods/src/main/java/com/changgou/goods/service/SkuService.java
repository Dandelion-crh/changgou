package com.changgou.goods.service;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.changgou.goods.pojo.Sku;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;

/****
 * @Author:itheima
 * @Description:Sku业务层接口
 *****/
public interface SkuService {

    /**
     * 扣减商品的库存
     * @param dataMap
     */
    void descount(Map<String,Integer> dataMap);

    /**
     *通过商品的状态查询商品的列表哦信息
     */
    List<Sku> findByStatus(String status);

    /***
     * Sku多条件分页查询
     * @param sku
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(Sku sku, int page, int size);

    /***
     * Sku分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage(int page, int size);

    /***
     * Sku多条件搜索方法
     * @param sku
     * @return
     */
    List<Sku> findList(Sku sku);

    /***
     * 删除Sku
     * @param id
     */
    void delete(String id);

    /***
     * 修改Sku数据
     * @param sku
     */
    void update(Sku sku);

    /***
     * 新增Sku
     * @param sku
     */
    void add(Sku sku);

    /**
     * 根据ID查询Sku
     * @param id
     * @return
     */
     Sku findById(String id);

    /***
     * 查询所有Sku
     * @return
     */
    List<Sku> findAll();

    //--------------------

    /**
     * 通过skuId条件分页查询sku
     * @param skuIdList
     * @param page
     * @param size
     * @return
     */
    PageInfo<Sku> findPage2(List<Sku> skuIdList, int page,int size);
}
