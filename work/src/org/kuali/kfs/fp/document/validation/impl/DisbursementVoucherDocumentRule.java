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
package org.kuali.module.financial.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.NonResidentAlienTaxPercent;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.document.DisbursementVoucherDocumentAuthorizer;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;


/**
 * Business rule(s) applicable to Disbursement Voucher documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentRule extends TransactionalDocumentRuleBase implements DisbursementVoucherRuleConstants,
        GenerateGeneralLedgerDocumentPendingEntriesRule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentRule.class);

    /**
     * Constructs a DisbursementVoucherDocumentRule instance.
     */
    public DisbursementVoucherDocumentRule() {
    }
    
    /**
     * Overrides to call super.  If super fails, then we invoke some DV specific rules about FO routing to double check 
     * if the individual has special conditions that they can alter accounting lines by.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, java.lang.String)
     */
    protected boolean checkAccountingLineAccountAccessibility(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, String errorKey) {
        // first check parent's isAccessible method for basic FO authz checking
        boolean isAccessible = accountIsAccessible(transactionalDocument, accountingLine);
        
        // get the authorizer class to check for special conditions routing and if the user is part of a particular workgroup
        // but only if the document is enroute
        if(!isAccessible && transactionalDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringServiceLocator
                    .getDocumentAuthorizationService().getDocumentAuthorizer(transactionalDocument);
            // if approval is requested and it is special conditions routing and the user is in a special conditions routing workgroup then
            // the line is accessible
            if (transactionalDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested() && 
                    dvAuthorizer.isSpecialRouting(transactionalDocument, GlobalVariables.getUserSession().getKualiUser()) && 
                    (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup() || isUserInDvAdminGroup())) {
                isAccessible = true;
            }
        }
        
        // report (and log) errors
        if (!isAccessible) {
            String[] errorParams = new String[] { accountingLine.getAccountNumber(),
                    GlobalVariables.getUserSession().getKualiUser().getPersonUserIdentifier() };
            GlobalVariables.getErrorMap().put(Constants.ACCOUNT_NUMBER_PROPERTY_NAME, errorKey, errorParams);
        }
        
        return isAccessible;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    protected boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, updatedAccountingLine);
    }

    /**
     * Override to check if we are in special handling where the check amount and accounting line total can decrease, else amounts
     * should not have changed.
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomApproveDocumentBusinessRules(Document document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;

        // amounts can only decrease
        DisbursementVoucherDocumentAuthorizer dvAuthorizer = (DisbursementVoucherDocumentAuthorizer) SpringServiceLocator
                .getDocumentAuthorizationService().getDocumentAuthorizer(document);
        if (dvAuthorizer.isSpecialRouting(document, GlobalVariables.getUserSession().getKualiUser())
                && (isUserInTaxGroup() || isUserInTravelGroup() || isUserInFRNGroup() || isUserInWireGroup())) {
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
                        GlobalVariables.getErrorMap().put(
                                PropertyConstants.DOCUMENT + "." + PropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT,
                                KeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
                        approveOK = false;
                    }
                }
            }

            return approveOK;
        }
        else {
            // amounts must not have been changed
            return super.processCustomApproveDocumentBusinessRules(document);
        }
    }
    
    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine)
     */
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine) {
        boolean allow = true;

        LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

        // don't validate generated tax lines
        if (((DisbursementVoucherDocument) transactionalDocument).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringServiceLocator.getDisbursementVoucherTaxService().getNRATaxLineNumbers(
                    ((DisbursementVoucherDocument) transactionalDocument).getDvNonResidentAlienTax()
                            .getFinancialDocumentAccountingLineText());
            if (taxLineNumbers.contains(accountingLine.getSequenceNumber())) {
                return true;
            }
        }

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* payment reason must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            errors.putWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DV_PAYEE_DETAIL + "."
                    + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON);
            allow = false;
        }

        /* payee must be selected before an accounting line can be entered */
        if (StringUtils.isBlank(dvDocument.getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            errors.putWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DV_PAYEE_DETAIL + "."
                    + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_DV_ADD_LINE_MISSING_PAYEE);
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
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        DisbursementVoucherPayeeDetail payeeDetail = dvDocument.getDvPayeeDetail();

        GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.DOCUMENT);

        LOG.debug("processing route rules for document " + document.getFinancialDocumentNumber());

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
        String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);
        if (RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(
                dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            LOG.debug("validating non employee travel");
            validateNonEmployeeTravel(dvDocument);
        }

        // pre-paid travel

        // retrieve prepaid travel payment reasons
        String[] travelPrepaidPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, PREPAID_TRAVEL_PAY_REASONS_PARM_NM);
        if (RulesUtils.makeSet(travelPrepaidPaymentReasonCodes).contains(
                dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            LOG.debug("validating pre paid travel");
            validatePrePaidTravel(dvDocument);
        }

        LOG.debug("validating document amounts");
        validateDocumentAmounts(dvDocument);

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
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument,
            AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;

        /* change document type based on payment method to pick up different offsets */
        if (PAYMENT_METHOD_CHECK.equals(dvDocument.getDisbVchrPaymentMethodCode())) {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTrnEntryLedgerSequenceNumber() + " to "
                    + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_CHECKACH);
        }
        else {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTrnEntryLedgerSequenceNumber() + " to "
                    + DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DOCUMENT_TYPE_WTFD);
        }
    }


    /**
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateDocumentGeneralLedgerPendingEntries(TransactionalDocument transactionalDocument,
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        if (dvDocument.getGeneralLedgerPendingEntries() == null || dvDocument.getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
            // throw new RuntimeException("No gl entries for accounting lines.");
        }

        /*
         * only generate additonal charge entries for payment method wire charge, and if the fee has not been waived
         */
        if (PAYMENT_METHOD_WIRE.equals(dvDocument.getDisbVchrPaymentMethodCode())
                && !dvDocument.getDvWireTransfer().isDisbursementVoucherWireTransferFeeWaiverIndicator()) {
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
    private GeneralLedgerPendingEntry processWireChargeDebitEntries(DisbursementVoucherDocument dvDocument,
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge) {

        // increment the sequence counter
        sequenceHelper.increment();

        // grab the explicit entry for the first accounting line and adjust for wire charge entry
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(dvDocument
                .getGeneralLedgerPendingEntry(0));
        explicitEntry.setTrnEntryLedgerSequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setFinancialObjectCode(wireCharge.getExpenseFinancialObjectCode());
        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        explicitEntry.setFinancialObjectTypeCode(OBJECT_TYPE_CODE.EXPENSE_EXPENDITURE);
        explicitEntry.setTransactionDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);

        if (dvDocument.getDvWireTransfer().isDisbVchrForeignBankIndicator()) {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getForeignChargeAmt());
        }
        else {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getDomesticChargeAmt());
        }

        explicitEntry.setTransactionLedgerEntryDesc("Automatic debit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument, explicitEntry, sequenceHelper, offsetEntry);

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
    private void processWireChargeCreditEntries(DisbursementVoucherDocument dvDocument,
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry) {

        // increment the sequence counter
        sequenceHelper.increment();

        // copy the charge entry and adjust for credit
        GeneralLedgerPendingEntry explicitEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(chargeEntry);
        explicitEntry.setTrnEntryLedgerSequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
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
        explicitEntry.setTransactionDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT);

        // TODO: get sufficient funds object code

        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        explicitEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER);
        explicitEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING);

        explicitEntry.setTransactionLedgerEntryDesc("Automatic credit for wire transfer fee");

        dvDocument.getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = (GeneralLedgerPendingEntry) ObjectUtils.deepCopy(explicitEntry);
        populateOffsetGeneralLedgerPendingEntry(dvDocument, explicitEntry, sequenceHelper, offsetEntry);

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
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrRemitPersonName())
                    || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeLine1Addr())) {
                errors.putWithoutFullErrorPath(Constants.GENERAL_SPECHAND_TAB_ERRORS, KeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode())
                && (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0)) {
            errors.put(PropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE,
                    KeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE);
        }

        /* if special handling indicated, must be a note exlaining why */
        if (document.isDisbVchrSpecialHandlingCode()
                && (document.getDocumentHeader().getNotes() == null || document.getDocumentHeader().getNotes().size() == 0)) {
            errors.putWithoutFullErrorPath(Constants.GENERAL_SPECHAND_TAB_ERRORS, KeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE);
        }

        /* state & zip must be given for us */
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeStateCode())) {
                errors.put(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_STATE_CODE,
                        KeyConstants.ERROR_DV_PAYEE_STATE_CODE);
            }

            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeZipCode())) {
                errors.put(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE,
                        KeyConstants.ERROR_DV_PAYEE_ZIP_CODE);
            }
        }

        /* country required except for employee payees */
        if (!document.getDvPayeeDetail().isEmployee()
                && StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrPayeeCountryCode())) {
            errors.put(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE,
                    KeyConstants.ERROR_REQUIRED, "Payee Country ");
        }
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

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankRoutingNumber())) {
            errors.put(PropertyConstants.DISB_VCHR_BANK_ROUTING_NUMBER, KeyConstants.ERROR_DV_BANK_ROUTING_NUMBER);
        }

        if (!document.getDvWireTransfer().isDisbVchrForeignBankIndicator()
                && StringUtils.isBlank(document.getDvWireTransfer().getDisbVchrBankStateCode())) {
            errors.put(PropertyConstants.DISB_VCHR_BANK_STATE_CODE, KeyConstants.ERROR_REQUIRED, "Bank State");
        }

        /* cannot have attachment checked for wire transfer */
        if (document.isDisbVchrAttachmentCode()) {
            errors.putWithoutFullErrorPath(PropertyConstants.DOCUMENT + "." + PropertyConstants.DISB_VCHR_ATTACHMENT_CODE,
                    KeyConstants.ERROR_DV_WIRE_ATTACHMENT);
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
            GlobalVariables.getErrorMap().put(PropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_CODE,
                    KeyConstants.ERROR_DV_CURRENCY_TYPE_CODE);
        }

        /* currency type name required */
        if (StringUtils.isBlank(document.getDvWireTransfer().getDisbursementVoucherForeignCurrencyTypeName())) {
            GlobalVariables.getErrorMap().put(PropertyConstants.DISB_VCHR_FD_CURRENCY_TYPE_NAME,
                    KeyConstants.ERROR_DV_CURRENCY_TYPE_NAME);
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
            errors.put(PropertyConstants.INCOME_CLASS_CODE, KeyConstants.ERROR_REQUIRED, "Income class code ");
        }
        else {
            /* for foreign source or treaty exempt, non reportable, tax percents must be 0 and gross indicator can not be checked */
            if (document.getDvNonResidentAlienTax().isForeignSourceIncomeCode()
                    || document.getDvNonResidentAlienTax().isIncomeTaxTreatyExemptCode()
                    || NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())) {

                if ((document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() != null && !(new KualiDecimal(0)
                        .equals(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent())))) {
                    errors.put(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() != null && !(new KualiDecimal(0)
                        .equals(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent())))) {
                    errors.put(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_STATE_TAX_NOT_ZERO);
                }

                if (document.getDvNonResidentAlienTax().isIncomeTaxGrossUpCode()) {
                    errors.put(PropertyConstants.INCOME_TAX_GROSS_UP_CODE, KeyConstants.ERROR_DV_GROSS_UP_INDICATOR);
                }

                if (NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(document.getDvNonResidentAlienTax().getIncomeClassCode())
                        && StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.put(PropertyConstants.POSTAL_COUNTRY_CODE, KeyConstants.ERROR_DV_POSTAL_COUNTRY_CODE);
                }
            }
            else {
                if (document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent() == null) {
                    errors.put(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_REQUIRED, "Federal tax percent ");
                }
                else {
                    // check tax percent is in nra tax pct table for income class code
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.put(PropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT,
                                new String[] { document.getDvNonResidentAlienTax().getFederalIncomeTaxPercent().toString(),
                                        document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (document.getDvNonResidentAlienTax().getStateIncomeTaxPercent() == null) {
                    errors.put(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_REQUIRED, "State tax percent ");
                }
                else {
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(document.getDvNonResidentAlienTax().getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(document.getDvNonResidentAlienTax().getStateIncomeTaxPercent());

                    BusinessObject retrievedPercent = SpringServiceLocator.getBusinessObjectService().retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.put(PropertyConstants.STATE_INCOME_TAX_PERCENT, KeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT,
                                new String[] { document.getDvNonResidentAlienTax().getStateIncomeTaxPercent().toString(),
                                        document.getDvNonResidentAlienTax().getIncomeClassCode() });
                    }
                }

                if (StringUtils.isBlank(document.getDvNonResidentAlienTax().getPostalCountryCode())) {
                    errors.put(PropertyConstants.POSTAL_COUNTRY_CODE, KeyConstants.ERROR_REQUIRED, "Country code ");
                }
            }
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
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObjectsRecursively(document.getDvNonEmployeeTravel(),
                1);

        /* travel from and to state required if country is us */
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDvTravelFromCountryCode())
                && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelFromStateCode())) {
            errors.put(PropertyConstants.DISB_VCHR_TRAVEL_FROM_STATE_CODE, KeyConstants.ERROR_DV_TRAVEL_FROM_STATE);
        }
        if (Constants.COUNTRY_CODE_UNITED_STATES.equals(document.getDvNonEmployeeTravel().getDisbVchrTravelToCountryCode())
                && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrTravelToStateCode())) {
            errors.put(PropertyConstants.DISB_VCHR_TRAVEL_TO_STATE_CODE, KeyConstants.ERROR_DV_TRAVEL_TO_STATE);
        }

        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(PropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
            return;
        }

        /* must have per diem change message if actual amount is different from calculated amount */
        if (document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt().compareTo(
                document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount()) != 0
                && StringUtils.isBlank(document.getDvNonEmployeeTravel().getDvPerdiemChangeReasonText())) {
            errors.put(PropertyConstants.DV_PERDIEM_CHANGE_REASON_TEXT, KeyConstants.ERROR_DV_PERDIEM_CHANGE_REQUIRED);
        }

        /* total on nonemployee travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount.add(SpringServiceLocator.getDisbursementVoucherTaxService().getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvNonEmployeeTravel().getTotalTravelAmount()) != 0) {
            errors.putWithoutFullErrorPath(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_DV_TRAVEL_CHECK_TOTAL);
        }

        /* make sure per diem fields have not changed since the per diem amount calculation */
        KualiDecimal calculatedPerDiem = SpringServiceLocator.getDisbursementVoucherTravelService().calculatePerDiemAmount(
                document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(),
                document.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(),
                document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());
        if (calculatedPerDiem.compareTo(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) != 0) {
            errors.putWithoutFullErrorPath(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_DV_PER_DIEM_CALC_CHANGE);
        }

        /* make sure mileage fields have not changed since the mileage amount calculation */
        if (document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt() != null
                && document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount() != null) {
            KualiDecimal calculatedMileageAmount = SpringServiceLocator.getDisbursementVoucherTravelService()
                    .calculateMileageAmount(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(),
                            document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());
            if (calculatedMileageAmount.compareTo(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) != 0) {
                errors.putWithoutFullErrorPath(Constants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KeyConstants.ERROR_DV_MILEAGE_CALC_CHANGE);
            }
        }

        errors.removeFromErrorPath(PropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
    }

    /**
     * Validates pre paid travel information.
     * 
     * @param document
     */
    private void validatePrePaidTravel(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        errors.addToErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObjectsRecursively(
                document.getDvPreConferenceDetail(), 1);
        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
            return;
        }

        /* check conference end date is not before conference start date */
        if (document.getDvPreConferenceDetail().getDisbVchrConferenceEndDate().compareTo(
                document.getDvPreConferenceDetail().getDisbVchrConferenceStartDate()) < 0) {
            errors.put(PropertyConstants.DISB_VCHR_CONFERENCE_END_DATE, KeyConstants.ERROR_DV_CONF_END_DATE);
        }

        /* total on prepaid travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount.add(SpringServiceLocator.getDisbursementVoucherTaxService().getNonResidentAlienTaxAmount(document));
        if (paidAmount.compareTo(document.getDvPreConferenceDetail().getDisbVchrConferenceTotalAmt()) != 0) {
            errors.putWithoutFullErrorPath(Constants.GENERAL_PREPAID_TAB_ERRORS, KeyConstants.ERROR_DV_PREPAID_CHECK_TOTAL);
        }

        errors.removeFromErrorPath(PropertyConstants.DV_PRE_CONFERENCE_DETAIL);
    }

    /**
     * Validates the selected documentation location field.
     * 
     * @param document
     */
    private void validateDocumentationLocation(DisbursementVoucherDocument document) {
        String errorKey = PropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE;

        // payment reason restrictions
        executeApplicationParameterRestriction(PAYMENT_DOC_LOCATION_GROUP_NM, PAYMENT_PARM_PREFIX
                + document.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), document
                .getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");

        // alien indicator restrictions
        if (document.getDvPayeeDetail().isDisbVchrAlienPaymentCode()) {
            executeApplicationParameterRestriction(ALIEN_INDICATOR_DOC_LOCATION_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM, document
                    .getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
        }

        // initiator campus code restrictions
        String initiatorCampusCode = getInitiator(document).getOrganization().getOrganizationPhysicalCampusCode();
        executeApplicationParameterRestriction(CAMPUS_DOC_LOCATION_GROUP_NM, CAMPUS_CODE_PARM_PREFIX + initiatorCampusCode,
                document.getDisbursementVoucherDocumentationLocationCode(), errorKey, "Documentation location");
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
            executeApplicationParameterRestriction(ALIEN_INDICATOR_PAYMENT_GROUP_NM, ALIEN_INDICATOR_CHECKED_PARM_NM,
                    paymentReasonCode, errorKey, "Payment reason");
        }

        /* check revolving fund restrictions */
        // retrieve revolving fund payment reasons
        String[] revolvingFundPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, REVOLVING_FUND_PAY_REASONS_PARM_NM);

        if (RulesUtils.makeSet(revolvingFundPaymentReasonCodes).contains(paymentReasonCode)
                && !document.getDvPayeeDetail().isDvPayeeRevolvingFundCode() && !document.getDvPayeeDetail().isVendor()) {
            errors.put(errorKey, KeyConstants.ERROR_DV_REVOLVING_PAYMENT_REASON, paymentReasonCode);
        }

        /* if payment reason is moving, payee must be an employee or have payee ownership type I (individual) */
        String[] movingPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(
                DV_DOCUMENT_PARAMETERS_GROUP_NM, MOVING_PAY_REASONS_PARM_NM);

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
                    errors.put(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER,
                            KeyConstants.ERROR_DV_MOVING_PAYMENT_PAYEE);
                }
            }
        }


        /* for research payments over a certain limit the payee must be a vendor */
        String[] researchPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(
                DV_DOCUMENT_PARAMETERS_GROUP_NM, RESEARCH_PAY_REASONS_PARM_NM);

        String researchPayLimit = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                DV_DOCUMENT_PARAMETERS_GROUP_NM, RESEARCH_CHECK_LIMIT_AMOUNT_PARM_NM);
        KualiDecimal payLimit = new KualiDecimal(researchPayLimit);

        if (RulesUtils.makeSet(researchPaymentReasonCodes).contains(document.getDvPayeeDetail().getDisbVchrPaymentReasonCode())
                && document.getDisbVchrCheckTotalAmount().isGreaterEqual(payLimit) && !document.getDvPayeeDetail().isVendor()) {
            errors.put(PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER,
                    KeyConstants.ERROR_DV_RESEARCH_PAYMENT_PAYEE, payLimit.toString());
        }
    }

    /**
     * Validates that the payee is not the initiator.
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
            KualiUser initUser = getInitiator(document);
            if (uuid.equals(initUser.getPersonUniversalIdentifier())) {
                GlobalVariables.getErrorMap().put(
                        PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER,
                        KeyConstants.ERROR_PAYEE_INITIATOR);
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
            errors.put(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EXISTENCE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* DV Payee must be active */
        if (!dvPayee.isPayeeActiveCode()) {
            errors.put(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_INACTIVE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* check payment reason is allowed for payee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, DVPAYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail()
                .getDisbVchrPaymentReasonCode(), PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        /* for payees with tax type ssn, check employee restrictions */
        if (TAX_TYPE_SSN.equals(dvPayee.getTaxpayerTypeCode())) {
            if (isActiveEmployeeSSN(dvPayee.getTaxIdNumber())) {
                // determine if the rule is flagged off in the parm setting
                String performPrepaidEmployeeInd = SpringServiceLocator.getKualiConfigurationService()
                        .getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, PERFORM_PREPAID_EMPL_PARM_NM);

                if (Constants.ACTIVE_INDICATOR.equals(performPrepaidEmployeeInd)) {
                    /* active payee employees cannot be paid for prepaid travel */
                    String travelPrepaidPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                            .getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, PREPAID_TRAVEL_PAY_REASONS_PARM_NM);
                    if (RulesUtils.makeSet(travelPrepaidPaymentReasonCodes).contains(payeeDetail.getDisbVchrPaymentReasonCode())) {
                        errors.put(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_ACTIVE_EMPLOYEE_PREPAID_TRAVEL);
                    }

                }
            }
            else if (isEmployeeSSN(dvPayee.getTaxIdNumber())) {
                // check parm setting for paid outside payroll check
                String performPaidOutsidePayrollInd = SpringServiceLocator.getKualiConfigurationService()
                        .getApplicationParameterValue(DV_DOCUMENT_PARAMETERS_GROUP_NM, PERFORM_EMPL_OUTSIDE_PAYROLL_PARM_NM);

                if (Constants.ACTIVE_INDICATOR.equals(performPaidOutsidePayrollInd)) {
                    /* If payee is type payee and employee, payee record must be flagged as paid outside of payroll */
                    if (!dvPayee.isPayeeEmployeeCode()) {
                        errors.put(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EMPLOYEE_PAID_OUTSIDE_PAYROLL);
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
            errors.put(PropertyConstants.DISB_VCHR_PAYEE_ID_NUMBER, KeyConstants.ERROR_EXISTENCE, "Payee ID ");
            errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
            return;
        }

        /* check payment reason is allowed for employee type */
        executeApplicationParameterRestriction(PAYEE_PAYMENT_GROUP_NM, EMPLOYEE_PAYEE_PAYMENT_PARM, document.getDvPayeeDetail()
                .getDisbVchrPaymentReasonCode(), PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        errors.removeFromErrorPath(PropertyConstants.DV_PAYEE_DETAIL);
    }

    /**
     * Checks the amounts on the document for reconciliation.
     */
    public void validateDocumentAmounts(DisbursementVoucherDocument document) {
        ErrorMap errors = GlobalVariables.getErrorMap();

        /* check total cannot be negative */
        if (Constants.ZERO.compareTo(document.getDisbVchrCheckTotalAmount()) == 1) {
            errors.put(PropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KeyConstants.ERROR_NEGATIVE_CHECK_TOTAL);
        }

        /* total accounting lines cannot be negative */
        if (Constants.ZERO.compareTo(document.getSourceTotal()) == 1) {
            errors.putWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_NEGATIVE_ACCOUNTING_TOTAL);
        }

        /* total of accounting lines must match check total */
        if (document.getDisbVchrCheckTotalAmount().compareTo(document.getSourceTotal()) != 0) {
            errors.putWithoutFullErrorPath(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CHECK_ACCOUNTING_TOTAL);
        }
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.put(errorKey, KeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* check object type global restrictions */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectTypeCode(),
                        errorKey, "Object type");

        /* check object sub type global restrictions */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectSubTypeCode(),
                        errorKey, "Object sub type");

        /* check object level global restrictions */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectLevelCode(),
                        errorKey, "Object level");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return objectCodeAllowed;
        }

        /* check object level is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(PAYMENT_OBJECT_LEVEL_GROUP_NM, PAYMENT_PARM_PREFIX
                        + documentPaymentReason, accountingLine.getObjectCode().getFinancialObjectLevelCode(), errorKey,
                        "Object level");

        /* check object code is in permitted list for payment reason */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(PAYMENT_OBJECT_CODE_GROUP_NM,
                        PAYMENT_PARM_PREFIX + documentPaymentReason, accountingLine.getFinancialObjectCode(), errorKey,
                        "Object code");

        /* check payment reason is valid for object code */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(OBJECT_CODE_PAYMENT_GROUP_NM, OBJECT_CODE_PARM_PREFIX
                        + accountingLine.getFinancialObjectCode(), dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode(),
                        PropertyConstants.DV_PAYEE_DETAIL + "." + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE,
                        "Payment reason code");

        /* check payment reason is valid for object level */
        objectCodeAllowed = objectCodeAllowed
                && executeApplicationParameterRestriction(OBJECT_LEVEL_PAYMENT_GROUP_NM, OBJECT_LEVEL_PARM_PREFIX
                        + accountingLine.getObjectCode().getFinancialObjectLevelCode(), dvDocument.getDvPayeeDetail()
                        .getDisbVchrPaymentReasonCode(), PropertyConstants.DV_PAYEE_DETAIL + "."
                        + PropertyConstants.DISB_VCHR_PAYMENT_REASON_CODE, "Payment reason code");

        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
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
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, SUB_FUND_GLOBAL_RESTRICTION_PARM_NM,
                        accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund code");

        /* global function code restrictions */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM,
                        FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd(),
                        errorKey, "Function code");

        String documentPaymentReason = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(documentPaymentReason)) {
            return accountNumberAllowed;
        }

        /* check sub fund is in permitted list for payment reason */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(PAYMENT_SUB_FUND_GROUP_NM, PAYMENT_PARM_PREFIX + documentPaymentReason,
                        accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund");

        /* check object sub type is allowed for sub fund code */
        accountNumberAllowed = accountNumberAllowed
                && executeApplicationParameterRestriction(SUB_FUND_OBJECT_SUB_TYPE_GROUP_NM, SUB_FUND_CODE_PARM_PREFIX
                        + accountingLine.getAccount().getSubFundGroupCode(), accountingLine.getObjectCode()
                        .getFinancialObjectSubTypeCode(), errorKey, "Object sub type");

        return accountNumberAllowed;
    }


    /**
     * Checks the given field value against a restriction defined in the application parameters table. If the rule fails, an error
     * is added to the global error map.
     * 
     * @param parameterGroupName - Security Group name
     * @param parameterName - Parameter Name
     * @param restrictedFieldValue - Value to check
     * @param errorField - Key to associate error with in error map
     * @param errorParameter - String parameter for the restriction error message
     * @return boolean indicating whether or not the rule passed
     */
    private boolean executeApplicationParameterRestriction(String parameterGroupName, String parameterName,
            String restrictedFieldValue, String errorField, String errorParameter) {
        boolean rulePassed = true;

        KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                parameterGroupName, parameterName);
        if (rule != null) {
            if (rule.failsRule(restrictedFieldValue)) {
                GlobalVariables.getErrorMap().put(
                        errorField,
                        rule.getErrorMessageKey(),
                        new String[] { errorParameter, restrictedFieldValue, parameterName, parameterGroupName,
                                rule.getParameterText() });
                rulePassed = false;
            }
        }
        else {
            LOG.warn("Did not find apc parameter record for group " + parameterGroupName + " with parm name " + parameterName);
        }

        return rulePassed;
    }

    /**
     * Overrides the parent to return true, because Disbursement Voucher documents only use the SourceAccountingLines data
     * structures. The list that holds TargetAccountingLines should be empty. This will be checked when the document is "routed" or
     * submitted to post - it's called automatically by the parent's processRouteDocument method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
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
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Override to check for tax accounting lines. These lines can have negative amounts which the super will reject.
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        if (((DisbursementVoucherDocument) document).getDvNonResidentAlienTax() != null) {
            List taxLineNumbers = SpringServiceLocator.getDisbursementVoucherTaxService().getNRATaxLineNumbers(
                    ((DisbursementVoucherDocument) document).getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
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
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_TAX_GROUP));
    }

    /**
     * Checks if the current user is a member of the dv travel workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTravelGroup() {
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_TRAVEL_GROUP));
    }

    /**
     * Checks if the current user is a member of the dv frn workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInFRNGroup() {
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_FRN_GROUP));
    }

    /**
     * Checks if the current user is a member of the dv wire workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInWireGroup() {
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_WIRE_GROUP));
    }
    
    /**
     * This method checks to see whether the user is in the dv admin group or not.
     * 
     * @return true if user is in group, false otherwise
     */
    private boolean isUserInDvAdminGroup() {
        return GlobalVariables.getUserSession().getKualiUser().isMember(new KualiGroup(KualiGroup.KUALI_DV_ADMIN_GROUP));
    }

    /**
     * Returns the initiator of the document as a KualiUser
     * 
     * @param document
     * @return
     */
    private KualiUser getInitiator(TransactionalDocument document) {
        KualiUser initUser = null;
        try {

            initUser = SpringServiceLocator.getKualiUserService().getUser(
                    new AuthenticationUserId(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Document Initiator not found " + e.getMessage());
        }

        return initUser;
    }

    /**
     * Retrieves the wire transfer information for the current fiscal year.
     * @return
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
     * @param payeeIdNumber
     * @return
     */
    private Payee retrievePayee(String payeeIdNumber) {
        Payee payee = new Payee();
        payee.setPayeeIdNumber(payeeIdNumber);
        return (Payee) SpringServiceLocator.getBusinessObjectService().retrieve(payee);
    }

    /**
     * Retrieves the UniversalUser object from the uuid.
     * @param uuid
     * @return
     */
    private UniversalUser retrieveEmployee(String uuid) {
        UniversalUser employee = new UniversalUser();
        employee.setPersonUniversalIdentifier(uuid);
        return (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);
    }

    /**
     * Performs a lookup on universal users for the given ssn number.
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
     * @return
     */

    public boolean isCoverSheetPrintable(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved()
                || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());

    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processSourceAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.SourceAccountingLine)
     */
    protected SufficientFundsItem processSourceAccountingLineSufficientFundsCheckingPreparation(
            TransactionalDocument transactionalDocument, SourceAccountingLine sourceAccountingLine) {
        SufficientFundsItem item = null;
        String chartOfAccountsCode = sourceAccountingLine.getChartOfAccountsCode();
        String accountNumber = sourceAccountingLine.getAccountNumber();
        String accountSufficientFundsCode = sourceAccountingLine.getAccount().getAccountSufficientFundsCode();
        String financialObjectCode = sourceAccountingLine.getFinancialObjectCode();
        String financialObjectLevelCode = sourceAccountingLine.getObjectCode().getFinancialObjectLevelCode();
        KualiDecimal lineAmount = sourceAccountingLine.getAmount();
        Integer fiscalYear = sourceAccountingLine.getPostingYear();
        String financialObjectTypeCode = sourceAccountingLine.getObjectTypeCode();

        // always credit
        String debitCreditCode = Constants.GL_CREDIT_CODE;
        String sufficientFundsObjectCode = SpringServiceLocator.getSufficientFundsService().getSufficientFundsObjectCode(
                chartOfAccountsCode, financialObjectCode, accountSufficientFundsCode, financialObjectLevelCode);
        item = buildSufficentFundsItem(accountNumber, accountSufficientFundsCode, lineAmount, chartOfAccountsCode,
                sufficientFundsObjectCode, debitCreditCode, financialObjectCode, financialObjectLevelCode, fiscalYear,
                financialObjectTypeCode);

        return item;
    }

    /**
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processTargetAccountingLineSufficientFundsCheckingPreparation(TransactionalDocument,
     *      org.kuali.core.bo.TargetAccountingLine)
     */
    protected SufficientFundsItem processTargetAccountingLineSufficientFundsCheckingPreparation(
            TransactionalDocument transactionalDocument, TargetAccountingLine targetAccountingLine) {
        if (targetAccountingLine != null) {
            throw new IllegalArgumentException(
                "DV document doesn't have target accounting lines. This method should have never been entered");
        }
        return null;
    }


}