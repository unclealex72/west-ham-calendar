/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.hammers.calendar.shared.services.AttendanceService;
import uk.co.unclealex.hammers.calendar.shared.services.SecurityInvalidator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */
public class AbstractAttendanceServlet extends RemoteServiceServlet {

	private BeanFactory i_beanFactory;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		setBeanFactory(applicationContext);
	}

	@Override
	protected String readContent(HttpServletRequest request) throws ServletException, IOException {
		request.getSession();
		return super.readContent(request);
	}

	protected AttendanceService createAttendanceService() {
		final AttendanceService attendanceService = getBeanFactory().getBean("attendanceService", AttendanceService.class);
		SecurityInvalidator securityInvalidator = new SecurityInvalidator() {
			@Override
			public void invalidate() {
				getThreadLocalRequest().getSession().invalidate();
			}
		};
		attendanceService.setSecurityInvalidator(securityInvalidator);
		final Logger log = LoggerFactory.getLogger(getClass());
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(attendanceService, args);
				}
				catch (InvocationTargetException e) {
					Throwable targetException = e.getTargetException();
					log.error(method.getName(), targetException);
					throw targetException;
				}
				catch (Throwable t) {
					log.error(method.getName(), t);
					throw t;
				}
			}
		};
		return (AttendanceService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { AttendanceService.class }, handler);
	}
	
	public BeanFactory getBeanFactory() {
		return i_beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		i_beanFactory = beanFactory;
	}

}
