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
package com.chatopera.cc.util;

import java.text.ParseException;
import java.util.Date;

import com.chatopera.cc.app.basic.MainUtils;
import org.quartz.CronExpression;


public class CronTools {
	/**
	 * 
	 * @param crontabExp
	 * @return
	 * @throws ParseException
	 */
	
	public static CronExpression getFireTime(String crontabExp) throws ParseException{
		return new CronExpression(crontabExp);
		
	}
	/**
	 * 
	 * @param crontabExp
	 * @return
	 * @throws ParseException
	 */
	
	public static Date getFinalFireTime(String crontabExp , Date date) throws ParseException{
		CronExpression expression = new CronExpression(crontabExp) ;
		return expression.getNextValidTimeAfter(date!=null ? date:new Date());
		
	}
	public static void main(String[] args) throws Exception{
		
		System.out.println(MainUtils.dateFormate.format(CronTools.getFinalFireTime("0 0/40 0/1 * * ?",new Date())));
	}
}
