/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.OrganizationExtension;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeService;

/**
 * PreRules checks for the {@link Org} that needs to occur while still in the Struts processing. This includes defaults,
 * confirmations, etc.
 */
public class OrgPreRules extends MaintenancePreRulesBase {
    protected Organization newOrg;
    protected PostalCodeService postalZipCodeService = SpringContext.getBean(PostalCodeService.class);

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
    protected void checkForContinuationAccounts() {
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
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newOrg convenience objects, make sure all possible sub-objects are populated
        newOrg = (Organization) document.getNewMaintainableObject().getBusinessObject();
    }

    /**
     * Check if the HRMS data has changed on this document. If so, update the last update date.
     * 
     * @param oldData
     * @param newData
     */
    protected void updateHRMSUpdateDate(Organization oldData, Organization newData) {
        if (oldData != null) {
            OrganizationExtension oldExt = oldData.getOrganizationExtension();
            OrganizationExtension newExt = newData.getOrganizationExtension();
            if (oldExt != null) {
                if (!StringUtils.equals(oldExt.getHrmsCompany(), newExt.getHrmsCompany()) || !StringUtils.equals(oldExt.getHrmsIuOrganizationAddress2(), newExt.getHrmsIuOrganizationAddress2()) || !StringUtils.equals(oldExt.getHrmsIuOrganizationAddress3(), newExt.getHrmsIuOrganizationAddress3()) || !StringUtils.equals(oldExt.getHrmsIuCampusCode(), newExt.getHrmsIuCampusCode()) || !StringUtils.equals(oldExt.getHrmsIuCampusBuilding(), newExt.getHrmsIuCampusBuilding()) || !StringUtils.equals(oldExt.getHrmsIuCampusRoom(), newExt.getHrmsIuCampusRoom()) || oldExt.isHrmsIuPositionAllowedFlag() != newExt.isHrmsIuPositionAllowedFlag() || oldExt.isHrmsIuTenureAllowedFlag() != newExt.isHrmsIuTenureAllowedFlag() || oldExt.isHrmsIuTitleAllowedFlag() != newExt.isHrmsIuTitleAllowedFlag() || oldExt.isHrmsIuOccupationalUnitAllowedFlag() != newExt.isHrmsIuOccupationalUnitAllowedFlag()
                        || !StringUtils.equals(oldExt.getHrmsPersonnelApproverUniversalId(), newExt.getHrmsPersonnelApproverUniversalId()) || !StringUtils.equals(oldExt.getFiscalApproverUniversalId(), newExt.getFiscalApproverUniversalId())) {
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
    protected void setLocationFromZip(MaintenanceDocument maintenanceDocument) {

        // organizationStateCode , organizationCityName are populated by looking up
        // the zip code and getting the state and city from that
        if (StringUtils.isNotBlank(newOrg.getOrganizationZipCode()) && StringUtils.isNotBlank(newOrg.getOrganizationCountryCode())) {
            PostalCode zip = postalZipCodeService.getPostalCode(newOrg.getOrganizationCountryCode(), newOrg.getOrganizationZipCode());

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                newOrg.setOrganizationCityName(zip.getCityName());
                newOrg.setOrganizationStateCode(zip.getStateCode());
            }
        }
    }


}
