/*
 * Copyright 2005-2006 The Kuali Foundation
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

package org.kuali.kfs.fp.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.AdHocPaymentIndicator;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.businessobject.options.PaymentDocumentationLocationValuesFinder;
import org.kuali.kfs.sys.businessobject.options.PaymentMethodValuesFinder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the business object that represents the DisbursementVoucher document in Kuali.
 */
public class DisbursementVoucherDocument extends AccountingDocumentBase implements Copyable, AmountTotaling, PaymentSource {
    protected static Logger LOG = Logger.getLogger(DisbursementVoucherDocument.class);

    protected static final String PAYEE_IS_PURCHASE_ORDER_VENDOR_SPLIT = "PayeeIsPurchaseOrderVendor";
    protected static final String PURCHASE_ORDER_VENDOR_TYPE = "PO";
    protected static final String DOCUMENT_REQUIRES_TAX_REVIEW_SPLIT = "RequiresTaxReview";
    protected static final String DOCUMENT_REQUIRES_TRAVEL_REVIEW_SPLIT = "RequiresTravelReview";
    protected static final String DOCUMENT_REQUIRES_SEPARATION_OF_DUTIES = "RequiresSeparationOfDutiesReview";
    protected static final String DISBURSEMENT_VOUCHER_TYPE = "DisbursementVoucher";

    protected static final String PAYMENT_REASONS_REQUIRING_TAX_REVIEW_PARAMETER_NAME = "PAYMENT_REASONS_REQUIRING_TAX_REVIEW";
    protected static final String USE_DEFAULT_EMPLOYEE_ADDRESS_PARAMETER_NAME = "USE_DEFAULT_EMPLOYEE_ADDRESS_IND";
    protected static final String DEFAULT_EMPLOYEE_ADDRESS_TYPE_PARAMETER_NAME = "DEFAULT_EMPLOYEE_ADDRESS_TYPE";
    protected static final String SEPARATION_OF_DUTIES_PARAMETER_NAME = "ENABLE_SEPARATION_OF_DUTIES_IND";

    protected static final String TAX_CONTROL_BACKUP_HOLDING = "B";
    protected static final String TAX_CONTROL_HOLD_PAYMENTS = "H";

    protected static transient PersonService personService;
    protected static transient ParameterService parameterService;
    protected static transient VendorService vendorService;
    protected static transient BusinessObjectService businessObjectService;
    protected static transient DateTimeService dateTimeService;
    protected static transient DisbursementVoucherPaymentReasonService dvPymentReasonService;
    protected static transient IdentityManagementService identityManagementService;
    protected static transient PaymentSourceExtractionService paymentSourceExtractionService;
    protected static volatile transient DisbursementVoucherPaymentService disbursementVoucherPaymentService;
    protected static volatile transient PaymentSourceHelperService paymentSourceHelperService;

    protected Integer finDocNextRegistrantLineNbr;
    protected String disbVchrContactPersonName;
    protected String disbVchrContactPhoneNumber;
    protected String disbVchrContactEmailId;
    protected Date disbursementVoucherDueDate;
    protected boolean disbVchrAttachmentCode;
    protected boolean disbVchrSpecialHandlingCode;
    protected KualiDecimal disbVchrCheckTotalAmount;
    protected boolean disbVchrForeignCurrencyInd;
    protected String disbursementVoucherDocumentationLocationCode;
    protected String disbVchrCheckStubText;
    protected boolean dvCheckStubOverflowCode;
    protected String campusCode;
    protected String disbVchrPayeeTaxControlCode;
    protected boolean disbVchrPayeeChangedInd;
    protected String disbursementVoucherCheckNbr;
    protected Timestamp disbursementVoucherCheckDate;
    protected boolean disbVchrPayeeW9CompleteCode;
    protected String disbVchrPaymentMethodCode;
    protected boolean exceptionIndicator;
    protected boolean disbExcptAttachedIndicator;
    protected Date extractDate;
    protected Date paidDate;
    protected Date cancelDate;
    protected String disbVchrBankCode;
    protected String disbVchrPdpBankCode;

    protected boolean payeeAssigned = false;
    protected boolean editW9W8BENbox = false;
    protected boolean immediatePaymentIndicator = false;
    protected boolean refundIndicator;

    protected DocumentHeader financialDocument;
    protected PaymentDocumentationLocation disbVchrDocumentationLoc;
    protected DisbursementVoucherNonEmployeeTravel dvNonEmployeeTravel;
    protected DisbursementVoucherNonResidentAlienTax dvNonResidentAlienTax;
    protected DisbursementVoucherPayeeDetail dvPayeeDetail;
    protected DisbursementVoucherPreConferenceDetail dvPreConferenceDetail;
    protected PaymentSourceWireTransfer wireTransfer;

    protected Bank bank;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherDocument() {
        super();
        exceptionIndicator = false;
        finDocNextRegistrantLineNbr = new Integer(1);
        dvNonEmployeeTravel = new DisbursementVoucherNonEmployeeTravel();
        dvNonResidentAlienTax = new DisbursementVoucherNonResidentAlienTax();
        dvPayeeDetail = new DisbursementVoucherPayeeDetail();
        dvPreConferenceDetail = new DisbursementVoucherPreConferenceDetail();
        wireTransfer = new PaymentSourceWireTransfer();
        disbVchrCheckTotalAmount = KualiDecimal.ZERO;
        bank = new Bank();
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    @Override
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        List<GeneralLedgerPendingEntry> ples = new ArrayList<GeneralLedgerPendingEntry>();

        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        FlexibleOffsetAccountService flexibleOffsetAccountService = SpringContext.getBean(FlexibleOffsetAccountService.class);

        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);

        for (GeneralLedgerPendingEntry ple : this.getGeneralLedgerPendingEntries()) {
            List<String> expenseObjectTypes = objectTypeService.getExpenseObjectTypes(ple.getUniversityFiscalYear());
            if (expenseObjectTypes.contains(ple.getFinancialObjectTypeCode())) {
                // is an expense object type, keep checking
                ple.refreshNonUpdateableReferences();
                if (ple.getAccount().isPendingAcctSufficientFundsIndicator() && ple.getAccount().getAccountSufficientFundsCode().equals(KFSConstants.SF_TYPE_CASH_AT_ACCOUNT)) {
                    // is a cash account
                    if (flexibleOffsetAccountService.getByPrimaryIdIfEnabled(ple.getChartOfAccountsCode(), ple.getAccountNumber(), ple.getChart().getFinancialCashObjectCode()) == null && flexibleOffsetAccountService.getByPrimaryIdIfEnabled(ple.getChartOfAccountsCode(), ple.getAccountNumber(), ple.getChart().getFinAccountsPayableObjectCode()) == null) {
                        // does not have a flexible offset for cash or liability, set the object code to cash and add to list of
                        // PLEs to check for SF

                        ple = new GeneralLedgerPendingEntry(ple);
                        ple.setFinancialObjectCode(ple.getChart().getFinancialCashObjectCode());
                        ple.setTransactionDebitCreditCode(ple.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                        ples.add(ple);
                    }

                }
                else {
                    // is not a cash account, process as normal
                    ples.add(ple);
                }
            }
        }

        return ples;
    }


    /**
     * Gets the finDocNextRegistrantLineNbr attribute.
     *
     * @return Returns the finDocNextRegistrantLineNbr
     */
    public Integer getFinDocNextRegistrantLineNbr() {
        return finDocNextRegistrantLineNbr;
    }


    /**
     * Sets the finDocNextRegistrantLineNbr attribute.
     *
     * @param finDocNextRegistrantLineNbr The finDocNextRegistrantLineNbr to set.
     */
    public void setFinDocNextRegistrantLineNbr(Integer finDocNextRegistrantLineNbr) {
        this.finDocNextRegistrantLineNbr = finDocNextRegistrantLineNbr;
    }

    /**
     * Gets the disbVchrContactPersonName attribute.
     *
     * @return Returns the disbVchrContactPersonName
     */
    public String getDisbVchrContactPersonName() {
        return disbVchrContactPersonName;
    }


    /**
     * Sets the disbVchrContactPersonName attribute.
     *
     * @param disbVchrContactPersonName The disbVchrContactPersonName to set.
     */
    public void setDisbVchrContactPersonName(String disbVchrContactPersonName) {
        this.disbVchrContactPersonName = disbVchrContactPersonName;
    }

