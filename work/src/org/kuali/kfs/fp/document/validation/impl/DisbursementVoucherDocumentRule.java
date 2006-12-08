/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/validation/impl/DisbursementVoucherDocumentRule.java,v $
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
package org.kuali.module.financial.rules;

import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.FinancialDocument;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.NonResidentAlienTaxPercent;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.bo.PaymentReasonCode;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.document.DisbursementVoucherDocumentAuthorizer;
import org.kuali.module.financial.util.CodeDescriptionFormatter;
import org.kuali.module.financial.util.DocumentationLocationCodeDescriptionFormatter;
import org.kuali.module.financial.util.ObjectCodeDescriptionFormatter;
import org.kuali.module.financial.util.ObjectLevelCodeDescriptionFormatter;
import org.kuali.module.financial.util.SubFundGroupCodeDescriptionFormatter;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;


/**
 * Business rule(s) applicable to Disbursement Voucher documents.
 * 
 * 
 */
public class DisbursementVoucherDocumentRule extends TransactionalDocumentRuleBase implements DisbursementVoucherRuleConstants, GenerateGeneralLedgerDocumentPendingEntriesRule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentRule.class);

    private static String taxGroupName;
    private static String travelGroupName;
    private static String wireTransferGroupName;
    private static String frnGroupName;
    private static String adminGroupName;
    
    /**
     * Constructs a DisbursementVoucherDocumentRule instance.
     */
    public DisbursementVoucherDocumentRule() {
    }

    /**
     * Overrides to call super. If super fails, then we invoke some DV specific rules about FO routing to double check if the
     * individual has special conditions that they can alter accounting lines by.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.financial.rules.TransactionalDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(TransactionalDocument transactionalDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // first check parent's isAccessible method for basic FO authz checking
        boolean isAccessible = accountIsAccessible(transactionalDocument, accountingLine);

        // get the authorizer class to check for special conditions routing and if the user is part of a particular workgroup
        // but only if the document is enroute
        if (!isAccessible && transactionalDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(transactionalDocument);
            // if approval is requested and it is special conditions routing and the user is in a special conditions routing
            // workgroup then
            // the line is accessible
            if (transactionalDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && dvAuthorizer.isSpecialRouting(transactionalDocument, GlobalVariables.getUserSession().getUniversalUser()) && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup() || isUserInDvAdminGroup())) {
                isAccessible = true;
            }
        }

        // report (and log) errors
        if (!isAccessible) {
            String[] errorParams = new String[] { accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier() };
            GlobalVariables.getErrorMap().putError(PropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, errorParams);
        }

        return isAccessible;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, updatedAccountingLine);
    }

    /**
     * Override to check if we are in special handling where the check amount and accounting line total can decrease, else amounts
     * should not have changed.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) approveEvent.getDocument();

        // amounts can only decrease
        DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(dvDocument);
        if (dvAuthorizer.isSpecialRouting(dvDocument, GlobalVariables.getUserSession().getUniversalUser()) && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup())) {
            boolean approveOK = true;

            // users in foreign or wire workgroup can increase or decrease amounts because of currency conversion
            if (!isUserInFRNGroup() && !isUserInWireGroup()) {
                DisbursementVoucherDocument persistedDocument = (DisbursementVoucherDocument) retrievePersistedDocument(dvDocument);
                if (persistedDocument == null) {
                    handleNonExistentDocumentWhenApproving(dvDocument);
                    return approveOK;
                }
                else {
                    // check total cannot decrease
                    if (persistedDocument.getDisbVchrCheckTotalAmount().isLessThan(dvDocument.getDisbVchrCheckTotalAmount())) {
                        GlobalVariables.getErrorMap().putError(PropertyConstants.DOCUMENT + "." + PropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
                        approveOK = false;
                    }
                }
            }

            return approveOK;
        }
        else {
            // amounts must not have been changed
            return super.processCustomApproveDocumentBusinessRules(approveEvent);
        }
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean allow = true;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        // don't validate generated tax lines
        if (((DisbursementVoucherDocument) transactionalDocument).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringServiceLocator.getDisbursementVoucherTaxService().getNRATaxLineNumbers(((DisbursementVoucherDocument) transactionalDocument).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
            if (taxLineNumbers.contains(accountingLine.getSequenceNumber())) {
                return true;
            }
        }

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* payment reason must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            if(!errors.containsMessageKey(KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON)) {
                errors.putErrorWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON);
            }
            allow = false;
        }

        /* payee must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            if(!errors.containsMessageKey(KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE)) {
                errors.putErrorWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE);
            }
            allow = false;
        }

        if (allow) {
            LOG.debug("beginning object code validation ");
            allow = validateObjectCode(transactionalDocument, accountingLine);

            LOG.debug("beginning account number validation ");
            allow = allow & validateAccountNumber(transactionalDocument, accountingLine);
        }

        LOG.debug("end validating accounting line, has errors: " + allow);

        return allow;
    }

    /**
     * Final business rule edits on routing of disbursement voucher document.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        DisbursementVoucherPayeeDetail payeeDetail = dvDocument.getDvPayeeDetail();

        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.DOCUMENT);

        LOG.debug("processing route rules for document " + document.getDocumentNumber());

        validateDocumentFields(dvDocument);

        LOG.debug("validating payment reason");
        validatePaymentReason(dvDocument);

        LOG.debug("validating payee initiator id");
        validatePayeeInitiatorID(dvDocument);

        if (payeeDetail.isPayee()) {
            LOG.debug("validating payee information");
            validatePayeeInformation(dvDocument);
        }

        if (payeeDetail.isEmployee()) {
            LOG.debug("validating employee information");
            validateEmployeeInformation(dvDocument);
        }

        /* specific validation depending on payment method */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("validating wire transfer");
            validateWireTransfer(dvDocument);
        }
        else if (PAYMENT_METHOD_DRAFT.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("validating foreign draft");
            validateForeignDraft(dvDocument);
        }

        /* if nra payment and user is in tax group, check nra tab */
        if (dvDocument.getDvPayeeDetail().isDisbVchrAlienPaymentCode() && isUserInTaxGroup()) {
            LOG.debug("validating non resident alien tax");
            validateNonResidentAlienInformation(dvDocument);
        }

        // non-employee travel

        // retrieve nonemployee travel payment reasons
        String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);
        if (RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            LOG.debug("validating non employee travel");
            validateNonEmployeeTravel(dvDocument);
        }

        // pre-paid travel

        // retrieve prepaid travel payment reasons
        String[] travelPrepaidPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, PREPAID_TRAVEL_PAY_REASONS_PARM_NM);
        if (RulesUtils.makeSet(travelPrepaidPaymentReasonCodes).contains(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            LOG.debug("validating pre paid travel");
            validatePrePaidTravel(dvDocument);
        }

        LOG.debug("validating document amounts");
        validateDocumentAmounts(dvDocument);

        LOG.debug("validating accounting line counts");
        validateAccountingLineCounts(dvDocument);

        LOG.debug("validating documentaton location");
        validateDocumentationLocation(dvDocument);

        GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.DOCUMENT);

        LOG.debug("finished route validation for document, has errors: " + !GlobalVariables.getErrorMap().isEmpty());

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * Override to change the doc type based on payment method. This is needed to pick up different offset definitions.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;

        /* change document type based on payment method to pick up different offsets */
        if (PAYMENT_METHOD_CHECK.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_CHECKACH);
        }
        else {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_WTFD);
        }
    }


    /**
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(FinancialDocument financialDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) financialDocument;
        if (dvDocument.getGeneralLedgerPendingEntries() == null || dvDocument.getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
            // throw new RuntimeException("No gl entries for accounting lines.");
        }

        /*
         * only generate additonal charge entries for payment method wire charge, and if the fee has not been waived
         */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode()) && !dvDocument.getDvWireTransfer().isDisbursementVoucherWireTransferFeeWaiverIndicator()) {
            LOG.debug("generating wire charge gl pending entries.");

            // retrieve wire charge
            WireCharge wireCharge = retrieveWireCharge();

            // generate debits
            GeneralLedgerPendingEntry chargeEntry = processWireChargeDebitEntries(dvDocument, sequenceHelper, wireCharge);

            // generate credits
            processWireChargeCreditEntries(dvDocument, sequenceHelper, wireCharge, chargeEntry);
        }

        return true;
    }

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     * 
     * @param dvDocument
     * @param sequenceHelper
     * @param wireCharge
     * @return GeneralLedgerPendingEntry
     */
    private GeneralLedgerPendingEntry processWireChargeDebitEntries(DisbursementVoucherDocument dvDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge) {

        // increment the sequence counter
        sequenceHelper.increment();

        // grab the explicit entry for the first accounting line and adjust for wire charge entry
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(dvDocument.getGeneralLedgerPendingEntry(0));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setFinancialObjectCode(wireCharge.getExpenseFinancialObjectCode());
        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        explicitEntry.setFinancialObjectTypeCode(SpringServiceLocator.getOptionsService().getCurrentYearOptions().getFinObjTypeExpenditureexpCd());
        explicitEntry.setTransactionDebitCreditCode(GL_DEBIT_CODE);

        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(dvDocument.getDvWireTransfer().getDisbVchrBankCountryCode())) {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getDomesticChargeAmt());
        }
        else {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getForeignChargeAmt());
        }

        explicitEntry.setTransactionLedgerEntryDescription("Automatic debit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        dvDocument.getGeneralLedgerPendingEntries().add(offsetEntry);

        return explicitEntry;
    }

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     * 
     * @param dvDocument
     * @param sequenceHelper
     * @param chargeEntry
     * @param wireCharge
     */
    private void processWireChargeCreditEntries(DisbursementVoucherDocument dvDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry) {

        // increment the sequence counter
        sequenceHelper.increment();

        // copy the charge entry and adjust for credit
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(chargeEntry);
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        explicitEntry.setAccountNumber(wireCharge.getAccountNumber());
        explicitEntry.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());

        // retrieve object type
        ObjectCode objectCode = new ObjectCode();
        objectCode.setUniversityFiscalYear(explicitEntry.getUniversityFiscalYear());
        objectCode.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        objectCode.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());
        objectCode = (ObjectCode) SpringServiceLocator.getBusinessObjectService().retrieve(objectCode);

        explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        explicitEntry.setTransactionDebitCreditCode(GL_CREDIT_CODE);

        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        explicitEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER);
        explicitEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING);

        explicitEntry.setTransactionLedgerEntryDescription("Automatic credit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        dvDocument.getGeneralLedgerPendingEntries().add(offsetEntry);
    }


    /**
     * Validates conditional required fields. Note fields that are always required are validated by the dictionary framework.
     * 
     * @param document
     */
    private void validateDocumentFields(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        // validate document required fields, and payee fields, and formatting
        SpringServiceLocator.getDictionaryValidationService().validateDocument(document);
        errors.addToErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
        if (!errors.isEmpty()) {
            return;
        }

        /* remit name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrRemitPersonName()) || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeLine1Addr())) {
                errors.putErrorWithoutFullErrorPath(Constants.GENERAL_SPECHAND_TAB_ERRORS, KeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode()) && hasNoNotes(document)) {
            errors.putError(PropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE, KeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING);
        }

        /* if special handling indicated, must be a note exlaining why */
        if (document.isDisbVchrSpecialHandlingCode() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(Constants.GENERAL_PAYMENT_TAB_ERRORS, KeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING);
        }

        /* if exception attached indicated, must be a note exlaining why */
        if (document.isExceptionIndicator() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(Constants.GENERAL_PAYMENT_TAB_ERRORS, KeyConstants.ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING);
        }

        /* city, state & zip must be given for us */
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCityName())) {
                errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_CITY_NAME, KeyConstants.ERROR_DV_PAYEE_CITY_NAME);
            }
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeStateCode())) {
                errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_STATE_CODE, KeyConstants.ERROR_DV_PAYEE_STATE_CODE);
            }
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeZipCode())) {
                errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE, KeyConstants.ERROR_DV_PAYEE_ZIP_CODE);
            }
        }

        /* country required except for employee payees */
        if (!document.getDvPayeeDetail().isEmployee() && StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE, KeyConstants.ERROR_REQUIRED, "Payee Country ");
        }
    }

    /**
     * @param document
     * @return whether the given document has no notes
     */
    private static boolean hasNoNotes(DisbursementVoucherDocument document) {
        return (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0);
    }

    /**
     * Validates wire transfer tab information
     * 
     * @param document
     */
    private void validateWireTransfer(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_WIRE_TRANSFER);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(document.getDvWireTransfer());

        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvWireTransfer().getDisbVchrBankCountryCode()) && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.putError(PropertyConstants.DISB_VCHR_BANK_ROUTING_NUMBER, KeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvWireTransfer().getDisbVchrBankCountryCode()) && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.putError(PropertyConstants.DISB_VCHR_BANK_STATE_CODE, KeyConstants.ERROR_REQUIRED, "Bank State");
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.putErrorWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DISB_VCHR_ATTACHMENT_CODE, KeyConstants.ERROR_DV_WIRE_ATTACHMENT);
        }

        errors.removeFromErrorPath(PropertyConstants.DV_WIRE_TRANSFER);
    }

    /**
     * Validates foreign draft tab information
     * 
     * @param document
     */
    private void validateForeignDraft(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(PropertyConstants.DV_WIRE_TRANSFER);

        /* currency type code required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherForeignCurrencyTypeCode())) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_CODE, KeyConstants.ERROR_DV_CURRENCY_TYPE_CODE);
        }

        /* currency type name required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherForeignCurrencyTypeName())) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_NAME, KeyConstants.ERROR_DV_CURRENCY_TYPE_NAME);
        }

        errors.removeFromErrorPath(PropertyConstants.DV_WIRE_TRANSFER);
    }

    /**
     * Validates fields for an alien payment.
     * 
     * @param document
     */
    public void validateNonResidentAlienInformation(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);

        /* income class code required */
        if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getIncomeClassCode())) {
            errors.putError(PropertyConstants.INCOME_CLASS_CODE, KeyConstants.ERROR_REQUIRED, "Income class code ");
        }
        else {
            /* for foreign source or treaty exempt, non reportable, tax percents must be 0 and gross indicator can not be checked */
            if (document.getDvNonResidentAlienTax().isForeignSourceIncomeCode() || document.getDvNonResidentAlienTax().isIncomeTaxTreatyExemptCode() || NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {

                if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null && !(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())))) {
                    errors.putError(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null && !(new KualiDecimal(0).equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())))) {
                    errors.putError(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_STATE_TAX_NOT_ZERO);
                }

                if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
                    errors.putError(PropertyConstants.INCOME_TAX_GROSS_UP_CODE, KeyConstants.ERROR_DV_GROSS_UP_INDICATOR);
                }

                if (NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode()) && StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.putError(PropertyConstants.POSTAL_COUNTRY_CODE, KeyConstants.ERROR_DV_POSTAL_COUNTRY_CODE);
                }
            }
            else {
                if (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() == null) {
                    errors.putError(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_REQUIRED, "Federal tax percent ");
                }
                else {
                    // check tax percent is in nra tax pct table for income class code
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT, new String[] { document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().toString(), document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() == null) {
                    errors.putError(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_REQUIRED, "State tax percent ");
                }
                else {
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT, new String[] { document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().toString(), document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }
            }
        }

        /* country code required, unless income type is nonreportable */
        if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getPostalCountryCode()) && !NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {
            errors.putError(PropertyConstants.POSTAL_COUNTRY_CODE, KeyConstants.ERROR_REQUIRED, "Country code ");
        }

        errors.removeFromErrorPath(PropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);
    }

    /**
     * Validates non employee travel information.
     * 
     * @param document
     */
    private void validateNonEmployeeTravel(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObjectsRecursively(document.getDvNonEmployeeTravel(), 1);

        /* travel from and to state required if country is us */
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDvTravelFromCountryCode()) && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelFromStateCode())) {
            errors.putError(PropertyConstants.DISB_VCHR_TRAVEL_FROM_STATE_CODE, KeyConstants.ERROR_DV_TRAVEL_FROM_STATE);
        }
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDisbVchrTravelToCountryCode()) && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelToStateCode())) {
            errors.putError(PropertyConstants.DISB_VCHR_TRAVEL_TO_STATE_CODE, KeyConstants.ERROR_DV_TRAVEL_TO_STATE);
        }

        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(PropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
            return;
        }

        /* must fill in all required per diem fields if any field is filled in */
        boolean perDiemSectionComplete = validatePerDiemSection(document, errors);

        /* must fill in all required personal vehicle fields if any field is filled in */
        boolean personalVehicleSectionComplete = validatePersonalVehicleSection(document, errors);

        /* must have per diem change message if actual amount is different from calculated amount */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            if (document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt().compareTo(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount()) != 0 && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDvPerdiemChangeReasonText())) {
                errors.putError(PropertyConstants.DV_PERDIEM_CHANGE_REASON_TEXT, KeyConstants.ERROR_DV_PERDIEM_CHANGE_REQUIRED);
            }
        }

        /* make sure per diem fields have not changed since the per diem amount calculation */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            KualiDecimal calculatedPerDiem = SpringServiceLocator.getDisbursementVoucherTravelService().calculatePerDiemAmount(document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), document.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());
            if (calculatedPerDiem.compareTo(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) != 0) {
                errors.putErrorWithoutFullErrorPath(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_DV_PER_DIEM_CALC_CHANGE);
            }
        }

        /* total on nonemployee travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringServiceLocator.getDisbursementVoucherTaxService().getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvNonEmployeeTravel().getTotalTravelAmount()) != 0) {
            errors.putErrorWithoutFullErrorPath(Constants.DV_CHECK_TRAVEL_TOTAL_ERROR, KeyConstants.ERROR_DV_TRAVEL_CHECK_TOTAL);
        }

        /* make sure mileage fields have not changed since the mileage amount calculation */
        if (personalVehicleSectionComplete) {
            KualiDecimal currentCalcAmt = document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt();
            KualiDecimal currentActualAmt = document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount();
            if (ObjectUtils.isNotNull(currentCalcAmt) && ObjectUtils.isNotNull(currentActualAmt)) {
                KualiDecimal calculatedMileageAmount = SpringServiceLocator.getDisbursementVoucherTravelService().calculateMileageAmount(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());
                if (calculatedMileageAmount.compareTo(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) != 0) {
                    errors.putErrorWithoutFullErrorPath(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_DV_MILEAGE_CALC_CHANGE);
                }

                // determine if the rule is flagged off in the parm setting
                boolean performTravelMileageLimitInd = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator(DV_DOCUMENT_PARAMETERS_GROUP_NM, NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM);
                if (performTravelMileageLimitInd) {
                    // if actual amount is greater than calculated amount
                    if (currentCalcAmt.subtract(currentActualAmt).isNegative()) {
                        errors.putError(PropertyConstants.DV_PERSONAL_CAR_AMOUNT, KeyConstants.ERROR_DV_ACTUAL_MILEAGE_TOO_HIGH);
                    }
                }
            }
        }

        errors.removeFromErrorPath(PropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
    }

    /**
     * 
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document
     * @param errors
     * @return Returns true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePerDiemSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean perDiemSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean perDiemUsed = StringUtils.isNotBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount());

        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (perDiemUsed) {
            if (StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName())) {
                errors.putError(PropertyConstants.DISB_VCHR_PERDIEM_CATEGORY_NAME, KeyConstants.ERROR_DV_PER_DIEM_CATEGORY);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate())) {
                errors.putError(PropertyConstants.DISB_VCHR_PERDIEM_RATE, KeyConstants.ERROR_DV_PER_DIEM_RATE);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt())) {
                errors.putError(PropertyConstants.DISB_VCHR_PERDIEM_CALCULATED_AMT, KeyConstants.ERROR_DV_PER_DIEM_CALC_AMT);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount())) {
                errors.putError(PropertyConstants.DISB_VCHR_PERDIEM_ACTUAL_AMOUNT, KeyConstants.ERROR_DV_PER_DIEM_ACTUAL_AMT);
                perDiemSectionComplete = false;
            }
        }
        perDiemSectionComplete = perDiemSectionComplete && perDiemUsed;
        return perDiemSectionComplete;
    }

    /**
     * 
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document
     * @param errors
     * @return Returns true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePersonalVehicleSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean personalVehicleSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean personalVehilcleUsed = ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount());


        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (personalVehilcleUsed) {
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName())) {
                errors.putError(PropertyConstants.DISB_VCHR_AUTO_FROM_CITY_NAME, KeyConstants.ERROR_DV_AUTO_FROM_CITY);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName())) {
                errors.putError(PropertyConstants.DISB_VCHR_AUTO_TO_CITY_NAME, KeyConstants.ERROR_DV_AUTO_TO_CITY);
                personalVehicleSectionComplete = false;
            }

            // are state fields required always or only for US travel?
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode())) {
                errors.putError(PropertyConstants.DISB_VCHR_AUTO_FROM_STATE_CODE, KeyConstants.ERROR_DV_AUTO_FROM_STATE);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode())) {
                errors.putError(PropertyConstants.DISB_VCHR_AUTO_TO_STATE_CODE, KeyConstants.ERROR_DV_AUTO_TO_STATE);
                personalVehicleSectionComplete = false;
            }
            // end state field validation


            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount())) {
                errors.putError(PropertyConstants.DV_PERSONAL_CAR_MILEAGE_AMOUNT, KeyConstants.ERROR_DV_MILEAGE_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt())) {
                errors.putError(PropertyConstants.DISB_VCHR_MILEAGE_CALCULATED_AMT, KeyConstants.ERROR_DV_MILEAGE_CALC_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount())) {
                errors.putError(PropertyConstants.DISB_VCHR_PERSONAL_CAR_AMOUNT, KeyConstants.ERROR_DV_MILEAGE_ACTUAL_AMT);
                personalVehicleSectionComplete = false;
            }
        }
        personalVehicleSectionComplete = personalVehicleSectionComplete && personalVehilcleUsed;
        return personalVehicleSectionComplete;
    }


    /**
     * Validates pre paid travel information.
     * 
     * @param document
     */
    private void validatePrePaidTravel(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObjectsRecursively(document.getDvPreConferenceDetail(), 1);
        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
            return;
        }

        /* check conference end date is not before conference start date */
        if (document.getDvPreConferenceDetail().getDisbVchrConferenceEndDate().compareTo(document.getDvPreConferenceDetail().getDisbVchrConferenceStartDate()) < 0) {
            errors.putError(PropertyConstants.DISB_VCHR_CONFERENCE_END_DATE, KeyConstants.ERROR_DV_CONF_END_DATE);
        }

        /* total on prepaid travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringServiceLocator.getDisbursementVoucherTaxService().getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvPreConferenceDetail().getDisbVchrConferenceTotalAmt()) != 0) {
            errors.putErrorWithoutFullErrorPath(Constants.GENERAL_PREPAID_TAB_ERRORS, KeyConstants.ERROR_DV_PREPAID_CHECK_TOTAL);
        }

        errors.removeFromErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
    }


    /**
     * Validates whether there is a error based on payment reason rules, and adds an error message if it does not exist
     * 
     * Note: This method is pretty much a copy of
     * org.kuali.core.rule.DocumentRuleBase.executeApplicationParameterRestriction(String, String, String, String, String), except
     * that a different error message is used. I'd like to be able to call the original method, and just replace the error value,
     * but it's not possible to do so because ErrorMap does not support replacing only one of the errors with a given errorKey (aka
     * targetKey), i.e. it's an all or nothing operation.
     * 
     * @param parameterGroupName
     * @param parameterName
     * @param restrictedFieldValue
     * @param errorField
     * @param errorParameter
     * @return
     */
    private boolean executePaymentReasonRestriction(String parameterGroupName, String parameterName, String restrictedFieldValue, String errorField, String errorParameter, String paymentReasonCode, CodeDescriptionFormatter restrictedFieldDescFormatter) {
        boolean rulePassed = true;
        if (SpringServiceLocator.getKualiConfigurationService().hasApplicationParameterRule(parameterGroupName, parameterName)) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(parameterGroupName, parameterName);
            if (rule.failsRule(restrictedFieldValue)) {
                String errorMsgKey = null;
                String endConjunction = null;
                if (rule.isAllowedRule()) {
                    errorMsgKey = KeyConstants.ERROR_PAYMENT_REASON_ALLOWED_RESTRICTION;
                    endConjunction = "or";
                }
                else {
                    errorMsgKey = KeyConstants.ERROR_PAYMENT_REASON_DENIED_RESTRICTION;
                    endConjunction = "and";
                }

                Map<String, String> paymentReasonParams = new HashMap<String, String>();
                paymentReasonParams.put("code", paymentReasonCode);
                // TODO: should we not ignore active flag?
                Collection<PaymentReasonCode> paymentReasons = SpringServiceLocator.getKeyValuesNonCachedService().findMatching(PaymentReasonCode.class, paymentReasonParams);

                String prName = "(Description unavailable)";

                // find max version #
                Long prMaxVer = null;
                for (PaymentReasonCode currPrc : paymentReasons) {
                    if (prMaxVer == null || prMaxVer.compareTo(currPrc.getVersionNumber()) < 0) {
                        prName = currPrc.getName();
                        prMaxVer = currPrc.getVersionNumber();
                    }
                }

                GlobalVariables.getErrorMap().putError(errorField, errorMsgKey, new String[] { errorParameter, restrictedFieldValue, parameterName, parameterGroupName, rule.getPrettyParameterValueString(), "Payment reason", paymentReasonCode, prName, restrictedFieldDescFormatter.getFormattedStringWithDescriptions(rule.getParameterValueSet(), null, endConjunction) });
                rulePassed = false;
            }
        }
        else {
            LOG.warn("Did not find apc parameter record for group " + parameterGroupName + " with parm name " + parameterName);
        }
        return rulePassed;
    }

    /**
     * Validates the selected documentation location field.
     * 
     * @param document
     */
    private void validateDocumentationLocation(DisbursementVoucherDocument document) {
        String errorKey = PropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE;

        // payment reason restrictions
        executePaymentReasonRestriction(PAYMENT_DOC_LOCATION_GROUP_NM, PAYMENT_PARM_PREFIX + document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), document.getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location", document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), new DocumentationLocationCodeDescriptionFormatter());

        // alien indicator restrictions
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            executeApplicationParameterRestriction(ALIEN_INDICATOR_DOC_LOCATION_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM, document.getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
        }

        // initiator campus code restrictions
        String initiatorCampusCode = ((ChartUser)getInitiator(document).getModuleUser( ChartUser.MODULE_ID )).getOrganization().getOrganizationPhysicalCampusCode();
        executeApplicationParameterRestriction(CAMPUS_DOC_LOCATION_GROUP_NM, CAMPUS_CODE_PARM_PREFIX + initiatorCampusCode, document.getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
    }

    /**
     * Validates the payment reason is valid with the other document attributes.
     * 
     * @param document
     */
    public void validatePaymentReason(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();
        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        String errorKey = PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE;

        // restrictions on payment reason when alien indicator is checked
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            executeApplicationParameterRestriction(ALIEN_INDICATOR_PAYMENT_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM, paymentReasonCode, errorKey, "Payment reason");
        }

        /* check revolving fund restrictions */
        // retrieve revolving fund payment reasons
        String[] revolvingFundPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, REVOLVING_FUND_PAY_REASONS_PARM_NM);

        if (RulesUtils.makeSet(revolvingFundPaymentReasonCodes).contains(paymentReasonCode) && !document.getDvPayeeDetail().isDvPayeeRevolvingFundCode() && !document.getDvPayeeDetail().isVendor()) {
            errors.putError(errorKey, KeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, paymentReasonCode);
        }

        /* if payment reason is moving, payee must be an employee or have payee ownership type I (individual) */
        String[] movingPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, MOVING_PAY_REASONS_PARM_NM);

        if (RulesUtils.makeSet(movingPaymentReasonCodes).contains(document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            if (!document.getDvPayeeDetail().isEmployee()) {
                boolean invalidMovingPayee = false;
                if (document.getDvPayeeDetail().isPayee()) {
                    Payee payee = retrievePayee(document.getDvPayeeDetail().getDisbVchrPayeeIdNumber());
                    // payee must be an individual
                    if (!OWNERSHIP_TYPE_INDIVIDUAL.equals(payee.getPayeeOwnershipTypCd())) {
                        invalidMovingPayee = true;
                    }
                }
                else {
                    // vendors cannot be paid for moving
                    invalidMovingPayee = true;
                }

                if (invalidMovingPayee) {
                    errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_DV_MOVING_PAYMENT_PAYEE);
                }
            }
        }


        /* for research payments over a certain limit the payee must be a vendor */
        String[] researchPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, RESEARCH_PAY_REASONS_PARM_NM);

        KualiParameterRule researchPayRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(DV_DOCUMENT_PARAMETERS_GROUP_NM, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM);
        String researchPayLimit = researchPayRule.getParameterText();
        KualiDecimal payLimit = new KualiDecimal(researchPayLimit);

        // check rule is active
        if (researchPayRule.isUsable()) {
            if (RulesUtils.makeSet(researchPaymentReasonCodes).contains(document.getDvPayeeDetail().getDisbVchrPaymentReasonCode()) && document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit) && !document.getDvPayeeDetail().isVendor()) {
                errors.putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, payLimit.toString());
            }
        }
    }

    /**
     * Validates that the payee is not the initiator.
     * 
     * @param document
     */
    public void validatePayeeInitiatorID(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        String uuid = "";
        if (payeeDetail.isPayee() && StringUtils.isNotBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            Payee dvPayee = retrievePayee(payeeDetail.getDisbVchrPayeeIdNumber());
            // if the payee tax type is SSN, then check the tax number
            if (dvPayee != null && TAX_TYPE_SSN.equals(dvPayee.getTaxpayerTypeCode())) {
                // check ssn against employee table
                UniversalUser user = new UniversalUser();
                user.setPersonSocialSecurityNbrId(dvPayee.getTaxIdNumber());
                user = (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(user);
                if (user != null) {
                    uuid = user.getPersonUniversalIdentifier();
                }
            }
        }
        else if (payeeDetail.isEmployee()) {
            // payee id number is uuid
            uuid = payeeDetail.getDisbVchrPayeeIdNumber();
        }

        // if a uuid was found, check it against the initiator uuid
        if (StringUtils.isNotBlank(uuid)) {
            UniversalUser initUser = getInitiator(document);
            if (uuid.equals(initUser.getPersonUniversalIdentifier())) {
                GlobalVariables.getErrorMap().putError(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_PAYEE_INITIATOR);
            }
        }
    }

    /**
     * Validate attributes of the payee for the document.
     * 
     * @param document
     */
    public void validatePayeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return;
        }

        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_PAYEE_DETAIL);

        /* Retrieve Payee */
        Payee dvPayee = retrievePayee(payeeDetail.getDisbVchrPayeeIdNumber());
        if (dvPayee == null) {
            errors.putError(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EXISTENCE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* DV Payee must be active */
        if (!dvPayee.isPayeeActiveCode()) {
            errors.putError(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_INACTIVE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* check payment reason is allowed for payee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, DVPAYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        /* for payees with tax type ssn, check employee restrictions */
        if (TAX_TYPE_SSN.equals(dvPayee.getTaxpayerTypeCode())) {
            if (isActiveEmployeeSSN(dvPayee.getTaxIdNumber()) || true) {
                // determine if the rule is flagged off in the parm setting
                boolean performPrepaidEmployeeInd = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator(DV_DOCUMENT_PARAMETERS_GROUP_NM, PERFORM_PREPAID_EMPL_PARM_NM);

                if (performPrepaidEmployeeInd) {
                    /* active payee employees cannot be paid for prepaid travel */
                    String[] travelPrepaidPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, PREPAID_TRAVEL_PAY_REASONS_PARM_NM);
                    if (RulesUtils.makeSet(travelPrepaidPaymentReasonCodes).contains(payeeDetail.getDisbVchrPaymentReasonCode())) {
                        errors.putError(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_ACTIVE_EMPLOYEE_PREPAID_TRAVEL);
                    }

                }
            }
            else if (isEmployeeSSN(dvPayee.getTaxIdNumber())) {
                // check parm setting for paid outside payroll check
                boolean performPaidOutsidePayrollInd = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator(DV_DOCUMENT_PARAMETERS_GROUP_NM, PERFORM_PREPAID_EMPL_PARM_NM);

                if (performPaidOutsidePayrollInd) {
                    /* If payee is type payee and employee, payee record must be flagged as paid outside of payroll */
                    if (!dvPayee.isPayeeEmployeeCode()) {
                        errors.putError(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EMPLOYEE_PAID_OUTSIDE_PAYROLL);
                    }
                }
            }
        }

        errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
    }

    /**
     * Validate attributes of an employee payee for the document.
     * 
     * @param document
     */
    public void validateEmployeeInformation(DisbursementVoucherDocument document) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        if (StringUtils.isBlank(payeeDetail.getDisbVchrPayeeIdNumber())) {
            return;
        }

        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_PAYEE_DETAIL);

        /* check existence of employee */
        UniversalUser employee = retrieveEmployee(payeeDetail.getDisbVchrPayeeIdNumber());
        if (employee == null) {
            errors.putError(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EXISTENCE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* check payment reason is allowed for employee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, EMPLOYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
    }

    /**
     * 
     * This method...
     * 
     * @param document
     */
    private void validateAccountingLineCounts(DisbursementVoucherDocument dvDocument) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        if (dvDocument.getSourceAccountingLines().size() < 1) {
            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NO_ACCOUNTING_LINES);
        }
    }

    /**
     * Checks the amounts on the document for reconciliation.
     * 
     * @param document
     */
    public void validateDocumentAmounts(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check total cannot be negative or zero */
        if (!document.getDisbVchrCheckTotalAmount().isPositive()) {
            errors.putError(PropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KeyConstants.ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL);
        }

        /* total accounting lines cannot be negative */
        if (Constants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.putErrorWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
        }
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.FINANCIAL_OBJECT_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(errorKey, KeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* check object type global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectTypeCode(), errorKey, "Object type");

        /* check object sub type global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectSubTypeCode(), errorKey, "Object sub type");

        /* check object level global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectLevelCode(), errorKey, "Object level");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && executePaymentReasonRestriction(PAYMENT_OBJECT_LEVEL_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode(), errorKey, "Object level", documentPaymentReason, new ObjectLevelCodeDescriptionFormatter(accountingLine.getChartOfAccountsCode()));

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed && executePaymentReasonRestriction(PAYMENT_OBJECT_CODE_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason, accountingLine.getFinancialObjectCode(), errorKey, "Object code", documentPaymentReason, new ObjectCodeDescriptionFormatter(accountingLine.getPostingYear(), accountingLine.getChartOfAccountsCode()));

        /* check payment reason is valid for object code */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(OBJECT_CODE_PAYMENT_GROUP_NM, OBJECT_CODE_PARM_PREFIX + accountingLine.getFinancialObjectCode(), dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        /* check payment reason is valid for object level */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(OBJECT_LEVEL_PAYMENT_GROUP_NM, OBJECT_LEVEL_PARM_PREFIX + accountingLine.getObjectCode().getFinancialObjectLevelCode(), dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateAccountNumber(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        /* account exist and object exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* global sub fund restrictions */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, SUB_FUND_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund code");

        /* global function code restrictions */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd(), errorKey, "Function code");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return accountNumberAllowed;
        }

        /* check sub fund is in permitted list for payment reason */
        accountNumberAllowed = accountNumberAllowed && executePaymentReasonRestriction(PAYMENT_SUB_FUND_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason, accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund", documentPaymentReason, new SubFundGroupCodeDescriptionFormatter());

        /* check object sub type is allowed for sub fund code */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(SUB_FUND_OBJECT_SUB_TYPE_GROUP_NM, SUB_FUND_CODE_PARM_PREFIX + accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode().getFinancialObjectSubTypeCode(), errorKey, "Object sub type");

        return accountNumberAllowed;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents do not have to balance in order to be submitted
     * for routing.
     * 
     * @param transactionalDocument
     * @return boolean True if the balance of the document is valid, false other wise.
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Override to check for tax accounting lines. These lines can have negative amounts which the super will reject.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        if (((DisbursementVoucherDocument) document).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringServiceLocator.getDisbursementVoucherTaxService().getNRATaxLineNumbers(((DisbursementVoucherDocument) document).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
            if (taxLineNumbers.contains(accountingLine.getSequenceNumber())) {
                return true;
            }
        }
        return super.isAmountValid(document, accountingLine);
    }


    /**
     * Checks if the current user is a member of the dv tax workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTaxGroup() {
        if ( taxGroupName == null ) {
            taxGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_TAX_WORKGROUP );
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember( taxGroupName );
    }

    /**
     * Checks if the current user is a member of the dv travel workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTravelGroup() {
        if ( travelGroupName == null ) {
            travelGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_TRAVEL_WORKGROUP );
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember( travelGroupName );
    }

    /**
     * Checks if the current user is a member of the dv frn workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInFRNGroup() {
        if ( frnGroupName == null ) {
            frnGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_FOREIGNDRAFT_WORKGROUP );
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember( frnGroupName );
    }

    /**
     * Checks if the current user is a member of the dv wire workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInWireGroup() {
        if ( wireTransferGroupName == null ) {
            wireTransferGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_WIRETRANSFER_WORKGROUP );
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember( wireTransferGroupName );
    }

    /**
     * This method checks to see whether the user is in the dv admin group or not.
     * 
     * @return true if user is in group, false otherwise
     */
    private boolean isUserInDvAdminGroup() {
        if ( adminGroupName == null ) {
            adminGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_ADMIN_WORKGROUP );
        }
        return GlobalVariables.getUserSession().getUniversalUser().isMember( adminGroupName );
    }

    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document
     * @return <code>KualiUser</code>
     */
    private UniversalUser getInitiator(TransactionalDocument document) {
        UniversalUser initUser = null;
        try {

            initUser = SpringServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
        }

        return initUser;
    }

    /**
     * Retrieves the wire transfer information for the current fiscal year.
     * 
     * @return <code>WireCharge</code>
     */
    private WireCharge retrieveWireCharge() {
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringServiceLocator.getDateTimeService().getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringServiceLocator.getBusinessObjectService().retrieve(wireCharge);
        if (wireCharge == null) {
            LOG.error("Wire charge information not found for current fiscal year.");
            throw new RuntimeException("Wire charge information not found for current fiscal year.");
        }

        return wireCharge;
    }

    /**
     * Retrieves the Payee object from the payee id number.
     * 
     * @param payeeIdNumber
     * @return <code>Payee</code>
     */
    private Payee retrievePayee(String payeeIdNumber) {
        Payee payee = new Payee();
        payee.setPayeeIdNumber(payeeIdNumber);
        return (Payee) SpringServiceLocator.getBusinessObjectService().retrieve(payee);
    }

    /**
     * Retrieves the UniversalUser object from the uuid.
     * 
     * @param uuid
     * @return <code>UniversalUser</code>
     */
    private UniversalUser retrieveEmployee(String uuid) {
        UniversalUser employee = new UniversalUser();
        employee.setPersonUniversalIdentifier(uuid);
        return (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber
     * @return true if the ssn number is a valid employee ssn
     */
    private boolean isEmployeeSSN(String ssnNumber) {
        boolean isEmployee = false;

        UniversalUser employee = new UniversalUser();
        employee.setPersonSocialSecurityNbrId(ssnNumber);
        UniversalUser foundEmployee = (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);

        if (foundEmployee != null) {
            isEmployee = true;
        }

        return isEmployee;
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
     * 
     * @param ssnNumber
     * @return true if the ssn number is a valid employee ssn and the employee is active
     */
    private boolean isActiveEmployeeSSN(String ssnNumber) {
        boolean isActiveEmployee = false;

        UniversalUser employee = new UniversalUser();
        employee.setPersonSocialSecurityNbrId(ssnNumber);
        UniversalUser foundEmployee = (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);

        if (foundEmployee != null && Constants.EMPLOYEE_ACTIVE_STATUS.equals(foundEmployee.getEmployeeStatusCode())) {
            isActiveEmployee = true;
        }

        return isActiveEmployee;
    }

    /**
     * 
     * checks the status of the document to see if the coversheet is printable
     * 
     * @param document
     * @return boolean
     */

    public boolean isCoverSheetPrintable(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());

    }

    /**
     * error corrections are not allowed
     * 
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // disallow error corrections
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        if (transactionalDocument instanceof DisbursementVoucherDocument) {
            // special case - dv NRA tax accounts can be negative, and are debits if positive
            DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) transactionalDocument;

            if (dvDoc.getDvNonResidentAlienTax() != null && dvDoc.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText() != null && dvDoc.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText().contains(accountingLine.getSequenceNumber().toString())) {
                return accountingLine.getAmount().isPositive();
            }
        }

        return IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, transactionalDocument, accountingLine);
    }

}