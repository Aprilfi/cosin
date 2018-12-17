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
package com.chatopera.cc.app.handler.admin.role;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.persistence.repository.RoleAuthRepository;
import com.chatopera.cc.app.persistence.repository.SysDicRepository;
import com.chatopera.cc.app.persistence.repository.UserRoleRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.persistence.repository.RoleRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.Role;
import com.chatopera.cc.app.model.RoleAuth;
import com.chatopera.cc.app.model.SysDic;
import com.chatopera.cc.app.model.UKeFuDic;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.model.UserRole;

@Controller
@RequestMapping("/admin/role")
public class RoleController extends Handler{
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRes;
	
	@Autowired
	private RoleAuthRepository roleAuthRes ;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SysDicRepository sysDicRes;

    @RequestMapping("/index")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String role) {
    	List<Role> roleList = roleRepository.findByOrgiAndOrgid(super.getOrgiByTenantshare(request),super.getOrgid(request));
    	map.addAttribute("roleList", roleList);
    	if(roleList.size() > 0){
    		Role roleData = null ;
    		if(!StringUtils.isBlank(role)){
    			for(Role data : roleList){
    				if(data.getId().equals(role)){
    					roleData = data ;
    					map.addAttribute("roleData", data);
    				}
    			}
    		}else{
    			map.addAttribute("roleData", roleData = roleList.get(0));
    		}
    		if(roleData!=null){
    			map.addAttribute("userRoleList", userRoleRes.findByOrgiAndRole(super.getOrgiByTenantshare(request), roleData, new PageRequest(super.getP(request), super.getPs(request))) );
    		}
    	}
        return request(super.createAdminTempletResponse("/admin/role/index"));
    }
    
    @RequestMapping("/add")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/role/add"));
    }
    
    @RequestMapping("/save")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView save(HttpServletRequest request ,@Valid Role role) {
    	Role tempRole = roleRepository.findByNameAndOrgiAndOrgid(role.getName(), super.getOrgiByTenantshare(request),super.getOrgid(request)) ;
    	String msg = "admin_role_save_success" ;
    	if(tempRole != null){
    		msg =  "admin_role_save_exist";
    	}else{
    		role.setOrgi(super.getOrgiByTenantshare(request));
    		role.setCreater(super.getUser(request).getId());
    		role.setCreatetime(new Date());
    		role.setUpdatetime(new Date());
    		
    		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
    			role.setOrgid(super.getUser(request).getOrgid());
    		}else {
    			role.setOrgid(MainContext.SYSTEM_ORGI);
    		}
    		
    		roleRepository.save(role) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?msg="+msg));
    }
    
    @RequestMapping("/seluser")
    @Menu(type = "admin" , subtype = "seluser" , admin = true)
    public ModelAndView seluser(ModelMap map , HttpServletRequest request , @Valid String role) {
    	map.addAttribute("userList", userRepository.findByOrgiAndDatastatusAndOrgid(super.getOrgiByTenantshare(request) , false,super.getOrgid(request))) ;
    	Role roleData = roleRepository.findByIdAndOrgi(role, super.getOrgiByTenantshare(request)) ;
    	map.addAttribute("userRoleList", userRoleRes.findByOrgiAndRole(super.getOrgiByTenantshare(request) , roleData)) ;
    	map.addAttribute("role", roleData) ;
        return request(super.createRequestPageTempletResponse("/admin/role/seluser"));
    }
    
    
    @RequestMapping("/saveuser")
    @Menu(type = "admin" , subtype = "saveuser" , admin = true)
    public ModelAndView saveuser(HttpServletRequest request ,@Valid String[] users , @Valid String role) {
    	Role roleData = roleRepository.findByIdAndOrgi(role, super.getOrgiByTenantshare(request)) ;
    	List<UserRole> userRoleList = userRoleRes.findByOrgiAndRole(super.getOrgiByTenantshare(request) , roleData) ;
    	if(users!=null && users.length > 0){
	    	for(String user : users){
	    		boolean exist = false ;
	    		for(UserRole userRole : userRoleList){
	    			if(user.equals(userRole.getUser().getId())){
	    				exist = true ; continue ;
	    			}
	    		}
	    		if(exist == false) {
					UserRole userRole = new UserRole() ;
					userRole.setUser(new User(user));
					userRole.setRole(new Role(role));
					userRole.setOrgi(super.getOrgiByTenantshare(request));
					userRole.setCreater(super.getUser(request).getId());
					userRoleRes.save(userRole) ;
	    		}
			}
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?role="+role));
    }
    
    @RequestMapping("/user/delete")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView userroledelete(HttpServletRequest request ,@Valid String id , @Valid String role) {
    	if(role!=null){
	    	userRoleRes.delete(id);
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?role="+role));
    }
    
    @RequestMapping("/edit")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView edit(ModelMap map , HttpServletRequest request ,@Valid String id) {
    	ModelAndView view = request(super.createRequestPageTempletResponse("/admin/role/edit")) ;
    	view.addObject("roleData", roleRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request))) ;
        return view;
    }
    
    @RequestMapping("/update")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView update(HttpServletRequest request ,@Valid Role role) {
    	Role tempRole = roleRepository.findByIdAndOrgi(role.getId(), super.getOrgiByTenantshare(request)) ;
    	String msg = "admin_role_update_success" ;
    	if(tempRole != null){
    		tempRole.setName(role.getName());
    		tempRole.setUpdatetime(new Date());
    		
    		if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
    			tempRole.setOrgid(super.getUser(request).getOrgid());
    		}else {
    			tempRole.setOrgid(MainContext.SYSTEM_ORGI);
    		}
    		
    		roleRepository.save(tempRole) ;
    	}else{
    		msg =  "admin_role_update_not_exist";
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?msg="+msg));
    }
    
    @RequestMapping("/delete")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView delete(HttpServletRequest request ,@Valid Role role) {
    	String msg = "admin_role_delete" ;
    	if(role!=null){
    		userRoleRes.delete(userRoleRes.findByOrgiAndRole(super.getOrgiByTenantshare(request), role));
	    	roleRepository.delete(role);
    	}else{
    		msg = "admin_role_not_exist" ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?msg="+msg));
    }
    
    @RequestMapping("/auth")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView auth(ModelMap map , HttpServletRequest request , @Valid String id) {
    	SysDic sysDic = sysDicRes.findByCode(MainContext.UKEFU_SYSTEM_AUTH_DIC) ;
    	if(sysDic!=null){
    		map.addAttribute("resourceList", sysDicRes.findByDicid(sysDic.getId())) ;
    	}
    	map.addAttribute("sysDic", sysDic) ;
    	Role role = roleRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)) ;
    	map.addAttribute("role", role) ;
    	map.addAttribute("roleAuthList", roleAuthRes.findByRoleidAndOrgi(role.getId(), super.getOrgiByTenantshare(request))) ;
        return request(super.createRequestPageTempletResponse("/admin/role/auth"));
    }
    
    @RequestMapping("/auth/save")
    @Menu(type = "admin" , subtype = "role")
    public ModelAndView authsave(HttpServletRequest request ,@Valid String id ,@Valid String menus) {
    	List<RoleAuth>  roleAuthList = roleAuthRes.findByRoleidAndOrgi(id, super.getOrgiByTenantshare(request)) ;
    	roleAuthRes.delete(roleAuthList);
    	if(!StringUtils.isBlank(menus)){
    		String[] menuarray = menus.split(",") ;
    		for(String menu : menuarray){
    			RoleAuth roleAuth = new RoleAuth();
    			
    			roleAuth.setRoleid(id);
    			roleAuth.setDicid(menu);
    			SysDic sysDic = UKeFuDic.getInstance().getDicItem(menu) ;
    			if(sysDic!=null && !"0".equals(sysDic.getParentid())) {
	    			roleAuth.setCreater(super.getUser(request).getId());
	    			roleAuth.setOrgi(super.getOrgiByTenantshare(request));
	    			roleAuth.setCreatetime(new Date());
	    			roleAuth.setName(sysDic.getName());
	    			roleAuth.setDicvalue(sysDic.getCode());
	    			roleAuthRes.save(roleAuth) ;
    			}
    		}
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/role/index.html?role="+id));
    }
}