    /**
     * Gets the disbVchrContactPhoneNumber attribute.
     *
     * @return Returns the disbVchrContactPhoneNumber
     */
    public String getDisbVchrContactPhoneNumber() {
        return disbVchrContactPhoneNumber;
    }


    /**
     * Sets the disbVchrContactPhoneNumber attribute.
     *
     * @param disbVchrContactPhoneNumber The disbVchrContactPhoneNumber to set.
     */
    public void setDisbVchrContactPhoneNumber(String disbVchrContactPhoneNumber) {
        this.disbVchrContactPhoneNumber = disbVchrContactPhoneNumber;
    }

    /**
     * Gets the disbVchrContactEmailId attribute.
     *
     * @return Returns the disbVchrContactEmailId
     */
    public String getDisbVchrContactEmailId() {
        return disbVchrContactEmailId;
    }


    /**
     * Sets the disbVchrContactEmailId attribute.
     *
     * @param disbVchrContactEmailId The disbVchrContactEmailId to set.
     */
    public void setDisbVchrContactEmailId(String disbVchrContactEmailId) {
        this.disbVchrContactEmailId = disbVchrContactEmailId;
    }

    /**
     * Gets the disbursementVoucherDueDate attribute.
     *
     * @return Returns the disbursementVoucherDueDate
     */
    public Date getDisbursementVoucherDueDate() {
        return disbursementVoucherDueDate;
    }


    /**
     * Sets the disbursementVoucherDueDate attribute.
     *
     * @param disbursementVoucherDueDate The disbursementVoucherDueDate to set.
     */
    public void setDisbursementVoucherDueDate(Date disbursementVoucherDueDate) {
        this.disbursementVoucherDueDate = disbursementVoucherDueDate;
    }

    /**
     * Gets the disbVchrAttachmentCode attribute.
     *
     * @return Returns the disbVchrAttachmentCode
     */
    public boolean isDisbVchrAttachmentCode() {
        return disbVchrAttachmentCode;
    }

    /**
     * Returns the value of disbVchrAttachmentCode
     * @see org.kuali.kfs.sys.document.PaymentSource#hasAttachment()
     */
    @Override
    public boolean hasAttachment() {
        return isDisbVchrAttachmentCode();
    }


