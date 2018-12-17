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
package com.chatopera.cc.app.handler.apps.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.app.persistence.repository.PublishedReportRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.app.persistence.repository.DataDicRepository;
import com.chatopera.cc.app.persistence.repository.ReportCubeService;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.PublishedReport;
import com.chatopera.cc.app.model.ReportFilter;

@Controller
@RequestMapping("/apps/view")
public class ReportViewController extends Handler{
	
	@Value("${web.upload-path}")
    private String path;
	
	@Value("${uk.im.server.port}")  
    private Integer port; 
	
	@Autowired
	private DataDicRepository dataDicRes;
	
	@Autowired
	private PublishedReportRepository publishedReportRes;
	
	@Autowired
	private ReportCubeService reportCubeService;
	
	
    @RequestMapping("/index")
    @Menu(type = "setting" , subtype = "report" , admin= true)
    public ModelAndView index(ModelMap map , HttpServletRequest request , @Valid String dicid , @Valid String id) throws Exception {
    	Page<PublishedReport> publishedReportList = null ;
    	if(!StringUtils.isBlank(dicid) && !"0".equals(dicid)){
        	map.put("dataDic", dataDicRes.findByIdAndOrgi(dicid, super.getOrgi(request))) ;
    		map.put("reportList", publishedReportList = publishedReportRes.findByOrgiAndDicid(super.getOrgi(request) , dicid , new PageRequest(super.getP(request), super.getPs(request)))) ;
    	}else{
    		map.put("reportList", publishedReportList = publishedReportRes.findByOrgi(super.getOrgi(request) , new PageRequest(super.getP(request), super.getPs(request)))) ;
    	}
    	if(publishedReportList!=null && publishedReportList.getContent().size() > 0) {
    		PublishedReport publishedReport = publishedReportList.getContent().get(0);
    		if(!StringUtils.isBlank(id)) {
    			for(PublishedReport report : publishedReportList) {
    				if(report.getId().equals(id)) {
    					publishedReport = report ; break ;
    				}
    			}
    		}
    		map.put("report", publishedReport) ;
    		
    		if(publishedReport!=null) {
				map.addAttribute("publishedReport", publishedReport);
				map.addAttribute("report", publishedReport.getReport());
				map.addAttribute("reportModels", publishedReport.getReport().getReportModels());
				List<ReportFilter> listFilters = publishedReport.getReport().getReportFilters();
				if(!listFilters.isEmpty()) {
					Map<String,ReportFilter> filterMap = new HashMap<String,ReportFilter>();
					for(ReportFilter rf:listFilters) {
						filterMap.put(rf.getId(), rf);
					}
					for(ReportFilter rf:listFilters) {
						if(!StringUtils.isBlank(rf.getCascadeid())) {
							rf.setChildFilter(filterMap.get(rf.getCascadeid()));
						}
					}
				}
				map.addAttribute("reportFilters", reportCubeService.fillReportFilterData(listFilters, request));
			}
    		
    	}
    	map.put("dataDicList", dataDicRes.findByOrgi(super.getOrgi(request))) ;
    	return request(super.createRequestPageTempletResponse("/apps/business/view/index"));
    }
}