/*
 * Copyright 2006-2007 The Kuali Foundation.
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
  private String password;

  /**
   * @return Returns the environment.
   */
  public String getEnvironment() {
    return environment;
  }
  /**
   * @param environment
   *          The environment to set.
   */
  public void setEnvironment(String environment) {
    this.environment = environment;
  }
  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return password;
  }
  /**
   * @param password
   *          The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }
  /**
   * @return Returns the punchbackURL.
   */
  public String getPunchbackURL() {
    return punchbackURL;
  }
  /**
   * @param punchbackURL
   *          The punchbackURL to set.
   */
  public void setPunchbackURL(String punchbackURL) {
    this.punchbackURL = punchbackURL;
  }
  /**
   * @return Returns the punchoutURL.
   */
  public String getPunchoutURL() {
    return punchoutURL;
  }
  /**
   * @param punchoutURL
   *          The punchoutURL to set.
   */
  public void setPunchoutURL(String punchoutURL) {
    this.punchoutURL = punchoutURL;
  }
  /**
   * @return Returns the userAgent.
   */
  public String getUserAgent() {
    return userAgent;
  }
  /**
   * @param userAgent
   *          The userAgent to set.
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }
  /**
   *  
   */
  public B2BInformation() {
    super();
  }

}