    /**
     * Sets the disbVchrAttachmentCode attribute.
     *
     * @param disbVchrAttachmentCode The disbVchrAttachmentCode to set.
     */
    public void setDisbVchrAttachmentCode(boolean disbVchrAttachmentCode) {
        this.disbVchrAttachmentCode = disbVchrAttachmentCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingCode attribute.
     *
     * @return Returns the disbVchrSpecialHandlingCode
     */
    public boolean isDisbVchrSpecialHandlingCode() {
        return disbVchrSpecialHandlingCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingCode attribute.
     *
     * @param disbVchrSpecialHandlingCode The disbVchrSpecialHandlingCode to set.
     */
    public void setDisbVchrSpecialHandlingCode(boolean disbVchrSpecialHandlingCode) {
        this.disbVchrSpecialHandlingCode = disbVchrSpecialHandlingCode;
    }

    /**
     * Gets the disbVchrCheckTotalAmount attribute.
     *
     * @return Returns the disbVchrCheckTotalAmount
     */
    public KualiDecimal getDisbVchrCheckTotalAmount() {
        return disbVchrCheckTotalAmount;
    }

    /**
     * Returns the value of disbVchrCheckTotalAmount
     * @see org.kuali.kfs.sys.document.PaymentSource#getPaymentAmount()
     */
    @Override
    public KualiDecimal getPaymentAmount() {
        return getDisbVchrCheckTotalAmount();
    }


    /**
     * Sets the disbVchrCheckTotalAmount attribute.
     *
     * @param disbVchrCheckTotalAmount The disbVchrCheckTotalAmount to set.
     */
    public void setDisbVchrCheckTotalAmount(KualiDecimal disbVchrCheckTotalAmount) {
        if (disbVchrCheckTotalAmount != null) {
            this.disbVchrCheckTotalAmount = disbVchrCheckTotalAmount;
        }
    }

    /**
     * Gets the disbVchrForeignCurrencyInd attribute.
     *
     * @return Returns the disbVchrForeignCurrencyInd
     */
    public boolean isDisbVchrForeignCurrencyInd() {
        return disbVchrForeignCurrencyInd;
    }


    /**
     * Sets the disbVchrForeignCurrencyInd attribute.
     *
     * @param disbVchrForeignCurrencyInd The disbVchrForeignCurrencyInd to set.
     */
    public void setDisbVchrForeignCurrencyInd(boolean disbVchrForeignCurrencyInd) {
        this.disbVchrForeignCurrencyInd = disbVchrForeignCurrencyInd;
    }

    /**
     * Gets the disbursementVoucherDocumentationLocationCode attribute.
     *
     * @return Returns the disbursementVoucherDocumentationLocationCode
     */
    public String getDisbursementVoucherDocumentationLocationCode() {
        return disbursementVoucherDocumentationLocationCode;
    }


    /**
     * Sets the disbursementVoucherDocumentationLocationCode attribute.
     *
     * @param disbursementVoucherDocumentationLocationCode The disbursementVoucherDocumentationLocationCode to set.
     */
    public void setDisbursementVoucherDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.disbursementVoucherDocumentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }

    /**
     * Gets the disbVchrCheckStubText attribute.
     *
     * @return Returns the disbVchrCheckStubText
     */
    public String getDisbVchrCheckStubText() {
        return disbVchrCheckStubText;
    }


    /**
     * Sets the disbVchrCheckStubText attribute.
     *
     * @param disbVchrCheckStubText The disbVchrCheckStubText to set.
     */
    public void setDisbVchrCheckStubText(String disbVchrCheckStubText) {
        this.disbVchrCheckStubText = disbVchrCheckStubText;
    }

    /**
     * Gets the dvCheckStubOverflowCode attribute.
     *
     * @return Returns the dvCheckStubOverflowCode
     */
    public boolean getDvCheckStubOverflowCode() {
        return dvCheckStubOverflowCode;
    }


    /**
     * Sets the dvCheckStubOverflowCode attribute.
     *
     * @param dvCheckStubOverflowCode The dvCheckStubOverflowCode to set.
     */
    public void setDvCheckStubOverflowCode(boolean dvCheckStubOverflowCode) {
        this.dvCheckStubOverflowCode = dvCheckStubOverflowCode;
    }

    /**
     * Gets the campusCode attribute.
     *
     * @return Returns the campusCode
     */
    @Override
    public String getCampusCode() {
        return campusCode;
    }


    /**
     * Sets the campusCode attribute.
     *
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the disbVchrPayeeTaxControlCode attribute.
     *
     * @return Returns the disbVchrPayeeTaxControlCode
     */
    public String getDisbVchrPayeeTaxControlCode() {
        return disbVchrPayeeTaxControlCode;
    }


    /**
     * Sets the disbVchrPayeeTaxControlCode attribute.
     *
     * @param disbVchrPayeeTaxControlCode The disbVchrPayeeTaxControlCode to set.
     */
    public void setDisbVchrPayeeTaxControlCode(String disbVchrPayeeTaxControlCode) {
        this.disbVchrPayeeTaxControlCode = disbVchrPayeeTaxControlCode;
    }

    /**
     * Gets the disbVchrPayeeChangedInd attribute.
     *
     * @return Returns the disbVchrPayeeChangedInd
     */
    public boolean isDisbVchrPayeeChangedInd() {
        return disbVchrPayeeChangedInd;
    }


    /**
     * Sets the disbVchrPayeeChangedInd attribute.
     *
     * @param disbVchrPayeeChangedInd The disbVchrPayeeChangedInd to set.
     */
    public void setDisbVchrPayeeChangedInd(boolean disbVchrPayeeChangedInd) {
        this.disbVchrPayeeChangedInd = disbVchrPayeeChangedInd;
    }

    /**
     * Gets the disbursementVoucherCheckNbr attribute.
     *
     * @return Returns the disbursementVoucherCheckNbr
     */
    public String getDisbursementVoucherCheckNbr() {
        return disbursementVoucherCheckNbr;
    }


    /**
     * Sets the disbursementVoucherCheckNbr attribute.
     *
     * @param disbursementVoucherCheckNbr The disbursementVoucherCheckNbr to set.
     */
    public void setDisbursementVoucherCheckNbr(String disbursementVoucherCheckNbr) {
        this.disbursementVoucherCheckNbr = disbursementVoucherCheckNbr;
    }

    /**
     * Gets the disbursementVoucherCheckDate attribute.
     *
     * @return Returns the disbursementVoucherCheckDate
     */
    public Timestamp getDisbursementVoucherCheckDate() {
        return disbursementVoucherCheckDate;
    }


    /**
     * Sets the disbursementVoucherCheckDate attribute.
     *
     * @param disbursementVoucherCheckDate The disbursementVoucherCheckDate to set.
     */
    public void setDisbursementVoucherCheckDate(Timestamp disbursementVoucherCheckDate) {
        this.disbursementVoucherCheckDate = disbursementVoucherCheckDate;
    }

    /**
     * Gets the disbVchrPayeeW9CompleteCode attribute.
     *
     * @return Returns the disbVchrPayeeW9CompleteCode
     */
    public boolean getDisbVchrPayeeW9CompleteCode() {
        return disbVchrPayeeW9CompleteCode;
    }


    /**
     * Sets the disbVchrPayeeW9CompleteCode attribute.
     *
     * @param disbVchrPayeeW9CompleteCode The disbVchrPayeeW9CompleteCode to set.
     */
    public void setDisbVchrPayeeW9CompleteCode(boolean disbVchrPayeeW9CompleteCode) {
        this.disbVchrPayeeW9CompleteCode = disbVchrPayeeW9CompleteCode;
    }

    /**
     * Gets the disbVchrPaymentMethodCode attribute.
     *
     * @return Returns the disbVchrPaymentMethodCode
     */
    public String getDisbVchrPaymentMethodCode() {
        return disbVchrPaymentMethodCode;
    }

    /**
     * Returns the disbVchrPaymentMethodCode
     * @see org.kuali.kfs.sys.document.PaymentSource#getPaymentMethodCode()
     */
    @Override
    public String getPaymentMethodCode() {
        return getDisbVchrPaymentMethodCode();
    }


    /**
     * Sets the disbVchrPaymentMethodCode attribute.
     *
     * @param disbVchrPaymentMethodCode The disbVchrPaymentMethodCode to set.
     */
    public void setDisbVchrPaymentMethodCode(String disbVchrPaymentMethodCode) {
        this.disbVchrPaymentMethodCode = disbVchrPaymentMethodCode;
    }

    /**
     * Gets the financialDocument attribute.
     *
     * @return Returns the financialDocument
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }


    /**
     * Sets the financialDocument attribute.
     *
     * @param financialDocument The financialDocument to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the disbVchrDocumentationLoc attribute.
     *
     * @return Returns the disbVchrDocumentationLoc
     */
    public PaymentDocumentationLocation getDisbVchrDocumentationLoc() {
        return disbVchrDocumentationLoc;
    }


    /**
     * Sets the disbVchrDocumentationLoc attribute.
     *
     * @param disbVchrDocumentationLoc The disbVchrDocumentationLoc to set.
     * @deprecated
     */
    @Deprecated
    public void setDisbVchrDocumentationLoc(PaymentDocumentationLocation disbVchrDocumentationLoc) {
        this.disbVchrDocumentationLoc = disbVchrDocumentationLoc;
    }


    /**
     * @return Returns the dvNonEmployeeTravel.
     */
    public DisbursementVoucherNonEmployeeTravel getDvNonEmployeeTravel() {
        return dvNonEmployeeTravel;
    }

    /**
     * @param dvNonEmployeeTravel The dvNonEmployeeTravel to set.
     */
    public void setDvNonEmployeeTravel(DisbursementVoucherNonEmployeeTravel dvNonEmployeeTravel) {
        this.dvNonEmployeeTravel = dvNonEmployeeTravel;
    }

    /**
     * @return Returns the dvNonResidentAlienTax.
     */
    public DisbursementVoucherNonResidentAlienTax getDvNonResidentAlienTax() {
        return dvNonResidentAlienTax;
    }

    /**
     * @param dvNonResidentAlienTax The dvNonResidentAlienTax to set.
     */
    public void setDvNonResidentAlienTax(DisbursementVoucherNonResidentAlienTax dvNonResidentAlienTax) {
        this.dvNonResidentAlienTax = dvNonResidentAlienTax;
    }

    /**
     * @return Returns the dvPayeeDetail.
     */
    public DisbursementVoucherPayeeDetail getDvPayeeDetail() {
        return dvPayeeDetail;
    }

    /**
     * @param dvPayeeDetail The dvPayeeDetail to set.
     */
    public void setDvPayeeDetail(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        this.dvPayeeDetail = dvPayeeDetail;
    }

    /**
     * @return Returns the dvPreConferenceDetail.
     */
    public DisbursementVoucherPreConferenceDetail getDvPreConferenceDetail() {
        return dvPreConferenceDetail;
    }

    /**
     * @param dvPreConferenceDetail The dvPreConferenceDetail to set.
     */
    public void setDvPreConferenceDetail(DisbursementVoucherPreConferenceDetail dvPreConferenceDetail) {
        this.dvPreConferenceDetail = dvPreConferenceDetail;
    }

    /**
     * @return Returns the wireTransfer.
     */
    @Override
    public PaymentSourceWireTransfer getWireTransfer() {
        return wireTransfer;
    }

    /**
     * @param wireTransfer The wireTransfer to set.
     */
    public void setWireTransfer(PaymentSourceWireTransfer dvWireTransfer) {
        this.wireTransfer = dvWireTransfer;
    }

    /**
     * @return Returns the exceptionIndicator.
     */
    public boolean isExceptionIndicator() {
        return exceptionIndicator;
    }

    /**
     * @param exceptionIndicator The exceptionIndicator to set.
     */
    public void setExceptionIndicator(boolean exceptionIndicator) {
        this.exceptionIndicator = exceptionIndicator;
    }

    /**
     * Gets the cancelDate attribute.
     *
     * @return Returns the cancelDate.
     */
    @Override
    public Date getCancelDate() {
        return cancelDate;
    }

    /**
     * Sets the cancelDate attribute value.
     *
     * @param cancelDate The cancelDate to set.
     */
    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    /**
     * Gets the extractDate attribute.
     *
     * @return Returns the extractDate.
     */
    public Date getExtractDate() {
        return extractDate;
    }

    /**
     * Sets the extractDate attribute value.
     *
     * @param extractDate The extractDate to set.
     */
    public void setExtractDate(Date extractDate) {
        this.extractDate = extractDate;
    }

    /**
     * Sets the extractDate to the passed in extractionDate
     * @see org.kuali.kfs.sys.document.PaymentSource#markAsExtracted(java.sql.Date)
     */
    @Override
    public void markAsExtracted(Date extractionDate) {
        setExtractDate(extractionDate);
    }

    /**
     * Gets the paidDate attribute.
     *
     * @return Returns the paidDate.
     */
    public Date getPaidDate() {
        return paidDate;
    }

    /**
     * Sets the paidDate attribute value.
     *
     * @param paidDate The paidDate to set.
     */
    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    /**
     * Calls setPaidDate to set when this DisbursementVoucher was paid
     * @see org.kuali.kfs.sys.document.PaymentSource#markAsPaid(java.sql.Date)
     */
    @Override
    public void markAsPaid(Date processDate) {
        setPaidDate(processDate);
    }

    /**
     * Resets the DisbursementVoucher so that it it no longer marked as extracted; to do that, it sets its financial system document status back to approved,
     * and sets the paid date and extract date to null
     * @see org.kuali.kfs.sys.document.PaymentSource#resetFromExtraction()
     */
    @Override
    public void resetFromExtraction() {
        setExtractDate(null);
        // reset the status to APPROVED so DV will be extracted to PDP again
        getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(DisbursementVoucherConstants.DocumentStatusCodes.APPROVED);
        setPaidDate(null);
    }

    /**
     * Has the DisbursementVoucherPaymentService implementation generate a PaymentGroup for this DisbursementVoucher
     * @see org.kuali.kfs.sys.document.PaymentSource#generatePaymentGroup()
     */
    @Override
    public PaymentGroup generatePaymentGroup(Date processRunDate) {
        return getDisbursementVoucherPaymentService().createPaymentGroupForDisbursementVoucher(this, processRunDate);
    }


    /**
     * Based on which pdp dates are present (extract, paid, canceled), determines a String for the status
     *
     * @return a String representation of the status
     */
    public String getDisbursementVoucherPdpStatus() {
        if (cancelDate != null) {
            return "Canceled";
        }
        else if (paidDate != null) {
            return "Paid";
        }
        else if (extractDate != null) {
            return "Extracted";
        }
        else {
            return "Pre-Extraction";
        }
    }

    /**
     * Pretends to set the PDP status for this document
     *
     * @param status the status to pretend to set
     */
    public void setDisbursementVoucherPdpStatus(String status) {
        // don't do nothing, 'cause this ain't a real field
    }

    /**
     * Adds a dv pre-paid registrant line
     *
     * @param line
     */
    public void addDvPrePaidRegistrantLine(DisbursementVoucherPreConferenceRegistrant line) {
        line.setFinancialDocumentLineNumber(getFinDocNextRegistrantLineNbr());
        this.getDvPreConferenceDetail().getDvPreConferenceRegistrants().add(line);
        this.finDocNextRegistrantLineNbr = new Integer(getFinDocNextRegistrantLineNbr().intValue() + 1);
    }

    /**
     * Returns the name associated with the payment method code
     *
     * @return String
     */
    public String getDisbVchrPaymentMethodName() {
        return new PaymentMethodValuesFinder().getKeyLabel(disbVchrPaymentMethodCode);
    }

    /**
     * This method...
     *
     * @param method
     * @deprecated This method should not be used. There is no protected attribute to store this value. The associated getter
     *             retrieves the value remotely.
     */
    @Deprecated
    public void setDisbVchrPaymentMethodName(String method) {
    }

    /**
     * Returns the name associated with the documentation location name
     *
     * @return String
     */
    public String getDisbursementVoucherDocumentationLocationName() {
        return new PaymentDocumentationLocationValuesFinder().getKeyLabel(disbursementVoucherDocumentationLocationCode);
    }

    /**
     * This method...
     *
     * @param name
     * @deprecated This method should not be used. There is no protected attribute to store this value. The associated getter
     *             retrieves the value remotely.
     */
    @Deprecated
    public void setDisbursementVoucherDocumentationLocationName(String name) {
    }


    /**
     * Gets the disbVchrBankCode attribute.
     *
     * @return Returns the disbVchrBankCode.
     */
    public String getDisbVchrBankCode() {
        return disbVchrBankCode;
    }


    /**
     * Sets the disbVchrBankCode attribute value.
     *
     * @param disbVchrBankCode The disbVchrBankCode to set.
     */
    public void setDisbVchrBankCode(String disbVchrBankCode) {
        this.disbVchrBankCode = disbVchrBankCode;
    }

    /**
     * Gets the bank attribute.
     *
     * @return Returns the bank.
     */
    @Override
    public Bank getBank() {
        return bank;
    }


    /**
     * Sets the bank attribute value.
     *
     * @param bank The bank to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }


    /**
     * Convenience method to set dv payee detail fields based on a given vendor.
     *
     * @param vendor
     */
    public void templateVendor(VendorDetail vendor, VendorAddress vendorAddress) {
        if (vendor == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(KFSConstants.PaymentPayeeTypes.VENDOR);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(vendor.getVendorNumber());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(vendor.getVendorName());

        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(vendor.getVendorHeader().getVendorForeignIndicator());


        if (ObjectUtils.isNotNull(vendorAddress) && ObjectUtils.isNotNull(vendorAddress.getVendorAddressGeneratedIdentifier())) {
            this.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(vendorAddress.getVendorAddressGeneratedIdentifier().toString());
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(vendorAddress.getVendorLine1Address());
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(vendorAddress.getVendorLine2Address());
            this.getDvPayeeDetail().setDisbVchrPayeeCityName(vendorAddress.getVendorCityName());
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode(vendorAddress.getVendorStateCode());
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode(vendorAddress.getVendorZipCode());
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(vendorAddress.getVendorCountryCode());
        }
        else {
            this.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeCityName(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode(StringUtils.EMPTY);
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(StringUtils.EMPTY);
        }

        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(vendor.getVendorHeader().getVendorForeignIndicator());
        this.getDvPayeeDetail().setDvPayeeSubjectPaymentCode(VendorConstants.VendorTypes.SUBJECT_PAYMENT.equals(vendor.getVendorHeader().getVendorTypeCode()));
        this.getDvPayeeDetail().setDisbVchrEmployeePaidOutsidePayrollCode(getVendorService().isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier()));

        this.getDvPayeeDetail().setHasMultipleVendorAddresses(1 < vendor.getVendorAddresses().size());

        boolean w9AndW8Checked = false;
        if ( (ObjectUtils.isNotNull(vendor.getVendorHeader().getVendorW9ReceivedIndicator()) && vendor.getVendorHeader().getVendorW9ReceivedIndicator() == true) ||
             (ObjectUtils.isNotNull(vendor.getVendorHeader().getVendorW8BenReceivedIndicator()) && vendor.getVendorHeader().getVendorW8BenReceivedIndicator() == true) ) {

            w9AndW8Checked = true;
        }

    //    this.disbVchrPayeeW9CompleteCode = vendor.getVendorHeader().getVendorW8BenReceivedIndicator()  == null ? false : vendor.getVendorHeader().getVendorW8BenReceivedIndicator();
        this.disbVchrPayeeW9CompleteCode = w9AndW8Checked;

        Date vendorFederalWithholdingTaxBeginDate = vendor.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate();
        Date vendorFederalWithholdingTaxEndDate = vendor.getVendorHeader().getVendorFederalWithholdingTaxEndDate();
        java.util.Date today = getDateTimeService().getCurrentDate();
        if ((vendorFederalWithholdingTaxBeginDate != null && vendorFederalWithholdingTaxBeginDate.before(today)) && (vendorFederalWithholdingTaxEndDate == null || vendorFederalWithholdingTaxEndDate.after(today))) {
            this.disbVchrPayeeTaxControlCode = DisbursementVoucherConstants.TAX_CONTROL_CODE_BEGIN_WITHHOLDING;
        }

        // if vendor is foreign, default alien payment code to true
        if (getVendorService().isVendorForeign(vendor.getVendorHeaderGeneratedIdentifier())) {
            getDvPayeeDetail().setDisbVchrAlienPaymentCode(true);
        }
    }

    /**
     * Convenience method to set dv payee detail fields based on a given Employee.
     *
     * @param employee
     */
    public void templateEmployee(Person employee) {
        if (employee == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(KFSConstants.PaymentPayeeTypes.EMPLOYEE);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(employee.getEmployeeId());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(employee.getName());

        final ParameterService parameterService = this.getParameterService();

        if (parameterService.parameterExists(DisbursementVoucherDocument.class, DisbursementVoucherDocument.USE_DEFAULT_EMPLOYEE_ADDRESS_PARAMETER_NAME) && parameterService.getParameterValueAsBoolean(DisbursementVoucherDocument.class, DisbursementVoucherDocument.USE_DEFAULT_EMPLOYEE_ADDRESS_PARAMETER_NAME)) {
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(employee.getAddressLine1Unmasked());
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(employee.getAddressLine2Unmasked());
            this.getDvPayeeDetail().setDisbVchrPayeeCityName(employee.getAddressCityUnmasked());
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode(employee.getAddressStateProvinceCodeUnmasked());
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode(employee.getAddressPostalCodeUnmasked());
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(employee.getAddressCountryCodeUnmasked());
        } else {
            final EntityAddress address = getNonDefaultAddress(employee);
            if (address != null) {
                this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(address.getLine1Unmasked());
                this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(address.getLine2Unmasked());
                this.getDvPayeeDetail().setDisbVchrPayeeCityName(address.getCityUnmasked());
                this.getDvPayeeDetail().setDisbVchrPayeeStateCode(address.getStateProvinceCodeUnmasked());
                this.getDvPayeeDetail().setDisbVchrPayeeZipCode(address.getPostalCodeUnmasked());
                this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(address.getCountryCodeUnmasked());
            }
            else {
                this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr("");
                this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
                this.getDvPayeeDetail().setDisbVchrPayeeCityName("");
                this.getDvPayeeDetail().setDisbVchrPayeeStateCode("");
                this.getDvPayeeDetail().setDisbVchrPayeeZipCode("");
                this.getDvPayeeDetail().setDisbVchrPayeeCountryCode("");
            }
        }

        //KFSMI-8935: When an employee is inactive, the Payment Type field on DV documents should display the message "Is this payee an employee" = No
        if (employee.isActive()) {
            this.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(true);
        } else {
            this.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(false);
        }

        // I'm assuming that if a tax id type code other than 'TAX' is present, then the employee must be foreign
        for ( String externalIdentifierTypeCode : employee.getExternalIdentifiers().keySet() ) {
            if (KimConstants.PersonExternalIdentifierTypes.TAX.equals(externalIdentifierTypeCode)) {
                this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);
            }
        }
        // Determine if employee is a research subject
        ParameterEvaluator researchPaymentReasonCodeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_PAYMENT_REASONS_PARM_NM, this.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        if (researchPaymentReasonCodeEvaluator.evaluationSucceeds()) {
            if (getParameterService().parameterExists(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM)) {
                String researchPayLimit = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM);
                if (StringUtils.isNotBlank(researchPayLimit)) {
                    KualiDecimal payLimit = new KualiDecimal(researchPayLimit);

                    if (getDisbVchrCheckTotalAmount().isLessThan(payLimit)) {
                        this.getDvPayeeDetail().setDvPayeeSubjectPaymentCode(true);
                    }
                }
            }
        }

        this.disbVchrPayeeTaxControlCode = "";
        this.disbVchrPayeeW9CompleteCode = true;
    }

    /**
     * Convenience method to set dv payee detail fields based on a given customer
     *
     * @param customer - customer to use as payee
     * @param customerAddress - customer address to use for payee address
     */
    public void templateCustomer(AccountsReceivableCustomer customer, AccountsReceivableCustomerAddress customerAddress) {
        if (customer == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(KFSConstants.PaymentPayeeTypes.CUSTOMER);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(customer.getCustomerNumber());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(customer.getCustomerName());
        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);

        if (ObjectUtils.isNotNull(customerAddress) && ObjectUtils.isNotNull(customerAddress.getCustomerAddressIdentifier())) {
            this.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(customerAddress.getCustomerAddressIdentifier().toString());
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(customerAddress.getCustomerLine1StreetAddress());
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(customerAddress.getCustomerLine2StreetAddress());
            this.getDvPayeeDetail().setDisbVchrPayeeCityName(customerAddress.getCustomerCityName());
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode(customerAddress.getCustomerStateCode());
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode(customerAddress.getCustomerZipCode());
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(customerAddress.getCustomerCountryCode());
        }
        else {
            this.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber("");
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr("");
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
            this.getDvPayeeDetail().setDisbVchrPayeeCityName("");
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode("");
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode("");
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode("");
        }
    }

    /**
     * Finds the address for the given employee, matching the type in the KFS-FP / Disbursement Voucher/ DEFAULT_EMPLOYEE_ADDRESS_TYPE parameter,
     * to use as the address for the employee
     * @param employee the employee to find a non-default address for
     * @return the non-default address, or null if not found
     */
    protected EntityAddress getNonDefaultAddress(Person employee) {
        final String addressType = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherDocument.DEFAULT_EMPLOYEE_ADDRESS_TYPE_PARAMETER_NAME);
        final Entity entity = getIdentityManagementService().getEntityByPrincipalId(employee.getPrincipalId());
        if (entity != null) {
            final EntityTypeContactInfo entityEntityType = getPersonEntityEntityType(entity);
            if (entityEntityType != null) {
                final List<? extends EntityAddress> addresses = entityEntityType.getAddresses();

                return findAddressByType(addresses, addressType);
            }
        }
        return null;
    }

