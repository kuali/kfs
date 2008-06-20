/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.module.financial.document;

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Copyable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.document.AmountTotaling;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.bo.BasicFormatWithLineDescriptionAccountingLineParser;
import org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.DisbursementVoucherWireTransfer;
import org.kuali.module.financial.bo.WireCharge;
import org.kuali.module.financial.lookup.keyvalues.DisbursementVoucherDocumentationLocationValuesFinder;
import org.kuali.module.financial.lookup.keyvalues.PaymentMethodValuesFinder;
import org.kuali.module.financial.rules.DisbursementVoucherDocumentRule;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherTaxService;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the DisbursementVoucher document in Kuali.
 */
public class DisbursementVoucherDocument extends AccountingDocumentBase implements Copyable, AmountTotaling {
    private static Logger LOG = Logger.getLogger(DisbursementVoucherDocument.class);
    
    private Integer finDocNextRegistrantLineNbr;
    private String disbVchrContactPersonName;
    private String disbVchrContactPhoneNumber;
    private String disbVchrContactEmailId;
    private Date disbursementVoucherDueDate;
    private boolean disbVchrAttachmentCode;
    private boolean disbVchrSpecialHandlingCode;
    private KualiDecimal disbVchrCheckTotalAmount;
    private boolean disbVchrForeignCurrencyInd;
    private String disbursementVoucherDocumentationLocationCode;
    private String disbVchrCheckStubText;
    private boolean dvCheckStubOverflowCode;
    private String campusCode;
    private String disbVchrPayeeTaxControlCode;
    private boolean disbVchrPayeeChangedInd;
    private String disbursementVoucherCheckNbr;
    private Timestamp disbursementVoucherCheckDate;
    private boolean disbVchrPayeeW9CompleteCode;
    private String disbVchrPaymentMethodCode;
    private boolean exceptionIndicator;
    private Date extractDate;
    private Date paidDate;
    private Date cancelDate;

    private boolean payeeAssigned = false;
    
