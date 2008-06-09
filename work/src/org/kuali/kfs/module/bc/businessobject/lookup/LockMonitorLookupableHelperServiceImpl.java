/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.lookupable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionLockSummary;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.service.LockService;

/**
 * Implements custom search routine to find the current budget locks and build up the result List. Set an unlock URL for each lock.
 */
public class LockMonitorLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List<BudgetConstructionLockSummary> results = new ArrayList<BudgetConstructionLockSummary>();
        String lockUserID = fieldValues.get(BCPropertyConstants.LOCK_USER_ID);

        getAccountLocks(results, lockUserID);
        getTransactionLocks(results, lockUserID);
        getOrphanFundingLocks(results, lockUserID);
        getPositionFundingLocks(results, lockUserID);
        getOrphanPositionLocks(results, lockUserID);


        return new CollectionIncomplete(results, new Long(0));
    }

    /**
     * Calls lock service to retrieve all current account locks and builds a lock summary object for each returned lock.
     * 
     * @param results - result list to add lock summaries
     */
    protected void getAccountLocks(List<BudgetConstructionLockSummary> results, String lockUserId) {
        List<BudgetConstructionHeader> accountLocks = SpringContext.getBean(LockService.class).getAllAccountLocks(lockUserId);
        for (BudgetConstructionHeader header : accountLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.ACCOUNT_LOCK);
            lockSummary.setLockUserId(header.getBudgetLockUserIdentifier());

            lockSummary.setDocumentNumber(header.getDocumentNumber());
            lockSummary.setUniversityFiscalYear(header.getUniversityFiscalYear());
            lockSummary.setChartOfAccountsCode(header.getChartOfAccountsCode());
            lockSummary.setAccountNumber(header.getAccountNumber());
            lockSummary.setSubAccountNumber(header.getSubAccountNumber());

            results.add(lockSummary);
        }
    }

    /**
     * Calls lock service to retrieve all current transaction locks and builds a lock summary object for each returned lock.
     * 
     * @param results - result list to add lock summaries
     */
    protected void getTransactionLocks(List<BudgetConstructionLockSummary> results, String lockUserId) {
        List<BudgetConstructionHeader> transLocks = SpringContext.getBean(LockService.class).getAllTransactionLocks(lockUserId);
        for (BudgetConstructionHeader header : transLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.TRANSACTION_LOCK);
            lockSummary.setLockUserId(header.getBudgetTransactionLockUserIdentifier());

            lockSummary.setDocumentNumber(header.getDocumentNumber());
            lockSummary.setUniversityFiscalYear(header.getUniversityFiscalYear());
            lockSummary.setChartOfAccountsCode(header.getChartOfAccountsCode());
            lockSummary.setAccountNumber(header.getAccountNumber());
            lockSummary.setSubAccountNumber(header.getSubAccountNumber());

            results.add(lockSummary);
        }
    }

    /**
     * Calls lock service to retrieve all funding locks that do not have a corresponding position locks and builds a lock summary
     * object for each returned lock.
     * 
     * @param results - result list to add lock summaries
     */
    protected void getOrphanFundingLocks(List<BudgetConstructionLockSummary> results, String lockUserId) {
        List<BudgetConstructionFundingLock> fundingLocks = SpringContext.getBean(LockService.class).getOrphanedFundingLocks(lockUserId);
        for (BudgetConstructionFundingLock fundingLock : fundingLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.FUNDING_LOCK);
            lockSummary.setLockUserId(fundingLock.getAppointmentFundingLockUserId());

            lockSummary.setUniversityFiscalYear(fundingLock.getUniversityFiscalYear());
            lockSummary.setChartOfAccountsCode(fundingLock.getChartOfAccountsCode());
            lockSummary.setAccountNumber(fundingLock.getAccountNumber());
            lockSummary.setSubAccountNumber(fundingLock.getSubAccountNumber());

            results.add(lockSummary);
        }
    }

    /**
     * Calls lock service to retrieve all current position/funding locks and builds a lock summary object for each returned lock.
     * 
     * @param results - result list to add lock summaries
     */
    protected void getPositionFundingLocks(List<BudgetConstructionLockSummary> results, String lockUserId) {
        List<PendingBudgetConstructionAppointmentFunding> positionFundingLocks = SpringContext.getBean(LockService.class).getAllPositionFundingLocks(lockUserId);
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : positionFundingLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.POSITION_FUNDING_LOCK);
            lockSummary.setLockUserId(appointmentFunding.getBudgetConstructionPosition().getPositionLockUserIdentifier());

            lockSummary.setUniversityFiscalYear(appointmentFunding.getUniversityFiscalYear());
            lockSummary.setChartOfAccountsCode(appointmentFunding.getChartOfAccountsCode());
            lockSummary.setAccountNumber(appointmentFunding.getAccountNumber());
            lockSummary.setSubAccountNumber(appointmentFunding.getSubAccountNumber());

            lockSummary.setPositionNumber(appointmentFunding.getBudgetConstructionPosition().getPositionNumber());
            lockSummary.setPositionDescription(appointmentFunding.getBudgetConstructionPosition().getPositionDescription());

            results.add(lockSummary);
        }
    }

    /**
     * Calls lock service to retrieve all current position locks without a corresponding funding lock and builds a lock summary
     * object for each returned lock.
     * 
     * @param results - result list to add lock summaries
     */
    protected void getOrphanPositionLocks(List<BudgetConstructionLockSummary> results, String lockUserId) {
        List<BudgetConstructionPosition> positionLocks = SpringContext.getBean(LockService.class).getOrphanedPositionLocks(lockUserId);
        for (BudgetConstructionPosition position : positionLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.POSITION_LOCK);
            lockSummary.setLockUserId(position.getPositionLockUserIdentifier());

            lockSummary.setUniversityFiscalYear(position.getUniversityFiscalYear());
            lockSummary.setPositionNumber(position.getPositionNumber());
            lockSummary.setPositionDescription(position.getPositionDescription());

            results.add(lockSummary);
        }
    }

    /**
     * Builds unlink action for each type of lock.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        BudgetConstructionLockSummary lockSummary = (BudgetConstructionLockSummary) businessObject;

        String imageDirectory = kualiConfigurationService.getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        String lockFields = lockSummary.getUniversityFiscalYear() + "%" + lockSummary.getChartOfAccountsCode() + "%" + lockSummary.getAccountNumber() + "%" + lockSummary.getSubAccountNumber() + "%" + lockSummary.getPositionNumber() + "%";
        lockFields = StringUtils.replace(lockFields, "null", "");
        
        String buttonSubmit = "<input name=\"" + KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + BCConstants.TEMP_LIST_UNLOCK_METHOD + ".";
        buttonSubmit += KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL + StringUtils.replace(lockSummary.getLockType()," ","_") + KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL;
        buttonSubmit += KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL + lockFields + KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL;
        buttonSubmit += KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL + lockSummary.getLockUserId() + KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL + "\" ";
        buttonSubmit += "src=\"" + imageDirectory + BCConstants.UNLOCK_BUTTON_NAME + "\"  type=\"image\" styleClass=\"tinybutton\" alt=\"unlock\" title=\"unlock\" border=\"0\" />";

        return buttonSubmit;
    }

    /**
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map, java.lang.String)
     */
    @Override
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        return "";
    }

    /**
     * Overridden to prevent a validation exception from thrown when the search method is called to refresh the
     * results after an error is encountered.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
