package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:itheima
 * @Description:Sku的Dao
 *****/
public interface SkuMapper extends Mapper<Sku> {

    /**
     * 扣减商品的库存
     * @param id
     * @param num
     * @return
     */
    @Update("UPDATE tb_sku set num = num - #{num},sale_num = sale_num + #{num} WHERE id = #{id} and num >= #{num}")
    int descount(String id,Integer num);
}
