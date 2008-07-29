/*
 * Created on Mar 7, 2006
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceContact {
  
  private String role;
  private String addressID;
  private String name;
  private List postalAddresses = new ArrayList();
  private Map emailAddresses = new HashMap();
  private Map phoneNumbers = new HashMap();
  private Map faxNumbers = new HashMap();
  private List webAddresses = new ArrayList();

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
  
  public void addPhoneNumber(String name,String number) {
    this.phoneNumbers.put(name, number);
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
  public Map getEmailAddresses() {
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
  public Map getFaxNumbers() {
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
  public Map getPhoneNumbers() {
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
  public List getPostalAddresses() {
    return postalAddresses;
  }
  /**
   * @param postalAddresses The postalAddresses to set.
   */
  public void setPostalAddresses(List postalAddresses) {
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
  public List getWebAddresses() {
    return webAddresses;
  }
  /**
   * @param webAddresses The webAddresses to set.
   */
  public void setWebAddresses(List webAddresses) {
    this.webAddresses = webAddresses;
  }
}


