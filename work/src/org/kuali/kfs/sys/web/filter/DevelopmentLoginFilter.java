/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * A login filter which forwards to a login page that allows for the desired
 * authentication ID to be entered without the need for a password.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DevelopmentLoginFilter implements Filter {
	
	private String user;
	
	public void init(FilterConfig filterConfig) throws ServletException {
        user = filterConfig.getInitParameter("loginUser");
        if (user == null) {
            throw new ServletException("loginUser parameter is required");
        }
      }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest hsreq = (HttpServletRequest) request;
//			UserSession session = null;
//			if (isUserSessionEstablished(hsreq)) {
//				session = getUserSession(hsreq);	
//			}
//			if (session == null) {
				request = new HttpServletRequestWrapper(hsreq) {
					public String getRemoteUser() {
						return user;
					}
				};
//
//			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
	}
	public static UserSession getUserSession(HttpServletRequest request) {
		return (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
	}
	public static boolean isUserSessionEstablished(HttpServletRequest request) {
		return (request.getSession(false) != null && request.getSession(false).getAttribute(KRADConstants.USER_SESSION_KEY) != null);
	}
}
