/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.io.Serializable;

/**
 * This holds information we need to start shopping with B2B.
 */
public class B2BInformation implements Serializable {

    private String punchoutURL;
    private String punchbackURL;
    private String environment;
    private String userAgent;
    private String identity;
    private String password;

    public String getPunchbackURL() {
        return punchbackURL;
    }

    public void setPunchbackURL(String punchbackURL) {
        this.punchbackURL = punchbackURL;
    }

    public String getPunchoutURL() {
        return punchoutURL;
    }

    public void setPunchoutURL(String punchoutURL) {
        this.punchoutURL = punchoutURL;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
        
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
