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
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.KFSConstants.BLANK_SPACE;
import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN;
import static org.kuali.kfs.KFSConstants.ZERO;
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
import java.sql.Timestamp;
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
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
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
import org.kuali.kfs.rule.GenerateGeneralLedgerPendingEntriesRule;
import org.kuali.kfs.rule.ReviewAccountingLineRule;
import org.kuali.kfs.rule.SufficientFundsCheckingPreparationRule;
import org.kuali.kfs.rule.UpdateAccountingLineRule;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.SufficientFundsService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class contains all of the business rules that are common to all of the Financial Transaction Processing documents. Any
 * document specific business rules are contained within the specific child class that extends off of this one.
 */
public abstract class AccountingDocumentRuleBase extends GeneralLedgerPostingDocumentRuleBase implements AddAccountingLineRule<AccountingDocument>, GenerateGeneralLedgerPendingEntriesRule<AccountingDocument>, DeleteAccountingLineRule<AccountingDocument>, UpdateAccountingLineRule<AccountingDocument>, ReviewAccountingLineRule<AccountingDocument>, SufficientFundsCheckingPreparationRule, AccountingDocumentRuleBaseConstants {
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
            valid &= AccountingLineRuleUtil.validateAccountingLine(accountingLine, SpringContext.getBean(DataDictionaryService.class));

