/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

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
public abstract class AbstractListPanel extends ComplexPanel {

  protected AbstractListPanel(String listElement) {
  	setElement(DOM.createElement(listElement));
  }

  @Override
  public void add(Widget w) {
    Element li = createLI();
    DOM.appendChild(getElement(), li);
    add(w, li);
  }

  public void insert(IsWidget w, int beforeIndex) {
    insert(asWidgetOrNull(w), beforeIndex);
  }

  public void insert(Widget w, int beforeIndex) {
    checkIndexBoundsForInsertion(beforeIndex);

    /*
     * The case where we reinsert an already existing child is tricky.
     * 
     * For the WIDGET, it ultimately removes first and inserts second, so we
     * have to adjust the index within ComplexPanel.insert(). But for the DOM,
     * we insert first and remove second, which means we DON'T need to adjust
     * the index.
     */
    Element li = createLI();
    DOM.insertChild(getElement(), li, beforeIndex);
    insert(w, li, beforeIndex, false);
  }

  @Override
  public boolean remove(Widget w) {
    // Get the LI to be removed, before calling super.remove(), because
    // super.remove() will detach the child widget's element from its parent.
    Element li = DOM.getParent(w.getElement());
    boolean removed = super.remove(w);
    if (removed) {
      DOM.removeChild(getElement(), li);
    }
    return removed;
  }

  /**
   * <b>Affected Elements:</b>
   * <ul>
   * <li>-# = the cell at the given index.</li>
   * </ul>
   * 
   * @see UIObject#onEnsureDebugId(String)
   */
  @Override
  protected void onEnsureDebugId(String baseID) {
    super.onEnsureDebugId(baseID);
    int numChildren = getWidgetCount();
    for (int i = 0; i < numChildren; i++) {
      ensureDebugId(getWidgetLi(getWidget(i)), baseID, "" + i);
    }
  }

  Element getWidgetLi(Widget w) {
    if (w.getParent() != this) {
      return null;
    }
    return DOM.getParent(w.getElement());
  }

  protected Element createLI() {
    return DOM.createElement("li");
  }
}
