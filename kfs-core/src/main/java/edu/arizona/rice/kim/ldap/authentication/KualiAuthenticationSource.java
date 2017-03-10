/*
 * Copyright 2014 The Kuali Foundation
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

package edu.arizona.rice.kim.ldap.authentication;

import org.springframework.security.ldap.authentication.SpringSecurityAuthenticationSource;


public class KualiAuthenticationSource extends SpringSecurityAuthenticationSource {
    private String credentials;
    private String principal;
    
    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
