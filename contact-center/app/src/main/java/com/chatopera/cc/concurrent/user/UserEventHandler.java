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
package com.chatopera.cc.concurrent.user;

import java.util.List;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.mail.Mail;
import com.chatopera.cc.app.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.app.persistence.repository.UserEventRepository;
import com.chatopera.cc.app.persistence.repository.UserTraceRepository;
import com.chatopera.cc.app.model.OnlineUser;
import com.chatopera.cc.app.model.UserTraceHistory;
import org.apache.commons.lang3.StringUtils;

import com.lmax.disruptor.EventHandler;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.persistence.repository.RequestLogRepository;
import com.chatopera.cc.app.model.RequestLog;
import com.chatopera.cc.app.model.UserHistory;


public class UserEventHandler implements EventHandler<UserDataEvent>{

	@Override
	public void onEvent(UserDataEvent arg0, long arg1, boolean arg2)
			throws Exception {
		if(arg0.getEvent() instanceof UserHistory){
			UserEventRepository userEventRes = MainContext.getContext().getBean(UserEventRepository.class) ;
			userEventRes.save((UserHistory)arg0.getEvent()) ;
		}else if(arg0.getEvent() instanceof UserTraceHistory){
			UserTraceRepository userTraceRes = MainContext.getContext().getBean(UserTraceRepository.class) ;
			userTraceRes.save((UserTraceHistory)arg0.getEvent()) ;
		}else if(arg0.getEvent() instanceof RequestLog){
			RequestLogRepository requestLogRes = MainContext.getContext().getBean(RequestLogRepository.class) ;
			requestLogRes.save((RequestLog)arg0.getEvent()) ;
		}else if(arg0.getEvent() instanceof OnlineUser){
			OnlineUserRepository onlineUserRes = MainContext.getContext().getBean(OnlineUserRepository.class) ;
			OnlineUser onlineUser = (OnlineUser)arg0.getEvent() ;
			List<OnlineUser> onlineUserList = onlineUserRes.findByUseridAndOrgi(onlineUser.getUserid(), onlineUser.getOrgi()) ;
			if(onlineUserList.size() == 0){
				onlineUserRes.save(onlineUser) ;
			}
		}if(arg0.getEvent() instanceof Mail){
			Mail mail = (Mail)arg0.getEvent() ;
			if(null!=mail&&!StringUtils.isBlank(mail.getEmail())) {
				MainUtils.sendMail(mail.getEmail(), mail.getCc(), mail.getSubject(), mail.getContent(), mail.getFilenames());
			}
		}
	}

}
