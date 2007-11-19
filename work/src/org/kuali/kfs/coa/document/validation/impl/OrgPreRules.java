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
package org.kuali.module.chart.rules;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.PostalZipCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.OrganizationExtension;

/**
 * PreRules checks for the {@link Org} that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class OrgPreRules extends MaintenancePreRulesBase {
    private Org newOrg;
    private Org copyOrg;


    public OrgPreRules() {

    }

    /**
     * This checks to see if a continuation account is necessary and if the HRMS data has changed
     * 
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        updateHRMSUpdateDate((Org) document.getOldMaintainableObject().getBusinessObject(), (Org) document.getNewMaintainableObject().getBusinessObject());

        return true;
    }

    /**
     * 
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
     * 
     * This method sets the convenience objects like newOrg and copyOrg, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @param document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newOrg convenience objects, make sure all possible sub-objects are populated
        newOrg = (Org) document.getNewMaintainableObject().getBusinessObject();
        copyOrg = (Org) ObjectUtils.deepCopy(newOrg);
        copyOrg.refresh();
    }

    /**
     * Check if the HRMS data has changed on this document. If so, update the last update date.
     * 
     * @param oldData
     * @param newData
     */
    private void updateHRMSUpdateDate(Org oldData, Org newData) {
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
     * 
     * This takes the org zip code and fills in state, city and country code based off of it
     * @param maintenanceDocument
     */
    private void setLocationFromZip(MaintenanceDocument maintenanceDocument) {

        // organizationStateCode , organizationCityName are populated by looking up
        // the zip code and getting the state and city from that
        if (!StringUtils.isBlank(copyOrg.getOrganizationZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", copyOrg.getOrganizationZipCode());
            PostalZipCode zip = (PostalZipCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PostalZipCode.class, primaryKeys);

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                newOrg.setOrganizationCityName(zip.getPostalCityName());
                newOrg.setOrganizationStateCode(zip.getPostalStateCode());
                newOrg.setOrganizationCountryCode("US");// no way to look up
            }
        }
    }


}
