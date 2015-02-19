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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * A filter that applies the configuration controlled redirect for a session timeout
 */
public class SessionExpirationFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        applyRedirectHeader(request, response);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    protected void applyRedirectHeader(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest)request;
        // KFSCNTRB-1721- Don't set HTTP refresh header on portal, only on the internal iframe.
        String queryString = req.getQueryString();
        if (!StringUtils.contains(queryString,"channelUrl") &&
                !StringUtils.contains(req.getRequestURI(), "/SessionInvalidateAction")){
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            String sessionTimeout = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("http.session.timeout.minutes");
            if(StringUtils.isNotBlank(sessionTimeout)){
                Integer timeout = Integer.parseInt(sessionTimeout);
                if(timeout != null && timeout > 0){
                    //convert from minutes to seconds
                    timeout *= 60;
                    String url = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kfs.url");
                    httpResponse.setHeader("Refresh", timeout - 3 + ";URL="+ url +  "/SessionInvalidateAction.do");
                }
            }
        }
    }

}