            if (valid) { // the following checks assume existence, so if the above method failed, we don't want to call these
                // Check the object code to see if it's restricted or not
                valid &= isObjectCodeAllowed(financialDocument, accountingLine);

                // Check the object code type allowances
                valid &= isObjectTypeAllowed(financialDocument, accountingLine);

                // Check the object sub-type code allowances
                valid &= isObjectSubTypeAllowed(financialDocument, accountingLine);

                // Check the object level allowances
                valid &= isObjectLevelAllowed(financialDocument, accountingLine);

                // Check the object consolidation allowances
                valid &= isObjectConsolidationAllowed(financialDocument, accountingLine);

                // Check the sub fund group allowances
                valid &= isSubFundGroupAllowed(financialDocument, accountingLine);

                // Check the fund group allowances
                valid &= isFundGroupAllowed(financialDocument, accountingLine);
            }
        }

        if (!valid) {
            LOG.info("business rule checks failed in processAccountingLine in KualiRuleServiceImpl");
        }

        LOG.debug("leaving processAccountingLine");

        return valid;
    }


    /**
     * Perform business rules common to all transactional documents when generating general ledger pending entries.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateGeneralLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - start");

        // handle the explicit entry
        // create a reference to the explicitEntry to be populated, so we can pass to the offset method later
        boolean success = true;
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        success &= processExplicitGeneralLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        success &= processOffsetGeneralLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, explicitEntry, offsetEntry);

        // handle the situation where the document is an error correction or is corrected
        handleDocumentErrorCorrection(accountingDocument, accountingLine);

        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - end");
        return success;
    }

    // Transactional Document Specific Rule Implementations
    /**
     * This method processes all necessary information to build an explicit general ledger entry, and then adds that to the
     * document.
     * 
     * @param accountingDocument
     * @param sequenceHelper
     * @param accountingLine
     * @param explicitEntry
     * @return boolean True if the explicit entry generation was successful, false otherwise.
     */
    protected boolean processExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("processExplicitGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - start");

        // populate the explicit entry
        populateExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, explicitEntry);

        // hook for children documents to implement document specific GLPE field mappings
        customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);

        // add the new explicit entry to the document now
        accountingDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        LOG.debug("processExplicitGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - end");
        return true;
    }

    /**
     * This method processes an accounting line's information to build an offset entry, and then adds that to the document.
     * 
     * @param accountingDocument
     * @param sequenceHelper
     * @param accountingLine
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean True if the offset generation is successful.
     */
    protected boolean processOffsetGeneralLedgerPendingEntry(AccountingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("processOffsetGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - start");

        boolean success = true;
        // populate the offset entry
        success &= populateOffsetGeneralLedgerPendingEntry(accountingDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        // hook for children documents to implement document specific field mappings for the GLPE
        success &= customizeOffsetGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry, offsetEntry);

        // add the new offset entry to the document now
        accountingDocument.getGeneralLedgerPendingEntries().add(offsetEntry);

        LOG.debug("processOffsetGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - end");
        return success;
    }

    /**
     * This method can be overridden to set attributes on the explicit entry in a way specific to a particular document. By default
     * the explicit entry is returned without modification.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param explicitEntry
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
    }

    /**
     * This method can be overridden to set attributes on the offset entry in a way specific to a particular document. By default
     * the offset entry is not modified.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param explicitEntry
     * @param offsetEntry
     * @return whether the offset generation is successful
     */
    protected boolean customizeOffsetGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("customizeOffsetGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - start");

        LOG.debug("customizeOffsetGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - end");
        return true;
    }

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
        if (ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }
        else {
            if (null == correctsDocumentId && ZERO.compareTo(amount) == 1) { // amount < 0
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
        KualiDecimal creditAmount = new KualiDecimal(0);
        KualiDecimal debitAmount = new KualiDecimal(0);
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
     * This populates an empty GeneralLedgerPendingEntry explicitEntry object instance with default values.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param explicitEntry
     */
    protected void populateExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        explicitEntry.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(accountingDocument.getClass()));
        explicitEntry.setVersionNumber(new Long(1));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setTransactionEntryProcessedTs(new java.sql.Date(transactionTimestamp.getTime()));
        explicitEntry.setAccountNumber(accountingLine.getAccountNumber());
        if (accountingLine.getAccount().getAccountSufficientFundsCode() == null) {
            accountingLine.getAccount().setAccountSufficientFundsCode(KFSConstants.SF_TYPE_NO_CHECKING);
        }
        explicitEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(accountingLine.getObjectCode(), accountingLine.getAccount().getAccountSufficientFundsCode()));
        explicitEntry.setFinancialDocumentApprovedCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.NO);
        explicitEntry.setTransactionEncumbranceUpdateCode(BLANK_SPACE);
        explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        explicitEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        explicitEntry.setTransactionDebitCreditCode(isDebit(accountingDocument, accountingLine) ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        explicitEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        explicitEntry.setDocumentNumber(accountingLine.getDocumentNumber());
        explicitEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        ObjectCode objectCode = accountingLine.getObjectCode();
        if (ObjectUtils.isNull(objectCode)) {
            accountingLine.refreshReferenceObject("objectCode");
        }
        explicitEntry.setFinancialObjectTypeCode(accountingLine.getObjectCode().getFinancialObjectTypeCode());
        explicitEntry.setOrganizationDocumentNumber(accountingDocument.getDocumentHeader().getOrganizationDocumentNumber());
        explicitEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        explicitEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode()));
        explicitEntry.setReferenceFinancialDocumentNumber(getEntryValue(accountingLine.getReferenceNumber(), BLANK_SPACE));
        explicitEntry.setReferenceFinancialDocumentTypeCode(getEntryValue(accountingLine.getReferenceTypeCode(), BLANK_SPACE));
        explicitEntry.setReferenceFinancialSystemOriginationCode(getEntryValue(accountingLine.getReferenceOriginCode(), BLANK_SPACE));
        explicitEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber()));
        explicitEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode()));
        explicitEntry.setTransactionEntryOffsetIndicator(false);
        explicitEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        explicitEntry.setTransactionLedgerEntryDescription(getEntryValue(accountingLine.getFinancialDocumentLineDescription(), accountingDocument.getDocumentHeader().getFinancialDocumentDescription()));
        explicitEntry.setUniversityFiscalPeriodCode(null); // null here, is assigned during batch or in specific document rule
        // classes
        explicitEntry.setUniversityFiscalYear(accountingDocument.getPostingYear());
        // TODO wait for core budget year data structures to be put in place
        // explicitEntry.setBudgetYear(accountingLine.getBudgetYear());
        // explicitEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);

        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }

    /**
     * This is responsible for properly negating the sign on an accounting line's amount when its associated document is an error
     * correction.
     * 
     * @param accountingDocument
     * @param accountingLine
     */
    private final void handleDocumentErrorCorrection(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("handleDocumentErrorCorrection(AccountingDocument, AccountingLine) - start");

        // If the document corrects another document, make sure the accounting line has the correct sign.
        if ((null == accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isNegative()) || (null != accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isPositive())) {
            accountingLine.setAmount(accountingLine.getAmount().multiply(new KualiDecimal(1)));
        }

        LOG.debug("handleDocumentErrorCorrection(AccountingDocument, AccountingLine) - end");
    }

    /**
     * Determines whether an accounting line is an asset line.
     * 
     * @param accountingLine
     * @return boolean True if a line is an asset line.
     */
    public final boolean isAsset(AccountingLine accountingLine) {
        LOG.debug("isAsset(AccountingLine) - start");

        boolean returnboolean = isAssetTypeCode(AccountingDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(accountingLine));
        LOG.debug("isAsset(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Determines whether an accounting line is a liability line.
     * 
     * @param accountingLine
     * @return boolean True if the line is a liability line.
     */
    public final boolean isLiability(AccountingLine accountingLine) {
        LOG.debug("isLiability(AccountingLine) - start");

        boolean returnboolean = isLiabilityTypeCode(AccountingDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(accountingLine));
        LOG.debug("isLiability(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Determines whether an accounting line is an income line or not. This goes agains the configurable object type code list in
     * the ApplicationParameter mechanism. This list can be configured externally.
     * 
     * @param accountingLine
     * @return boolean True if the line is an income line.
     */
    public final boolean isIncome(AccountingLine accountingLine) {
        LOG.debug("isIncome(AccountingLine) - start");

        boolean returnboolean = AccountingDocumentRuleUtil.isIncome(accountingLine);
        LOG.debug("isIncome(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Check object code type to determine whether the accounting line is expense.
     * 
     * @param accountingLine
     * @return boolean True if the line is an expense line.
     */
    public boolean isExpense(AccountingLine accountingLine) {
        LOG.debug("isExpense(AccountingLine) - start");

        boolean returnboolean = AccountingDocumentRuleUtil.isExpense(accountingLine);
        LOG.debug("isExpense(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Determines whether an accounting line is an expense or asset.
     * 
     * @param line
     * @return boolean True if it's an expense or asset.
     */
    public final boolean isExpenseOrAsset(AccountingLine line) {
        LOG.debug("isExpenseOrAsset(AccountingLine) - start");

        boolean returnboolean = isAsset(line) || isExpense(line);
        LOG.debug("isExpenseOrAsset(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Determines whether an accounting line is an income or liability line.
     * 
     * @param line
     * @return boolean True if the line is an income or liability line.
     */
    public final boolean isIncomeOrLiability(AccountingLine line) {
        LOG.debug("isIncomeOrLiability(AccountingLine) - start");

        boolean returnboolean = isLiability(line) || isIncome(line);
        LOG.debug("isIncomeOrLiability(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Check object code type to determine whether the accounting line is revenue.
     * 
     * @param line
     * @return boolean True if the line is a revenue line.
     */
    public final boolean isRevenue(AccountingLine line) {
        LOG.debug("isRevenue(AccountingLine) - start");

        boolean returnboolean = !isExpense(line);
        LOG.debug("isRevenue(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * GLPE amounts are ALWAYS positive, so just take the absolute value of the accounting line's amount.
     * 
     * @param accountingLine
     * @return KualiDecimal The amount that will be used to populate the GLPE.
     */
    protected KualiDecimal getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine accountingLine) {
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - start");

        KualiDecimal returnKualiDecimal = accountingLine.getAmount().abs();
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - end");
        return returnKualiDecimal;
    }


    /**
     * Determines whether an accounting line represents a credit line.
     * 
     * @param accountingLine
     * @param financialDocument
     * @return boolean True if the line is a credit line.
     * @throws IllegalStateException
     */
    public boolean isCredit(AccountingLine accountingLine, AccountingDocument financialDocument) throws IllegalStateException {
        LOG.debug("isCredit(AccountingLine, AccountingDocument) - start");

        boolean returnboolean = !isDebit(financialDocument, accountingLine);
        LOG.debug("isCredit(AccountingLine, AccountingDocument) - end");
        return returnboolean;
    }

    /**
     * This method checks to see if the object code for the passed in accounting line exists in the list of restricted object codes.
     * Note, the values that this checks against can be externally configured with the ApplicationParameter maintenance mechanism.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code is allowed.
     */
    public boolean isObjectCodeAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_OBJECT_CODES, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode());
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
                getParameterService().getParameterEvaluator(ParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, propertyValue).evaluateAndAddError(SourceAccountingLine.class, propertyName, userEnteredPropertyName);
            }
            if (getParameterService().parameterExists(documentClass, parameterName)) {
                getParameterService().getParameterEvaluator(documentClass, parameterName, propertyValue).evaluateAndAddError(SourceAccountingLine.class, propertyName, userEnteredPropertyName);
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
    public boolean isObjectTypeAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_OBJECT_TYPE_CODES, "objectCode.financialObjectTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the fund group code for the accouting line's account is allowed. The common implementation
     * allows any fund group code.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isFundGroupAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_FUND_GROUP_CODES, "account.subFundGroup.fundGroupCode", "accountNumber");
    }

    /**
     * This method checks to see if the sub fund group code for the accouting line's account is allowed. The common implementation
     * allows any sub fund group code.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isSubFundGroupAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_SUB_FUND_GROUP_CODES, "account.subFundGroupCode", "accountNumber");
    }

    /**
     * This method checks to see if the object sub-type code for the accouting line's object code is allowed. The common
     * implementation allows any object sub-type code.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectSubTypeAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_OBJECT_SUB_TYPE_CODES, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the object level for the accouting line's object code is allowed. The common implementation
     * allows any object level.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectLevelAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_OBJECT_LEVELS, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks to see if the object consolidation for the accouting line's object code is allowed. The common
     * implementation allows any object consolidation.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectConsolidationAllowed(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return isAccountingLineValueAllowed(accountingDocument.getClass(), accountingLine, RESTRICTED_OBJECT_CONSOLIDATIONS, "objectCode.financialObjectLevel.financialConsolidationObjectCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * Determines whether the <code>objectTypeCode</code> is an asset.
     * 
     * @param objectTypeCode
     * @return Is she asset or something completely different?
     */
    public final boolean isAssetTypeCode(String objectTypeCode) {
        LOG.debug("isAssetTypeCode(String) - start");

        boolean returnboolean = SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinancialObjectTypeAssetsCd().equals(objectTypeCode);
        LOG.debug("isAssetTypeCode(String) - end");
        return returnboolean;
    }

    /**
     * Determines whether the <code>objectTypeCode</code> is a liability.
     * 
     * @param objectTypeCode
     * @return Is she liability or something completely different?
     */
    public final boolean isLiabilityTypeCode(String objectTypeCode) {
        LOG.debug("isLiabilityTypeCode(String) - start");

        boolean returnboolean = SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinObjectTypeLiabilitiesCode().equals(objectTypeCode);
        LOG.debug("isLiabilityTypeCode(String) - end");
        return returnboolean;
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

        KualiDecimal sourceLinesTotal = new KualiDecimal(0);
        KualiDecimal targetLinesTotal = new KualiDecimal(0);

        // iterate over each accounting line and if it has an account with a
        // fund group that should be balanced, then add that lines amount to the bucket
        for (Iterator i = lines.iterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            String fundGroupCode = line.getAccount().getSubFundGroup().getFundGroupCode();

            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(componentClass, parameterName, fundGroupCode);
            if (evaluator.evaluationSucceeds()) {
                KualiDecimal glpeLineAmount = getGeneralLedgerPendingEntryAmountForAccountingLine(line);
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

    /**
     * Convience method for determine if a document is an error correction document.
     * 
     * @param accountingDocument
     * @return true if document is an error correct
     */
    protected boolean isErrorCorrection(AccountingDocument accountingDocument) {
        LOG.debug("isErrorCorrection(AccountingDocument) - start");

        boolean isErrorCorrection = false;

        String correctsDocumentId = accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber();
        if (StringUtils.isNotBlank(correctsDocumentId)) {
            isErrorCorrection = true;
        }

        LOG.debug("isErrorCorrection(AccountingDocument) - end");
        return isErrorCorrection;
    }

    /**
     * util class that contains common algorithms for determining debit amounts
     */
    public static class IsDebitUtils {
        public static final String isDebitCalculationIllegalStateExceptionMessage = "an invalid debit/credit check state was detected";
        public static final String isErrorCorrectionIllegalStateExceptionMessage = "invalid (error correction) document not allowed";
        public static final String isInvalidLineTypeIllegalArgumentExceptionMessage = "invalid accounting line type";

        /**
         * @param debitCreditCode
         * @return true if debitCreditCode equals the the debit constant
         */
        public static boolean isDebitCode(String debitCreditCode) {
            LOG.debug("isDebitCode(String) - start");

            boolean returnboolean = StringUtils.equals(KFSConstants.GL_DEBIT_CODE, debitCreditCode);
            LOG.debug("isDebitCode(String) - end");
            return returnboolean;
        }

        /**
         * <ol>
         * <li>object type is included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount > 0)
         * <li> (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability) && (lineAmount < 0)
         * <li> (isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> document isErrorCorrection
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        public static boolean isDebitConsideringType(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {
            LOG.debug("isDebitConsideringType(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();

            // income/liability
            if (rule.isIncomeOrLiability(accountingLine)) {
                isDebit = !isPositiveAmount;
            }
            // expense/asset
            else {
                if (rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }

            LOG.debug("isDebitConsideringType(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
            return isDebit;
        }

        /**
         * <ol>
         * <li>object type is not included in determining if a line is debit or credit.
         * <li>accounting line section (source/target) is not included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> none
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> (isIncome || isLiability || isExpense || isAsset) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount <= 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        public static boolean isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {
            LOG.debug("isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            boolean isPositiveAmount = amount.isPositive();
            // isDebit if income/liability/expense/asset and line amount is positive
            if (isPositiveAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                isDebit = true;
            }
            else {
                // non error correction
                if (!rule.isErrorCorrection(accountingDocument)) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);

                }
                // error correction
                else {
                    isDebit = false;
                }
            }

            LOG.debug("isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) type is included in determining if a line is debit or credit.
         * <li> zero line amounts are never allowed
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)
         * <li> isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> lineAmount == 0
         * <li> ! (isIncome || isLiability || isExpense || isAsset)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        public static boolean isDebitConsideringSection(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {
            LOG.debug("isDebitConsideringSection(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();
            // source line
            if (accountingLine.isSourceAccountingLine()) {
                // income/liability/expense/asset
                if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = !isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // target line
            else {
                if (accountingLine.isTargetAccountingLine()) {
                    if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                        isDebit = isPositiveAmount;
                    }
                    else {
                        throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                    }
                }
                else {
                    throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                }
            }

            LOG.debug("isDebitConsideringSection(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) and object type is included in determining if a line is debit or credit.
         * <li> negative line amounts are <b>Only</b> allowed during error correction
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> isSourceLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isTargetLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isErrorCorrection && isSourceLine && (isIncome || isLiability) && (lineAmount < 0)
         * <li> isErrorCorrection && isTargetLine && (isExpense || isAsset) && (lineAmount < 0)
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> isSourceLine && (isIncome || isLiability) && (lineAmount > 0)
         * <li> isTargetLine && (isExpense || isAsset) && (lineAmount > 0)
         * <li> isErrorCorrection && (isExpense || isAsset) && (lineAmount < 0)
         * <li> isErrorCorrection && (isIncome || isLiability) && (lineAmount < 0)
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> !isErrorCorrection && !(lineAmount > 0)
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        public static boolean isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {
            LOG.debug("isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            boolean isPositiveAmount = amount.isPositive();
            // non error correction - only allow amount >0
            if (!isPositiveAmount && !rule.isErrorCorrection(accountingDocument)) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            // source line
            if (accountingLine.isSourceAccountingLine()) {
                // could write below block in one line using == as XNOR operator, but that's confusing to read:
                // isDebit = (rule.isIncomeOrLiability(accountingLine) == isPositiveAmount);
                if (isPositiveAmount) {
                    isDebit = rule.isIncomeOrLiability(accountingLine);
                }
                else {
                    isDebit = rule.isExpenseOrAsset(accountingLine);
                }
            }
            // target line
            else {
                if (accountingLine.isTargetAccountingLine()) {
                    if (isPositiveAmount) {
                        isDebit = rule.isExpenseOrAsset(accountingLine);
                    }
                    else {
                        isDebit = rule.isIncomeOrLiability(accountingLine);
                    }
                }
                else {
                    throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                }
            }

            LOG.debug("isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
            return isDebit;
        }

        /**
         * throws an <code>IllegalStateException</code> if the document is an error correction. otherwise does nothing
         * 
         * @param rule
         * @param accountingDocument
         */
        public static void disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument) {
            LOG.debug("disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase, AccountingDocument) - start");

            if (rule.isErrorCorrection(accountingDocument)) {
                throw new IllegalStateException(isErrorCorrectionIllegalStateExceptionMessage);
            }

            LOG.debug("disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase, AccountingDocument) - end");
        }
    }
}
