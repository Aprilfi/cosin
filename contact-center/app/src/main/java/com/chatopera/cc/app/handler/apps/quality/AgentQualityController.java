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

package com.chatopera.cc.app.handler.apps.quality;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.persistence.repository.SessionConfigRepository;
import com.chatopera.cc.app.persistence.repository.TagRepository;
import com.chatopera.cc.app.model.Quality;
import com.chatopera.cc.app.model.QualityRequest;
import com.chatopera.cc.app.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.persistence.repository.QualityRepository;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.SessionConfig;

@Controller
@RequestMapping("/apps/quality")
public class AgentQualityController extends Handler{
	
	
	@Autowired
	private QualityRepository qualityRes ;
	
	@Autowired
	private SessionConfigRepository sessionConfigRes ;
	
	@Autowired
	private TagRepository tagRes ;
	
	
	@RequestMapping(value = "/index")
    @Menu(type = "agent" , subtype = "quality" , access = false)
    public ModelAndView index(ModelMap map , HttpServletRequest request ) {
		map.addAttribute("sessionConfig", AutomaticServiceDist.initSessionConfig(super.getOrgi(request))) ;
		map.addAttribute("qualityList", qualityRes.findByQualitytypeAndOrgi(MainContext.QualityType.CHAT.toString(), super.getOrgi(request))) ;
		map.addAttribute("tagList", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.TagTypeEnum.QUALITY.toString())) ;
    	return request(super.createAppsTempletResponse("/apps/quality/index"));
    }
	
	
	@RequestMapping(value = "/save")
    @Menu(type = "agent" , subtype = "quality" , access = false)
    public ModelAndView save(ModelMap map , HttpServletRequest request ,@Valid QualityRequest qualityArray) {
		if(qualityArray!=null && qualityArray.getTitle()!=null) {
			List<Quality> qualityList = qualityRes.findByQualitytypeAndOrgi(MainContext.QualityType.CHAT.toString(), super.getOrgi(request)) ;
			qualityRes.delete(qualityList);
			List<Quality> tempList = new ArrayList<Quality>();
			for(int i=0 ; i<qualityArray.getTitle().length ; i++) {
				Quality temp = new Quality();
				temp.setName(qualityArray.getTitle()[i]);
				if(qualityArray.getDescription().length == qualityArray.getTitle().length) {
					temp.setDescription(qualityArray.getDescription()[i]);
				}
				if(qualityArray.getScore().length == qualityArray.getTitle().length) {
					temp.setScore(qualityArray.getScore()[i]);
				}
				temp.setOrgi(super.getOrgi(request));
				temp.setQualitytype(MainContext.QualityType.CHAT.toString());
				tempList.add(temp) ;
			}
			if(tempList.size() > 0) {
				qualityRes.save(tempList) ;
			}
			SessionConfig config = AutomaticServiceDist.initSessionConfig(super.getOrgi(request)) ;
			if(config!=null) {
				if("points".equals(request.getParameter("qualityscore"))) {
					config.setQualityscore("points");
				}else {
					config.setQualityscore("score");
				}
				
				sessionConfigRes.save(config) ;
		    	
		    	CacheHelper.getSystemCacheBean().put(MainContext.SYSTEM_CACHE_SESSION_CONFIG+"_"+super.getOrgi(request),config, super.getOrgi(request)) ;
		    	
		    	CacheHelper.getSystemCacheBean().delete(MainContext.SYSTEM_CACHE_SESSION_CONFIG_LIST , MainContext.SYSTEM_ORGI) ;
			}
			if(qualityArray!=null && qualityArray.getTag()!=null && qualityArray.getTag().length > 0) {
				List<Tag> tagList = tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.TagTypeEnum.QUALITY.toString()) ;
				if(tagList.size() > 0) {
					tagRes.delete(tagList);
				}
				List<Tag> tagTempList = new ArrayList<Tag>() ;
				for(String tag : qualityArray.getTag()) {
					Tag temp = new Tag();
					temp.setOrgi(super.getOrgi(request));
					temp.setCreater(super.getUser(request).getId());
					temp.setTag(tag);
					temp.setCreater(super.getOrgi(request));
					temp.setTagtype(MainContext.TagTypeEnum.QUALITY.toString());
					tagTempList.add(temp) ;
				}
				if(tagTempList.size() > 0) {
					tagRes.save(tagTempList) ;
				}
			}
		}
    	return request(super.createRequestPageTempletResponse("redirect:/apps/quality/index.html"));
    }
}
