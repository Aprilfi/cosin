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
package com.chatopera.cc.app.handler.api.rest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.RestResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chatopera.cc.app.persistence.repository.ChatMessageRepository;
import com.chatopera.cc.util.RestResultType;
import com.chatopera.cc.app.handler.Handler;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/chatmessage")
@Api(value = "获取对话内容", description = "获取访客对话的内容")
public class ApiChatMessageController extends Handler{
	
	@Autowired
	private ChatMessageRepository chatMessageRes ;

	/**
	 * 
	 * @param request
	 * @param serviceid		AgentServiceID
	 * @return
	 */
	@RequestMapping( method = RequestMethod.GET)
	@Menu(type = "apps" , subtype = "agentuser" , access = true)
	@ApiOperation("获取访客对话内容")
    public ResponseEntity<RestResult> list(HttpServletRequest request , @Valid String serviceid) {
		ResponseEntity<RestResult> result = null ;
		if(!StringUtils.isBlank(serviceid)) {
			result = new ResponseEntity<>(new RestResult(RestResultType.OK , chatMessageRes.findByAgentserviceidAndOrgi(serviceid , super.getUser(request).getOrgi(),new PageRequest(super.getP(request), super.getPs(request) , Sort.Direction.DESC, "createtime"))), HttpStatus.OK) ;
		}else {
			result = new ResponseEntity<>(new RestResult(RestResultType.LACKDATA , RestResultType.LACKDATA.getMessage()), HttpStatus.OK) ;
		}
        return result ;
    }
}