package com.changgou.user.pojo;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.lang.String;
import java.lang.Integer;
/****
 * @Author:shenkunlin
 * @Description:Comments构建
 * @Date 2019/6/14 19:13
 *****/
@Table(name="comments")
public class Comments implements Serializable{

	@Id
    @Column(name = "id")
	private String id;//

    @Column(name = "username")
	private String username;//

    @Column(name = "content")
	private String content;//

    @Column(name = "createtime")
	private Date createtime;//

    @Column(name = "images")
	private String images;//

    @Column(name = "count")
	private Integer count;//

    @Column(name = "level")
	private Integer level;//

    @Column(name = "score")
	private Integer score;//

    @Column(name = "type")
	private Integer type;//类型，1：用户评价，2：客户回复

    @Column(name = "pid")
	private String pid;//客服回复的评价id

    @Column(name = "spuid")
	private String spuid;//



	//get方法
	public String getId() {
		return id;
	}

	//set方法
	public void setId(String id) {
		this.id = id;
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
	public String getContent() {
		return content;
	}

	//set方法
	public void setContent(String content) {
		this.content = content;
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
	public String getImages() {
		return images;
	}

	//set方法
	public void setImages(String images) {
		this.images = images;
	}
	//get方法
	public Integer getCount() {
		return count;
	}

	//set方法
	public void setCount(Integer count) {
		this.count = count;
	}
	//get方法
	public Integer getLevel() {
		return level;
	}

	//set方法
	public void setLevel(Integer level) {
		this.level = level;
	}
	//get方法
	public Integer getScore() {
		return score;
	}

	//set方法
	public void setScore(Integer score) {
		this.score = score;
	}
	//get方法
	public Integer getType() {
		return type;
	}

	//set方法
	public void setType(Integer type) {
		this.type = type;
	}
	//get方法
	public String getPid() {
		return pid;
	}

	//set方法
	public void setPid(String pid) {
		this.pid = pid;
	}
	//get方法
	public String getSpuid() {
		return spuid;
	}

	//set方法
	public void setSpuid(String spuid) {
		this.spuid = spuid;
	}


}
