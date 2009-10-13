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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.OrganizationExtension;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * PreRules checks for the {@link Org} that needs to occur while still in the Struts processing. This includes defaults,
 * confirmations, etc.
 */
public class OrgPreRules extends MaintenancePreRulesBase {
    private Organization newOrg;
    private PostalCodeService postalZipCodeService = SpringContext.getBean(PostalCodeService.class);

    public OrgPreRules() {

    }

    /**
     * This checks to see if a continuation account is necessary and if the HRMS data has changed
     * 
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        updateHRMSUpdateDate((Organization) document.getOldMaintainableObject().getBusinessObject(), (Organization) document.getNewMaintainableObject().getBusinessObject());

        return true;
    }

    /**
     * This looks for the org default account number and then sets the values to the continuation account value if it exists
     */
    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newOrg.getOrganizationDefaultAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newOrg.getChartOfAccountsCode(), newOrg.getOrganizationDefaultAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newOrg.setOrganizationDefaultAccountNumber(account.getAccountNumber());
                newOrg.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * This method sets the convenience objects like newOrg and copyOrg, so you have short and easy handles to the new and old
     * objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load all
     * sub-objects from the DB by their primary keys, if available.
     * 
     * @param document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newOrg convenience objects, make sure all possible sub-objects are populated
        newOrg = (Organization) document.getNewMaintainableObject().getBusinessObject();
    }

    /**
     * Check if the HRMS data has changed on this document. If so, update the last update date.
     * 
     * @param oldData
     * @param newData
     */
    private void updateHRMSUpdateDate(Organization oldData, Organization newData) {
        if (oldData != null) {
            OrganizationExtension oldExt = oldData.getOrganizationExtension();
            OrganizationExtension newExt = newData.getOrganizationExtension();
            if (oldExt != null) {
                if (!ObjectUtils.nullSafeEquals(oldExt.getHrmsCompany(), newExt.getHrmsCompany()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuOrganizationAddress2(), newExt.getHrmsIuOrganizationAddress2()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuOrganizationAddress3(), newExt.getHrmsIuOrganizationAddress3()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusCode(), newExt.getHrmsIuCampusCode()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusBuilding(), newExt.getHrmsIuCampusBuilding()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusRoom(), newExt.getHrmsIuCampusRoom()) || oldExt.isHrmsIuPositionAllowedFlag() != newExt.isHrmsIuPositionAllowedFlag() || oldExt.isHrmsIuTenureAllowedFlag() != newExt.isHrmsIuTenureAllowedFlag() || oldExt.isHrmsIuTitleAllowedFlag() != newExt.isHrmsIuTitleAllowedFlag() || oldExt.isHrmsIuOccupationalUnitAllowedFlag() != newExt.isHrmsIuOccupationalUnitAllowedFlag()
                        || !ObjectUtils.nullSafeEquals(oldExt.getHrmsPersonnelApproverUniversalId(), newExt.getHrmsPersonnelApproverUniversalId()) || !ObjectUtils.nullSafeEquals(oldExt.getFiscalApproverUniversalId(), newExt.getFiscalApproverUniversalId())) {
                    newExt.setHrmsLastUpdateDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
                }
            }
            else {
                newExt.setHrmsLastUpdateDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            }
        }
        else {
            newData.getOrganizationExtension().setHrmsLastUpdateDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        }
    }

    /**
     * This takes the org zip code and fills in state, city and country code based off of it
     * 
     * @param maintenanceDocument
     */
    private void setLocationFromZip(MaintenanceDocument maintenanceDocument) {

        // organizationStateCode , organizationCityName are populated by looking up
        // the zip code and getting the state and city from that
        if (!StringUtils.isBlank(newOrg.getOrganizationZipCode()) && !StringUtils.isBlank(newOrg.getOrganizationCountryCode())) {
            PostalCode zip = postalZipCodeService.getByPrimaryId(newOrg.getOrganizationCountryCode(), newOrg.getOrganizationZipCode());

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                newOrg.setOrganizationCityName(zip.getPostalCityName());
                newOrg.setOrganizationStateCode(zip.getPostalStateCode());
            }
        }
    }


}
