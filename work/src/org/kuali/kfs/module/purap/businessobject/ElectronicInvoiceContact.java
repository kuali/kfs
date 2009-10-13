/*
 * Copyright 2006-2009 The Kuali Foundation
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
 * Created on Mar 7, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ElectronicInvoiceContact {
  
  private String role;
  private String addressID;
  private String name;
  private List postalAddresses = new ArrayList();
  private Map<String,String> emailAddresses = new HashMap<String,String>();
  private Map<String,String> phoneNumbers = new HashMap<String,String>();
  private Map<String,String> faxNumbers = new HashMap<String,String>();
  private List<String> webAddresses = new ArrayList<String>();

  /**
   * 
   */
  public ElectronicInvoiceContact() {
    super();
  }
  
  public void addPostalAddress(ElectronicInvoicePostalAddress cpa) {
    this.postalAddresses.add(cpa);
  }
  
  public void addEmailAddress(String name,String address) {
    this.emailAddresses.put(name, address);
  }
  
  public void addPhoneNumber(String name,
                             String countryCode,
                             String cityOrAreaCode,
                             String number) {
    this.phoneNumbers.put(name, countryCode + cityOrAreaCode + number);
  }
  
  public void addFaxNumber(String name,
                           String countryCode,
                           String cityOrAreaCode,
                           String number) {
     this.faxNumbers.put(name, countryCode + cityOrAreaCode + number);
  }
  
  public void addFaxNumber(String name,String number) {
    this.faxNumbers.put(name, number);
  }
  
  public void addWebAddress(String address) {
    this.webAddresses.add(address);
  }
  
  /**
   * @return Returns the addressID.
   */
  public String getAddressID() {
    return addressID;
  }
  /**
   * @param addressID The addressID to set.
   */
  public void setAddressID(String addressID) {
    this.addressID = addressID;
  }
  /**
   * @return Returns the emailAddresses.
   */
  public Map<String,String> getEmailAddresses() {
    return emailAddresses;
  }
  /**
   * @param emailAddresses The emailAddresses to set.
   */
  public void setEmailAddresses(Map emailAddresses) {
    this.emailAddresses = emailAddresses;
  }
  /**
   * @return Returns the faxNumbers.
   */
  public Map<String,String> getFaxNumbers() {
    return faxNumbers;
  }
  /**
   * @param faxNumbers The faxNumbers to set.
   */
  public void setFaxNumbers(Map faxNumbers) {
    this.faxNumbers = faxNumbers;
  }
  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }
  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return Returns the phoneNumbers.
   */
  public Map<String,String> getPhoneNumbers() {
    return phoneNumbers;
  }
  /**
   * @param phoneNumbers The phoneNumbers to set.
   */
  public void setPhoneNumbers(Map phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }
  /**
   * @return Returns the postalAddresses.
   */
  public List<ElectronicInvoicePostalAddress> getPostalAddresses() {
    return postalAddresses;
  }
  /**
   * @param postalAddresses The postalAddresses to set.
   */
  public void setPostalAddresses(List<ElectronicInvoicePostalAddress> postalAddresses) {
    this.postalAddresses = postalAddresses;
  }
  /**
   * @return Returns the role.
   */
  public String getRole() {
    return role;
  }
  /**
   * @param role The role to set.
   */
  public void setRole(String role) {
    this.role = role;
  }
  /**
   * @return Returns the webAddresses.
   */
  public List<String> getWebAddresses() {
    return webAddresses;
  }
  /**
   * @param webAddresses The webAddresses to set.
   */
  public void setWebAddresses(List webAddresses) {
    this.webAddresses = webAddresses;
  }
  
  public String toString(){
      
      ToStringBuilder toString = new ToStringBuilder(this);
      toString.append("Role",getRole());
      toString.append("Name",getName());
      toString.append("AddressId",getAddressID());
      toString.append("PostalAddress",getPostalAddresses());
      toString.append("EmailAddresses",getEmailAddresses());
      toString.append("phoneNumbers",getPhoneNumbers());
      toString.append("FaxNumbers",getFaxNumbers());
      toString.append("URLs",getWebAddresses());
      
      
      return toString.toString();
  }
}


