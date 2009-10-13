/*
 * Copyright 2005-2009 The Kuali Foundation
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
/*
 * Created on Mar 10, 2005
 *
 */
package org.kuali.kfs.module.purap.util.cxml;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CxmlHeader {
  private String fromIdentity;
  private String fromDomain;
  private String fromType;
  private String toIdentity;
  private String toDomain;
  private String toType;
  private String senderIdentity;
  private String senderDomain;
  private String senderType;
  private String senderUserAgent;
  
  /**
   * Newly Added
   */
  private String fromSharedSecret;
  private String toSharedSecret;
  private String senderSharedSecret;

  public CxmlHeader() {
    super();
  }

  public void setFrom(String domain,String identity) {
    this.setFrom(domain,identity,null);
  }

  public void setFrom(String domain,String identity,String type) {
    this.fromDomain = domain;
    this.fromIdentity = identity;
    this.fromType = type;
  }

  public void setTo(String domain,String identity) {
    this.setTo(domain,identity,null);
  }

  public void setTo(String domain,String identity,String type) {
    this.toDomain = domain;
    this.toIdentity = identity;
    this.toType = type;
  }

  public void setSender(String domain,String identity) {
    this.setSender(domain,identity,null);
  }

  public void setSender(String domain,String identity,String type) {
    this.senderDomain = domain;
    this.senderIdentity = identity;
    this.senderType = type;
  }

  /**
   * @return Returns the fromDomain.
   */
  public String getFromDomain() {
    return fromDomain;
  }
  /**
   * @param fromDomain The fromDomain to set.
   */
  public void setFromDomain(String fromDomain) {
    this.fromDomain = fromDomain;
  }
  /**
   * @return Returns the fromIdentity.
   */
  public String getFromIdentity() {
    return fromIdentity;
  }
  /**
   * @param fromIdentity The fromIdentity to set.
   */
  public void setFromIdentity(String fromIdentity) {
    this.fromIdentity = fromIdentity;
  }
  /**
   * @return Returns the fromType.
   */
  public String getFromType() {
    return fromType;
  }
  /**
   * @param fromType The fromType to set.
   */
  public void setFromType(String fromType) {
    this.fromType = fromType;
  }
  /**
   * @return Returns the senderDomain.
   */
  public String getSenderDomain() {
    return senderDomain;
  }
  /**
   * @param senderDomain The senderDomain to set.
   */
  public void setSenderDomain(String senderDomain) {
    this.senderDomain = senderDomain;
  }
  /**
   * @return Returns the senderIdentity.
   */
  public String getSenderIdentity() {
    return senderIdentity;
  }
  /**
   * @param senderIdentity The senderIdentity to set.
   */
  public void setSenderIdentity(String senderIdentity) {
    this.senderIdentity = senderIdentity;
  }
  /**
   * @return Returns the senderType.
   */
  public String getSenderType() {
    return senderType;
  }
  /**
   * @param senderType The senderType to set.
   */
  public void setSenderType(String senderType) {
    this.senderType = senderType;
  }
  /**
   * @return Returns the senderUserAgent.
   */
  public String getSenderUserAgent() {
    return senderUserAgent;
  }
  /**
   * @param senderUserAgent The senderUserAgent to set.
   */
  public void setSenderUserAgent(String senderUserAgent) {
    this.senderUserAgent = senderUserAgent;
  }
  /**
   * @return Returns the toDomain.
   */
  public String getToDomain() {
    return toDomain;
  }
  /**
   * @param toDomain The toDomain to set.
   */
  public void setToDomain(String toDomain) {
    this.toDomain = toDomain;
  }
  /**
   * @return Returns the toIdentity.
   */
  public String getToIdentity() {
    return toIdentity;
  }
  /**
   * @param toIdentity The toIdentity to set.
   */
  public void setToIdentity(String toIdentity) {
    this.toIdentity = toIdentity;
  }
  /**
   * @return Returns the toType.
   */
  public String getToType() {
    return toType;
  }
  /**
   * @param toType The toType to set.
   */
  public void setToType(String toType) {
    this.toType = toType;
  }
  
  public String getSenderSharedSecret() {
      return senderSharedSecret;
  }

  public void setSenderSharedSecret(String senderSharedSecret) {
      this.senderSharedSecret = senderSharedSecret;
  }
  
  public String getFromSharedSecret() {
      return fromSharedSecret;
  }

  public void setFromSharedSecret(String fromSharedSecret) {
      this.fromSharedSecret = fromSharedSecret;
  }

  public String getToSharedSecret() {
      return toSharedSecret;
  }

  public void setToSharedSecret(String toSharedSecret) {
      this.toSharedSecret = toSharedSecret;
  }
  
  public String toString(){
      
      ToStringBuilder toString = new ToStringBuilder(this);
      
      toString.append("FromDomain",getFromDomain());
      toString.append("FromIdentity",getFromIdentity());
      toString.append("FromSharedSecret",getFromSharedSecret());
      toString.append("FromType",getFromType());
      
      toString.append("ToDomain",getToDomain());
      toString.append("ToIdentity",getToIdentity());
      toString.append("ToSharedSecret",getToSharedSecret());
      toString.append("ToType",getToType());
      
      toString.append("SenderDomain",getSenderDomain());
      toString.append("SenderIdentity",getSenderIdentity());
      toString.append("SenderType",getSenderType());
      toString.append("SenderSharedSecret",getSenderSharedSecret());
      toString.append("SenderUserAgent",getSenderUserAgent());
      
      return toString.toString();
  }



}
