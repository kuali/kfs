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
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ElectronicInvoicePostalAddress {
  // no deliverTo attributes currently
  private String type;
  private String line1 = "";
  private String line2 = "";
  private String line3 = "";
  private String cityName;
  private String stateCode;
  private String postalCode;
  private String countryCode;
  private String countryName;
  
  private List names = new ArrayList();
  
  private List<String> street = new ArrayList<String>();
  
  public ElectronicInvoicePostalAddress() {
    super();
  }
  
  public void addName(String name) {
    this.names.add(name);
  }
  
  /**
   * @return first name found in names list
   */
  public String getName() {
    if (names.isEmpty()) {
      return "";
    } else {
      return (String)names.get(0);
    }
  }
  /**
   * @return Returns the cityName.
   */
  public String getCityName() {
    return cityName;
  }
  /**
   * @param cityName The cityName to set.
   */
  public void setCityName(String cityName) {
    this.cityName = cityName;
  }
  /**
   * @return Returns the countryCode.
   */
  public String getCountryCode() {
    return countryCode;
  }
  /**
   * @param countryCode The countryCode to set.
   */
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }
  /**
   * @return Returns the countryName.
   */
  public String getCountryName() {
    return countryName;
  }
  /**
   * @param countryName The countryName to set.
   */
  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }
  /**
   * @return Returns the line1.
   */
  public String getLine1() {
//    return line1;
      if (street.size() > 0){
          return street.get(0);
      }else{
          return null;
      }
  }
  /**
   * @param line1 The line1 to set.
   */
  public void setLine1(String line1) {
    this.line1 = line1;
  }
  /**
   * @return Returns the line2.
   */
  public String getLine2() {
//    return line2;
      if (street.size() > 1){
          return street.get(1);
      }else{
          return null;
      }
  }
  /**
   * @param line2 The line2 to set.
   */
  public void setLine2(String line2) {
    this.line2 = line2;
  }
  /**
   * @return Returns the line3.
   */
  public String getLine3() {
//    return line3;
      if (street.size() > 2){
          return street.get(2);
      }else{
          return null;
      }
  }
  /**
   * @param line3 The line3 to set.
   */
  public void setLine3(String line3) {
    this.line3 = line3;
  }
  /**
   * @return Returns the names.
   */
  public List getNames() {
    return names;
  }
  /**
   * @param names The names to set.
   */
  public void setNames(List names) {
    this.names = names;
  }
  /**
   * @return Returns the postalCode.
   */
  public String getPostalCode() {
    return postalCode;
  }
  /**
   * @param postalCode The postalCode to set.
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }
  /**
   * @return Returns the stateCode.
   */
  public String getStateCode() {
    return stateCode;
  }
  /**
   * @param stateCode The stateCode to set.
   */
  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }
  /**
   * @return Returns the type.
   */
  public String getType() {
    return type;
  }
  /**
   * @param type The type to set.
   */
  public void setType(String type) {
    this.type = type;
  }
  
  public void addStreet(String street){
      this.street.add(street);
  }
  
  public String toString(){
      
      ToStringBuilder toString = new ToStringBuilder(this);
      
      toString.append("type",getType());
      toString.append("line1",getLine1());
      toString.append("line2",getLine2());
      toString.append("line3",getLine3());
      toString.append("cityName",getCityName());
      toString.append("stateCode",getStateCode());
      toString.append("postalCode",getPostalCode());
      toString.append("countryCode",getCountryCode());
      toString.append("countryName",getCountryName());
      toString.append("Names(DeliverTo)",getNames());
      
      return toString.toString();
  }
}
