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
package org.kuali.kfs.rules;

import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_BALANCE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_FUND_GROUP_SET_DOES_NOT_BALANCE;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_TARGET_SECTION_NO_ACCOUNTING_LINES;
import static org.kuali.kfs.KFSKeyConstants.ERROR_INVALID_FORMAT;
import static org.kuali.kfs.KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION;
import static org.kuali.kfs.KFSKeyConstants.ERROR_MAX_LENGTH;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_FUND_GROUP_CODES;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_CODES;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_CONSOLIDATIONS;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_LEVELS;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_SUB_TYPE_CODES;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER.RESTRICTED_SUB_FUND_GROUP_CODES;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rule.event.BlanketApproveDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.AddAccountingLineRule;
import org.kuali.kfs.rule.DeleteAccountingLineRule;
import org.kuali.kfs.rule.ReviewAccountingLineRule;
import org.kuali.kfs.rule.SufficientFundsCheckingPreparationRule;
import org.kuali.kfs.rule.UpdateAccountingLineRule;
import org.kuali.kfs.service.AccountingLineRuleHelperService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.ChartUser;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class contains all of the business rules that are common to all of the Financial Transaction Processing documents. Any
 * document specific business rules are contained within the specific child class that extends off of this one.
 */
public abstract class AccountingDocumentRuleBase extends GeneralLedgerPostingDocumentRuleBase implements AddAccountingLineRule<AccountingDocument>, DeleteAccountingLineRule<AccountingDocument>, UpdateAccountingLineRule<AccountingDocument>, ReviewAccountingLineRule<AccountingDocument>, SufficientFundsCheckingPreparationRule, AccountingDocumentRuleBaseConstants {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingDocumentRuleBase.class);
    private ParameterService parameterService;

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    /**
     * Indicates what is being done to an accounting line. This allows the same method to be used for different actions.
     */
    public enum AccountingLineAction {
        ADD(ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD), DELETE(ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE), UPDATE(ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);

        public final String accessibilityErrorKey;

        AccountingLineAction(String accessabilityErrorKey) {
            this.accessibilityErrorKey = accessabilityErrorKey;
        }
    }

    // Inherited Document Specific Business Rules
    /**
     * This method performs common validation for Transactional Document routes. Note the rule framework will handle validating all
     * of the accounting lines and also those checks that would normally be done on a save, automatically for us.
     * 
     * @param document
     * @return boolean True if the document is valid for routing, false otherwise.
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.debug("processCustomRouteDocumentBusinessRules(Document) - start");

        boolean valid = true;

        AccountingDocument financialDocument = (AccountingDocument) document;

        // check to make sure the required number of accounting lines were met
        valid &= isAccountingLinesRequiredNumberForRoutingMet(financialDocument);

        // check balance
        valid &= isDocumentBalanceValid(financialDocument);

        LOG.debug("processCustomRouteDocumentBusinessRules(Document) - end");
        return valid;
    }

    /**
     * This method performs common validation for Transactional Document approvals. Note the rule framework will handle validating
     * all of the accounting lines and also those checks that would normally be done on an approval, automatically for us.
     * 
     * @param approveEvent
     * @return boolean True if the document is valid for approval, false otherwise.
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        LOG.debug("processCustomApproveDocumentBusinessRules(ApproveDocumentEvent) - start");

        boolean valid = true;

        // allow accountingLine totals to change for BlanketApproveDocumentEvents, and only
        // for BlanketApproveDocumentEvents
        if (!(approveEvent instanceof BlanketApproveDocumentEvent)) {
            valid &= isAccountingLineTotalsUnchanged((AccountingDocument) approveEvent.getDocument());
        }

        LOG.debug("processCustomApproveDocumentBusinessRules(ApproveDocumentEvent) - end");
        return valid;
    }

    // Rule interface specific methods
    /**
     * This method performs common validation for adding of accounting lines. Then calls a custom method for more specific
     * validation.
     * 
     * @see org.kuali.core.rule.AddAccountingLineRule#processAddAccountingLineBusinessRules(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("processAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - start");

        boolean valid = checkAccountingLine(financialDocument, accountingLine);
        if (valid) {
            valid &= checkAccountingLineAccountAccessibility(financialDocument, accountingLine, AccountingLineAction.ADD);
        }
        if (valid) {
            valid &= processCustomAddAccountingLineBusinessRules(financialDocument, accountingLine);
        }

        LOG.debug("processAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");
        return valid;
    }

    /**
     * This method should be overridden in the children classes to implement business rules that don't fit into any of the other
     * AddAccountingLineRule interface methods.
     * 
     * @param financialDocument
     * @param accountingLine
     * @return boolean
     */
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("processCustomAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - start");

        LOG.debug("processCustomAddAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");
        return true;
    }

    /**
     * This method performs common validation for deleting of accounting lines. Then calls a custom method for more specific
     * validation.
     * 
     * @see org.kuali.core.rule.DeleteAccountingLineRule#processDeleteAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, boolean)
     */
    public boolean processDeleteAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        LOG.debug("processDeleteAccountingLineBusinessRules(AccountingDocument, AccountingLine, boolean) - start");

        return verifyExistenceOfOtherAccessibleAccountingLines(financialDocument, lineWasAlreadyDeletedFromDocument);
    }

    /**
     * This method should be overridden in the children classes to implement deleteAccountingLine checks that don't fit into any of
     * the other DeleteAccountingLineRule interface methods.
     * 
     * @param financialDocument
     * @param accountingLine
     * @param lineWasAlreadyDeletedFromDocument
     * @return boolean
     */
    protected boolean processCustomDeleteAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        LOG.debug("processCustomDeleteAccountingLineBusinessRules(AccountingDocument, AccountingLine, boolean) - start");

        LOG.debug("processCustomDeleteAccountingLineBusinessRules(AccountingDocument, AccountingLine, boolean) - end");
        return true;
    }

    /**
     * This method verifies that other lines exist on the document that this user has access to.
     * 
     * @param financialDocument
     * @param lineWasAlreadyDeletedFromDocument
     * @return boolean
     */
    private boolean verifyExistenceOfOtherAccessibleAccountingLines(AccountingDocument financialDocument, boolean lineWasAlreadyDeletedFromDocument) {
        LOG.debug("verifyExistenceOfOtherAccessibleAccountingLines(AccountingDocument, boolean) - start");

        // verify that other accountingLines will exist after the deletion which are accessible to this user
        int minimumRemainingAccessibleLines = 1 + (lineWasAlreadyDeletedFromDocument ? 0 : 1);
        boolean sufficientLines = hasAccessibleAccountingLines(financialDocument, minimumRemainingAccessibleLines);
        if (!sufficientLines) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE);
        }

        LOG.debug("verifyExistenceOfOtherAccessibleAccountingLines(AccountingDocument, boolean) - end");
        return sufficientLines;
    }

    /**
     * This method performs common validation for update of accounting lines. Then calls a custom method for more specific
     * validation.
     * 
     * @see org.kuali.core.rule.UpdateAccountingLineRule#processUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        LOG.debug("processUpdateAccountingLineBusinessRules(AccountingDocument, AccountingLine, AccountingLine) - start");

        boolean valid = checkAccountingLine(financialDocument, updatedAccountingLine);
        if (valid) {
            valid &= checkAccountingLineAccountAccessibility(financialDocument, updatedAccountingLine, AccountingLineAction.UPDATE);
        }
        if (valid) {
            valid &= processCustomUpdateAccountingLineBusinessRules(financialDocument, accountingLine, updatedAccountingLine);
        }

        LOG.debug("processUpdateAccountingLineBusinessRules(AccountingDocument, AccountingLine, AccountingLine) - end");
        return valid;
    }

    /**
     * This method should be overridden in the children classes to implement updateAccountingLine checks that don't fit into any of
     * the other UpdateAccountingLineRule interface methods.
     * 
     * @param accountingDocument
     * @param originalAccountingLine
     * @param updatedAccountingLine
     * @return boolean
     */
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        LOG.debug("processCustomUpdateAccountingLineBusinessRules(AccountingDocument, AccountingLine, AccountingLine) - start");

        LOG.debug("processCustomUpdateAccountingLineBusinessRules(AccountingDocument, AccountingLine, AccountingLine) - end");
        return true;
    }

    /**
     * Wrapper around global errorMap.put call, to allow better logging
     * 
     * @param propertyName
     * @param errorKey
     * @param errorParams
     */
    protected void reportError(String propertyName, String errorKey, String... errorParams) {
        LOG.debug("reportError(String, String, String) - start");

        GlobalVariables.getErrorMap().putError(propertyName, errorKey, errorParams);
        if (LOG.isDebugEnabled()) {
            LOG.debug("rule failure at " + ExceptionUtils.describeStackLevels(1, 2));
        }
    }

    /**
     * Adds a global error for a missing required property. This is used for properties, such as reference origin code, which cannot
     * be required by the DataDictionary validation because not all documents require them.
     * 
     * @param boe
     * @param propertyName
     */
    public static void putRequiredPropertyError(BusinessObjectEntry boe, String propertyName) {
        LOG.debug("putRequiredPropertyError(BusinessObjectEntry, String) - start");

        String label = boe.getAttributeDefinition(propertyName).getShortLabel();
        GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_REQUIRED, label);

        LOG.debug("putRequiredPropertyError(BusinessObjectEntry, String) - end");
    }

    /**
     * If the given accountingLine has an account which is inaccessible to the current user, an error message will be put into the
     * global ErrorMap and into the logfile.
     * 
     * @param financialDocument
     * @param accountingLine
     * @param action
     * @return true if the given accountingLine refers to an account which allows it to be added, deleted, or updated
     */
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        LOG.debug("checkAccountingLineAccountAccessibility(AccountingDocument, AccountingLine, AccountingLineAction) - start");

        boolean isAccessible = accountIsAccessible(financialDocument, accountingLine);

        // report (and log) errors
        if (!isAccessible) {
            String[] errorParams = new String[] { accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier() };
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, errorParams);

            LOG.info("accountIsAccessible check failed: account " + errorParams[0] + ", user " + errorParams[1]);
        }

        LOG.debug("checkAccountingLineAccountAccessibility(AccountingDocument, AccountingLine, AccountingLineAction) - end");
        return isAccessible;
    }

    /**
     * @param financialDocument
     * @param accountingLine
     * @return true if the given accountingLine refers to an account which allows it to be added, deleted, or updated
     */
    protected boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("accountIsAccessible(AccountingDocument, AccountingLine) - start");

        boolean isAccessible = false;

        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        ChartUser currentUser = (ChartUser) GlobalVariables.getUserSession().getUniversalUser().getModuleUser(ChartUser.MODULE_ID);

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            isAccessible = true;
        }
        else {
            if (workflowDocument.stateIsEnroute()) {
                String chartCode = accountingLine.getChartOfAccountsCode();
                String accountNumber = accountingLine.getAccountNumber();

                // if a document is enroute, user can only refer to for accounts for which they are responsible
                isAccessible = currentUser.isResponsibleForAccount(chartCode, accountNumber);
            }
            else {
                if (workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal() || workflowDocument.stateIsDisapproved()) {
                    isAccessible = false;
                }
                else {
                    if (workflowDocument.stateIsException() && currentUser.getUniversalUser().isWorkflowExceptionUser()) {
                        isAccessible = true;
                    }
                }
            }
        }

        LOG.debug("accountIsAccessible(AccountingDocument, AccountingLine) - end");
        return isAccessible;
    }

    /**
     * @param financialDocument
     * @param min
     * @return true if the document has n (or more) accessible accountingLines
     */
    protected boolean hasAccessibleAccountingLines(AccountingDocument financialDocument, int min) {
        LOG.debug("hasAccessibleAccountingLines(AccountingDocument, int) - start");

        boolean hasLines = false;

        // only count if the doc is enroute
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();
        ChartUser currentUser = (ChartUser) GlobalVariables.getUserSession().getUniversalUser().getModuleUser(ChartUser.MODULE_ID);
        if (workflowDocument.stateIsEnroute()) {
            int accessibleLines = 0;
            for (Iterator i = financialDocument.getSourceAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                if (accountIsAccessible(financialDocument, line)) {
                    ++accessibleLines;
                }
            }
            for (Iterator i = financialDocument.getTargetAccountingLines().iterator(); (accessibleLines < min) && i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                if (accountIsAccessible(financialDocument, line)) {
                    ++accessibleLines;
                }
            }

            hasLines = (accessibleLines >= min);
        }
        else {
            if (workflowDocument.stateIsException() && currentUser.getUniversalUser().isWorkflowExceptionUser()) {
                hasLines = true;
            }
            else {
                if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                    hasLines = true;
                }
                else {
                    hasLines = false;
                }
            }
        }

        LOG.debug("hasAccessibleAccountingLines(AccountingDocument, int) - end");
        return hasLines;
    }

    /**
     * This method performs common validation for review of accounting lines. Then calls a custom method for more specific
     * validation.
     * 
     * @see org.kuali.core.rule.ReviewAccountingLineRule#processReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean processReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("processReviewAccountingLineBusinessRules(AccountingDocument, AccountingLine) - start");

        boolean valid = checkAccountingLine(financialDocument, accountingLine);
        if (valid) {
            valid &= processCustomReviewAccountingLineBusinessRules(financialDocument, accountingLine);
        }

        LOG.debug("processReviewAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");
        return valid;
    }

    /**
     * This method should be overridden in the child classes to implement business rules that don't fit into any of the other
     * ReviewAccountingLineRule interface methods.
     * 
     * @param financialDocument
     * @param accountingLine
     * @return boolean
     */
    protected boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("processCustomReviewAccountingLineBusinessRules(AccountingDocument, AccountingLine) - start");

        LOG.debug("processCustomReviewAccountingLineBusinessRules(AccountingDocument, AccountingLine) - end");
        return true;
    }

    /**
     * This contains business rule checks that are common to all accounting line events for all Transaction Processing Financial
     * eDocs. Note existence, requirement, and format checking are not done in this method, because those checks are handled
     * automatically by the data dictionary validation framework. This method is responsible for call validate methods that check
     * the activity of an instance.
     * 
     * @param accountingLine
     * @param financialDocument
     * @return true if no errors occurred
     */
    private final boolean checkAccountingLine(AccountingDocument financialDocument, AccountingLine accountingLine) {
        LOG.debug("entering processAccountingLine");

        boolean valid = true;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();

        // now make sure all the necessary business objects are fully populated
        accountingLine.refreshNonUpdateableReferences();

        // validate required checks in addition to format checks
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(accountingLine);

        // check to see if any errors were reported
        int currentErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        valid &= (currentErrorCount == originalErrorCount);

        if (!valid) {
            // logic to replace generic amount error messages
            // create a list of accounting line attribute keys
            ArrayList linePatterns = new ArrayList();
            // source patterns: removing wildcards
            linePatterns.addAll(Arrays.asList(StringUtils.replace(SOURCE_ACCOUNTING_LINE_ERROR_PATTERN, "*", "").split(",")));
            // target patterns: removing wildcards
            linePatterns.addAll(Arrays.asList(StringUtils.replace(TARGET_ACCOUNTING_LINE_ERROR_PATTERN, "*", "").split(",")));

            // see if any lines have errors
            for (Iterator i = GlobalVariables.getErrorMap().getPropertiesWithErrors().iterator(); i.hasNext();) {
                String property = (String) i.next();
                // only concerned about amount field errors
                if (property.endsWith("." + AMOUNT_PROPERTY_NAME)) {
                    // check if the amount field is associated with an accounting line
                    boolean isLineProperty = true;
                    for (Iterator linePatternsIterator = linePatterns.iterator(); i.hasNext() && !isLineProperty;) {
                        isLineProperty = property.startsWith((String) linePatternsIterator.next());
                    }
                    if (isLineProperty) {
                        // find the specific error messages for the property
                        for (ListIterator errors = GlobalVariables.getErrorMap().getMessages(property).listIterator(); errors.hasNext();) {
                            ErrorMessage error = (ErrorMessage) errors.next();
                            String errorKey = null;
                            String[] params = new String[2];
                            if (StringUtils.equals(ERROR_INVALID_FORMAT, error.getErrorKey())) {
                                errorKey = ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT;
                                params[1] = accountingLine.getAmount().toString();
                            }
                            else {
                                if (StringUtils.equals(ERROR_MAX_LENGTH, error.getErrorKey())) {
                                    errorKey = ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH;

                                    // String value = ObjectUtils.getPropertyValue(accountingLine,
                                    // KFSConstants.AMOUNT_PROPERTY_NAME)

                                }
                            }
                            if (errorKey != null) {

                                LOG.debug("replacing: " + error);
                                // now replace error message
                                error.setErrorKey(errorKey);
                                // replace parameters
                                params[0] = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(accountingLine.getClass(), AMOUNT_PROPERTY_NAME);
                                error.setMessageParameters(params);
                                // put back where it came form
                                errors.set(error);
                                LOG.debug("with: " + error);
                            }
                        }
                    }
                }
            }
        }
        else { // continue on with the rest of the validation if the accounting line contains valid values
            // Check the amount entered
            valid &= isAmountValid(financialDocument, accountingLine);

            // Perform the standard accounting line rule checking - checks activity
            // of each attribute in addition to existence
            valid &= getAccountingLineRuleHelperService().validateAccountingLine(accountingLine);

            if (valid) { // the following checks assume existence, so if the above method failed, we don't want to call these
                Class documentClass = getAccountingLineDocumentClass(financialDocument);

                // Check the object code to see if it's restricted or not
                valid &= isObjectCodeAllowed(documentClass, accountingLine);

                // Check the object code type allowances
                valid &= isObjectTypeAllowed(documentClass, accountingLine);

                // Check the object sub-type code allowances
                valid &= isObjectSubTypeAllowed(documentClass, accountingLine);

                // Check the object level allowances
                valid &= isObjectLevelAllowed(documentClass, accountingLine);

                // Check the object consolidation allowances
                valid &= isObjectConsolidationAllowed(documentClass, accountingLine);

                // Check the sub fund group allowances
                valid &= isSubFundGroupAllowed(documentClass, accountingLine);

                // Check the fund group allowances
                valid &= isFundGroupAllowed(documentClass, accountingLine);
            }
        }

        if (!valid) {
            LOG.info("business rule checks failed in processAccountingLine in KualiRuleServiceImpl");
        }

        LOG.debug("leaving processAccountingLine");

        return valid;
    }
    
    /**
     * Returns the default version of the AccountingLineRuleHelperService
     * @return the default implementation of the AccountingLineRuleHelperService
     */
    protected AccountingLineRuleHelperService getAccountingLineRuleHelperService() {
        return SpringContext.getBean(AccountingLineRuleHelperService.class);
    }


    /**
     * This method returns the document class associated with this accounting document and is used to find the appropriate parameter
     * rule This can be overridden to return a different class depending on the situation, initially this is used for Year End
     * documents so that they use the same rules as their parent docs
     * 
     * @see org.kuali.module.financial.rules.YearEndGeneralErrorCorrectionDocumentRule#getAccountingLineDocumentClass(AccountingDocument)
     * @param financialDocument
     * @return documentClass associated with this accounting document
     */
    protected Class getAccountingLineDocumentClass(AccountingDocument financialDocument) {
        return financialDocument.getClass();
    }

    // Transactional Document Specific Rule Implementations

    /**
     * Checks accounting line totals for approval to make sure that they have not changed.
     * 
     * @param accountingDocument
     * @return boolean True if the number of accounting lines are valid for routing, false otherwise.
     */
    protected boolean isAccountingLineTotalsUnchanged(AccountingDocument accountingDocument) {
        LOG.debug("isAccountingLineTotalsUnchanged(AccountingDocument) - start");

        AccountingDocument persistedDocument = null;

        persistedDocument = retrievePersistedDocument(accountingDocument);

        boolean isUnchanged = true;
        if (persistedDocument == null) {
            handleNonExistentDocumentWhenApproving(accountingDocument);
        }
        else {
            // retrieve the persisted totals
            KualiDecimal persistedSourceLineTotal = persistedDocument.getSourceTotal();
            KualiDecimal persistedTargetLineTotal = persistedDocument.getTargetTotal();

            // retrieve the updated totals
            KualiDecimal currentSourceLineTotal = accountingDocument.getSourceTotal();
            KualiDecimal currentTargetLineTotal = accountingDocument.getTargetTotal();

            // make sure that totals have remained unchanged, if not, recognize that, and
            // generate appropriate error messages
            if (currentSourceLineTotal.compareTo(persistedSourceLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(SOURCE_ACCOUNTING_LINE_ERRORS, persistedSourceLineTotal, currentSourceLineTotal);
            }

            if (currentTargetLineTotal.compareTo(persistedTargetLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(TARGET_ACCOUNTING_LINE_ERRORS, persistedTargetLineTotal, currentTargetLineTotal);
            }
        }

        LOG.debug("isAccountingLineTotalsUnchanged(AccountingDocument) - end");
        return isUnchanged;
    }

    /**
     * attempt to retrieve the document from the DB for comparison
     * 
     * @param accountingDocument
     * @return AccountingDocument
     */
    protected AccountingDocument retrievePersistedDocument(AccountingDocument accountingDocument) {
        LOG.debug("retrievePersistedDocument(AccountingDocument) - start");

        AccountingDocument persistedDocument = null;

        try {
            persistedDocument = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(accountingDocument.getDocumentNumber());
        }
        catch (WorkflowException we) {
            LOG.error("retrievePersistedDocument(AccountingDocument)", we);

            handleNonExistentDocumentWhenApproving(accountingDocument);
        }

        LOG.debug("retrievePersistedDocument(AccountingDocument) - end");
        return persistedDocument;
    }

    /**
     * This method builds out the error message for when totals have changed.
     * 
     * @param propertyName
     * @param persistedSourceLineTotal
     * @param currentSourceLineTotal
     */
    protected void buildTotalChangeErrorMessage(String propertyName, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        LOG.debug("buildTotalChangeErrorMessage(String, KualiDecimal, KualiDecimal) - start");

        String persistedTotal = (String) new CurrencyFormatter().format(persistedSourceLineTotal);
        String currentTotal = (String) new CurrencyFormatter().format(currentSourceLineTotal);
        GlobalVariables.getErrorMap().putError(propertyName, ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED, new String[] { persistedTotal, currentTotal });

        LOG.debug("buildTotalChangeErrorMessage(String, KualiDecimal, KualiDecimal) - end");
    }

    /**
     * Handles the case when a non existent document is attempted to be retrieve and that if it's in an initiated state, it's ok.
     * 
     * @param accountingDocument
     */
    protected final void handleNonExistentDocumentWhenApproving(AccountingDocument accountingDocument) {
        LOG.debug("handleNonExistentDocumentWhenApproving(AccountingDocument) - start");

        // check to make sure this isn't an initiated document being blanket approved
        if (!accountingDocument.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            throw new IllegalStateException("Document " + accountingDocument.getDocumentNumber() + " is not a valid document that currently exists in the system.");
        }

        LOG.debug("handleNonExistentDocumentWhenApproving(AccountingDocument) - end");
    }

    /**
     * Checks accounting line number limits for routing. This method is for overriding by documents with rules about the total
     * number of lines in both sections combined.
     * 
     * @param accountingDocument
     * @return boolean True if the number of accounting lines are valid for routing, false otherwise.
     */
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument accountingDocument) {
        LOG.debug("isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - start");

        boolean met = true;
        met &= isSourceAccountingLinesRequiredNumberForRoutingMet(accountingDocument);
        met &= isTargetAccountingLinesRequiredNumberForRoutingMet(accountingDocument);

        LOG.debug("isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
        return met;
    }

    /**
     * Some double-sided documents also allow for one sided entries for correcting - so if one side is empty, the other side must
     * have at least two lines in it. The balancing rules take care of validation of amounts.
     * 
     * @param accountingDocument
     * @return boolean
     */
    protected boolean isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(AccountingDocument accountingDocument) {
        LOG.debug("isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - start");

        int sourceSectionSize = accountingDocument.getSourceAccountingLines().size();
        int targetSectionSize = accountingDocument.getTargetAccountingLines().size();

        if ((sourceSectionSize == 0 && targetSectionSize < 2) || (targetSectionSize == 0 && sourceSectionSize < 2)) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET);

            LOG.debug("isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
            return false;
        }

        LOG.debug("isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
        return true;
    }

    /**
     * This method checks the amount of a given accounting line to make sure it's not 0, it's positive for regular documents, and
     * negative for correction documents.
     * 
     * @param document
     * @param accountingLine
     * @return boolean True if there aren't any issues, false otherwise.
     */
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        LOG.debug("isAmountValid(AccountingDocument, AccountingLine) - start");

        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amount, or negative on original (non-correction) document; no sign check for documents that are
        // corrections to previous documents
        String correctsDocumentId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();
        if (KualiDecimal.ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }
        else {
            if (null == correctsDocumentId && KualiDecimal.ZERO.compareTo(amount) == 1) { // amount < 0
                GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION);
                LOG.info("failing isAmountValid - correctsDocumentId check && amount == 1");
                return false;
            }
        }

        return true;
    }

    /**
     * This method will check to make sure that the required number of target accounting lines for routing, exist in the document.
     * This method represents the default implementation, which is that at least one target accounting line must exist.
     * 
     * @param accountingDocument
     * @return isOk
     */
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument accountingDocument) {
        LOG.debug("isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - start");

        if (0 == accountingDocument.getTargetAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(TARGET_ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_TARGET_SECTION_NO_ACCOUNTING_LINES, new String[] { accountingDocument.getTargetAccountingLinesSectionTitle() });

            LOG.debug("isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
            return false;
        }
        else {
            LOG.debug("isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
            return true;
        }
    }

    /**
     * This method will check to make sure that the required number of source accounting lines for routing, exist in the document.
     * This method represents the default implementation, which is that at least one source accounting line must exist.
     * 
     * @param accountingDocument
     * @return isOk
     */
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument accountingDocument) {
        LOG.debug("isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - start");

        if (0 == accountingDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(SOURCE_ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES, new String[] { accountingDocument.getSourceAccountingLinesSectionTitle() });

            LOG.debug("isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
            return false;
        }
        else {
            LOG.debug("isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument) - end");
            return true;
        }
    }

    /**
     * This is the default implementation for Transactional Documents, which sums the amounts of all of the Debit GLPEs, and
     * compares it to the sum of all of the Credit GLPEs. In general, this algorithm works, but it does not work for some specific
     * documents such as the Journal Voucher. The method name denotes not an expected behavior, but a more general title so that
     * some documents that don't use this default implementation, can override just this method without having to override the
     * calling method.
     * 
     * @param accountingDocument
     * @return boolean True if the document is balanced, false otherwise.
     */
    protected boolean isDocumentBalanceValid(AccountingDocument accountingDocument) {
        LOG.debug("isDocumentBalanceValid(AccountingDocument) - start");

        boolean returnboolean = isDocumentBalanceValidConsideringDebitsAndCredits(accountingDocument);
        LOG.debug("isDocumentBalanceValid(AccountingDocument) - end");
        return returnboolean;
    }

    /**
     * This method sums all of the debit GLPEs up and sums all of the credit GLPEs up and then compares the totals to each other,
     * returning true if they are equal and false if they are not.
     * 
     * @param accountingDocument
     * @return boolean
     */
    protected boolean isDocumentBalanceValidConsideringDebitsAndCredits(AccountingDocument accountingDocument) {
        LOG.debug("isDocumentBalanceValidConsideringDebitsAndCredits(AccountingDocument) - start");

        // generate GLPEs specifically here so that we can compare debits to credits
        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(accountingDocument)) {
            throw new ValidationException("general ledger GLPE generation failed");
        }

        // now loop through all of the GLPEs and calculate buckets for debits and credits
        KualiDecimal creditAmount = KualiDecimal.ZERO;
        KualiDecimal debitAmount = KualiDecimal.ZERO;
        Iterator i = accountingDocument.getGeneralLedgerPendingEntries().iterator();
        while (i.hasNext()) {
            GeneralLedgerPendingEntry glpe = (GeneralLedgerPendingEntry) i.next();
            if (!glpe.isTransactionEntryOffsetIndicator()) { // make sure we are looking at only the explicit entries
                if (KFSConstants.GL_CREDIT_CODE.equals(glpe.getTransactionDebitCreditCode())) {
                    creditAmount = creditAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
                else { // DEBIT
                    debitAmount = debitAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
            }
        }
        boolean isValid = debitAmount.compareTo(creditAmount) == 0;

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE);
        }

        LOG.debug("isDocumentBalanceValidConsideringDebitsAndCredits(AccountingDocument) - end");
        return isValid;
    }

    // Other Helper Methods

    /**
     * This method checks to see if the object code for the passed in accounting line exists in the list of restricted object codes.
     * Note, the values that this checks against can be externally configured with the ApplicationParameter maintenance mechanism.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code is allowed.
     */
    public boolean isObjectCodeAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_OBJECT_CODES, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode());
    }

    private boolean isAccountingLineValueAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine, String parameterName, String propertyName) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, parameterName, propertyName, propertyName);
    }

    private boolean isAccountingLineValueAllowed(Class documentClass, AccountingLine accountingLine, String parameterName, String propertyName, String userEnteredPropertyName) {
        boolean isAllowed = true;
        String exceptionMessage = "Invalue property name provided to AccountingDocumentRuleBase isAccountingLineValueAllowed method: " + propertyName;
        try {
            String propertyValue = (String) PropertyUtils.getProperty(accountingLine, propertyName);
            if (getParameterService().parameterExists(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName)) {
                isAllowed = getParameterService().getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, propertyValue).evaluateAndAddError(SourceAccountingLine.class, propertyName, userEnteredPropertyName);
            }
            if (getParameterService().parameterExists(documentClass, parameterName)) {
                isAllowed = getParameterService().getParameterEvaluator(documentClass, parameterName, propertyValue).evaluateAndAddError(SourceAccountingLine.class, propertyName, userEnteredPropertyName);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        return isAllowed;
    }

    /**
     * This checks the accounting line's object type code to ensure that it is not a fund balance object type. This is a universal
     * business rule that all transaction processing documents should abide by.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isObjectTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_OBJECT_TYPE_CODES, "objectCode.financialObjectTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the fund group code for the accouting line's account is allowed. The common implementation
     * allows any fund group code.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isFundGroupAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_FUND_GROUP_CODES, "account.subFundGroup.fundGroupCode", "accountNumber");
    }

    /**
     * This method checks to see if the sub fund group code for the accounting line's account is allowed. The common implementation
     * allows any sub fund group code.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isSubFundGroupAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_SUB_FUND_GROUP_CODES, "account.subFundGroupCode", "accountNumber");
    }

    /**
     * This method checks to see if the object sub-type code for the accounting line's object code is allowed. The common
     * implementation allows any object sub-type code.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectSubTypeAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_OBJECT_SUB_TYPE_CODES, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the object level for the accounting line's object code is allowed. The common implementation
     * allows any object level.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectLevelAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_OBJECT_LEVELS, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the object consolidation for the accounting line's object code is allowed. The common
     * implementation allows any object consolidation.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectConsolidationAllowed(Class documentClass, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(documentClass, accountingLine, RESTRICTED_OBJECT_CONSOLIDATIONS, "objectCode.financialObjectLevel.financialConsolidationObjectCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isFundBalanceCode(String objectCode) {
        LOG.debug("isFundBalanceCode(String) - start");

        boolean returnboolean = (CONSOLIDATED_OBJECT_CODE.FUND_BALANCE.equals(objectCode));
        LOG.debug("isFundBalanceCode(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isBudgetOnlyCodesSubType(String objectCode) {
        LOG.debug("isBudgetOnlyCodesSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.BUDGET_ONLY.equals(objectCode));
        LOG.debug("isBudgetOnlyCodesSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isCashSubType(String objectCode) {
        LOG.debug("isCashSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.CASH.equals(objectCode));
        LOG.debug("isCashSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isFundBalanceSubType(String objectCode) {
        LOG.debug("isFundBalanceSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.SUBTYPE_FUND_BALANCE.equals(objectCode));
        LOG.debug("isFundBalanceSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isHourlyWagesSubType(String objectCode) {
        LOG.debug("isHourlyWagesSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.HOURLY_WAGES.equals(objectCode));
        LOG.debug("isHourlyWagesSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectCode
     * @return boolean
     */
    public final boolean isSalariesSubType(String objectCode) {
        LOG.debug("isSalariesSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.SALARIES.equals(objectCode));
        LOG.debug("isSalariesSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method...
     * 
     * @param objectSubTypeCode
     * @return boolean
     */
    public final boolean isValuationsAndAdjustmentsSubType(String objectSubTypeCode) {
        LOG.debug("isValuationsAndAdjustmentsSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.VALUATIONS_AND_ADJUSTMENTS.equals(objectSubTypeCode));
        LOG.debug("isValuationsAndAdjustmentsSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method determines whether an object sub-type code is a mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a manadatory transfer, false otherwise.
     */
    public final boolean isMandatoryTransfersSubType(String objectSubTypeCode) {
        LOG.debug("isMandatoryTransfersSubType(String) - start");

        boolean returnboolean = checkMandatoryTransfersSubType(objectSubTypeCode, APPLICATION_PARAMETER.MANDATORY_TRANSFER_SUBTYPE_CODES);
        LOG.debug("isMandatoryTransfersSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method determines whether an object sub-type code is a non-mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a non-mandatory transfer, false otherwise.
     */
    public final boolean isNonMandatoryTransfersSubType(String objectSubTypeCode) {
        LOG.debug("isNonMandatoryTransfersSubType(String) - start");

        boolean returnboolean = checkMandatoryTransfersSubType(objectSubTypeCode, APPLICATION_PARAMETER.NONMANDATORY_TRANSFER_SUBTYPE_CODES);
        LOG.debug("isNonMandatoryTransfersSubType(String) - end");
        return returnboolean;
    }

    /**
     * Helper method for checking the isMandatoryTransfersSubType() and isNonMandatoryTransfersSubType().
     * 
     * @param objectSubTypeCode
     * @param parameterName
     * @return boolean
     */
    private final boolean checkMandatoryTransfersSubType(String objectSubTypeCode, String parameterName) {
        LOG.debug("checkMandatoryTransfersSubType(String, String) - start");

        if (objectSubTypeCode == null) {
            throw new IllegalArgumentException(EXCEPTIONS.NULL_OBJECT_SUBTYPE_MESSAGE);
        }
        ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, objectSubTypeCode);
        boolean returnboolean = evaluator.evaluationSucceeds();
        LOG.debug("checkMandatoryTransfersSubType(String, String) - end");
        return returnboolean;
    }

    /**
     * @param objectCode
     * @return boolean
     */
    public final boolean isFringeBenefitsSubType(String objectCode) {
        LOG.debug("isFringeBenefitsSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.FRINGE_BEN.equals(objectCode));
        LOG.debug("isFringeBenefitsSubType(String) - end");
        return returnboolean;
    }

    /**
     * @param objectCode
     * @return boolean
     */
    public final boolean isCostRecoveryExpenseSubType(String objectCode) {
        LOG.debug("isCostRecoveryExpenseSubType(String) - start");

        boolean returnboolean = (OBJECT_SUB_TYPE_CODE.COST_RECOVERY_EXPENSE.equals(objectCode));
        LOG.debug("isCostRecoveryExpenseSubType(String) - end");
        return returnboolean;
    }

    /**
     * This method will make sure that totals for a specified set of fund groups is valid across the two different accounting line
     * sections.
     * 
     * @param tranDoc
     * @param fundGroupCodes An array of the fund group codes that will be considered for balancing.
     * @return True if they balance; false otherwise.
     */
    protected boolean isFundGroupSetBalanceValid(AccountingDocument tranDoc, Class componentClass, String parameterName) {
        LOG.debug("isFundGroupSetBalanceValid(AccountingDocument, String[]) - start");

        // don't need to do any of this if there's no parameter
        if (!getParameterService().parameterExists(componentClass, parameterName)) {
            LOG.debug("isFundGroupSetBalanceValid(AccountingDocument, String[]) - end");
            return true;
        }

        List lines = new ArrayList();

        lines.addAll(tranDoc.getSourceAccountingLines());
        lines.addAll(tranDoc.getTargetAccountingLines());

        KualiDecimal sourceLinesTotal = KualiDecimal.ZERO;
        KualiDecimal targetLinesTotal = KualiDecimal.ZERO;

        // iterate over each accounting line and if it has an account with a
        // fund group that should be balanced, then add that lines amount to the bucket
        for (Iterator i = lines.iterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            String fundGroupCode = line.getAccount().getSubFundGroup().getFundGroupCode();

            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(componentClass, parameterName, fundGroupCode);
            if (evaluator.evaluationSucceeds()) {
                KualiDecimal glpeLineAmount = tranDoc.getGeneralLedgerPendingEntryAmountForDetail(line);
                if (line.isSourceAccountingLine()) {
                    sourceLinesTotal = sourceLinesTotal.add(glpeLineAmount);
                }
                else {
                    targetLinesTotal = targetLinesTotal.add(glpeLineAmount);
                }
            }
        }

        // check that the amounts balance across sections
        boolean isValid = true;

        if (sourceLinesTotal.compareTo(targetLinesTotal) != 0) {
            isValid = false;

            // creating an evaluator to just format the fund codes into a nice string
            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(componentClass, parameterName, "");
            GlobalVariables.getErrorMap().putError("document.sourceAccountingLines", ERROR_DOCUMENT_FUND_GROUP_SET_DOES_NOT_BALANCE, new String[] { tranDoc.getSourceAccountingLinesSectionTitle(), tranDoc.getTargetAccountingLinesSectionTitle(), evaluator.getParameterValuesForMessage() });
        }

        LOG.debug("isFundGroupSetBalanceValid(AccountingDocument, String[]) - end");
        return isValid;
    }

    /**
     * A helper method which builds out a human readable string of the fund group codes that were used for the balancing rule.
     * 
     * @param fundGroupCodes
     * @return String
     */
    private String buildFundGroupCodeBalancingErrorMessage(String[] fundGroupCodes) {
        // TODO: delete this method
        LOG.debug("buildFundGroupCodeBalancingErrorMessage(String[]) - start");

        String balancingFundGroups = "";
        int arrayLen = fundGroupCodes.length;
        if (arrayLen == 1) {
            balancingFundGroups = fundGroupCodes[0];
        }
        else {
            if (arrayLen == 2) {
                balancingFundGroups = fundGroupCodes[0] + " or " + fundGroupCodes[1];
            }
            else {
                for (int i = 0; i < arrayLen; i++) {
                    String balancingFundGroupCode = fundGroupCodes[i];
                    if ((i + 1) == arrayLen) {
                        balancingFundGroups = balancingFundGroups + ", or " + balancingFundGroupCode;
                    }
                    else {
                        balancingFundGroups = balancingFundGroups + ", " + balancingFundGroupCode;
                    }
                }
            }
        }

        LOG.debug("buildFundGroupCodeBalancingErrorMessage(String[]) - end");
        return balancingFundGroups;
    }
}
