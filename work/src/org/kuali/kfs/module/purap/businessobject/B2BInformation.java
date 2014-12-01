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
