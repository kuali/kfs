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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.StateService;

/**
 * Campus Parameter Business Object. Maintenance document for campus parameters.
 */
public class CampusParameter extends PersistableBusinessObjectBase implements Inactivateable{

    private String campusCode;
    private String campusPurchasingDirectorName;
    private String campusPurchasingDirectorTitle;
    private String campusAccountsPayableEmailAddress;
    private String purchasingInstitutionName;
    private String purchasingDepartmentName;
    private String purchasingDepartmentLine1Address;
    private String purchasingDepartmentLine2Address;
    private String purchasingDepartmentCityName;
    private String purchasingDepartmentStateCode;
    private String purchasingDepartmentZipCode;
    private String purchasingDepartmentCountryCode;
    private boolean activeIndicator;
    private boolean active;

    private Campus campus;
    private State purchasingDepartmentState;
    private Country purchasingDepartmentCountry;

    /**
     * Default constructor.
     */
    public CampusParameter() {

    }

    public Campus getCampus() {
        return campus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, campus, "campus");
    }

    /**
     * @deprecated
     */
    public void setCampus(Campus campus) {
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

    public Country getPurchasingDepartmentCountry() {
        purchasingDepartmentCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, purchasingDepartmentCountryCode, purchasingDepartmentCountry);
        return purchasingDepartmentCountry;
    }

    /**
     * @deprecated
     */
    public void setPurchasingDepartmentCountry(Country purchasingDepartmentCountry) {
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

    public State getPurchasingDepartmentState() {
        purchasingDepartmentState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, purchasingDepartmentCountryCode, purchasingDepartmentStateCode, purchasingDepartmentState);
        return purchasingDepartmentState;
    }

    /**
     * @deprecated
     */
    public void setPurchasingDepartmentState(State purchasingDepartmentState) {
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

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        return m;
    }

    public boolean isActiveIndicator() {
        return activeIndicator;
    }

    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
