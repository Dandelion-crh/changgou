package com.changgou.user.pojo;
import javax.persistence.*;
import java.io.Serializable;
import java.lang.String;
/****
 * @Author:shenkunlin
 * @Description:ItemsCollect构建
 * @Date 2019/6/14 19:13
 *****/
@Table(name="items_collect")
public class ItemsCollect implements Serializable{

	@Id
    @Column(name = "id")
	private String id;//

    @Column(name = "name")
	private String name;//

    @Column(name = "skuid")
	private String skuid;//

    @Column(name = "image")
	private String image;//

    @Column(name = "userid")
	private String userid;//



	//get方法
	public String getId() {
		return id;
	}

	//set方法
	public void setId(String id) {
		this.id = id;
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
	public String getSkuid() {
		return skuid;
	}

	//set方法
	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}
	//get方法
	public String getImage() {
		return image;
	}

	//set方法
	public void setImage(String image) {
		this.image = image;
	}
	//get方法
	public String getUserid() {
		return userid;
	}

	//set方法
	public void setUserid(String userid) {
		this.userid = userid;
	}


}
