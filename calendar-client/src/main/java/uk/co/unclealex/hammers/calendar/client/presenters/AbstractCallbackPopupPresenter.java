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
package uk.co.unclealex.hammers.calendar.client.presenters;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.presenters.AbstractCallbackPopupPresenter.Display;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class AbstractCallbackPopupPresenter<P extends PopupPanel, D extends Display<P>> extends AbstractPopupPresenter<P, D> {

  public interface Display<P> extends AbstractPopupPresenter.Display<P>, CanWait {
    // Empty interface.
  }

  private final AsyncCallbackExecutor i_asyncCallbackExecutor;
  
  public AbstractCallbackPopupPresenter(AsyncCallbackExecutor asyncCallbackExecutor) {
    super();
    i_asyncCallbackExecutor = asyncCallbackExecutor;
  }

  protected abstract class AsyncRunnable<T> extends FailureAsPopupExecutableAsyncCallback<T> implements Runnable {

    private final boolean i_hideOnExit;
    
    public AsyncRunnable(boolean hideOnExit) {
      super();
      i_hideOnExit = hideOnExit;
    }


    public void run() {
      ExecutableAsyncCallback<T> callback = new FailureAsPopupExecutableAsyncCallback<T>() {
        public void onSuccess(T result) {
          AsyncRunnable.this.onSuccess(result);
          if (isHideOnExit()) {
            hide();
          }
        }
        @Override
        public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
            UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
            AsyncCallback<T> callback) {
          AsyncRunnable.this.execute(anonymousAttendanceService, userAttendanceService, adminAttendanceService, callback);          
        }
      };
      getAsyncCallbackExecutor().executeAndWait(callback, getDisplay());
    }


    public boolean isHideOnExit() {
      return i_hideOnExit;
    }
  }

  protected interface ConfirmationRequired {
    public boolean isConfirmationRequired();
  }
  
  protected static class ConfirmationRequiredAlways implements ConfirmationRequired {
    
    public static ConfirmationRequiredAlways INSTANCE = new ConfirmationRequiredAlways();
    @Override
    public boolean isConfirmationRequired() {
      return true;
    }
  }
  
  protected void addConfirmableAction(
      HasClickHandlers target, final String action, final ConfirmationRequired confirmationRequired, final Runnable runnable) {
    ClickHandler handler = new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        if (!confirmationRequired.isConfirmationRequired() || Window.confirm("Are you sure you want to " + action + "?")) {
          runnable.run();
        }
      }
    };
    target.addClickHandler(handler);
  }


  public AsyncCallbackExecutor getAsyncCallbackExecutor() {
    return i_asyncCallbackExecutor;
  }

}
