/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentTargetAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.integration.cg.dto.BudgetAdjustmentParametersDTO.Details;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

public class BudgetAdjustmentServiceImpl implements BudgetAdjustmentService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentServiceImpl.class);

    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected BusinessObjectService businessObjectService;

    /**
     * This is the web service method that facilitates budget adjustment 1. Creates a Budget Adjustment Doc using the parameters
     * from KC 2. Returns the status object
     *
     * @param BudgetAdjustmentParametersDTO
     * @return BudgetAdjustmentStatusDTO
     */
    @Override
    public BudgetAdjustmentCreationStatusDTO createBudgetAdjustment(BudgetAdjustmentParametersDTO budgetAdjustmentParameters) {
        BudgetAdjustmentDocument budgetAdjustmentDoc = null;

        BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus = new BudgetAdjustmentCreationStatusDTO();
        budgetAdjustmentCreationStatus.setErrorMessages(new ArrayList<String>());
        budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);

        // check to see if the user has the permission to create account
        String principalId = budgetAdjustmentParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            budgetAdjustmentCreationStatus.getErrorMessages().add(KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_INVALID_USER, new String[]{principalId}));
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return budgetAdjustmentCreationStatus;
        }
        try {
            // create a Budget Adjustment object
            budgetAdjustmentDoc = createBudgetAdjustmentObject( budgetAdjustmentCreationStatus);

            if (!isValidParameters(budgetAdjustmentDoc.getPostingYear(), budgetAdjustmentCreationStatus, budgetAdjustmentParameters)) {
                return budgetAdjustmentCreationStatus;
            }

            // create a Budget Adjustment object, then route if successful
            if( populateBudgetAdjustmentDocDetails(budgetAdjustmentParameters, budgetAdjustmentDoc, budgetAdjustmentCreationStatus) ){
                routeBudgetAdjustmentDocument(budgetAdjustmentDoc, budgetAdjustmentCreationStatus);
            }else{
                //return as we have a failure
                return budgetAdjustmentCreationStatus;
            }

        }
        catch (Exception ex) {
            this.setFailStatus(budgetAdjustmentCreationStatus, KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_ACCOUNT_GENERATION_PROBLEM);
            return budgetAdjustmentCreationStatus;
        }
        // set required values to AccountCreationStatus

        if (budgetAdjustmentCreationStatus.getStatus().equals(KcConstants.KcWebService.STATUS_KC_SUCCESS) && getDocumentService().documentExists(budgetAdjustmentDoc.getDocumentHeader().getDocumentNumber())) {
            budgetAdjustmentCreationStatus.setDocumentNumber(budgetAdjustmentDoc.getDocumentNumber());
        }
        else {
            // save the document
            try {
                try{
                    GlobalVariables.getMessageMap().clearErrorMessages();
                    getDocumentService().saveDocument(budgetAdjustmentDoc);
                }catch(ValidationException ve){
                }
                budgetAdjustmentCreationStatus.setDocumentNumber(budgetAdjustmentDoc.getDocumentNumber());
            }
            catch (Exception ex) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_NOT_SAVED, null) + ": " + ex.getMessage());
                budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);

            }

        }
        return budgetAdjustmentCreationStatus;
    }

    protected boolean checkforEmptyField(BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatusDTO, String fieldName, String value, int lineNumber) {
        if ((value == null) || value.isEmpty()) {
            if (lineNumber != 0)
                value = "Detail " + lineNumber + " " + value;
            String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.AUTOMATCICG_ACCOUNT_MAINTENANCE_CHART_REQUIRED_FIELD, fieldName);
            this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
            return false;
        }
        return true;
    }


    protected void setFailStatus(BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatusDTO, String message) {
        budgetAdjustmentCreationStatusDTO.getErrorMessages().add(message);
        budgetAdjustmentCreationStatusDTO.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
    }

    protected boolean isValidParameters(Integer postingFiscalYear, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatusDTO, BudgetAdjustmentParametersDTO budgetAdjustmentParameters) {
        boolean isValid = true;
        boolean isValidAcct = true;
        isValid &= checkforEmptyField(budgetAdjustmentCreationStatusDTO, "Description", budgetAdjustmentParameters.getDescription(), 0);
        List<Details> details = budgetAdjustmentParameters.getDetails();
        AccountCreationService accountCreationService = SpringContext.getBean(AccountCreationService.class);
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        int lineNumber = 0;
        for (Details detail : details) {
            lineNumber++;
            isValid &= checkforEmptyField(budgetAdjustmentCreationStatusDTO, "Account", detail.getAccount(), lineNumber);
            isValid &= checkforEmptyField(budgetAdjustmentCreationStatusDTO, "Chart", detail.getChart(), lineNumber);
            isValid &= checkforEmptyField(budgetAdjustmentCreationStatusDTO, "Object Code", detail.getObjectCode(), lineNumber);
            isValid &= checkforEmptyField(budgetAdjustmentCreationStatusDTO, "Amount", detail.getCurrentAmount(), lineNumber);
            if (!KualiDecimal.isNumeric(detail.getCurrentAmount())) {
                isValid = false;
                String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_AMT_IS_NONUMERIC, detail.getObjectCode(), detail.getCurrentAmount());
                this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
            }
            else {
                // test if amount is non zero
                KualiDecimal amt = new KualiDecimal(detail.getCurrentAmount());
                if (amt.isZero()) {
                    isValid = false;
                    String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_AMT_IS_NONUMERIC, detail.getObjectCode(), detail.getCurrentAmount());
                    this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
                }
            }
            if (isValidAcct && (!accountCreationService.isValidChartAccount(detail.getChart(), detail.getAccount()))) {
                isValid = false;
                isValidAcct = false;
                String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_INVALID_ACCT, detail.getChart(), detail.getAccount());
                this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
            }
            else {

                ObjectCode objCode = objectCodeService.getByPrimaryId(postingFiscalYear, detail.getChart(), detail.getObjectCode());
                if (objCode == null) {
                    isValid = false;
                    String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_INVALID_OBJECTCODE, detail.getChart(), detail.getObjectCode());
                    this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
                } else if (!objCode.isFinancialObjectActiveCode()) {
                    isValid = false;
                    String message = GlobalVariablesExtractHelper.replaceTokens(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_INACTIVE_OBJECTCODE, detail.getChart(), detail.getObjectCode(), postingFiscalYear.toString());
                    this.setFailStatus(budgetAdjustmentCreationStatusDTO, message);
                }
            }
        }
        return isValid;
    }

    /**
     * This method creates an account to be used for automatic maintenance document
     *
     * @param AccountParametersDTO
     * @return Account
     */
    protected BudgetAdjustmentDocument createBudgetAdjustmentObject(BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {
        BudgetAdjustmentDocument budgetAdjustmentDocument = (BudgetAdjustmentDocument) createBADocument(budgetAdjustmentCreationStatus);
        // also populates posting year
        budgetAdjustmentDocument.initiateDocument();
         return budgetAdjustmentDocument;
    }

    protected boolean populateBudgetAdjustmentDocDetails(BudgetAdjustmentParametersDTO parameters, BudgetAdjustmentDocument budgetAdjustmentDocument, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {

        boolean methodSuccessful = true;

        // The Description of the BA document should carry the Award Document Number and Budget Version Number.
        budgetAdjustmentDocument.getDocumentHeader().setDocumentDescription(parameters.getDescription());
        budgetAdjustmentDocument.getDocumentHeader().setExplanation(parameters.getExplanation());
        budgetAdjustmentDocument.getDocumentHeader().setOrganizationDocumentNumber(parameters.getOrgDocNumber());
        KualiDecimal runningtotal = KualiDecimal.ZERO;
        Integer fiscalYear = budgetAdjustmentDocument.getPostingYear();
        if (parameters.getDetails() != null) {
            for (BudgetAdjustmentParametersDTO.Details detail : parameters.getDetails()) {
                if (KualiDecimal.isNumeric(detail.getCurrentAmount())) {
                    KualiDecimal amount = new KualiDecimal(detail.getCurrentAmount());
                    runningtotal = runningtotal.add(amount);
                    if (amount.isPositive()) {
                        budgetAdjustmentDocument.addTargetAccountingLine(createBudgetAdjustmentTargetAccountingLine(detail,fiscalYear));
                    }
                    else {
                        budgetAdjustmentDocument.addSourceAccountingLine(createBudgetAdjustmentSourceAccountingLine(detail,fiscalYear));
                    }
                }
            }
            if (runningtotal.isNonZero()) {
                boolean success = this.generateIncomeAccountingLine(fiscalYear, parameters.getSponsorType(), budgetAdjustmentDocument, runningtotal);
                if (!success) {
                    budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_VALUE + KcConstants.BudgetAdjustmentService.PARAMETER_INCOME_OBJECT_CODES_BY_SPONSOR_TYPE);
                    budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
                    methodSuccessful = false;
                }
            }
        }

        return methodSuccessful;
    }

    protected void populateAccountingLine(BudgetAdjustmentAccountingLine acctLine, Integer postingYear, String chart, String accountNumber, String proj, String objCode, KualiDecimal currentBudgetAdjustmentAmount) {
        acctLine.setChartOfAccountsCode(chart);
        acctLine.setAccountNumber(accountNumber);
        if ((proj != null) && (!proj.isEmpty()))
            acctLine.setProjectCode(proj);
        acctLine.setFinancialObjectCode(objCode);
        acctLine.setCurrentBudgetAdjustmentAmount(currentBudgetAdjustmentAmount);
        acctLine.setPostingYear(postingYear);
        acctLine.refresh();
    }

    protected BudgetAdjustmentSourceAccountingLine createBudgetAdjustmentSourceAccountingLine(BudgetAdjustmentParametersDTO.Details detail, Integer postingYear) {

        BudgetAdjustmentSourceAccountingLine budgetAdjustmentSourceAccountingLine = new BudgetAdjustmentSourceAccountingLine();
        // from / decrease chart -account
        KualiDecimal amount = new KualiDecimal(detail.getCurrentAmount()).abs();
        populateAccountingLine(budgetAdjustmentSourceAccountingLine, postingYear, detail.getChart(), detail.getAccount(), detail.getProjectCode(), detail.getObjectCode(), amount);
        return budgetAdjustmentSourceAccountingLine;
    }

    protected BudgetAdjustmentTargetAccountingLine createBudgetAdjustmentTargetAccountingLine(BudgetAdjustmentParametersDTO.Details detail, Integer postingYear) {
        BudgetAdjustmentTargetAccountingLine budgetAdjustmentTargetAccountingLine = new BudgetAdjustmentTargetAccountingLine();
        // TO / increase chart -account
        KualiDecimal amount = new KualiDecimal(detail.getCurrentAmount()).abs();
        populateAccountingLine(budgetAdjustmentTargetAccountingLine, postingYear, detail.getChart(), detail.getAccount(), detail.getProjectCode(), detail.getObjectCode(), amount);
        return budgetAdjustmentTargetAccountingLine;

    }

    protected boolean generateIncomeAccountingLine(Integer postingYear, String sponsorType, BudgetAdjustmentDocument baDoc, KualiDecimal amount) {
        BudgetAdjustmentParametersDTO.Details incomeDetail = new BudgetAdjustmentParametersDTO.Details();
        String sponsorCodeMapValue = parameterService.getSubParameterValueAsString(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_INCOME_OBJECT_CODES_BY_SPONSOR_TYPE, sponsorType);
        if ( StringUtils.isBlank(sponsorCodeMapValue) ) {
            return false;
        }
        if (amount.isNegative()) { // from side
            AccountingLine accountingLineDetail = baDoc.getSourceAccountingLine(0);
            BudgetAdjustmentSourceAccountingLine budgetAdjustmentSourceAccountingLine = new BudgetAdjustmentSourceAccountingLine();
            populateAccountingLine(budgetAdjustmentSourceAccountingLine,postingYear, accountingLineDetail.getChartOfAccountsCode(), accountingLineDetail.getAccountNumber(), accountingLineDetail.getProjectCode(), sponsorCodeMapValue, amount.abs());
            baDoc.addSourceAccountingLine(budgetAdjustmentSourceAccountingLine);
        }
        else {
            AccountingLine accountingLineDetail = baDoc.getTargetAccountingLine(0);
            BudgetAdjustmentTargetAccountingLine budgetAdjustmentTargetAccountingLine = new BudgetAdjustmentTargetAccountingLine();
            populateAccountingLine(budgetAdjustmentTargetAccountingLine,postingYear, accountingLineDetail.getChartOfAccountsCode(), accountingLineDetail.getAccountNumber(), accountingLineDetail.getProjectCode(), sponsorCodeMapValue, amount.abs());
            baDoc.addTargetAccountingLine(budgetAdjustmentTargetAccountingLine);
        }
        return true;
    }


    /**
     * This method will use the DocumentService to create a new document. The documentTypeName is gathered by using
     * MaintenanceDocumentDictionaryService which uses Account class to get the document type name.
     *
     * @param AccountCreationStatusDTO
     * @return document returns a new document for the account document type or null if there is an exception thrown.
     */
    protected Document createBADocument(BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatusDTO) {
        try {
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(TransactionalDocumentDictionaryService.class).getDocumentClassByName("BA"));
            return document;
        }
        catch (Exception e) {
            budgetAdjustmentCreationStatusDTO.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            budgetAdjustmentCreationStatusDTO.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return null;


        }
    }

    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value. If the system parameter value is not of save or submit or blanketapprove,
     * put an error message and quit. Throws an document WorkflowException if the specific document action fails to perform.
     *
     * @param maintenanceAccountDocument, errorMessages
     * @return success returns true if the workflow document action is successful else return false.
     */
    protected boolean routeBudgetAdjustmentDocument(BudgetAdjustmentDocument budgetAdjustmentDocument, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {

        try {
            // getParameterService().setParameterForTesting(BudgetAdjustmentDocument.class,
            // KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE,
            // KFSConstants.WORKFLOW_DOCUMENT_ROUTE);

            String BudgetAdjustAutoRouteValue = getParameterService().getParameterValueAsString(BudgetAdjustmentDocument.class, KcConstants.BudgetAdjustmentService.PARAMETER_KC_ADMIN_AUTO_BA_DOCUMENT_WORKFLOW_ROUTE);
            // String BudgetAdjustAutoRouteValue = getParameterService().getParameterValueAsString(Account.class,
            // KcConstants.BudgetAdjustmentService.PARAMETER_KC_BA_DOCUMENT_ROUTE);
            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && !BudgetAdjustAutoRouteValue.equalsIgnoreCase("submit") && !BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
                return false;
            }

            if (BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE)) {
                //attempt to save if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new SaveDocumentEvent(budgetAdjustmentDocument));

                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().saveDocument(budgetAdjustmentDocument);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(budgetAdjustmentDocument);
                    }catch(ValidationException ve){}

                    budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
                    LOG.error( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_BA_RULES_EXCEPTION, new String[]{budgetAdjustmentDocument.getDocumentNumber()}));

                    return false;
                }

            }
            else if (BudgetAdjustAutoRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {

                //attempt to blanket approve if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new BlanketApproveDocumentEvent(budgetAdjustmentDocument));

                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().blanketApproveDocument(budgetAdjustmentDocument, "", null);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(budgetAdjustmentDocument);
                    }catch(ValidationException ve){}

                    budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
                    LOG.error( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_BA_RULES_EXCEPTION, new String[]{budgetAdjustmentDocument.getDocumentNumber()}));

                    return false;
                }

            }
            else if (BudgetAdjustAutoRouteValue.equalsIgnoreCase("submit")) {

                //attempt to blanket approve if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RouteDocumentEvent(budgetAdjustmentDocument));

                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().routeDocument(budgetAdjustmentDocument, "", null);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(budgetAdjustmentDocument);
                    }catch(ValidationException ve){}

                    budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
                    LOG.error( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_BA_RULES_EXCEPTION, new String[]{budgetAdjustmentDocument.getDocumentNumber()}));

                    return false;
                }

            }
            return true;

        }
        catch (Exception ex) {
            LOG.error(KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS,null) + ": " + ex.getMessage());
            budgetAdjustmentCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            budgetAdjustmentCreationStatus.getErrorMessages().add( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + ex.getMessage());
            budgetAdjustmentCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return false;
        }
    }

    /**
     * This method check to see if the user can create the account maintenance document and set the user session
     *
     * @param String principalId
     * @return boolean
     */

    protected boolean isValidUser(String principalId) {

        PersonService personService = SpringContext.getBean(PersonService.class);

        try {
            Person user = personService.getPerson(principalId);
            DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
            if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class), user)) {
                // set the user session so that the user name can be displayed in the saved document
                GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception ex) {

            LOG.error( KcUtils.getErrorMessage(KcConstants.BudgetAdjustmentService.ERROR_KC_DOCUMENT_INVALID_USER, new String[]{principalId}));
            return false;
        }
    }


    /**
     * Gets the documentService attribute.
     *
     * @return Current value of documentService.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

}
