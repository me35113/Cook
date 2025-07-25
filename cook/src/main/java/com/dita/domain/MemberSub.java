package com.dita.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "member_sub")
@IdClass(MemberSubId.class) 
public class MemberSub {
	
	@Id
	@Column(name = "sub_user")
	private String subUser;
	
	
	@Id
	@Column(name = "subed_user")
	private String subedUser;
	
    @Column(name = "state")
    private Integer state;
	
	@CreationTimestamp
	@Column(name = "mem_sub_date")
	private LocalDateTime memSubDate;
	
	

}
