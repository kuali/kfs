package edu.arizona.kfs.tax.businessobject;

/*
 * Copyright 2008 The Kuali Foundation.
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

import java.io.Serializable;
import java.util.LinkedHashMap;

//import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

/**
 * Payer 
 * 
 * @author Derek Helbert
 * @version $Revision$
 */
public class Payer extends PersistableBusinessObjectExtensionBase implements Serializable {

    private Integer id; // PAYER_ID

    private String transCd; // TRANS_CD

    private String nameControl; // NAME_CONTROL
    
    private String testFlg; // TEST_FLG

    private String replaceAlpha; // REPLACE_ALPHA

    private String tinType; // TIN_TYPE
 
    private String tin; // TIN

    private String name1; // NAME1

    private String name2; // NAME2

    private String contact; // CONTACT

    private String phoneNumber; // PHONE_NUM

    private String ext; // EXT

    private String companyName1; // COMPANY_NAME1

    private String companyName2; // COMPANY_NAME2

    private String address; // ADDRESS
    
    private String city; // CITY
    
    private String state; // STATE
    
    private String email; // EMAIL
    
    private String zipCode; // ZIP_CODE
    
    private String countryCode;  // CNTRY_CD
    
    /**
     * Constructs a business object.
     */
    public Payer() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getTransCd() {
        return transCd;
    }

    public void setTransCd(String transCd) {
        this.transCd = transCd;
    }

    public String getNameControl() {
        return nameControl;
    }

    public void setNameControl(String nameControl) {
        this.nameControl = nameControl;
    }

    public String getTestFlg() {
        return testFlg;
    }

    public void setTestFlg(String testFlg) {
        this.testFlg = testFlg;
    }

    public String getReplaceAlpha() {
        return replaceAlpha;
    }

    public void setReplaceAlpha(String replaceAlpha) {
        this.replaceAlpha = replaceAlpha;
    }

    public String getTinType() {
        return tinType;
    }

    public void setTinType(String tinType) {
        this.tinType = tinType;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getCompanyName1() {
        return companyName1;
    }

    public void setCompanyName1(String companyName1) {
        this.companyName1 = companyName1;
    }

    public String getCompanyName2() {
        return companyName2;
    }

    public void setCompanyName2(String companyName2) {
        this.companyName2 = companyName2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("transCd", getTransCd());
        m.put("nameControl", getNameControl());
        m.put("name1", getName1());
        m.put("name2", getName2());
        m.put("tin", getTin());
        
        return m;
    }
}