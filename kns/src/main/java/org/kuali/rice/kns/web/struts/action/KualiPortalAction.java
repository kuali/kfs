/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the action for the portal.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KualiPortalAction extends KualiSimpleAction {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String gotoUrl = null;
        String selectedTab = null;

        if (request.getQueryString() != null && request.getQueryString().indexOf("channelUrl") >= 0) {
            gotoUrl = request.getQueryString().substring(request.getQueryString().indexOf("channelUrl") + 11, request.getQueryString().length());
        } else if (request.getParameter("channelUrl") != null && request.getParameter("channelUrl").length() > 0) {
            gotoUrl = request.getParameter("channelUrl");
        }

        if (gotoUrl != null) {
            // encode some characters for security purposes if present in url
            gotoUrl = gotoUrl.replace(">", "%3E");
            gotoUrl = gotoUrl.replace("<", "%3C");
            gotoUrl = gotoUrl.replace("\"", "%22");

            // check url allowed to display in portal
            Pattern pattern = Pattern.compile(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.PORTAL_ALLOWED_REGEX));
            Matcher matcher = pattern.matcher(gotoUrl);
            if(!matcher.matches()) {
                throw new Exception("The requested channel URL is not authorized for display in portal.");
            }
        }

        if (request.getParameter("selectedTab") != null && request.getParameter("selectedTab").length() > 0) {
            request.getSession().setAttribute("selectedTab", request.getParameter("selectedTab"));
        }

        request.setAttribute("gotoUrl", gotoUrl);

        return super.execute(mapping, form, request, response);
    }
}
