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
package uk.co.unclealex.hammers.calendar.client.util;

import com.google.gwt.user.client.ui.ListBox;

/**
 * @author aj016368
 *
 */
public abstract class EnumListBoxAdaptor<E extends Enum<E>> extends ValueListBoxAdaptor<E> {

  private final Class<E> i_enumClass;

  public EnumListBoxAdaptor(Class<E> enumClass, ListBox listBox, String nullText) {
    super(listBox, nullText);
    i_enumClass = enumClass;
  }

  @Override
  protected E parse(String value) {
    return Enum.valueOf(getEnumClass(), value);
  }

  @Override
  protected String toString(E value) {
    return value.name();
  }

  public Class<E> getEnumClass() {
    return i_enumClass;
  }

}
