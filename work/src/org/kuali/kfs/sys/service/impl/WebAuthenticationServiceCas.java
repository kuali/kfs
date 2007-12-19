/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.service.WebAuthenticationService;
import org.kuali.core.web.filter.KualiCasFilter;

import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * This class is used to perform the webAuthentication Service when using the Kuali Cas Filter.
 */
public class WebAuthenticationServiceCas implements WebAuthenticationService {
    private boolean validatePassword = true;

    /**
     * @see org.kuali.core.service.WebAuthenticationService#getNetworkId(javax.servlet.http.HttpServletRequest)
     */
    public String getNetworkId(HttpServletRequest request) {
        return KualiCasFilter.getRemoteUser(request);
    }

    /**
     * @see edu.iu.uis.eden.web.WebAuthenticationService#updateUserSession(edu.iu.uis.eden.web.session.UserSession,
     *      javax.servlet.http.HttpServletRequest)
     */
    public UserSession updateUserSession(UserSession userSession, HttpServletRequest request) {
        return userSession;
    }

    /**
     * @see org.kuali.core.service.WebAuthenticationService#isValidatePassword()
     */
    public boolean isValidatePassword() {
        return validatePassword;
    }    

    /**
     * @see edu.iu.uis.eden.web.WebAuthenticationService#establishInitialUserSession(edu.iu.uis.eden.web.session.UserSession, javax.servlet.http.HttpServletRequest)
     */
    public UserSession establishInitialUserSession(UserSession userSession, HttpServletRequest request) {
        return userSession;
    }

    /**
     * @see edu.iu.uis.eden.web.WebAuthenticationService#getUserId(javax.servlet.http.HttpServletRequest)
     */
    public UserId getUserId(HttpServletRequest request) {
        return new AuthenticationUserId(getNetworkId(request));
    }

    public void setValidatePassword(boolean validatePassword) {
        this.validatePassword = validatePassword;
    }
}
