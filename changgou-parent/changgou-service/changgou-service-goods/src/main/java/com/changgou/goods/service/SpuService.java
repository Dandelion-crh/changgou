package com.changgou.goods.service;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;
import java.util.List;
/****
 * @Author:itheima
 * @Description:Spu业务层接口
 *****/
public interface SpuService {


    /**
     * 保存商品
     * @param goods
     */
    void save(Goods goods);

    /***
     * 根据SPU的ID查找SPU以及对应的SKU集合
     * @param spuId
     */
    Goods findGoodsById(String spuId);

    /***
     * 商品审核
     * @param spuId
     */
    void audit(String spuId);

    /***
     * 商品下架
     * @param spuId
     */
    void pull(String spuId);

    /***
     * 商品上架
     * @param spuId
     */
    void put(String spuId);

    /***
     * 商品批量上架
     */
    int putMany(String[] ids);

    /***
     * 商品批量下架
     */
    int pullMany(String[] ids);

    /***
     * 逻辑删除
     * @param spuId
     */
     void logicDelete(String spuId);

    /***
     * 还原被删除商品
     * @param spuId
     */
    void restore(String spuId);
    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(String id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(String id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();
}
