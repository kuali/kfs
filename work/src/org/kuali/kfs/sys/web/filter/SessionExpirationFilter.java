/*
 * Copyright 2012 The Kuali Foundation.
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
