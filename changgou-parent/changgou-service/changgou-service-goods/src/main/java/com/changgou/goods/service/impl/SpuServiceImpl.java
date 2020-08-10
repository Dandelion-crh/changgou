package com.changgou.goods.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/****
 * @Author:itheima
 * @Description:Spu业务层接口实现类
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;
    /**
     * 新增商品
     *
     * @param goods
     */
    @Override
    public void save(Goods goods) {

        //获取spu的数据
        Spu spu = goods.getSpu();

        //spuid为空代表新增数据
        if(StringUtils.isEmpty(spu.getId())){
            //设置spu的id
            spu.setId("No" + idWorker.nextId());
            //保存spu
            spuMapper.insertSelective(spu);
        }else{ //spuid不为空,直接去更新数据
            spuMapper.updateByPrimaryKeySelective(spu);

            //清除掉就的sku的数据
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }

        //查询类别的名字
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

        //查询品牌的名字
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());

        //获取sku列表的数据
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            //id
            sku.setId("No" + idWorker.nextId());
            //name
            String spec = sku.getSpec();
            Map<String, String> map = JSONObject.parseObject(spec, Map.class);
            String name = spu.getName();
            //循环规格数据
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //key: 书籍分类 value :科技
                name += " " + entry.getValue();
            }
            //黑马培训 科技 有一定java基础的人
            sku.setName(name);
            //创建时间
            sku.setCreateTime(new Date());
            //修改时间
            sku.setUpdateTime(new Date());
            //spuId
            sku.setSpuId(spu.getId());
            //类别id
            sku.setCategoryId(spu.getCategory3Id());
            //类别的名字
            sku.setCategoryName(category.getName());
            //品牌的名字
            sku.setBrandName(brand.getName());

            //保存sku的信息
            skuMapper.insertSelective(sku);
        }
    }

    /***
     * 根据SPU的ID查找SPU以及对应的SKU集合
     * @param spuId
     */
    @Override
    public Goods findGoodsById(String spuId) {
        //查询Spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);

        //查询List<Sku>
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        //封装Goods
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skus);
        return goods;
    }

    /***
     * 商品审核
     * @param spuId
     */
    @Override
    public void audit(String spuId) {
        //查询商品
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //判断商品是否已经删除
        if (spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("改商品已被删除！");
        }
        //实现上架和审核
        spu.setStatus("1");//审核通过
        spu.setIsMarketable("1");//上架
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /***
     * 商品下架
     * @param spuId
     */
    @Override
    public void pull(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu.getIsDelete().equals("1")){
            throw new RuntimeException("商品已删除！");
        }
        spu.setIsMarketable("0");//下架状态
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /***
     * 商品上架
     * @param spuId
     */
    @Override
    public void put(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu.getIsDelete().equals("1")){
            throw new RuntimeException("商品已删除！");
        }
        if (!spu.getStatus().equals("1")){
            throw new RuntimeException("未通过审核商品不能上架");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /***
     * 批量上架
     * @param ids:需要上架的商品ID集合
     * @return
     */
    @Override
    public int putMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");//上架
        //批量修改
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));//id
        //下架
        criteria.andEqualTo("isMarketable","0");
        //审核通过的
        criteria.andEqualTo("status","1");
        //非删除的
        criteria.andEqualTo("isDelete","0");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    /***
     * 商品批量下架
     * @param ids
     */
    @Override
    public int pullMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        //商品是上架
        criteria.andEqualTo("isMarketable","1");
        //非删除
        criteria.andEqualTo("isDelete","0");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    /***
     * 逻辑删除
     * @param spuId
     */
    @Override
    @Transactional
    public void logicDelete(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //检查是否下架的商品
        if (!spu.getIsMarketable().equals("0")){
            throw new RuntimeException("必须先下架再删除");
        }
        //删除
        spu.setIsDelete("1");
        //未审核
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 恢复数据
     * @param spuId
     */
    @Override
    public void restore(String spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (!spu.getIsDelete().equals(1)){
            throw new RuntimeException("商品未删除!");
        }
        spu.setIsDelete("0");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * Spu条件+分页查询
     * @param spu 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size){
        //分页
        PageHelper.startPage(page,size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size){
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu){
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     * @param spu
     * @return
     */
    public Example createExample(Spu spu){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu!=null){
            // 主键
            if(!StringUtils.isEmpty(spu.getId())){
                    criteria.andEqualTo("id",spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())){
                    criteria.andEqualTo("sn",spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())){
                    criteria.andLike("name","%"+spu.getName()+"%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())){
                    criteria.andEqualTo("caption",spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())){
                    criteria.andEqualTo("brandId",spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())){
                    criteria.andEqualTo("category1Id",spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())){
                    criteria.andEqualTo("category2Id",spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())){
                    criteria.andEqualTo("category3Id",spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())){
                    criteria.andEqualTo("templateId",spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())){
                    criteria.andEqualTo("freightId",spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())){
                    criteria.andEqualTo("image",spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())){
                    criteria.andEqualTo("images",spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())){
                    criteria.andEqualTo("saleService",spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())){
                    criteria.andEqualTo("introduction",spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())){
                    criteria.andEqualTo("specItems",spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())){
                    criteria.andEqualTo("paraItems",spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())){
                    criteria.andEqualTo("saleNum",spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())){
                    criteria.andEqualTo("commentNum",spu.getCommentNum());
            }
            // 是否上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())){
                    criteria.andEqualTo("isMarketable",spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())){
                    criteria.andEqualTo("isEnableSpec",spu.getIsEnableSpec());
            }
            // 是否删除
            if(!StringUtils.isEmpty(spu.getIsDelete())){
                    criteria.andEqualTo("isDelete",spu.getIsDelete());
            }
            // 审核状态
            if(!StringUtils.isEmpty(spu.getStatus())){
                    criteria.andEqualTo("status",spu.getStatus());
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
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //检查是否被逻辑删除  ,必须先逻辑删除后才能物理删除
        if (!spu.getIsDelete().equals("1")){
            throw new RuntimeException("次商品不能被删除");
        }
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
