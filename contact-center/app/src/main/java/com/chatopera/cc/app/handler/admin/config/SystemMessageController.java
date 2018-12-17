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
package com.chatopera.cc.app.handler.admin.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.persistence.repository.SystemMessageRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.SystemMessage;
import com.chatopera.cc.app.model.UKeFuDic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.basic.MainUtils;

@Controller
@RequestMapping("/admin")
public class SystemMessageController extends Handler {
	
	@Autowired
	private SystemMessageRepository systemMessageRepository;
	
    @RequestMapping("/email/index")
    @Menu(type = "setting" , subtype = "email")
    public ModelAndView index(ModelMap map , HttpServletRequest request) throws FileNotFoundException, IOException {
    	map.addAttribute("emailList", systemMessageRepository.findByMsgtypeAndOrgi("email" , super.getOrgi(request) , new PageRequest(super.getP(request), super.getPs(request))));
    	return request(super.createAdminTempletResponse("/admin/email/index"));
    }
    
    @RequestMapping("/email/add")
    @Menu(type = "admin" , subtype = "email")
    public ModelAndView add(ModelMap map , HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/email/add"));
    }
    
    @RequestMapping("/email/save")
    @Menu(type = "admin" , subtype = "user")
    public ModelAndView save(HttpServletRequest request ,@Valid SystemMessage email) throws NoSuchAlgorithmException {
    	email.setOrgi(super.getOrgi(request));
    	email.setMsgtype(MainContext.SystemMessageType.EMAIL.toString());
    	if(!StringUtils.isBlank(email.getSmtppassword())) {
			email.setSmtppassword(MainUtils.encryption(email.getSmtppassword()));
		}
    	systemMessageRepository.save(email) ;
    	return request(super.createRequestPageTempletResponse("redirect:/admin/email/index.html"));
    }
    
    @RequestMapping("/email/edit")
    @Menu(type = "admin" , subtype = "email")
    public ModelAndView edit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("email", systemMessageRepository.findByIdAndOrgi(id, super.getOrgi(request))) ;
        return request(super.createRequestPageTempletResponse("/admin/email/edit"));
    }
    
    @RequestMapping("/email/update")
    @Menu(type = "admin" , subtype = "user" , admin = true)
    public ModelAndView update(HttpServletRequest request ,@Valid SystemMessage email) throws NoSuchAlgorithmException {
    	SystemMessage temp = systemMessageRepository.findByIdAndOrgi(email.getId(), super.getOrgi(request)) ;
    	if(email!=null) {
    		email.setCreatetime(temp.getCreatetime());
    		email.setOrgi(temp.getOrgi());
    		email.setMsgtype(MainContext.SystemMessageType.EMAIL.toString());
    		if(!StringUtils.isBlank(email.getSmtppassword())) {
    			email.setSmtppassword(MainUtils.encryption(email.getSmtppassword()));
    		}else {
    			email.setSmtppassword(temp.getSmtppassword());
    		}
    		systemMessageRepository.save(email) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/email/index.html"));
    }
    
    @RequestMapping("/email/delete")
    @Menu(type = "admin" , subtype = "user")
    public ModelAndView delete(HttpServletRequest request ,@Valid SystemMessage email) {
    	SystemMessage temp = systemMessageRepository.findByIdAndOrgi(email.getId(), super.getOrgi(request)) ;
    	if(email!=null) {
    		systemMessageRepository.delete(temp);
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/email/index.html"));
    }
    
    
    @RequestMapping("/sms/index")
    @Menu(type = "setting" , subtype = "sms")
    public ModelAndView smsindex(ModelMap map , HttpServletRequest request) throws FileNotFoundException, IOException {
    	map.addAttribute("smsList", systemMessageRepository.findByMsgtypeAndOrgi("sms" , super.getOrgi(request) , new PageRequest(super.getP(request), super.getPs(request))));
    	return request(super.createAdminTempletResponse("/admin/sms/index"));
    }
    
    @RequestMapping("/sms/add")
    @Menu(type = "admin" , subtype = "sms")
    public ModelAndView smsadd(ModelMap map , HttpServletRequest request) {
    	
    	map.addAttribute("smsType", UKeFuDic.getInstance().getDic("com.dic.sms.type")) ;
        return request(super.createRequestPageTempletResponse("/admin/sms/add"));
    }
    
    @RequestMapping("/sms/save")
    @Menu(type = "admin" , subtype = "sms")
    public ModelAndView smssave(HttpServletRequest request ,@Valid SystemMessage sms) throws NoSuchAlgorithmException {
    	sms.setOrgi(super.getOrgi(request));
    	sms.setMsgtype(MainContext.SystemMessageType.SMS.toString());
    	if(!StringUtils.isBlank(sms.getSmtppassword())) {
    		sms.setSmtppassword(MainUtils.encryption(sms.getSmtppassword()));
		}
    	systemMessageRepository.save(sms) ;
    	return request(super.createRequestPageTempletResponse("redirect:/admin/sms/index.html"));
    }
    
    @RequestMapping("/sms/edit")
    @Menu(type = "admin" , subtype = "sms")
    public ModelAndView smsedit(ModelMap map , HttpServletRequest request , @Valid String id) {
    	map.addAttribute("smsType", UKeFuDic.getInstance().getDic("com.dic.sms.type")) ;
    	map.addAttribute("sms", systemMessageRepository.findByIdAndOrgi(id, super.getOrgi(request))) ;
        return request(super.createRequestPageTempletResponse("/admin/sms/edit"));
    }
    
    @RequestMapping("/sms/update")
    @Menu(type = "admin" , subtype = "sms" , admin = true)
    public ModelAndView smsupdate(HttpServletRequest request ,@Valid SystemMessage sms) throws NoSuchAlgorithmException {
    	SystemMessage temp = systemMessageRepository.findByIdAndOrgi(sms.getId(), super.getOrgi(request)) ;
    	if(sms!=null) {
    		sms.setCreatetime(temp.getCreatetime());
    		sms.setOrgi(temp.getOrgi());
    		sms.setMsgtype(MainContext.SystemMessageType.SMS.toString());
    		if(!StringUtils.isBlank(sms.getSmtppassword())) {
    			sms.setSmtppassword(MainUtils.encryption(sms.getSmtppassword()));
    		}else {
    			sms.setSmtppassword(temp.getSmtppassword());
    		}
    		systemMessageRepository.save(sms) ;
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/sms/index.html"));
    }
    
    @RequestMapping("/sms/delete")
    @Menu(type = "admin" , subtype = "sms")
    public ModelAndView smsdelete(HttpServletRequest request ,@Valid SystemMessage sms) {
    	SystemMessage temp = systemMessageRepository.findByIdAndOrgi(sms.getId(), super.getOrgi(request)) ;
    	if(sms!=null) {
    		systemMessageRepository.delete(temp);
    	}
    	return request(super.createRequestPageTempletResponse("redirect:/admin/sms/index.html"));
    }
}