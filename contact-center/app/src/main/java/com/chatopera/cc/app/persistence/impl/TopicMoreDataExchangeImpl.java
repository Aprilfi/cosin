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
package com.chatopera.cc.app.persistence.impl;

import java.util.List;

import com.chatopera.cc.app.persistence.repository.TopicItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatopera.cc.exchange.DataExchangeInterface;
import com.chatopera.cc.concurrent.dsdata.process.TopicProcess;
import com.chatopera.cc.app.model.TopicItem;

@Service("topicmore")
public class TopicMoreDataExchangeImpl implements DataExchangeInterface{
	@Autowired
	private TopicItemRepository topicItemRes ;
	
	private TopicProcess topicProcess = new TopicProcess();
	
	public TopicItem getDataByIdAndOrgi(String id, String orgi){
		return topicItemRes.findByIdAndOrgi(id, orgi) ;
	}

	@Override
	public List<TopicItem> getListDataByIdAndOrgi(String id , String creater, String orgi) {
		return null ;
	}
	
	public void process(Object data , String orgi) {
		topicProcess.process(data, orgi);
	}
}
