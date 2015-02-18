/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
