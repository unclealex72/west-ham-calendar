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
package uk.co.unclealex.hammers.calendar.server.spring;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;

/**
 * @author aj016368
 *
 */
public class DatasourceFactory implements FactoryBean<DataSource> {

  private static final Logger log = LoggerFactory.getLogger(DatasourceFactory.class);
  
  private String i_jndiName;
  private String i_derbyDb;
  
  @Override
  public DataSource getObject() throws SQLException {
    String jndiName = getJndiName();
    try {
      JndiObjectFactoryBean jndiFactoryBean = new JndiObjectFactoryBean();
      jndiFactoryBean.setProxyInterface(DataSource.class);
      jndiFactoryBean.setJndiName(jndiName);
      jndiFactoryBean.setBeanClassLoader(getClass().getClassLoader());
      jndiFactoryBean.afterPropertiesSet();
      DataSource dataSource = (DataSource) jndiFactoryBean.getObject();
      if (dataSource == null) {
        throw new NullPointerException();
      }
      return dataSource;
    }
    catch (Throwable t) {
      String derbyDb = getDerbyDb();
      log.warn("Cannot locate the JNDI datasource " + jndiName + ". Using " + derbyDb + " instead.", t);
      DataSource dataSource = createDataSource(";create=true");
      dataSource.getConnection();
      return createDataSource("");
    }
  }

  /**
   * @param string
   * @return
   */
  protected DataSource createDataSource(String extra) {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(org.apache.derby.jdbc.EmbeddedDriver.class.getName());
    String derbyDb = getDerbyDb();
    dataSource.setUrl(derbyDb + extra);
    return dataSource;
  }

  @Override
  public Class<?> getObjectType() {
    return DataSource.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  public String getJndiName() {
    return i_jndiName;
  }

  public void setJndiName(String jndiName) {
    i_jndiName = jndiName;
  }

  public String getDerbyDb() {
    return i_derbyDb;
  }

  public void setDerbyDb(String derbyDb) {
    i_derbyDb = derbyDb;
  }

}
