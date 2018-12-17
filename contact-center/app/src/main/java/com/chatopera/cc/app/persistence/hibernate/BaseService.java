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
package com.chatopera.cc.app.persistence.hibernate;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseService<T> {

	private SessionFactory hibernateFactory;

	@Autowired
	public BaseService(EntityManagerFactory factory) {
		if (factory.unwrap(SessionFactory.class) == null) {
			throw new NullPointerException("factory is not a hibernate factory");
		}
		this.hibernateFactory = factory.unwrap(SessionFactory.class);
	}
	
	
	public void save(Object t){
		Session session = hibernateFactory.openSession() ; 
		try{
			session.save(t) ;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			session.flush();
			session.close();	
		}
	}
	
	public void delete(Object object){
		Session session = hibernateFactory.openSession() ; 
		try{
			session.delete(session.merge(object));
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			session.flush();
			session.close();	
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> list(String bean){
		List<T> dataList = null ;
		Session session = hibernateFactory.openSession() ; 
		try{
			dataList = session.createCriteria(Class.forName(bean)).list() ;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			session.flush();
			session.close();	
		}
		return dataList;
	}
}