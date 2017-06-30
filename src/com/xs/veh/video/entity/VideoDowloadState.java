package com.xs.veh.video.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Scope("prototype")
@Component("videoDowloadState")
@Entity
@Table(name = "Video_Dowload_State")
@JsonIgnoreProperties(value ={"hibernateLazyInitializer","handler","fieldHandler"})
public class VideoDowloadState {
	
	@Id
	private VideoDowloadStatePK pk;
	
	@Column
	private String xzzt;

	public VideoDowloadStatePK getPk() {
		return pk;
	}

	public String getXzzt() {
		return xzzt;
	}

	public void setPk(VideoDowloadStatePK pk) {
		this.pk = pk;
	}

	public void setXzzt(String xzzt) {
		this.xzzt = xzzt;
	}

	@Override
	public String toString() {
		return "VideoDowloadState [pk=" + pk + ", xzzt=" + xzzt + "]";
	}

	
}
