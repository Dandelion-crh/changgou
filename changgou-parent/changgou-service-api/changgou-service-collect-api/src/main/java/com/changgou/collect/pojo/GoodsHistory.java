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
 * @Description:GoodsHistory构建
 *****/
@ApiModel(description = "GoodsHistory",value = "GoodsHistory")
@Table(name="goods_history")
public class GoodsHistory implements Serializable{

	@ApiModelProperty(value = "商品的参数",required = false)
    @Column(name = "skuid")
	private String skuid;//商品的参数

	@ApiModelProperty(value = "用户",required = false)
	@Id
    @Column(name = "username")
	private String username;//用户

	@ApiModelProperty(value = "最后浏览时间",required = false)
    @Column(name = "createtime")
	private Date createtime;//最后浏览时间

	@ApiModelProperty(value = "是否删除",required = false)
    @Column(name = "isdelete")
	private String isdelete;//是否删除



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
