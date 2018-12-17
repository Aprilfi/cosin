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

import java.io.Serializable;
import java.util.List;

import com.chatopera.cc.exchange.DataExchangeInterface;
import com.chatopera.cc.app.model.QuickType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatopera.cc.app.persistence.repository.QuickTypeRepository;

@Service("quicktypedata")
public class QuickTypeDataExchangeImpl implements DataExchangeInterface {
	@Autowired
	private QuickTypeRepository quickTypeRes ;
	
	public String getDataByIdAndOrgi(String id, String orgi){
		QuickType quickType = quickTypeRes.findByIdAndOrgi(id,orgi);
		return quickType!=null ? quickType.getName() : id;
	}

	@Override
	public List<Serializable> getListDataByIdAndOrgi(String id , String creater, String orgi) {
		return null ;
	}
	
	public void process(Object data , String orgi) {
		
	}
}
