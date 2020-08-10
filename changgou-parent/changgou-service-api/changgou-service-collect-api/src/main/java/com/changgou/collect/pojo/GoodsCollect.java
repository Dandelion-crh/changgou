package com.changgou.collect.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/****
 * @Author:itheima
 * @Description:GoodsCollect构建
 *****/
@ApiModel(description = "GoodsCollect",value = "GoodsCollect")
@Table(name="goods_collect")
public class GoodsCollect implements Serializable{

	@ApiModelProperty(value = "商品的参数 0取消收藏 1收藏",required = false)
    @Column(name = "skuid")
	private String skuid;//商品的参数 0取消收藏 1收藏

	@ApiModelProperty(value = "用户的ID",required = false)
	@Id
    @Column(name = "username")
	private String username;//用户的ID

	@ApiModelProperty(value = "收藏的时间",required = false)
    @Column(name = "createtime")
	private Date createtime;//收藏的时间

	@ApiModelProperty(value = "收藏的状态",required = false)
    @Column(name = "isdelete")
	private String isdelete;//收藏的状态



	//get方法
	public String getSkuid() {
		return skuid;
	}

	//set方法
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	//get方法
	public String getUsername() {
		return username;
	}

	//set方法
	public void setUsername(String username) {
		this.username = username;
	}
	//get方法
	public Date getCreatetime() {
		return createtime;
	}

	//set方法
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	//get方法
	public String getIsdelete() {
		return isdelete;
	}

	//set方法
	public void setIsdelete(String isdelete) {
		this.isdelete = isdelete;
	}


}
