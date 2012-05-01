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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
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
        if ( StringUtils.isBlank(campusCode) ) {
            campus = null;
        } else {
            if ( campus == null || !StringUtils.equals( campus.getCode(),campusCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return campus;
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
        if ( StringUtils.isBlank(purchasingDepartmentCountryCode) ) {
            purchasingDepartmentCountry = null;
        } else {
            if ( purchasingDepartmentCountry == null || !StringUtils.equals( purchasingDepartmentCountry.getCode(),purchasingDepartmentCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, purchasingDepartmentCountryCode);
                    purchasingDepartmentCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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
        if ( StringUtils.isBlank(purchasingDepartmentStateCode) || StringUtils.isBlank(purchasingDepartmentCountryCode ) ) {
            purchasingDepartmentState = null;
        } else {
            if ( purchasingDepartmentState == null || !StringUtils.equals( purchasingDepartmentState.getCode(),purchasingDepartmentStateCode) || !StringUtils.equals(purchasingDepartmentState.getCountryCode(), purchasingDepartmentCountryCode ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, purchasingDepartmentCountryCode);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, purchasingDepartmentStateCode);
                    purchasingDepartmentState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
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

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