    /**
     * Someday this method will be in Rice.  But...'til it is...lazy loop through the entity entity types in the given
     * KimEntityInfo and return the one who has the type of "PERSON"
     * @param entityInfo the entity info to loop through entity entity types of
     * @return a found entity entity type or null if a PERSON entity entity type is not associated with the given KimEntityInfo record
     */
    protected EntityTypeContactInfo getPersonEntityEntityType(Entity entity) {
        final List<EntityTypeContactInfo> entityEntityTypes = entity.getEntityTypeContactInfos();
        int count = 0;
        EntityTypeContactInfo foundInfo = null;

        while (count < entityEntityTypes.size() && foundInfo == null) {
            if (entityEntityTypes.get(count).getEntityTypeCode().equals(KimConstants.EntityTypes.PERSON)) {
                foundInfo = entityEntityTypes.get(count);
            }
            count += 1;
        }

        return foundInfo;
    }

    /**
     * Given a List of KimEntityAddress and an address type, finds the address in the List with the given type (or null if no matching KimEntityAddress is found)
     * @param addresses the List of KimEntityAddress records to search
     * @param addressType the address type of the address to return
     * @return the found KimEntityAddress, or null if not found
     */
    protected EntityAddress findAddressByType(List<? extends EntityAddress> addresses, String addressType) {
        EntityAddress foundAddress = null;
        int count = 0;

        while (count < addresses.size() && foundAddress == null) {
            final EntityAddress currentAddress = addresses.get(count);
            if (currentAddress.isActive() && currentAddress.getAddressType().getCode().equals(addressType)) {
                foundAddress = currentAddress;
            }
            count += 1;
        }

        return foundAddress;
    }

