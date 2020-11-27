package com.model.member;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.model.post.PostVO;


@Entity
@Table(name= "Member")
public class MemberVO implements Serializable {
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Integer memberId;
	private String mname;
	private String email;
	private Date bdate;
	private String password;
	private String photo;
	
	@OneToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY, mappedBy="memberVO")
	@OrderBy("postId asc")
	@Fetch(FetchMode.JOIN)
	private Set<PostVO> posts = new HashSet<PostVO>();
	
//	//��1:�i�{�b�O�]�w�� cascade="all" lazy="false" inverse="true"���N�j
//	//��2:�imappedBy="�h�誺���p�ݩʦW"�G�Φb���V���p���A�����Y�������v����j�i�ثedeptVO�OEmpVO���@���ݩʡj
//	//��3:�i��w�]��@OneToMany(fetch=FetchType.LAZY, mappedBy="deptVO")���N�j--> �i�O���쬰  lazy="true"  inverse="true"���N�j
//	//FetchType.EAGER : Defines that data must be eagerly fetched
//	//FetchType.LAZY  : Defines that data can be lazily fetched
//	
	
	@JsonIgnore
	public Set<PostVO> getPosts(){
		return this.posts;		
	}
	
	public void setPosts(Set<PostVO> posts) {
		this.posts = posts;
	}
	
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	
	@NotEmpty(message="不可為空")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "只能輸入中文 英文 數字 _ 在2到10之間")
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	
	@Email(message = "格式錯誤")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	//@DateTimeFormat(pattern="yyyy-MM-dd") 
	public Date getBdate() {
		return bdate;
	}
	public void setBdate(Date bdate) {
		this.bdate = bdate;
	}
	
	@Pattern(regexp = "^[(a-zA-Z0-9_)]{8,}$", message = "只能輸入英文 數字和 _, 英文大小寫 必須含只少一位,密碼長度最少為8位")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
