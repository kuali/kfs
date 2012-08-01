/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.location.impl.campus.CampusBo;

/**
 * 
 */
public class AccountDescription extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6233459415790165510L;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String campusDescription;
    private String organizationDescription;
    private String responsibilityCenterDescription;
    private String campusCode;
    private String buildingCode;

    private CampusBo campus;
    

    private Building building;

    /**
     * Default constructor.
     */
    public AccountDescription() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the campusDescription attribute.
     * 
     * @return Returns the campusDescription
     */
    public String getCampusDescription() {
        return campusDescription;
    }

    /**
     * Sets the campusDescription attribute.
     * 
     * @param campusDescription The campusDescription to set.
     */
    public void setCampusDescription(String campusDescription) {
        this.campusDescription = campusDescription;
    }


    /**
     * Gets the organizationDescription attribute.
     * 
     * @return Returns the organizationDescription
     */
    public String getOrganizationDescription() {
        return organizationDescription;
    }

    /**
     * Sets the organizationDescription attribute.
     * 
     * @param organizationDescription The organizationDescription to set.
     */
    public void setOrganizationDescription(String organizationDescription) {
        this.organizationDescription = organizationDescription;
    }


    /**
     * Gets the responsibilityCenterDescription attribute.
     * 
     * @return Returns the responsibilityCenterDescription
     */
    public String getResponsibilityCenterDescription() {
        return responsibilityCenterDescription;
    }

    /**
     * Sets the responsibilityCenterDescription attribute.
     * 
     * @param responsibilityCenterDescription The responsibilityCenterDescription to set.
     */
    public void setResponsibilityCenterDescription(String responsibilityCenterDescription) {
        this.responsibilityCenterDescription = responsibilityCenterDescription;
    }


    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    /**
     * Gets the building attribute.
     * 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * 
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus
     */
    public CampusBo getCampus() {
        return campus;
    }

    /** 
     * Sets the campus attribute.
     * 
     * @param campus The campus to set.
     */
    public void setCampus(CampusBo campus) {
        this.campus = campus;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        return m;
    }
}
