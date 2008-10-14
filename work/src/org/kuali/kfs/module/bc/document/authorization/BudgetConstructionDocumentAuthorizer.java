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
package org.kuali.kfs.module.bc.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

// public class BudgetConstructionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
public class BudgetConstructionDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(BudgetConstructionDocumentAuthorizer.class);

    /**
     * This inits and returns an editModeMap based on the BC security model
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document d, UniversalUser u) {


        // This does not use workflow states to calculate editmode
        // instead it uses the BC security model based on the user and
        // the current level of the document (account, subaccount)
        // return super.getEditMode(d, u);

        BudgetConstructionDocument bcDoc = (BudgetConstructionDocument) d;
        return getEditMode(bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber(), u);
    }


    /**
     * Gets the previously cached editModeMap from UserSession. The cached editModeMap is (re)initialized
     * only during BC Document open, pullup or pushdown actions. All other actions associated with the BC document
     * and the expansion screens used in budget by account mode use the cached editModeMap for authorization.
     * This ensures a consistent access mode during the entire budget by account viewing/editing session.
     * 
     * Edit access is maintained during the entire document open/pullup/pushdown/close cycle even when the system
     * is set to system view only mode, allowing the user to finish the current editing session.
     * 
     * Read only access is maintained during the entire document open/close cycle, with pullup and pushdown
     * honoring any security model level changes and any changes to the system view only setting.
     *  
     * @return
     */
    public Map getEditModeFromSession(){
        Map editModeMap = (Map) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_DOC_EDIT_MODE_SESSIONKEY);
        return editModeMap;
    }
    
    /**
     * This calculates editMode based on the BC Security model. A Budget Construction Document contains information associated with
     * a Fiscal Year, Chart, Account and SubAccount. This candidate key is used to find the document and calculate the editMode
     * based on the document's current level and the user's approval level. Document level < user level = VIEW_ONLY access, document =
     * user = FULL_ENTRY access, and document > USER_BELOW_DOC_LEVEL or user not an approver anywhere in the account's hierarchy = UNVIEWABLE access.
     * Users that aren't organization level approvers = USER_NOT_ORG_APPROVER
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param u
     * @return
     */
    public Map getEditMode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, UniversalUser u) {

        BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
        FiscalYearFunctionControlService fiscalYearFunctionControlService = SpringContext.getBean(FiscalYearFunctionControlService.class);

        Map editModeMap = new HashMap();
//        String editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        String editMode = budgetDocumentService.getAccessMode(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, u);
        editModeMap.put(editMode, "TRUE");
        
        // adding the case where system is in view only mode in case we need this fact for functionality
        // getAccessMode() will not return FULL_ENTRY if the system is in view only mode,
        // so we may or may not need this extra map row
        if (!fiscalYearFunctionControlService.isBudgetUpdateAllowed(universityFiscalYear)){
            editModeMap.put(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY, "TRUE");
        }

        return editModeMap;
    }

    /**
     * This method calculates editMode based on whether or not a user is a BC document approver for at least one organization. It is
     * used by the Salary Setting by Position and Incumbent expansion screens when not running in budgetByAccountMode a.k.a.
     * Organization Salary Setting
     * 
     * @return
     */
    public Map getEditMode() {

        // TODO this eventually needs to check when the BC system itself is in viewonly mode
        // probably need to change the signature to pass in fiscalYear from the Position/Incumbent expansion screens

        Map editModeMap = new HashMap();

        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        try {
            List<Org> pointOfViewOrgs = permissionService.getOrgReview(universalUser);
            if (pointOfViewOrgs.isEmpty()) {
                // GlobalVariables.getErrorMap().putError("pointOfViewOrg","error.budget.userNotOrgApprover");
                String editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER;
                editModeMap.put(editMode, "TRUE");
            }
            else {
                String editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY;
                editModeMap.put(editMode, "TRUE");
            }

        }
        catch (Exception e) {

            // TODO for now just return unviewable
            // really should report the exception in some soft way - maybe another EditMode value

            LOG.error("Could not get list of pointOfViewOrgs from permissionService.getOrgReview() for: " + universalUser, e);
            String editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER;
            editModeMap.put(editMode, "TRUE");
        }

        return editModeMap;

    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        // TODO this needs to call service methods that implements the BC security model
        // TODO instead the form should call the verson of this method that passes in the editmode map, actions are based on that
        // return super.getDocumentActionFlags(document, user);
        LOG.debug("calling BudgetConstructionDocumentAuthorizer.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");

        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(); // all flags default to false

        flags.setCanClose(true);
        flags.setCanSave(true);

        // TODO is this needed for BC??
        setAnnotateFlag(flags);

        return flags;
    }

    /**
     * This version uses editModeMap to set the action flags
     * 
     * @param document
     * @param user
     * @param editModeMap
     * @return
     */
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user, Map editModeMap) {
        
        LOG.debug("calling editMode version of BudgetConstructionDocumentAuthorizer.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");

        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(); // all flags default to false

        flags.setCanClose(true);
        if (editModeMap.containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY) && !editModeMap.containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)){
            flags.setCanSave(true);
        }

        // TODO is this needed for BC??
        setAnnotateFlag(flags);

        return flags;
    }

}
