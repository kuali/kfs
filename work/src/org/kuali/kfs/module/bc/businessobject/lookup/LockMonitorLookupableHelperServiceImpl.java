/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.InputHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Implements custom search routine to find the current budget locks and build up the result List. Set an unlock URL for each lock.
 */
public class LockMonitorLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private ConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List<BudgetConstructionLockSummary> results = new ArrayList<BudgetConstructionLockSummary>();

        // get universal identifier from network id
        String lockUserID = fieldValues.get(BCPropertyConstants.LOCK_USER_ID);
        String lockUnivId = getUniversalIdFromNetworkID(lockUserID);

        getAccountLocks(results, lockUnivId);
        getTransactionLocks(results, lockUnivId);
        getOrphanFundingLocks(results, lockUnivId);
        getPositionFundingLocks(results, lockUnivId);
        getOrphanPositionLocks(results, lockUnivId);

        return new CollectionIncomplete(results, new Long(0));
    }

    /**
     * Calls lock service to retrieve all current account locks and builds a lock summary object for each returned lock.
     *
     * @param results - result list to add lock summaries
     */
    protected void getAccountLocks(List<BudgetConstructionLockSummary> results, String lockUnivId) {
        List<BudgetConstructionHeader> accountLocks = SpringContext.getBean(LockService.class).getAllAccountLocks(lockUnivId);
        for (BudgetConstructionHeader header : accountLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.ACCOUNT_LOCK);
            lockSummary.setLockUserId(header.getBudgetLockUser().getPrincipalName());

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
    protected void getTransactionLocks(List<BudgetConstructionLockSummary> results, String lockUnivId) {
        List<BudgetConstructionHeader> transLocks = SpringContext.getBean(LockService.class).getAllTransactionLocks(lockUnivId);
        for (BudgetConstructionHeader header : transLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.TRANSACTION_LOCK);
            lockSummary.setLockUserId(header.getBudgetTransactionLockUser().getPrincipalName());

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
    protected void getOrphanFundingLocks(List<BudgetConstructionLockSummary> results, String lockUnivId) {
        List<BudgetConstructionFundingLock> fundingLocks = SpringContext.getBean(LockService.class).getOrphanedFundingLocks(lockUnivId);
        for (BudgetConstructionFundingLock fundingLock : fundingLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.FUNDING_LOCK);
            lockSummary.setLockUserId(fundingLock.getAppointmentFundingLockUser().getPrincipalName());

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
    protected void getPositionFundingLocks(List<BudgetConstructionLockSummary> results, String lockUnivId) {
        List<PendingBudgetConstructionAppointmentFunding> positionFundingLocks = SpringContext.getBean(LockService.class).getAllPositionFundingLocks(lockUnivId);
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : positionFundingLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.POSITION_FUNDING_LOCK);
            lockSummary.setLockUserId(appointmentFunding.getBudgetConstructionPosition().getPositionLockUser().getPrincipalName());

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
    protected void getOrphanPositionLocks(List<BudgetConstructionLockSummary> results, String lockUnivId) {
        List<BudgetConstructionPosition> positionLocks = SpringContext.getBean(LockService.class).getOrphanedPositionLocks(lockUnivId);
        for (BudgetConstructionPosition position : positionLocks) {
            BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
            lockSummary.setLockType(BCConstants.LockTypes.POSITION_LOCK);
            lockSummary.setLockUserId(position.getPositionLockUser().getPrincipalName());

            lockSummary.setUniversityFiscalYear(position.getUniversityFiscalYear());
            lockSummary.setPositionNumber(position.getPositionNumber());
            lockSummary.setPositionDescription(position.getPositionDescription());

            results.add(lockSummary);
        }
    }

    /**
     * Builds unlink action for each type of lock.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        BudgetConstructionLockSummary lockSummary = (BudgetConstructionLockSummary) businessObject;

        String imageDirectory = kualiConfigurationService.getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        String lockFields = lockSummary.getUniversityFiscalYear() + BCConstants.LOCK_STRING_DELIMITER + lockSummary.getChartOfAccountsCode() + BCConstants.LOCK_STRING_DELIMITER + lockSummary.getAccountNumber() + BCConstants.LOCK_STRING_DELIMITER + lockSummary.getSubAccountNumber() + BCConstants.LOCK_STRING_DELIMITER + lockSummary.getPositionNumber() + BCConstants.LOCK_STRING_DELIMITER;
        lockFields = StringUtils.replace(lockFields, "null", "");

        String name = KFSConstants.DISPATCH_REQUEST_PARAMETER + "." + BCConstants.TEMP_LIST_UNLOCK_METHOD + ".";
        name +=
            KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL + StringUtils.replace(lockSummary.getLockType()," ","_") +
            KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL;
        name += KFSConstants.METHOD_TO_CALL_PARM9_LEFT_DEL + lockFields + KFSConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL;
        name +=
            KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL + lockSummary.getLockUserId() +
            KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL;
        String src = imageDirectory + BCConstants.UNLOCK_BUTTON_NAME;
        String inputType = "image";
        String styleClass = "tinybutton";
        String border= "0";

        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        InputHtmlData inputHtmlData = new InputHtmlData(name, inputType, src);
        inputHtmlData.setStyleClass(styleClass);
        inputHtmlData.setBorder(border);
        htmlDataList.add(inputHtmlData);
        return htmlDataList;
    }

    /**
     * Uses org.kuali.rice.kim.api.identity.IdentityService to retrieve user object associated with the given network id (if not blank) and then
     * returns universal id. Add error to GlobalVariables if the user was not found.
     *
     * @param networkID - network id for the user to find
     * @return universal id for the user or null if not found or the network id was blank
     */
    protected String getUniversalIdFromNetworkID(String networkID) {
        String universalId = null;
        if (StringUtils.isNotBlank(networkID)) {
            Principal user = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(networkID);
            if (user != null) {
                universalId = user.getPrincipalId();
            } else {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_LOCK_INVALID_USER, networkID);
            }
        }

        return universalId;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.krad.bo.BusinessObject, java.util.Map, java.lang.String)
     */
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        return getEmptyAnchorHtmlData();
    }

    /**
     * Overridden to prevent a validation exception from thrown when the search method is called to refresh the
     * results after an error is encountered.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
    }

    /**
     * Since this lookupable is called by the budget lookup action, the context will be KFS, not Rice. So the generated inquiries
     * will not have the Rice context (kr/) and be invalid. This override adds the Rice context to the inquiry Url to working
     * around the issue.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData inquiryUrl = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        inquiryUrl.setHref(StringUtils.replace(inquiryUrl.getHref(), KRADConstants.INQUIRY_ACTION, KFSConstants.INQUIRY_ACTION));

        return inquiryUrl;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
