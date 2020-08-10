package com.changgou.seckill.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:shenkunlin
 * @Description:SeckillGoods构建
 * @Date 2019/6/14 19:13
 *****/
@Table(name="tb_seckill_goods")
public class SeckillGoods implements Serializable{

	@Id
    @Column(name = "id")
	private String id;//

    @Column(name = "sup_id")
	private String supId;//spu ID

    @Column(name = "sku_id")
	private String skuId;//sku ID

    @Column(name = "name")
	private String name;//标题

    @Column(name = "small_pic")
	private String smallPic;//商品图片

    @Column(name = "price")
	private Double price;//原价格

    @Column(name = "cost_price")
	private Double costPrice;//秒杀价格

    @Column(name = "create_time")
	private Date createTime;//添加日期

    @Column(name = "check_time")
	private Date checkTime;//审核日期

    @Column(name = "status")
	private String status;//审核状态，0未审核，1审核通过，2审核不通过

    @Column(name = "start_time")
	private Date startTime;//开始时间

    @Column(name = "end_time")
	private Date endTime;//结束时间

    @Column(name = "num")
	private Integer num;//秒杀商品数

    @Column(name = "stock_count")
	private Integer stockCount;//剩余库存数

    @Column(name = "introduction")
	private String introduction;//描述



	//get方法
	public String getId() {
		return id;
	}

	//set方法
	public void setId(String id) {
		this.id = id;
	}
	//get方法
	public String getSupId() {
		return supId;
	}

	//set方法
	public void setSupId(String supId) {
		this.supId = supId;
	}
	//get方法
	public String getSkuId() {
		return skuId;
	}

	//set方法
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	//get方法
	public String getName() {
		return name;
	}

	//set方法
	public void setName(String name) {
		this.name = name;
	}
	//get方法
	public String getSmallPic() {
		return smallPic;
	}

	//set方法
	public void setSmallPic(String smallPic) {
		this.smallPic = smallPic;
	}
	//get方法
	public Double getPrice() {
		return price;
	}

	//set方法
	public void setPrice(Double price) {
		this.price = price;
	}
	//get方法
	public Double getCostPrice() {
		return costPrice;
	}

	//set方法
	public void setCostPrice(Double costPrice) {
		this.costPrice = costPrice;
	}
	//get方法
	public Date getCreateTime() {
		return createTime;
	}

	//set方法
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	//get方法
	public Date getCheckTime() {
		return checkTime;
	}

	//set方法
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	//get方法
	public String getStatus() {
		return status;
	}

	//set方法
	public void setStatus(String status) {
		this.status = status;
	}
	//get方法
	public Date getStartTime() {
		return startTime;
	}

	//set方法
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	//get方法
	public Date getEndTime() {
		return endTime;
	}

	//set方法
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	//get方法
	public Integer getNum() {
		return num;
	}

	//set方法
	public void setNum(Integer num) {
		this.num = num;
	}
	//get方法
	public Integer getStockCount() {
		return stockCount;
	}

	//set方法
	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}
	//get方法
	public String getIntroduction() {
		return introduction;
	}

	//set方法
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}


}
