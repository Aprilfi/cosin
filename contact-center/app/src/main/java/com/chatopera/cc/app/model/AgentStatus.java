/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chatopera.cc.app.basic.MainContext;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;

@Entity
@Table(name = "uk_agentstatus")
@org.hibernate.annotations.Proxy(lazy = false)
public class AgentStatus implements java.io.Serializable ,  Comparable<AgentStatus>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5883426846142132613L;
	
	private String id ;					//坐席ID
	private String agentno ;			//坐席号码
	private Date logindate ;			//登陆时间
	private String status = MainContext.AgentStatusEnum.NOTREADY.toString() ;		//坐席状态
	private String orgi ;				//租户ID	
	private String agentserviceid ;		//会话ID
	private int serusernum 	= 10 ;		//最大服务用户数量
	private String skill ;				//接入的 技能组ID
	
	private boolean busy ;				//是否忙
	
	private Date createtime = new Date();
	private String skillname ;			//接入的技能组 名称
	private int users 	;				//已接入的 用户数量
	@SuppressWarnings("unused")
	private int maxusers;				//最大允许接入的用户数量
	@SuppressWarnings("unused")
	private int initmaxusers;				//最大允许接入的用户数量
	private boolean pulluser ;			//是否允许坐席自己拉取用户
	private String username ;			//坐席用户名
	private String name	;				//坐席姓名
	
	private Date updatetime ;				//最后一次状态更新时间，通常是 坐席 接入新客户的时候更新
	
	private String workstatus ;			//工作状态 ， 坐席的工作状态，计算绩效用 ， 示忙、示闲对应的更详细的 工作状态
	
	private String userid ;
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgentno() {
		return agentno;
	}
	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}
	public Date getLogindate() {
		return logindate;
	}
	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getAgentserviceid() {
		return agentserviceid;
	}
	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}
	public int getSerusernum() {
		return serusernum;
	}
	public void setSerusernum(int serusernum) {
		this.serusernum = serusernum;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}
	public String getSkillname() {
		return skillname;
	}
	public void setSkillname(String skillname) {
		this.skillname = skillname;
	}
	public int getUsers() {
		return users;
	}
	public void setUsers(int users) {
		this.users = users;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	@Transient
	public int getMaxusers() {
		SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(this.orgi) ;
		return sessionConfig!=null ? sessionConfig.getMaxuser() : MainContext.AGENT_STATUS_MAX_USER ;
	}
	public void setMaxusers(int maxusers) {
		this.maxusers = maxusers;
	}
	@Transient
	public int getInitmaxusers() {
		SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(this.orgi) ;
		return sessionConfig!=null ? sessionConfig.getInitmaxuser() : getMaxusers();
	}
	public void setInitmaxusers(int initmaxusers) {
		this.initmaxusers = initmaxusers;
	}
	public boolean isPulluser() {
		return pulluser;
	}
	public void setPulluser(boolean pulluser) {
		this.pulluser = pulluser;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int compareTo(AgentStatus o) {
		int retValue = 0 ;
		SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(this.orgi) ;
		if(sessionConfig!=null && !StringUtils.isBlank(sessionConfig.getDistribution()) && sessionConfig.getDistribution().equals("0")) {
			if(this.getUpdatetime()!=null && o.getUpdatetime()!=null) {
				retValue = (int) (this.getUpdatetime().getTime() - o.getUpdatetime().getTime());
			}else if(o.getUpdatetime()!=null) {
				retValue = -1; 
			}else {
				retValue = 1;
			}
		}else {
			retValue = this.users - o.users ;
		}
		return retValue;
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public String getWorkstatus() {
		return workstatus;
	}
	public void setWorkstatus(String workstatus) {
		this.workstatus = workstatus;
	}
}
