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

package org.kuali.kfs.vnd.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * Campus Parameter Business Object. Maintenance document for campus parameters.
 */
public class CampusParameter extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String campusCode;
    protected String campusPurchasingDirectorName;
    protected String campusPurchasingDirectorTitle;
    protected String campusAccountsPayableEmailAddress;
    protected String purchasingInstitutionName;
    protected String purchasingDepartmentName;
    protected String purchasingDepartmentLine1Address;
    protected String purchasingDepartmentLine2Address;
    protected String purchasingDepartmentCityName;
    protected String purchasingDepartmentStateCode;
    protected String purchasingDepartmentZipCode;
    protected String purchasingDepartmentCountryCode;
    protected boolean active;

    protected CampusEbo campus;
    protected StateEbo purchasingDepartmentState;
    protected CountryEbo purchasingDepartmentCountry;

    public CampusEbo getCampus() {
        return campus = StringUtils.isBlank( campusCode)?null:((campus!=null && campus.getCode().equals( campusCode))?campus:CampusEbo.from(SpringContext.getBean(CampusService.class).getCampus( campusCode)));
    }

    @Deprecated
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    public String getCampusAccountsPayableEmailAddress() {
        return campusAccountsPayableEmailAddress;
    }

    public void setCampusAccountsPayableEmailAddress(String campusAccountsPayableEmailAddress) {
        this.campusAccountsPayableEmailAddress = campusAccountsPayableEmailAddress;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getCampusPurchasingDirectorName() {
        return campusPurchasingDirectorName;
    }

    public void setCampusPurchasingDirectorName(String campusPurchasingDirectorName) {
        this.campusPurchasingDirectorName = campusPurchasingDirectorName;
    }

    public String getCampusPurchasingDirectorTitle() {
        return campusPurchasingDirectorTitle;
    }

    public void setCampusPurchasingDirectorTitle(String campusPurchasingDirectorTitle) {
        this.campusPurchasingDirectorTitle = campusPurchasingDirectorTitle;
    }

    public String getPurchasingDepartmentCityName() {
        return purchasingDepartmentCityName;
    }

    public void setPurchasingDepartmentCityName(String purchasingDepartmentCityName) {
        this.purchasingDepartmentCityName = purchasingDepartmentCityName;
    }

    public CountryEbo getPurchasingDepartmentCountry() {
        purchasingDepartmentCountry = (purchasingDepartmentCountryCode == null)?null:( purchasingDepartmentCountry == null || !StringUtils.equals( purchasingDepartmentCountry.getCode(),purchasingDepartmentCountryCode))?CountryEbo.from(SpringContext.getBean(CountryService.class).getCountry(purchasingDepartmentCountryCode)): purchasingDepartmentCountry;
        return purchasingDepartmentCountry;
    }

    @Deprecated
    public void setPurchasingDepartmentCountry(CountryEbo purchasingDepartmentCountry) {
        this.purchasingDepartmentCountry = purchasingDepartmentCountry;
    }

    public String getPurchasingDepartmentCountryCode() {
        return purchasingDepartmentCountryCode;
    }

    public void setPurchasingDepartmentCountryCode(String purchasingDepartmentCountryCode) {
        this.purchasingDepartmentCountryCode = purchasingDepartmentCountryCode;
    }

    public String getPurchasingDepartmentLine1Address() {
        return purchasingDepartmentLine1Address;
    }

    public void setPurchasingDepartmentLine1Address(String purchasingDepartmentLine1Address) {
        this.purchasingDepartmentLine1Address = purchasingDepartmentLine1Address;
    }

    public String getPurchasingDepartmentLine2Address() {
        return purchasingDepartmentLine2Address;
    }

    public void setPurchasingDepartmentLine2Address(String purchasingDepartmentLine2Address) {
        this.purchasingDepartmentLine2Address = purchasingDepartmentLine2Address;
    }

    public String getPurchasingDepartmentName() {
        return purchasingDepartmentName;
    }

    public void setPurchasingDepartmentName(String purchasingDepartmentName) {
        this.purchasingDepartmentName = purchasingDepartmentName;
    }

    public StateEbo getPurchasingDepartmentState() {
        purchasingDepartmentState = (StringUtils.isBlank(purchasingDepartmentCountryCode) || StringUtils.isBlank( purchasingDepartmentStateCode))?null:( purchasingDepartmentState == null || !StringUtils.equals( purchasingDepartmentState.getCountryCode(),purchasingDepartmentCountryCode)|| !StringUtils.equals( purchasingDepartmentState.getCode(), purchasingDepartmentStateCode))?StateEbo.from(SpringContext.getBean(StateService.class).getState(purchasingDepartmentCountryCode, purchasingDepartmentStateCode)): purchasingDepartmentState;
        return purchasingDepartmentState;
    }

    /**
     * @deprecated
     */
    public void setPurchasingDepartmentState(StateEbo purchasingDepartmentState) {
        this.purchasingDepartmentState = purchasingDepartmentState;
    }

    public String getPurchasingDepartmentStateCode() {
        return purchasingDepartmentStateCode;
    }

    public void setPurchasingDepartmentStateCode(String purchasingDepartmentStateCode) {
        this.purchasingDepartmentStateCode = purchasingDepartmentStateCode;
    }

    public String getPurchasingDepartmentZipCode() {
        return purchasingDepartmentZipCode;
    }

    public void setPurchasingDepartmentZipCode(String purchasingDepartmentZipCode) {
        this.purchasingDepartmentZipCode = purchasingDepartmentZipCode;
    }

    public String getPurchasingInstitutionName() {
        return purchasingInstitutionName;
    }

    public void setPurchasingInstitutionName(String purchasingInstitutionName) {
        this.purchasingInstitutionName = purchasingInstitutionName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
