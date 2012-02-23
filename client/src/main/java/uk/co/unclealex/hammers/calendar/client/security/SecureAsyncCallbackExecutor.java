/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.security;

import javax.inject.Inject;

import uk.co.unclealex.hammers.calendar.client.factories.AsyncCallbackExecutor;
import uk.co.unclealex.hammers.calendar.client.factories.GoogleAuthenticationPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.factories.LoginPresenterFactory;
import uk.co.unclealex.hammers.calendar.client.presenters.GoogleAuthenticationPresenter;
import uk.co.unclealex.hammers.calendar.client.presenters.WaitingPresenter;
import uk.co.unclealex.hammers.calendar.client.remote.AdminAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.AnonymousAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.remote.UserAttendanceServiceAsync;
import uk.co.unclealex.hammers.calendar.client.util.CanWait;
import uk.co.unclealex.hammers.calendar.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.hammers.calendar.shared.exceptions.GoogleAuthenticationFailedException;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

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
public class SecureAsyncCallbackExecutor implements AsyncCallbackExecutor {

  private final WaitingPresenter i_waitingPresenter;
	private final LoginPresenterFactory i_loginPresenterFactory;
	private final AnonymousAttendanceServiceAsync i_anonymousAttendanceService;
	private final UserAttendanceServiceAsync i_userAttendanceService;
	private final AdminAttendanceServiceAsync i_adminAttendanceService;
	private final GoogleAuthenticationPresenterFactory i_googleAuthenticationPresenterFactory;
	
	@Inject
	public SecureAsyncCallbackExecutor(LoginPresenterFactory loginPresenterFactory,
	    GoogleAuthenticationPresenterFactory googleAuthenticationPresenterFactory,
			AnonymousAttendanceServiceAsync anonymousAttendanceService, UserAttendanceServiceAsync userAttendanceService,
			AdminAttendanceServiceAsync adminAttendanceService, WaitingPresenter waitingPresenter) {
		super();
		i_googleAuthenticationPresenterFactory = googleAuthenticationPresenterFactory;
		i_loginPresenterFactory = loginPresenterFactory;
		i_anonymousAttendanceService = anonymousAttendanceService;
		i_userAttendanceService = userAttendanceService;
		i_adminAttendanceService = adminAttendanceService;
		i_waitingPresenter = waitingPresenter;
	}

	@Override
	public <T> void execute(final ExecutableAsyncCallback<T> callback) {
		class RunnableAsyncCallback implements AsyncCallback<T>, Runnable {
			public void onSuccess(T result) {
				callback.onSuccess(result);
			}			
			public void onFailure(Throwable caught) {
				SecureAsyncCallbackExecutor.this.onFailure(caught, this, callback);
			}
			public void run() {
				callback.execute(getAnonymousAttendanceService(), getUserAttendanceService(), getAdminAttendanceService(), this);
			}
		}
		new RunnableAsyncCallback().run();
	}

	public <T> void onFailure(Throwable caught, Runnable originalAction, final ExecutableAsyncCallback<T> callback) {
		if (isRefused(caught)) {
			onRefused();
		}
		else if (isLoginRequired(caught)) {
			onLoginRequired(originalAction);
		}
		else if (isGoogleAuthenticationRequired(caught)) {
		  onGoogleAuthenticationRequired(originalAction);
		}
		else {
			callback.onFailure(caught);
		}
	}

  protected boolean isGoogleAuthenticationRequired(Throwable caught) {
    return caught instanceof GoogleAuthenticationFailedException;
  }

  protected void onGoogleAuthenticationRequired(final Runnable originalAction) {
    ExecutableAsyncCallback<String> authenticationUrlCallback = new FailureAsPopupExecutableAsyncCallback<String>() {
      @Override
      public void onSuccess(String authenticationUrl) {
        if (authenticationUrl != null) {
          GoogleAuthenticationPresenter googleAuthenticationPresenter = 
              getGoogleAuthenticationPresenterFactory().createGoogleAuthenticationPresenter(authenticationUrl, originalAction);
          googleAuthenticationPresenter.center();
        }
      }
      @Override
      public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
          UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
          AsyncCallback<String> callback) {
        adminAttendanceService.createGoogleAuthorisationUrlIfRequired(callback);
      }
    };
    execute(authenticationUrlCallback);
  }

  protected boolean isRefused(Throwable t) {
		return (t instanceof StatusCodeException) && 403 == ((StatusCodeException) t).getStatusCode();
	}
	
	protected void onRefused() {
		Window.alert("You were refused access to this resource.");
	}
	
	protected void onLoginRequired(Runnable originalAction) {
		getLoginPresenterFactory().createLoginPresenter(originalAction).center();
	}

	/**
	 * @param caught
	 * @return
	 */
	private boolean isLoginRequired(Throwable caught) {
		return caught instanceof InvocationException && caught.getMessage().contains("/j_spring_security_check");
	}

	protected boolean isLoginFailed(Throwable caught) {
		return false;
	}

	@Override
	public <T> void executeAndWait(final ExecutableAsyncCallback<T> executableAsyncCallback, final CanWait canWait) {
	  ExecutableAsyncCallback<T> waitCallback = new ExecutableAsyncCallback<T>() {
	    @Override
	    public void onSuccess(T result) {
	      stopWaiting(canWait);
	      executableAsyncCallback.onSuccess(result);
	    }

	    @Override
	    public void onFailure(Throwable cause) {
	      stopWaiting(canWait);
	      executableAsyncCallback.onFailure(cause);
	    }
	    
	    @Override
	    public void execute(AnonymousAttendanceServiceAsync anonymousAttendanceService,
	        UserAttendanceServiceAsync userAttendanceService, AdminAttendanceServiceAsync adminAttendanceService,
	        AsyncCallback<T> callback) {
	      startWaiting(canWait);
	      executableAsyncCallback.execute(anonymousAttendanceService, userAttendanceService, adminAttendanceService, callback);

	    }

	    protected void startWaiting(CanWait canWait) {
	      getWaitingPresenter().registerAsWaiting(canWait);
	      canWait.startWaiting();
	    }

	    protected void stopWaiting(CanWait canWait) {
	      canWait.stopWaiting();
	      getWaitingPresenter().unregisterAsWaiting(canWait);
	    }
    };
    execute(waitCallback);
	}
	
	public AnonymousAttendanceServiceAsync getAnonymousAttendanceService() {
		return i_anonymousAttendanceService;
	}


	public UserAttendanceServiceAsync getUserAttendanceService() {
		return i_userAttendanceService;
	}


	public AdminAttendanceServiceAsync getAdminAttendanceService() {
		return i_adminAttendanceService;
	}


	public LoginPresenterFactory getLoginPresenterFactory() {
		return i_loginPresenterFactory;
	}


  public WaitingPresenter getWaitingPresenter() {
    return i_waitingPresenter;
  }

  public GoogleAuthenticationPresenterFactory getGoogleAuthenticationPresenterFactory() {
    return i_googleAuthenticationPresenterFactory;
  }
}
