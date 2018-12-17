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
package com.chatopera.cc.app.handler.apps.tenant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.util.OnlineUserUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.persistence.repository.AgentUserRepository;
import com.chatopera.cc.app.persistence.repository.OrganRepository;
import com.chatopera.cc.app.persistence.repository.OrganizationRepository;
import com.chatopera.cc.app.persistence.repository.OrgiSkillRelRepository;
import com.chatopera.cc.app.persistence.repository.TenantRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AgentStatus;
import com.chatopera.cc.app.model.AgentUser;
import com.chatopera.cc.app.model.Organ;
import com.chatopera.cc.app.model.OrgiSkillRel;
import com.chatopera.cc.app.model.Tenant;

@Controller
@RequestMapping("/apps/tenant")
public class TenantController extends Handler{
	
	@Autowired
	private TenantRepository tenantRes;
	
	@Autowired
	private OrgiSkillRelRepository orgiSkillRelRes;
	
	@Autowired
	private OrganRepository organRes;
	
	@Autowired
	private OrganizationRepository organizationRes;
	
	@Autowired
	private AgentUserRepository agentUserRepository;
	
	@Value("${web.upload-path}")
    private String path;
    @RequestMapping("/index")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView index(ModelMap map , HttpServletRequest request,@Valid String msg,@Valid String currentorgi,@Valid String currentname) throws FileNotFoundException, IOException {
    	if(super.isEnabletneant()) {
    		if("0".equals(super.getUser(request).getUsertype())) {
    			map.addAttribute("tenantList", tenantRes.findByOrgid(super.getOrgid(request)));
    		}else {
    			List<OrgiSkillRel> orgiSkillRelList = orgiSkillRelRes.findBySkillid((super.getUser(request)).getOrgan());
    			List<Tenant> tenantList = null;
    			if(!orgiSkillRelList.isEmpty()) {
    				tenantList = new ArrayList<Tenant>();
    				for(OrgiSkillRel orgiSkillRel:orgiSkillRelList) {
    					Tenant t = tenantRes.findById(orgiSkillRel.getOrgi());
    					if(t!=null) {
    						tenantList.add(t);
    					}
    				}
    			}
    			map.addAttribute("tenantList", tenantList);
    		}
    	}else{
    		map.addAttribute("tenantList", tenantRes.findById(super.getOrgi(request)));
    	}
    	map.addAttribute("organization", organizationRes.findById(super.getUser(request).getOrgid()));
    	map.addAttribute("msg",msg);
    	map.addAttribute("currentorgi",currentorgi);
    	if(currentname!=null) {
    		map.addAttribute("currentname",URLDecoder.decode(currentname,"UTF-8"));
    	}
    	return request(super.createRequestPageTempletResponse("/apps/tenant/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
    	if(super.isTenantshare()) {
    		map.addAttribute("isShowSkillList",true);
    		List<Organ> organList = organRes.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request));
        	map.addAttribute("skillList",organList);
    	}
        return request(super.createRequestPageTempletResponse("/apps/tenant/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView save(HttpServletRequest request ,@Valid Tenant tenant, @RequestParam(value = "tenantpic", required = false) MultipartFile tenantpic,@Valid String skills) throws NoSuchAlgorithmException, IOException {
    	Tenant tenanttemp = tenantRes.findByOrgidAndTenantname(super.getOrgid(request),tenant.getTenantname());
    	if(tenanttemp!=null) {
    		return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=tenantexist"));
    	}
    	tenantRes.save(tenant) ;
    	if(tenantpic!=null && tenantpic.getOriginalFilename().lastIndexOf(".") > 0){
    		File logoDir = new File(path , "tenantpic");
    		if(!logoDir.exists()){
    			logoDir.mkdirs() ;
    		}
    		String fileName = "tenantpic/"+tenant.getId()+tenantpic.getOriginalFilename().substring(tenantpic.getOriginalFilename().lastIndexOf(".")) ;
    		FileCopyUtils.copy(tenantpic.getBytes(), new File(path , fileName));
    		tenant.setTenantlogo(fileName);
    	}
    	String tenantid = tenant.getId();
    	List<OrgiSkillRel>  orgiSkillRelList = orgiSkillRelRes.findByOrgi(tenantid) ;
    	orgiSkillRelRes.delete(orgiSkillRelList);
    	if(!StringUtils.isBlank(skills)){
    		String[] skillsarray = skills.split(",") ;
    		for(String skill : skillsarray){
    			OrgiSkillRel rel = new OrgiSkillRel();
    			rel.setOrgi(tenant.getId());
    			rel.setSkillid(skill);
    			rel.setCreater(super.getUser(request).getId());
    			rel.setCreatetime(new Date());
    			orgiSkillRelRes.save(rel) ;
    		}
    	}
    	if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
    		tenant.setOrgid(super.getUser(request).getOrgid());
		}else {
			tenant.setOrgid(MainContext.SYSTEM_ORGI);
		}
    	tenantRes.save(tenant) ;
    	OnlineUserUtils.clean(tenantid);
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	if(super.isTenantshare()) {
    		map.addAttribute("isShowSkillList",true);
    		List<Organ> organList = organRes.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request));
        	map.addAttribute("skillList",organList);
        	List<OrgiSkillRel>  orgiSkillRelList = orgiSkillRelRes.findByOrgi(id) ;
        	map.addAttribute("orgiSkillRelList",orgiSkillRelList);
    	}
    	map.addAttribute("tenant", tenantRes.findById(id)) ;
        return request(super.createRequestPageTempletResponse("/apps/tenant/edit"));
    }
    
    @RequestMapping("/update")
    @Menu(type = "apps" , subtype = "tenant" , admin = true)
    public ModelAndView update(HttpServletRequest request ,@Valid Tenant tenant, @RequestParam(value = "tenantpic", required = false) MultipartFile tenantpic,@Valid String skills) throws NoSuchAlgorithmException, IOException {
    	Tenant temp = tenantRes.findById(tenant.getId()) ;
    	Tenant tenanttemp = tenantRes.findByOrgidAndTenantname(super.getOrgid(request),tenant.getTenantname());
    	if(temp!=null&&tenanttemp!=null&&!temp.getId().equals(tenanttemp.getId())) {
    		return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=tenantexist"));
    	}
    	if(tenant!=null) {
    		tenant.setCreatetime(temp.getCreatetime());
    		if(tenantpic!=null && tenantpic.getOriginalFilename().lastIndexOf(".") > 0){
        		File logoDir = new File(path , "tenantpic");
        		if(!logoDir.exists()){
        			logoDir.mkdirs() ;
        		}
        		String fileName = "tenantpic/"+tenant.getId()+tenantpic.getOriginalFilename().substring(tenantpic.getOriginalFilename().lastIndexOf(".")) ;
        		FileCopyUtils.copy(tenantpic.getBytes(), new File(path , fileName));
        		tenant.setTenantlogo(fileName);
        	}else {
        		tenant.setTenantlogo(temp.getTenantlogo());
        	}
    		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
        		tenant.setOrgid(super.getUser(request).getOrgid());
    		}else {
    			tenant.setOrgid(MainContext.SYSTEM_ORGI);
    		}
    		tenantRes.save(tenant) ;
    		List<OrgiSkillRel>  orgiSkillRelList = orgiSkillRelRes.findByOrgi(tenant.getId()) ;
        	orgiSkillRelRes.delete(orgiSkillRelList);
        	if(!StringUtils.isBlank(skills)){
        		String[] skillsarray = skills.split(",") ;
        		for(String skill : skillsarray){
        			OrgiSkillRel rel = new OrgiSkillRel();
        			rel.setOrgi(tenant.getId());
        			rel.setSkillid(skill);
        			rel.setCreater(super.getUser(request).getId());
        			rel.setCreatetime(new Date());
        			orgiSkillRelRes.save(rel) ;
        		}
        	}
        	OnlineUserUtils.clean(tenant.getId());
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView delete(HttpServletRequest request ,@Valid Tenant tenant) {
    	Tenant temp = tenantRes.findById(tenant.getId()) ;
    	if(tenant!=null) {
    		tenantRes.delete(temp);
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
    }
    
    @RequestMapping("/canswitch")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView canswitch(HttpServletRequest request ,@Valid Tenant tenant) throws UnsupportedEncodingException {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
    	AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject((super.getUser(request)).getId(), super.getOrgi(request));
    	if(agentStatus==null && AutomaticServiceDist.getAgentUsers(super.getUser(request).getId(), super.getOrgi(request))==0) {
    		Tenant temp = tenantRes.findById(tenant.getId()) ;
        	if(temp!=null) {
        		super.getUser(request).setOrgi(temp.getId());
        	}
        	return view;
    	}
    	if(agentStatus!=null) {
    		if(tenant.getId().equals(agentStatus.getOrgi())){
    			Tenant temp = tenantRes.findById(tenant.getId()) ;
            	if(temp!=null) {
            		super.getUser(request).setOrgi(temp.getId());
            	}
            	return view;
    		}else {
    			Tenant temp = tenantRes.findById(agentStatus.getOrgi()) ;
    			return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=t0"+"&currentorgi="+agentStatus.getOrgi()+"&currentname="+URLEncoder.encode(temp!=null?temp.getTenantname():"","UTF-8")));
    		}
    	}
    	AgentUser agentUser = agentUserRepository.findOneByAgentnoAndStatusAndOrgi(super.getUser(request).getId(), MainContext.AgentUserStatusEnum.INSERVICE.toString(), super.getOrgi(request));
    	if(agentUser!=null) {
    		if(tenant.getId().equals(agentUser.getOrgi())){
    			Tenant temp = tenantRes.findById(tenant.getId()) ;
            	if(temp!=null) {
            		super.getUser(request).setOrgi(temp.getId());
            	}
            	return view;
    		}else {
    			Tenant temp = tenantRes.findById(agentUser.getOrgi()) ;
    			return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=t0"+"&currentorgi="+agentUser.getOrgi()+"&currentname="+URLEncoder.encode(temp!=null?temp.getTenantname():"","UTF-8")));
    		}
    		
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=t0"));
    }
    
    /*@RequestMapping("/switch")
    @Menu(type = "apps" , subtype = "tenant")
    public ModelAndView switchTenant(HttpServletRequest request ,@Valid Tenant tenant) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
    	AgentStatus agentStatus = (AgentStatus)CacheHelper.getAgentStatusCacheBean().getCacheObject((super.getUser(request)).getId(), super.getOrgi(request));
    	if(agentStatus==null && AutomaticServiceDist.getAgentUsers(super.getUser(request).getId(), super.getOrgi(request))==0) {
    		Tenant temp = tenantRes.findById(tenant.getId()) ;
        	if(temp!=null) {
        		super.getUser(request).setOrgi(temp.getId());
        	}
        	return view;
    	}
    	if(agentStatus!=null && tenant.getId().equals(agentStatus.getOrgi())) {
    		Tenant temp = tenantRes.findById(tenant.getId()) ;
        	if(temp!=null) {
        		super.getUser(request).setOrgi(temp.getId());
        	}
        	return view;
    	}
    	AgentUser agentUser = agentUserRepository.findOneByAgentnoAndStatusAndOrgi(super.getUser(request).getId(), MainContext.AgentUserStatusEnum.INSERVICE.toString(), super.getOrgi(request));
    	if(agentUser!=null&&tenant.getId().equals(agentUser.getOrgi())) {
    		Tenant temp = tenantRes.findById(tenant.getId()) ;
        	if(temp!=null) {
        		super.getUser(request).setOrgi(temp.getId());
        	}
        	return view;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index.html?msg=t0"));
    }*/
}