package com.xs.veh.video.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class VideoDowloadStatePK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column
	private String lsh;
	
	@Column
	private Integer jycs;
	
	@Column
	private String jyxm;
	
	@Column
	private Date jlztgxsj;

	public String getLsh() {
		return lsh;
	}

	public Integer getJycs() {
		return jycs;
	}

	public String getJyxm() {
		return jyxm;
	}

	public Date getJlztgxsj() {
		return jlztgxsj;
	}

	public void setLsh(String lsh) {
		this.lsh = lsh;
	}

	public void setJycs(Integer jycs) {
		this.jycs = jycs;
	}

	public void setJyxm(String jyxm) {
		this.jyxm = jyxm;
	}

	public void setJlztgxsj(Date jlztgxsj) {
		this.jlztgxsj = jlztgxsj;
	}
	
	@Override  
    public boolean equals(Object o) {  
        if(o instanceof VideoDowloadStatePK){  
        	VideoDowloadStatePK key = (VideoDowloadStatePK)o ;  
            if(this.lsh.equals(key.getLsh()) && this.jycs.equals(key.getJycs())&&
            		this.jyxm.equals(key.getJyxm())&&this.jlztgxsj.getTime()==key.getJlztgxsj().getTime()){  
                return true ;  
            }  
        }  
        return false ;  
    }

	@Override
	public String toString() {
		return "VideoDowloadStatePK [lsh=" + lsh + ", jycs=" + jycs + ", jyxm=" + jyxm + ", jlztgxsj=" + jlztgxsj + "]";
	}  
	
	

}