    /**
     * @see org.kuali.rice.kns.document.Document#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (this instanceof AmountTotaling) {
            if (getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.ENROUTE)) {
                if (getParameterService().parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, UPDATE_TOTAL_AMOUNT_IN_POST_PROCESSING_PARAMETER_NAME)
                        && getParameterService().getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_DOCUMENT.class, UPDATE_TOTAL_AMOUNT_IN_POST_PROCESSING_PARAMETER_NAME)) {
                    getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
                }
            } else {
                getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
            }
        }

        if (wireTransfer != null) {
            wireTransfer.setDocumentNumber(this.documentNumber);
        }

        if (dvNonResidentAlienTax != null) {
            dvNonResidentAlienTax.setDocumentNumber(this.documentNumber);
        }

        dvPayeeDetail.setDocumentNumber(this.documentNumber);

        if (dvNonEmployeeTravel != null) {
            dvNonEmployeeTravel.setDocumentNumber(this.documentNumber);
            dvNonEmployeeTravel.setTotalTravelAmount(dvNonEmployeeTravel.getTotalTravelAmount());
        }

        if (dvPreConferenceDetail != null) {
            dvPreConferenceDetail.setDocumentNumber(this.documentNumber);
            dvPreConferenceDetail.setDisbVchrConferenceTotalAmt(dvPreConferenceDetail.getDisbVchrConferenceTotalAmt());
        }

        if (shouldClearSpecialHandling()) {
            clearSpecialHandling();
        }
    }

    /**
     * Determines if the special handling fields should be cleared, based on whether the special handling has been turned off and whether the current node is CAMPUS
     * @return true if special handling should be cleared, false otherwise
     */
    protected boolean shouldClearSpecialHandling() {
        if (!isDisbVchrSpecialHandlingCode()) {
            // are we at the campus route node?
            List<RouteNodeInstance> currentNodes = getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeInstances();
            return (currentNodes.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS));
        }
        return false;
    }

    /**
     * Clears all set special handling fields
     */
    protected void clearSpecialHandling() {
        DisbursementVoucherPayeeDetail payeeDetail = getDvPayeeDetail();

        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingPersonName())) {
            payeeDetail.setDisbVchrSpecialHandlingPersonName(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingLine1Addr())) {
            payeeDetail.setDisbVchrSpecialHandlingLine1Addr(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingLine2Addr())) {
            payeeDetail.setDisbVchrSpecialHandlingLine2Addr(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingCityName())) {
            payeeDetail.setDisbVchrSpecialHandlingCityName(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingStateCode())) {
            payeeDetail.setDisbVchrSpecialHandlingStateCode(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingZipCode())) {
            payeeDetail.setDisbVchrSpecialHandlingZipCode(null);
        }
        if (!StringUtils.isBlank(payeeDetail.getDisbVchrSpecialHandlingCountryCode())) {
            payeeDetail.setDisbVchrSpecialHandlingCountryCode(null);
        }
    }

    /**
     * This method is overridden to populate some local variables that are not persisted to the database. These values need to be
     * computed and saved to the DV Payee Detail BO so they can be serialized to XML for routing. Some of the routing rules rely on
     * these variables.
     *
     * @see org.kuali.rice.kns.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        DisbursementVoucherPayeeDetail payeeDetail = getDvPayeeDetail();

        if (payeeDetail.isVendor()) {
            payeeDetail.setDisbVchrPayeeEmployeeCode(getVendorService().isVendorInstitutionEmployee(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger()));
            payeeDetail.setDvPayeeSubjectPaymentCode(getVendorService().isSubjectPaymentVendor(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger()));
        }
        else if (payeeDetail.isEmployee()) {

            // Determine if employee is a research subject
            ParameterEvaluator researchPaymentReasonCodeEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_PAYMENT_REASONS_PARM_NM, payeeDetail.getDisbVchrPaymentReasonCode());
            if (researchPaymentReasonCodeEvaluator.evaluationSucceeds()) {
                if (getParameterService().parameterExists(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM)) {
                    String researchPayLimit = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM);
                    if (StringUtils.isNotBlank(researchPayLimit)) {
                        KualiDecimal payLimit = new KualiDecimal(researchPayLimit);

                        if (getDisbVchrCheckTotalAmount().isLessThan(payLimit)) {
                            payeeDetail.setDvPayeeSubjectPaymentCode(true);
                        }
                    }
                }
            }
        }

        super.populateDocumentForRouting(); // Call last, serializes to XML
    }

    /**
     * Clears information that might have been entered for sub tables, but because of changes to the document is longer needed and
     * should not be persisted.
     */
    protected void cleanDocumentData() {
        // TODO: warren: this method ain't called!!! maybe this should be called by prepare for save above
        if (!DisbursementVoucherConstants.PAYMENT_METHOD_WIRE.equals(this.getDisbVchrPaymentMethodCode()) && !DisbursementVoucherConstants.PAYMENT_METHOD_DRAFT.equals(this.getDisbVchrPaymentMethodCode())) {
            getBusinessObjectService().delete(wireTransfer);
            wireTransfer = null;
        }

        if (!dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            getBusinessObjectService().delete(dvNonResidentAlienTax);
            dvNonResidentAlienTax = null;
        }

        DisbursementVoucherPaymentReasonService paymentReasonService = SpringContext.getBean(DisbursementVoucherPaymentReasonService.class);
        String paymentReasonCode = this.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (!paymentReasonService.isNonEmployeeTravelPaymentReason(paymentReasonCode)) {
            getBusinessObjectService().delete(dvNonEmployeeTravel);
            dvNonEmployeeTravel = null;
        }

        if (!paymentReasonService.isPrepaidTravelPaymentReason(paymentReasonCode)) {
            getBusinessObjectService().delete(dvPreConferenceDetail);
            dvPreConferenceDetail = null;
        }
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        initiateDocument();

        // clear fields
        setDisbVchrContactPhoneNumber(StringUtils.EMPTY);
        setDisbVchrContactEmailId(StringUtils.EMPTY);
        setDisbVchrPayeeTaxControlCode(StringUtils.EMPTY);

        // clear nra
        SpringContext.getBean(DisbursementVoucherTaxService.class).clearNRATaxLines(this);
        setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());

        // clear waive wire
        getWireTransfer().setWireTransferFeeWaiverIndicator(false);

        // check vendor id number to see if still valid, if not, clear dvPayeeDetail; otherwise, use the current dvPayeeDetail as is
        if (!StringUtils.isBlank(getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            VendorDetail vendorDetail = getVendorService().getVendorDetail(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), dvPayeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            if (vendorDetail == null) {
                dvPayeeDetail = new DisbursementVoucherPayeeDetail();;
                getDvPayeeDetail().setDisbVchrPayeeIdNumber(StringUtils.EMPTY);
                KNSGlobalVariables.getMessageList().add(KFSKeyConstants.WARNING_DV_PAYEE_NONEXISTANT_CLEARED);
            }
        }

        // this copied DV has not been extracted
        this.extractDate = null;
        this.paidDate = null;
        this.cancelDate = null;
        getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.INITIATED);
    }

    /**
     * generic, shared logic used to initiate a dv document
     */
    public void initiateDocument() {
        PhoneNumberService phoneNumberService = SpringContext.getBean(PhoneNumberService.class);
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        setDisbVchrContactPersonName(currentUser.getName());

        if(!phoneNumberService.isDefaultFormatPhoneNumber(currentUser.getPhoneNumber())) {
            setDisbVchrContactPhoneNumber(phoneNumberService.formatNumberIfPossible(currentUser.getPhoneNumber()));
        }

        setDisbVchrContactEmailId(currentUser.getEmailAddress());
        ChartOrgHolder chartOrg = SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getPrimaryOrganization(currentUser, KFSConstants.ParameterNamespaces.FINANCIAL);

        // Does a valid campus code exist for this person?  If so, simply grab
        // the campus code via the business object service.
        if (chartOrg != null && chartOrg.getOrganization() != null) {
            setCampusCode(chartOrg.getOrganization().getOrganizationPhysicalCampusCode());
        }
        // A valid campus code was not found; therefore, use the default affiliated
        // campus code.
        else {
            String affiliatedCampusCode = currentUser.getCampusCode();
            setCampusCode(affiliatedCampusCode);
        }

        // due date
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setDisbursementVoucherDueDate(new Date(calendar.getTimeInMillis()));

        // default doc location
        if (StringUtils.isBlank(getDisbursementVoucherDocumentationLocationCode())) {
            setDisbursementVoucherDocumentationLocationCode(getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DEFAULT_DOC_LOCATION_PARM_NM));
        }

        // default bank code
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(this.getClass());
        if (defaultBank != null) {
            this.disbVchrBankCode = defaultBank.getBankCode();
            this.bank = defaultBank;
        }
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @SuppressWarnings("unchecked")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        if (dvNonEmployeeTravel != null) {
            managedLists.add(dvNonEmployeeTravel.getDvNonEmployeeExpenses());
            managedLists.add(dvNonEmployeeTravel.getDvPrePaidEmployeeExpenses());
        }

        if (dvPreConferenceDetail != null) {
            managedLists.add(dvPreConferenceDetail.getDvPreConferenceRegistrants());
        }

        return managedLists;
    }

    /**
     * Returns check total.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return this.getDisbVchrCheckTotalAmount();
    }

    /**
     * Returns true if accounting line debit
     *
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @return true if document is debit
     * @see IsDebitUtils#isDebitConsideringNothingPositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.kns.rule.AccountingLineRule#isDebit(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // disallow error corrections
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        isDebitUtils.disallowErrorCorrectionDocumentCheck(this);

        if (getDvNonResidentAlienTax() != null && getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText() != null && getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText().contains(((AccountingLine) postable).getSequenceNumber().toString())) {
            return postable.getAmount().isPositive();
        }

        return isDebitUtils.isDebitConsideringNothingPositiveOnly(this, postable);
    }

    /**
     * Override to change the doc type based on payment method. This is needed to pick up different offset definitions.
     *
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in submitted accounting document
     * @param explicitEntry explicit GLPE
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry) {

        /* change document type based on payment method to pick up different offsets */
        if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK.equals(getDisbVchrPaymentMethodCode())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
            }
            explicitEntry.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
        }
        else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
            }
            explicitEntry.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_WTFD);
        }
    }

    /**
     * Return true if GLPE's are generated successfully (i.e. there are either 0 GLPE's or 1 GLPE in disbursement voucher document)
     *
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if GLPE's are generated successfully
     * @see org.kuali.rice.kns.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.rice.kns.document.FinancialDocument,org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (getGeneralLedgerPendingEntries() == null || getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            return true;
        }

        /*
         * only generate additional charge entries for payment method wire charge, and if the fee has not been waived
         */
        if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getDisbVchrPaymentMethodCode()) && !getWireTransfer().isWireTransferFeeWaiverIndicator()) {
            LOG.debug("generating wire charge gl pending entries.");

            // retrieve wire charge
            WireCharge wireCharge = getPaymentSourceHelperService().retrieveCurrentYearWireCharge();

            // generate debits
            GeneralLedgerPendingEntry chargeEntry = getPaymentSourceHelperService().processWireChargeDebitEntries(this, sequenceHelper, wireCharge);

            // generate credits
            getPaymentSourceHelperService().processWireChargeCreditEntries(this, sequenceHelper, wireCharge, chargeEntry);
        }

        // for wire or drafts generate bank offset entry (if enabled), for ACH and checks offset will be generated by PDP
        if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getDisbVchrPaymentMethodCode()) || KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_DRAFT.equals(getDisbVchrPaymentMethodCode())) {
            getPaymentSourceHelperService().generateDocumentBankOffsetEntries(this, sequenceHelper, DisbursementVoucherConstants.DOCUMENT_TYPE_WTFD);
        }

        return true;
    }

    /**
     * Gets the payeeAssigned attribute. This method returns a flag that is used to indicate if the payee type and value has been
     * set on the DV. This value is used to determine the correct page that should be loaded by the DV flow.
     *
     * @return Returns the payeeAssigned.
     */
    public boolean isPayeeAssigned() {
        // If value is false, check state of document. We should assume payee is assigned if document has been saved.
        // Otherwise, value will be set during creation process.
        if (!payeeAssigned) {
            payeeAssigned = !this.getDocumentHeader().getWorkflowDocument().isInitiated();
        }
        return payeeAssigned;
    }


    /**
     * Sets the payeeAssigned attribute value.
     *
     * @param payeeAssigned The payeeAssigned to set.
     */
    public void setPayeeAssigned(boolean payeeAssigned) {
        this.payeeAssigned = payeeAssigned;
    }

    /**
     * Gets the editW9W8BENbox attribute. This method returns a flag that is used to indicate if the W9/W8BEN check box can be edited
     * by the initiator on the DV.
     *
     * @return Returns the editW9W8BENbox.
     */
    public boolean isEditW9W8BENbox() {
        String initiatorPrincipalID = this.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        if (GlobalVariables.getUserSession().getPrincipalId().equals(initiatorPrincipalID)) {
            editW9W8BENbox = true;
        }
        return editW9W8BENbox;
    }

    /**
     * Sets the editW9W8BENbox attribute value.
     *
     * @param editW9W8BENbox The editW9W8BENbox to set.
     */
    public void setEditW9W8BENbox(boolean editW9W8BENbox) {
        this.editW9W8BENbox = editW9W8BENbox;
    }

    /**
     * Gets the disbVchrPdpBankCode attribute.
     *
     * @return Returns the disbVchrPdpBankCode.
     */
    public String getDisbVchrPdpBankCode() {
        return disbVchrPdpBankCode;
    }

    /**
     * Sets the disbVchrPdpBankCode attribute value.
     *
     * @param disbVchrPdpBankCode The disbVchrPDPBankCode to set.
     */
    public void setDisbVchrPdpBankCode(String disbVchrPdpBankCode) {
        this.disbVchrPdpBankCode = disbVchrPdpBankCode;
    }

    /* Start TEM REFUND Merge */
    public boolean isRefundIndicator() {
        return refundIndicator;
    }

    public void setRefundIndicator(boolean refundIndicator) {
        this.refundIndicator = refundIndicator;
    }
    /* End TEM REFUND Merge */

    /**
     * @return whether this document should be paid out immediately from PDP
     */
    public boolean isImmediatePaymentIndicator() {
        return immediatePaymentIndicator;
    }

    /**
     * Sets whether this document should be paid out as quickly as possible from PDP
     * @param immediatePaymentIndicator true if this should be immediately disbursed; false otherwise
     */
    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String documentTitle = super.getDocumentTitle();
        return this.buildDocumentTitle(documentTitle);
    }

    /**
     * build document title based on the properties of current document
     *
     * @param the default document title
     * @return the combine information of the given title and additional payment indicators
     */
    protected String buildDocumentTitle(String title) {
        DisbursementVoucherPayeeDetail payee = getDvPayeeDetail();
        if(payee == null) {
            return title;
        }

        DisbursementVoucherPaymentReasonService paymentReasonService = SpringContext.getBean(DisbursementVoucherPaymentReasonService.class);
        if (title != null && title.contains(DisbursementVoucherConstants.DV_DOC_NAME)) {
            String paymentCodeAndDescription = StringUtils.EMPTY;

            if(StringUtils.isNotBlank(payee.getDisbVchrPaymentReasonCode())){
                PaymentReasonCode paymentReasonCode = paymentReasonService.getPaymentReasonByPrimaryId(payee.getDisbVchrPaymentReasonCode());

                paymentCodeAndDescription = ObjectUtils.isNotNull(paymentReasonCode) ? paymentReasonCode.getCodeAndDescription() : paymentCodeAndDescription;
            }

            String replaceTitle = DisbursementVoucherConstants.DV_DOC_NAME + " " + paymentCodeAndDescription;
            title = title.replace(DisbursementVoucherConstants.DV_DOC_NAME, replaceTitle);
        }

        Object[] indicators = new String[4];
        indicators[0] = payee.isEmployee() ? AdHocPaymentIndicator.EMPLOYEE_PAYEE : AdHocPaymentIndicator.OTHER;
        indicators[1] = payee.isDisbVchrAlienPaymentCode() ? AdHocPaymentIndicator.ALIEN_PAYEE : AdHocPaymentIndicator.OTHER;

        String taxControlCode = this.getDisbVchrPayeeTaxControlCode();
        if (StringUtils.equals(taxControlCode, DisbursementVoucherDocument.TAX_CONTROL_BACKUP_HOLDING) || StringUtils.equals(taxControlCode,DisbursementVoucherDocument.TAX_CONTROL_HOLD_PAYMENTS)) {
            indicators[2] =  AdHocPaymentIndicator.TAX_CONTROL_REQUIRING_TAX_REVIEW ;
        }else{
            indicators[2] =  AdHocPaymentIndicator.OTHER;
        }

        boolean isTaxReviewRequired = paymentReasonService.isTaxReviewRequired(payee.getDisbVchrPaymentReasonCode());
        indicators[3] = isTaxReviewRequired ? AdHocPaymentIndicator.PAYMENT_REASON_REQUIRING_TAX_REVIEW : AdHocPaymentIndicator.OTHER;

        for(Object indicator : indicators) {
            if(!AdHocPaymentIndicator.OTHER.equals(indicator)) {
                String adHocPaymentIndicator = MessageFormat.format(" [{0}:{1}:{2}:{3}]", indicators);
                return title + adHocPaymentIndicator;
            }
        }

        return title;
    }


    /**
     * Provides answers to the following splits: PayeeIsPurchaseOrderVendor RequiresTaxReview RequiresTravelReview
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(DisbursementVoucherDocument.PAYEE_IS_PURCHASE_ORDER_VENDOR_SPLIT)) {
            return isPayeePurchaseOrderVendor();
        }
        if (nodeName.equals(DisbursementVoucherDocument.DOCUMENT_REQUIRES_TAX_REVIEW_SPLIT)) {
            return isTaxReviewRequired();
        }
        if (nodeName.equals(DisbursementVoucherDocument.DOCUMENT_REQUIRES_TRAVEL_REVIEW_SPLIT)) {
            return isTravelReviewRequired();
        }
        if (nodeName.equals(DOCUMENT_REQUIRES_SEPARATION_OF_DUTIES)) {
            return isSeparationOfDutiesReviewRequired();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }


    protected boolean isSeparationOfDutiesReviewRequired() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        boolean sepOfDutiesRequired = parameterService.getParameterValueAsBoolean(KFSConstants.CoreModuleNamespaces.FINANCIAL, DISBURSEMENT_VOUCHER_TYPE, SEPARATION_OF_DUTIES_PARAMETER_NAME);

        if (sepOfDutiesRequired) {
            try {
                Set<Person> priorApprovers = getAllPriorApprovers();
                // The payee cannot be the only approver
                String payeeEmployeeId = this.getDvPayeeDetail().getDisbVchrPayeeIdNumber();
                Person payee = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(payeeEmployeeId);
                // If there is only one approver, and that approver is also the payee, then Separation of Duties is required.
                boolean priorApproverIsPayee = priorApprovers.contains(payee);
                boolean onlyOneApprover = (priorApprovers.size() == 1);
                if ( priorApproverIsPayee && onlyOneApprover) {
                    return true;
                }

                // if there are more than 0 prior approvers which means there had been at least another approver than the current approver
                // then no need for separation of duties
                if (priorApprovers.size() > 0) {
                    return false;
                }
            }catch (WorkflowException we) {
                LOG.error("Exception while attempting to retrieve all prior approvers from workflow: " + we);
            }
        }
            return false;
    }

    public Set<Person> getAllPriorApprovers() throws WorkflowException {
        PersonService personService = KimApiServiceLocator.getPersonService();
         List<ActionTaken> actionsTaken = getDocumentHeader().getWorkflowDocument().getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();

        for (ActionTaken actionTaken : actionsTaken) {
            if (KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }

    /**
     * @return true if the payee is a purchase order vendor and therefore should receive vendor review, false otherwise
     */
    protected boolean isPayeePurchaseOrderVendor() {
        if (!this.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode().equals(KFSConstants.PaymentPayeeTypes.VENDOR)) {
            return false;
        }

        VendorDetail vendor = getVendorService().getByVendorNumber(this.getDvPayeeDetail().getDisbVchrPayeeIdNumber());
        if (vendor == null) {
            return false;
        }

        vendor.refreshReferenceObject("vendorHeader");
        return vendor.getVendorHeader().getVendorTypeCode().equals(DisbursementVoucherDocument.PURCHASE_ORDER_VENDOR_TYPE);
    }

    /**
     * Tax review is required under the following circumstances: the payee was an employee the payee was a non-resident alien vendor
     * the tax control code = "B" or "H" the payment reason code was "D" the payment reason code was "M" and the campus was listed
     * in the CAMPUSES_TAXED_FOR_MOVING_REIMBURSEMENTS_PARAMETER_NAME parameter
     *
     * @return true if any of the above conditions exist and this document should receive tax review, false otherwise
     */
    protected boolean isTaxReviewRequired() {
        if (isPayeePurchaseOrderVendorHasWithholding()) {
            return true;
        }

        String payeeTypeCode = this.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode();
        if (payeeTypeCode.equals(KFSConstants.PaymentPayeeTypes.EMPLOYEE)) {
            return false;
        } else if (payeeTypeCode.equals(KFSConstants.PaymentPayeeTypes.VENDOR)) {
            if(vendorService.isVendorInstitutionEmployee(this.getDvPayeeDetail().getDisbVchrVendorHeaderIdNumberAsInteger())){
                return true;
            }
        }

        if (payeeTypeCode.equals(KFSConstants.PaymentPayeeTypes.VENDOR) && this.getVendorService().isVendorForeign(getDvPayeeDetail().getDisbVchrVendorHeaderIdNumberAsInteger())) {
            return true;
        }

        String taxControlCode = this.getDisbVchrPayeeTaxControlCode();
        if (StringUtils.equals(taxControlCode, DisbursementVoucherDocument.TAX_CONTROL_BACKUP_HOLDING) || StringUtils.equals(taxControlCode,DisbursementVoucherDocument.TAX_CONTROL_HOLD_PAYMENTS)) {
            return true;
        }

        String paymentReasonCode = this.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (this.getDvPymentReasonService().isDecedentCompensationPaymentReason(paymentReasonCode)) {
            return true;
        }

        if (this.getDvPymentReasonService().isMovingPaymentReason(paymentReasonCode) && taxedCampusForMovingReimbursements()) {
            return true;
        }

        if (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(this.getClass(), DisbursementVoucherDocument.PAYMENT_REASONS_REQUIRING_TAX_REVIEW_PARAMETER_NAME, paymentReasonCode).evaluationSucceeds()) {
            return true;
        }

        return false;
    }

    /**
     * @return true if the payee is a vendor and has withholding dates therefore should receive tax review, false otherwise
     */
    protected boolean isPayeePurchaseOrderVendorHasWithholding() {
        if (!this.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode().equals(KFSConstants.PaymentPayeeTypes.VENDOR)) {
            return false;
        }

        VendorDetail vendor = getVendorService().getByVendorNumber(this.getDvPayeeDetail().getDisbVchrPayeeIdNumber());
        if (vendor == null) {
            return false;
        }

        vendor.refreshReferenceObject("vendorHeader");
        return (vendor.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate()!= null || vendor.getVendorHeader().getVendorFederalWithholdingTaxEndDate()!= null);
    }

    /**
     * Rolls the disbursement voucher back to a cancelled state
     * @see org.kuali.kfs.sys.document.PaymentSource#cancelPayment(java.sql.Date)
     */
    @Override
    public void cancelPayment(Date cancelDate) {
        getDisbursementVoucherPaymentService().cancelDisbursementVoucher(this, cancelDate);
    }

    /**
     * Returns DVCA
     * @see org.kuali.kfs.sys.document.PaymentSource#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType() {
        return DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH;
    }


    /**
     * Determines if the campus this DV is related to is taxed (and should get tax review routing) for moving reimbursements
     *
     * @return true if the campus is taxed for moving reimbursements, false otherwise
     */
    protected boolean taxedCampusForMovingReimbursements() {
        return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(this.getClass(), DisbursementVoucherConstants.CAMPUSES_TAXED_FOR_MOVING_REIMBURSEMENTS_PARM_NM, this.getCampusCode()).evaluationSucceeds();
    }

    /**
     * Travel review is required under the following circumstances: payment reason code is "P" or "N"
     *
     * @return
     */
    public boolean isTravelReviewRequired() {
        String paymentReasonCode = this.getDvPayeeDetail().getDisbVchrPaymentReasonCode();

        return this.getDvPymentReasonService().isPrepaidTravelPaymentReason(paymentReasonCode) || this.getDvPymentReasonService().isNonEmployeeTravelPaymentReason(paymentReasonCode);
        }

    /**
     * Overridden to immediately extract DV, if it has been marked for immediate extract
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            if (isImmediatePaymentIndicator()) {
                getDisbursementVoucherExtractService().extractSingleImmediatePayment(this);
            }
        }
    }

    @Override
    protected ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    protected VendorService getVendorService() {
        if ( vendorService == null ) {
            vendorService = SpringContext.getBean(VendorService.class);
        }
        return vendorService;
    }

    @Override
    protected BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    /**
     * Gets the dvPymentReasonService attribute.
     *
     * @return Returns the dvPymentReasonService.
     */
    public DisbursementVoucherPaymentReasonService getDvPymentReasonService() {
        if (dvPymentReasonService == null) {
            dvPymentReasonService = SpringContext.getBean(DisbursementVoucherPaymentReasonService.class);
        }
        return dvPymentReasonService;
    }


    /**
     * Gets the identityManagementService attribute.
     * @return Returns the identityManagementService.
     */
    public static IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    /**
     * Sets the identityManagementService attribute value.
     * @param identityManagementService The identityManagementService to set.
     */
    public static void setIdentityManagementService(IdentityManagementService identityManagementService) {
        DisbursementVoucherDocument.identityManagementService = identityManagementService;
    }

    /**
     * @return the default implementation of the PaymentSourceExtractionService
     */
    public static PaymentSourceExtractionService getDisbursementVoucherExtractService() {
        if (paymentSourceExtractionService == null) {
            paymentSourceExtractionService = SpringContext.getBean(PaymentSourceExtractionService.class);
        }
        return paymentSourceExtractionService;
    }

    /**
     * @return the default implementation of the PaymentSourceHelperService
     */
    public static PaymentSourceHelperService getPaymentSourceHelperService() {
        if (paymentSourceHelperService == null) {
            paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
        }
        return paymentSourceHelperService;
    }

    public static DisbursementVoucherPaymentService getDisbursementVoucherPaymentService() {
        if (disbursementVoucherPaymentService == null) {
            disbursementVoucherPaymentService = SpringContext.getBean(DisbursementVoucherPaymentService.class);
        }
        return disbursementVoucherPaymentService;
    }

    /**
     * @return Returns the disbExcptAttachedIndicator.
     */
    public boolean isDisbExcptAttachedIndicator() {
        return disbExcptAttachedIndicator;
    }

    /**
     * @param disbExcptAttachedIndicator The disbExcptAttachedIndicator to set.
     */
    public void setDisbExcptAttachedIndicator(boolean disbExcptAttachedIndicator) {
        this.disbExcptAttachedIndicator = disbExcptAttachedIndicator;
    }


    /**
     * RQ_AP_0760: Ability to view disbursement information on the
     * Disbursement Voucher Document.
     *
     * This method returns the document type of payment detail of the
     * Disbursement Voucher Document. It is invoked when the user clicks
     * on the disbursement info button on the Pre-Disbursement Processor
     * Status tab on Disbursement Voucher Document.
     *
     *
     * @return
     */
    public String getPaymentDetailDocumentType() {
        return DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH;
    }
}