    private DocumentHeader financialDocument;
    private DisbursementVoucherDocumentationLocation disbVchrDocumentationLoc;
    private DisbursementVoucherNonEmployeeTravel dvNonEmployeeTravel;
    private DisbursementVoucherNonResidentAlienTax dvNonResidentAlienTax;
    private DisbursementVoucherPayeeDetail dvPayeeDetail;
    private DisbursementVoucherPreConferenceDetail dvPreConferenceDetail;
    private DisbursementVoucherWireTransfer dvWireTransfer;

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
        dvWireTransfer = new DisbursementVoucherWireTransfer();
        disbVchrCheckTotalAmount = KualiDecimal.ZERO;
    }


    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    @Override
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        List<GeneralLedgerPendingEntry> ples = new ArrayList<GeneralLedgerPendingEntry>();

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        FlexibleOffsetAccountService flexibleOffsetAccountService = SpringContext.getBean(FlexibleOffsetAccountService.class);

        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);

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

                        ple = new GeneralLedgerPendingEntry( ple );
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
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the disbVchrDocumentationLoc attribute.
     * 
     * @return Returns the disbVchrDocumentationLoc
     */
    public DisbursementVoucherDocumentationLocation getDisbVchrDocumentationLoc() {
        return disbVchrDocumentationLoc;
    }


    /**
     * Sets the disbVchrDocumentationLoc attribute.
     * 
     * @param disbVchrDocumentationLoc The disbVchrDocumentationLoc to set.
     * @deprecated
     */
    public void setDisbVchrDocumentationLoc(DisbursementVoucherDocumentationLocation disbVchrDocumentationLoc) {
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
     * @return Returns the dvWireTransfer.
     */
    public DisbursementVoucherWireTransfer getDvWireTransfer() {
        return dvWireTransfer;
    }

    /**
     * @param dvWireTransfer The dvWireTransfer to set.
     */
    public void setDvWireTransfer(DisbursementVoucherWireTransfer dvWireTransfer) {
        this.dvWireTransfer = dvWireTransfer;
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
     * @return Returns the cancelDate.
     */
    public Date getCancelDate() {
        return cancelDate;
    }

    /**
     * Sets the cancelDate attribute value.
     * @param cancelDate The cancelDate to set.
     */
    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    /**
     * Gets the extractDate attribute. 
     * @return Returns the extractDate.
     */
    public Date getExtractDate() {
        return extractDate;
    }

    /**
     * Sets the extractDate attribute value.
     * @param extractDate The extractDate to set.
     */
    public void setExtractDate(Date extractDate) {
        this.extractDate = extractDate;
    }

    /**
     * Gets the paidDate attribute. 
     * @return Returns the paidDate.
     */
    public Date getPaidDate() {
        return paidDate;
    }

    /**
     * Sets the paidDate attribute value.
     * @param paidDate The paidDate to set.
     */
    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
    
    /**
     * Based on which pdp dates are present (extract, paid, canceled), determines a String for the status
     * @return a String representation of the status
     */
    public String getDisbursementVoucherPdpStatus() {
        if (cancelDate != null) {
            return "Canceled";
        } else if (paidDate != null) {
            return "Paid";
        } else if (extractDate != null) {
            return "Extracted";
        } else {
            return "Pre-Extraction";
        }
    }
    
    /**
     * Pretends to set the PDP status for this document
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
     * 
     * This method...
     * @param method
     * @deprecated This method should not be used.  There is no private attribute to store this value.
     * The associated getter retrieves the value remotely.  
     */
    public void setDisbVchrPaymentMethodName(String method) {
    }

    /**
     * Returns the name associated with the documentation location name
     * 
     * @return String
     */
    public String getDisbursementVoucherDocumentationLocationName() {
        return new DisbursementVoucherDocumentationLocationValuesFinder().getKeyLabel(disbursementVoucherDocumentationLocationCode);
    }

    /**
     * 
     * This method...
     * @param name
     * @deprecated This method should not be used.  There is no private attribute to store this value.
     * The associated getter retrieves the value remotely.  
     */
    public void setDisbursementVoucherDocumentationLocationName(String name) {
    }

    /**
     * Convenience method to set dv payee detail fields based on a given vendor.
     * 
     * @param vendor
     */
    public void templatePayee(VendorDetail vendor, VendorAddress vendorAddress) {
        if (vendor == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(vendor.getVendorNumber());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(vendor.getVendorName());
        
        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(vendor.getVendorHeader().getVendorForeignIndicator());

        if(ObjectUtils.isNull(vendorAddress) || ObjectUtils.isNull(vendorAddress.getVendorAddressGeneratedIdentifier())) {
            for(VendorAddress addr : vendor.getVendorAddresses()) {
                if(addr.isVendorDefaultAddressIndicator()) {
                    vendorAddress = addr;
                    break;
                }
            }
        }

        if(ObjectUtils.isNotNull(vendorAddress) && ObjectUtils.isNotNull(vendorAddress.getVendorAddressGeneratedIdentifier())) {
            this.getDvPayeeDetail().setDisbVchrVendorAddressIdNumber(vendorAddress.getVendorAddressGeneratedIdentifier().toString());
            this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(vendorAddress.getVendorLine1Address());
            this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(vendorAddress.getVendorLine2Address());
            this.getDvPayeeDetail().setDisbVchrPayeeCityName(vendorAddress.getVendorCityName());
            this.getDvPayeeDetail().setDisbVchrPayeeStateCode(vendorAddress.getVendorStateCode());
            this.getDvPayeeDetail().setDisbVchrPayeeZipCode(vendorAddress.getVendorZipCode());
            this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(vendorAddress.getVendorCountryCode());
        }

        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(vendor.getVendorHeader().getVendorForeignIndicator());
        this.getDvPayeeDetail().setDvPayeeSubjectPaymentCode(VendorConstants.VendorTypes.SUBJECT_PAYMENT.equals(vendor.getVendorHeader().getVendorTypeCode()));

        this.getDvPayeeDetail().setHasMultipleVendorAddresses(1 < vendor.getVendorAddresses().size());
        
        this.disbVchrPayeeW9CompleteCode = vendor.getVendorHeader().getVendorW9ReceivedIndicator();
    }

    /**
     * Convenience method to set dv payee detail fields based on a given Employee.
     * 
     * @param employee
     */
    public void templateEmployee(UniversalUser employee) {
        if (employee == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(employee.getPersonUniversalIdentifier());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(employee.getPersonName());
        
        this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(employee.getPersonCampusAddress());
        this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
        this.getDvPayeeDetail().setDisbVchrPayeeCityName("");
        this.getDvPayeeDetail().setDisbVchrPayeeStateCode("");
        this.getDvPayeeDetail().setDisbVchrPayeeZipCode("");
        this.getDvPayeeDetail().setDisbVchrPayeeCountryCode("");

        this.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(true);
        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);

        this.disbVchrPayeeTaxControlCode = "";
        this.disbVchrPayeeW9CompleteCode = true;
    }

    /**
     * @see org.kuali.core.document.Document#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (this instanceof AmountTotaling) {
            getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
        }

        if (dvWireTransfer != null) {
            dvWireTransfer.setDocumentNumber(this.documentNumber);
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
    }

    /**
     * This method is overridden to populate some local variables that are not persisted to the database.  These values 
     * need to be computed and saved to the DV Payee Detail BO so they can be serialized to XML for routing.  Some of 
     * the routing rules rely on these variables.
     * 
     * @see org.kuali.core.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        DisbursementVoucherPayeeDetail payeeDetail = getDvPayeeDetail();
        
        if(StringUtils.equals(payeeDetail.getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
            payeeDetail.setDisbVchrAlienPaymentCode(SpringContext.getBean(VendorService.class).isVendorForeign(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger()));
            payeeDetail.setDisbVchrPayeeEmployeeCode(SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger()));
            payeeDetail.setDvPayeeSubjectPaymentCode(SpringContext.getBean(VendorService.class).isSubjectPaymentVendor(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger()));
        }
        else if(StringUtils.equals(payeeDetail.getDisbursementVoucherPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR)) {
            payeeDetail.setDisbVchrAlienPaymentCode(false); // TODO We're assuming that an employee cannot be foreign, right?
            payeeDetail.setDisbVchrPayeeEmployeeCode(true); // If dv payee type is Employee, then flag should be true... obviously!!
//            payeeDetail.setDvPayeeSubjectPaymentCode("");
        }
            
        super.populateDocumentForRouting(); // Call last, serializes to XML        
    }
    
    /**
     * Clears information that might have been entered for sub tables, but because of changes to the document is longer needed and
     * should not be persisted.
     */
    private void cleanDocumentData() {
        // TODO: warren: this method ain't called!!! maybe this should be called by prepare for save above
        if (!DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE.equals(this.getDisbVchrPaymentMethodCode()) && !DisbursementVoucherRuleConstants.PAYMENT_METHOD_DRAFT.equals(this.getDisbVchrPaymentMethodCode())) {
            SpringContext.getBean(BusinessObjectService.class).delete(dvWireTransfer);
            dvWireTransfer = null;
        }

        if (!dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            SpringContext.getBean(BusinessObjectService.class).delete(dvNonResidentAlienTax);
            dvNonResidentAlienTax = null;
        }

        DisbursementVoucherDocumentRule dvDocRule = (DisbursementVoucherDocumentRule) SpringContext.getBean(KualiRuleService.class).getBusinessRulesInstance(this, DisbursementVoucherDocumentRule.class);

        if (!dvDocRule.isTravelNonEmplPaymentReason(this)) {
            SpringContext.getBean(BusinessObjectService.class).delete(dvNonEmployeeTravel);
            dvNonEmployeeTravel = null;
        }

        if (!dvDocRule.isTravelPrepaidPaymentReason(this)) {
            SpringContext.getBean(BusinessObjectService.class).delete(dvPreConferenceDetail);
            dvPreConferenceDetail = null;
        }
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        initiateDocument();

        // clear fields
        setDisbVchrContactPhoneNumber("");
        setDisbVchrContactEmailId("");
        getDvPayeeDetail().setDisbVchrPayeePersonName("");

        getDvPayeeDetail().setDisbVchrPayeeLine1Addr("");
        getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
        getDvPayeeDetail().setDisbVchrPayeeCityName("");
        getDvPayeeDetail().setDisbVchrPayeeStateCode("");
        getDvPayeeDetail().setDisbVchrPayeeZipCode("");
        getDvPayeeDetail().setDisbVchrPayeeCountryCode("");
        
        setDisbVchrPayeeTaxControlCode("");

        // clear nra
        SpringContext.getBean(DisbursementVoucherTaxService.class).clearNRATaxLines(this);
        setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());

        // clear waive wire
        getDvWireTransfer().setDisbursementVoucherWireTransferFeeWaiverIndicator(false);

        // check vendor id number to see if still valid, if so retrieve their last information and set in the detail inform.
        if (!StringUtils.isBlank(getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(dvPayeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), dvPayeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            VendorAddress vendorAddress = new VendorAddress();
            vendorAddress.setVendorAddressGeneratedIdentifier(dvPayeeDetail.getDisbVchrVendorAddressIdNumberAsInteger());
            vendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorAddress);
            
            if (vendorDetail == null) {
                getDvPayeeDetail().setDisbVchrPayeeIdNumber("");
                GlobalVariables.getMessageList().add(KFSKeyConstants.WARNING_DV_PAYEE_NONEXISTANT_CLEARED);
            }
            else {
                templatePayee(vendorDetail, vendorAddress);
            }
        }
    }

    /**
     * generic, shared logic used to initiate a dv document
     */
    public void initiateDocument() {
        FinancialSystemUser currentUser = GlobalVariables.getUserSession().getFinancialSystemUser();
        setDisbVchrContactPersonName(currentUser.getPersonName());
        setDisbVchrContactPhoneNumber(currentUser.getPersonLocalPhoneNumber());
        ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(currentUser,KFSConstants.Modules.CHART);
        if ( chartOrg != null && chartOrg.getOrganization() != null ) {
            setCampusCode(chartOrg.getOrganization().getOrganizationPhysicalCampusCode());
        }

        // due date
        Calendar calendar = SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setDisbursementVoucherDueDate(new Date(calendar.getTimeInMillis()));

        // default doc location
        if (StringUtils.isBlank(getDisbursementVoucherDocumentationLocationCode())) {
            setDisbursementVoucherDocumentationLocationCode(SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.DEFAULT_DOC_LOCATION_PARM_NM));
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
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
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BasicFormatWithLineDescriptionAccountingLineParser();
    }

    /**
     * Returns check total.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
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
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // disallow error corrections
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        isDebitUtils.disallowErrorCorrectionDocumentCheck(this);

        if (getDvNonResidentAlienTax() != null && getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText() != null && getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText().contains(((AccountingLine)postable).getSequenceNumber().toString())) {
            return postable.getAmount().isPositive();
        }

        return isDebitUtils.isDebitConsideringNothingPositiveOnly(this, (AccountingLine)postable);
    }
    
    /**
     * Override to change the doc type based on payment method. This is needed to pick up different offset definitions.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line in submitted accounting document 
     * @param explicitEntry explicit GLPE 
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry) {

        /* change document type based on payment method to pick up different offsets */
        if (DisbursementVoucherRuleConstants.PAYMENT_METHOD_CHECK.equals(getDisbVchrPaymentMethodCode())) {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DisbursementVoucherRuleConstants.DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DisbursementVoucherRuleConstants.DOCUMENT_TYPE_CHECKACH);
        }
        else {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + DisbursementVoucherRuleConstants.DOCUMENT_TYPE_CHECKACH);
            explicitEntry.setFinancialDocumentTypeCode(DisbursementVoucherRuleConstants.DOCUMENT_TYPE_WTFD);
        }
    }

    /**
     * Return true if GLPE's are generated successfully (i.e. there are either 0 GLPE's or 1 GLPE in dibursement voucher document)
     * 
     * @param financialDocument submitted financial document
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if GLPE's are generated successfully
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (getGeneralLedgerPendingEntries() == null || getGeneralLedgerPendingEntries().size() < 2) {
            LOG.warn("No gl entries for accounting lines.");
            // throw new RuntimeException("No gl entries for accounting lines.");
        }

        /*
         * only generate additional charge entries for payment method wire charge, and if the fee has not been waived
         */
        if (DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE.equals(getDisbVchrPaymentMethodCode()) && !getDvWireTransfer().isDisbursementVoucherWireTransferFeeWaiverIndicator()) {
            LOG.debug("generating wire charge gl pending entries.");

            // retrieve wire charge
            WireCharge wireCharge = retrieveWireCharge();

            // generate debits
            GeneralLedgerPendingEntry chargeEntry = processWireChargeDebitEntries(sequenceHelper, wireCharge);

            // generate credits
            processWireChargeCreditEntries(sequenceHelper, wireCharge, chargeEntry);
        }
        return true;
    }

    /**
     * Builds an explicit and offset for the wire charge debit. The account associated with the first accounting is used for the
     * debit. The explicit and offset entries for the first accounting line and copied and customized for the wire charge.
     * 
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence 
     * @param wireCharge wireCharge object from current fiscal year
     * @return GeneralLedgerPendingEntry generated wire charge debit
     */
    private GeneralLedgerPendingEntry processWireChargeDebitEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge) {

        // increment the sequence counter
        sequenceHelper.increment();

        // grab the explicit entry for the first accounting line and adjust for wire charge entry
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry(getGeneralLedgerPendingEntry(0));
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setFinancialObjectCode(wireCharge.getExpenseFinancialObjectCode());
        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setFinancialObjectTypeCode(SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getFinObjTypeExpenditureexpCd());
        explicitEntry.setTransactionDebitCreditCode(GL_DEBIT_CODE);

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(getDvWireTransfer().getDisbVchrBankCountryCode())) {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getDomesticChargeAmt());
        }
        else {
            explicitEntry.setTransactionLedgerEntryAmount(wireCharge.getForeignChargeAmt());
        }

        explicitEntry.setTransactionLedgerEntryDescription("Automatic debit for wire transfer fee");

        getGeneralLedgerPendingEntries().add(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        getGeneralLedgerPendingEntries().add(offsetEntry);

        return explicitEntry;
    }

    /**
     * Builds an explicit and offset for the wire charge credit. The account and income object code found in the wire charge table
     * is used for the entry.
     * 
     * @param dvDocument submitted disbursement voucher document
     * @param sequenceHelper helper class to keep track of GLPE sequence 
     * @param chargeEntry GLPE charge
     * @param wireCharge wireCharge object from current fiscal year
     *  
     */
    private void processWireChargeCreditEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, WireCharge wireCharge, GeneralLedgerPendingEntry chargeEntry) {

        // increment the sequence counter
        sequenceHelper.increment();

        // copy the charge entry and adjust for credit
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry(chargeEntry);
        explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        explicitEntry.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        explicitEntry.setAccountNumber(wireCharge.getAccountNumber());
        explicitEntry.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());

        // retrieve object type
        ObjectCode objectCode = new ObjectCode();
        objectCode.setUniversityFiscalYear(explicitEntry.getUniversityFiscalYear());
        objectCode.setChartOfAccountsCode(wireCharge.getChartOfAccountsCode());
        objectCode.setFinancialObjectCode(wireCharge.getIncomeFinancialObjectCode());
        objectCode = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).retrieve(objectCode);

        explicitEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
        explicitEntry.setTransactionDebitCreditCode(GL_CREDIT_CODE);

        explicitEntry.setFinancialSubObjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode());
        explicitEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        explicitEntry.setProjectCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode());

        explicitEntry.setTransactionLedgerEntryDescription("Automatic credit for wire transfer fee");

        addPendingEntry(explicitEntry);

        // create offset
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        addPendingEntry(offsetEntry);
    }
    
    /**
     * Retrieves the wire transfer information for the current fiscal year.
     * 
     * @return <code>WireCharge</code>
     */
    private WireCharge retrieveWireCharge() {
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        if (wireCharge == null) {
            LOG.error("Wire charge information not found for current fiscal year.");
            throw new RuntimeException("Wire charge information not found for current fiscal year.");
        }

        return wireCharge;
    }


    /**
     * Gets the payeeAssigned attribute. 
     * 
     * This method returns a flag that is used to indicate if the payee type and value has been set on the DV.
     * This value is used to determine the correct page that should be loaded by the DV flow.
     * 
     * @return Returns the payeeAssigned.
     */
    public boolean isPayeeAssigned() {
        // If value is false, check state of document.  We should assume payee is assigned if document has been saved.
        // Otherwise, value will be set during creation process.
        if(!payeeAssigned) {
            payeeAssigned = !this.getDocumentHeader().getWorkflowDocument().stateIsInitiated();
        }
        return payeeAssigned;
    }


    /**
     * Sets the payeeAssigned attribute value.
     * @param payeeAssigned The payeeAssigned to set.
     */
    public void setPayeeAssigned(boolean payeeAssigned) {
        this.payeeAssigned = payeeAssigned;
    }
}