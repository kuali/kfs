/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.OrganizationExtension;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class OrgPreRules extends MaintenancePreRulesBase {
    private Org newAccount;
    private Org copyAccount;


    public OrgPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        updateHRMSUpdateDate((Org) document.getOldMaintainableObject().getBusinessObject(), (Org) document.getNewMaintainableObject().getBusinessObject());

        return true;
    }

    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newAccount.getOrganizationDefaultAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newAccount.getChartOfAccountsCode(), newAccount.getOrganizationDefaultAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setOrganizationDefaultAccountNumber(account.getAccountNumber());
                newAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Org) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (Org) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
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
                UniversalUser oldFAUser = oldExt.getFiscalApproverUniversal();
                UniversalUser newFAUser = newExt.getFiscalApproverUniversal();
                UniversalUser oldPAUser = oldExt.getHrmsPersonnelApproverUniversal();
                UniversalUser newPAUser = newExt.getHrmsPersonnelApproverUniversal();
                if (!ObjectUtils.nullSafeEquals(oldExt.getHrmsCompany(), newExt.getHrmsCompany()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuOrganizationAddress2(), newExt.getHrmsIuOrganizationAddress2()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuOrganizationAddress3(), newExt.getHrmsIuOrganizationAddress3()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusCode(), newExt.getHrmsIuCampusCode()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusBuilding(), newExt.getHrmsIuCampusBuilding()) || !ObjectUtils.nullSafeEquals(oldExt.getHrmsIuCampusRoom(), newExt.getHrmsIuCampusRoom()) || oldExt.isHrmsIuPositionAllowedFlag() != newExt.isHrmsIuPositionAllowedFlag() || oldExt.isHrmsIuTenureAllowedFlag() != newExt.isHrmsIuTenureAllowedFlag() || oldExt.isHrmsIuTitleAllowedFlag() != newExt.isHrmsIuTitleAllowedFlag() || oldExt.isHrmsIuOccupationalUnitAllowedFlag() != newExt.isHrmsIuOccupationalUnitAllowedFlag()
                        || !ObjectUtils.nullSafeEquals(oldExt.getHrmsPersonnelApproverUniversalId(), newExt.getHrmsPersonnelApproverUniversalId()) || !ObjectUtils.nullSafeEquals(oldExt.getFiscalApproverUniversalId(), newExt.getFiscalApproverUniversalId()) || !ObjectUtils.nullSafeEquals(oldFAUser.getPersonUserIdentifier(), newFAUser.getPersonUserIdentifier()) || !ObjectUtils.nullSafeEquals(oldPAUser.getPersonUserIdentifier(), newPAUser.getPersonUserIdentifier())) {
                    newExt.setHrmsLastUpdateDate(new Timestamp(new Date().getTime()));
                }
            }
            else {
                newExt.setHrmsLastUpdateDate(new Timestamp(new Date().getTime()));
            }
        }
        else {
            newData.getOrganizationExtension().setHrmsLastUpdateDate(new Timestamp(new Date().getTime()));
        }
    }

}
