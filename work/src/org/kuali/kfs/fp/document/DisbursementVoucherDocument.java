/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.document;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.lookup.keyvalues.DisbursementVoucherDocumentationLocationValuesFinder;
import org.kuali.core.lookup.keyvalues.PaymentMethodValuesFinder;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.DisbursementVoucherWireTransfer;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocument extends TransactionalDocumentBase {
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
    }

    /**
     * Gets the finDocNextRegistrantLineNbr attribute.
     * 
     * @return - Returns the finDocNextRegistrantLineNbr
     *  
     */
    public Integer getFinDocNextRegistrantLineNbr() {
        return finDocNextRegistrantLineNbr;
    }


    /**
     * Sets the finDocNextRegistrantLineNbr attribute.
     * 
     * @param - finDocNextRegistrantLineNbr The finDocNextRegistrantLineNbr to set.
     *  
     */
    public void setFinDocNextRegistrantLineNbr(Integer finDocNextRegistrantLineNbr) {
        this.finDocNextRegistrantLineNbr = finDocNextRegistrantLineNbr;
    }

    /**
     * Gets the disbVchrContactPersonName attribute.
     * 
     * @return - Returns the disbVchrContactPersonName
     *  
     */
    public String getDisbVchrContactPersonName() {
        return disbVchrContactPersonName;
    }


    /**
     * Sets the disbVchrContactPersonName attribute.
     * 
     * @param - disbVchrContactPersonName The disbVchrContactPersonName to set.
     *  
     */
    public void setDisbVchrContactPersonName(String disbVchrContactPersonName) {
        this.disbVchrContactPersonName = disbVchrContactPersonName;
    }

    /**
     * Gets the disbVchrContactPhoneNumber attribute.
     * 
     * @return - Returns the disbVchrContactPhoneNumber
     *  
     */
    public String getDisbVchrContactPhoneNumber() {
        return disbVchrContactPhoneNumber;
    }


    /**
     * Sets the disbVchrContactPhoneNumber attribute.
     * 
     * @param - disbVchrContactPhoneNumber The disbVchrContactPhoneNumber to set.
     *  
     */
    public void setDisbVchrContactPhoneNumber(String disbVchrContactPhoneNumber) {
        this.disbVchrContactPhoneNumber = disbVchrContactPhoneNumber;
    }

    /**
     * Gets the disbVchrContactEmailId attribute.
     * 
     * @return - Returns the disbVchrContactEmailId
     *  
     */
    public String getDisbVchrContactEmailId() {
        return disbVchrContactEmailId;
    }


    /**
     * Sets the disbVchrContactEmailId attribute.
     * 
     * @param - disbVchrContactEmailId The disbVchrContactEmailId to set.
     *  
     */
    public void setDisbVchrContactEmailId(String disbVchrContactEmailId) {
        this.disbVchrContactEmailId = disbVchrContactEmailId;
    }

    /**
     * Gets the disbursementVoucherDueDate attribute.
     * 
     * @return - Returns the disbursementVoucherDueDate
     *  
     */
    public Date getDisbursementVoucherDueDate() {
        return disbursementVoucherDueDate;
    }


    /**
     * Sets the disbursementVoucherDueDate attribute.
     * 
     * @param - disbursementVoucherDueDate The disbursementVoucherDueDate to set.
     *  
     */
    public void setDisbursementVoucherDueDate(Date disbursementVoucherDueDate) {
        this.disbursementVoucherDueDate = disbursementVoucherDueDate;
    }

    /**
     * Gets the disbVchrAttachmentCode attribute.
     * 
     * @return - Returns the disbVchrAttachmentCode
     *  
     */
    public boolean isDisbVchrAttachmentCode() {
        return disbVchrAttachmentCode;
    }


    /**
     * Sets the disbVchrAttachmentCode attribute.
     * 
     * @param - disbVchrAttachmentCode The disbVchrAttachmentCode to set.
     *  
     */
    public void setDisbVchrAttachmentCode(boolean disbVchrAttachmentCode) {
        this.disbVchrAttachmentCode = disbVchrAttachmentCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingCode attribute.
     * 
     * @return - Returns the disbVchrSpecialHandlingCode
     *  
     */
    public boolean isDisbVchrSpecialHandlingCode() {
        return disbVchrSpecialHandlingCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingCode attribute.
     * 
     * @param - disbVchrSpecialHandlingCode The disbVchrSpecialHandlingCode to set.
     *  
     */
    public void setDisbVchrSpecialHandlingCode(boolean disbVchrSpecialHandlingCode) {
        this.disbVchrSpecialHandlingCode = disbVchrSpecialHandlingCode;
    }

    /**
     * Gets the disbVchrCheckTotalAmount attribute.
     * 
     * @return - Returns the disbVchrCheckTotalAmount
     *  
     */
    public KualiDecimal getDisbVchrCheckTotalAmount() {
        return disbVchrCheckTotalAmount;
    }


    /**
     * Sets the disbVchrCheckTotalAmount attribute.
     * 
     * @param - disbVchrCheckTotalAmount The disbVchrCheckTotalAmount to set.
     *  
     */
    public void setDisbVchrCheckTotalAmount(KualiDecimal disbVchrCheckTotalAmount) {
        this.disbVchrCheckTotalAmount = disbVchrCheckTotalAmount;
    }

    /**
     * Gets the disbVchrForeignCurrencyInd attribute.
     * 
     * @return - Returns the disbVchrForeignCurrencyInd
     *  
     */
    public boolean isDisbVchrForeignCurrencyInd() {
        return disbVchrForeignCurrencyInd;
    }


    /**
     * Sets the disbVchrForeignCurrencyInd attribute.
     * 
     * @param - disbVchrForeignCurrencyInd The disbVchrForeignCurrencyInd to set.
     *  
     */
    public void setDisbVchrForeignCurrencyInd(boolean disbVchrForeignCurrencyInd) {
        this.disbVchrForeignCurrencyInd = disbVchrForeignCurrencyInd;
    }

    /**
     * Gets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @return - Returns the disbursementVoucherDocumentationLocationCode
     *  
     */
    public String getDisbursementVoucherDocumentationLocationCode() {
        return disbursementVoucherDocumentationLocationCode;
    }


    /**
     * Sets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @param - disbursementVoucherDocumentationLocationCode The disbursementVoucherDocumentationLocationCode to set.
     *  
     */
    public void setDisbursementVoucherDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.disbursementVoucherDocumentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }

    /**
     * Gets the disbVchrCheckStubText attribute.
     * 
     * @return - Returns the disbVchrCheckStubText
     *  
     */
    public String getDisbVchrCheckStubText() {
        return disbVchrCheckStubText;
    }


    /**
     * Sets the disbVchrCheckStubText attribute.
     * 
     * @param - disbVchrCheckStubText The disbVchrCheckStubText to set.
     *  
     */
    public void setDisbVchrCheckStubText(String disbVchrCheckStubText) {
        this.disbVchrCheckStubText = disbVchrCheckStubText;
    }

    /**
     * Gets the dvCheckStubOverflowCode attribute.
     * 
     * @return - Returns the dvCheckStubOverflowCode
     *  
     */
    public boolean getDvCheckStubOverflowCode() {
        return dvCheckStubOverflowCode;
    }


    /**
     * Sets the dvCheckStubOverflowCode attribute.
     * 
     * @param - dvCheckStubOverflowCode The dvCheckStubOverflowCode to set.
     *  
     */
    public void setDvCheckStubOverflowCode(boolean dvCheckStubOverflowCode) {
        this.dvCheckStubOverflowCode = dvCheckStubOverflowCode;
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return - Returns the campusCode
     *  
     */
    public String getCampusCode() {
        return campusCode;
    }


    /**
     * Sets the campusCode attribute.
     * 
     * @param - campusCode The campusCode to set.
     *  
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the disbVchrPayeeTaxControlCode attribute.
     * 
     * @return - Returns the disbVchrPayeeTaxControlCode
     *  
     */
    public String getDisbVchrPayeeTaxControlCode() {
        return disbVchrPayeeTaxControlCode;
    }


    /**
     * Sets the disbVchrPayeeTaxControlCode attribute.
     * 
     * @param - disbVchrPayeeTaxControlCode The disbVchrPayeeTaxControlCode to set.
     *  
     */
    public void setDisbVchrPayeeTaxControlCode(String disbVchrPayeeTaxControlCode) {
        this.disbVchrPayeeTaxControlCode = disbVchrPayeeTaxControlCode;
    }

    /**
     * Gets the disbVchrPayeeChangedInd attribute.
     * 
     * @return - Returns the disbVchrPayeeChangedInd
     *  
     */
    public boolean isDisbVchrPayeeChangedInd() {
        return disbVchrPayeeChangedInd;
    }


    /**
     * Sets the disbVchrPayeeChangedInd attribute.
     * 
     * @param - disbVchrPayeeChangedInd The disbVchrPayeeChangedInd to set.
     *  
     */
    public void setDisbVchrPayeeChangedInd(boolean disbVchrPayeeChangedInd) {
        this.disbVchrPayeeChangedInd = disbVchrPayeeChangedInd;
    }

    /**
     * Gets the disbursementVoucherCheckNbr attribute.
     * 
     * @return - Returns the disbursementVoucherCheckNbr
     *  
     */
    public String getDisbursementVoucherCheckNbr() {
        return disbursementVoucherCheckNbr;
    }


    /**
     * Sets the disbursementVoucherCheckNbr attribute.
     * 
     * @param - disbursementVoucherCheckNbr The disbursementVoucherCheckNbr to set.
     *  
     */
    public void setDisbursementVoucherCheckNbr(String disbursementVoucherCheckNbr) {
        this.disbursementVoucherCheckNbr = disbursementVoucherCheckNbr;
    }

    /**
     * Gets the disbursementVoucherCheckDate attribute.
     * 
     * @return - Returns the disbursementVoucherCheckDate
     *  
     */
    public Timestamp getDisbursementVoucherCheckDate() {
        return disbursementVoucherCheckDate;
    }


    /**
     * Sets the disbursementVoucherCheckDate attribute.
     * 
     * @param - disbursementVoucherCheckDate The disbursementVoucherCheckDate to set.
     *  
     */
    public void setDisbursementVoucherCheckDate(Timestamp disbursementVoucherCheckDate) {
        this.disbursementVoucherCheckDate = disbursementVoucherCheckDate;
    }

    /**
     * Gets the disbVchrPayeeW9CompleteCode attribute.
     * 
     * @return - Returns the disbVchrPayeeW9CompleteCode
     *  
     */
    public boolean getDisbVchrPayeeW9CompleteCode() {
        return disbVchrPayeeW9CompleteCode;
    }


    /**
     * Sets the disbVchrPayeeW9CompleteCode attribute.
     * 
     * @param - disbVchrPayeeW9CompleteCode The disbVchrPayeeW9CompleteCode to set.
     *  
     */
    public void setDisbVchrPayeeW9CompleteCode(boolean disbVchrPayeeW9CompleteCode) {
        this.disbVchrPayeeW9CompleteCode = disbVchrPayeeW9CompleteCode;
    }

    /**
     * Gets the disbVchrPaymentMethodCode attribute.
     * 
     * @return - Returns the disbVchrPaymentMethodCode
     *  
     */
    public String getDisbVchrPaymentMethodCode() {
        return disbVchrPaymentMethodCode;
    }


    /**
     * Sets the disbVchrPaymentMethodCode attribute.
     * 
     * @param - disbVchrPaymentMethodCode The disbVchrPaymentMethodCode to set.
     *  
     */
    public void setDisbVchrPaymentMethodCode(String disbVchrPaymentMethodCode) {
        this.disbVchrPaymentMethodCode = disbVchrPaymentMethodCode;
    }

    /**
     * Gets the financialDocument attribute.
     * 
     * @return - Returns the financialDocument
     *  
     */
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }


    /**
     * Sets the financialDocument attribute.
     * 
     * @param - financialDocument The financialDocument to set.
     * @deprecated
     */
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the disbVchrDocumentationLoc attribute.
     * 
     * @return - Returns the disbVchrDocumentationLoc
     *  
     */
    public DisbursementVoucherDocumentationLocation getDisbVchrDocumentationLoc() {
        return disbVchrDocumentationLoc;
    }


    /**
     * Sets the disbVchrDocumentationLoc attribute.
     * 
     * @param - disbVchrDocumentationLoc The disbVchrDocumentationLoc to set.
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
     * Adds a dv pre paid registran line
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
     * @return
     */
    public String getDisbVchrPaymentMethodName(){
        return new PaymentMethodValuesFinder().getKeyLabel(disbVchrPaymentMethodCode);
    }
    
    public void setDisbVchrPaymentMethodName(String method){
    }
    
    /**
     * Returns the name associated with the documentation location name
     * @return
     */
    public String getDisbursementVoucherDocumentationLocationName(){
        return new DisbursementVoucherDocumentationLocationValuesFinder().getKeyLabel(disbursementVoucherDocumentationLocationCode);
    }
    
    public void setDisbursementVoucherDocumentationLocationName(String name){
    }
    
    

    /**
     * @see org.kuali.core.document.Document#handleRouteStatusChange(java.lang.String)
     */
    public void handleRouteStatusChange(String newRouteStatus) {
        // TODO Auto-generated method stub

    }


    /**
     * Convenience method to set dv payee detail fields based on a given Payee.
     * 
     * @param payee
     */
    public void templatePayee(Payee payee) {
        if (payee == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_PAYEE);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(payee.getPayeeIdNumber());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(payee.getPayeePersonName());
        this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(payee.getPayeeLine1Addr());
        this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(payee.getPayeeLine2Addr());
        this.getDvPayeeDetail().setDisbVchrPayeeCityName(payee.getPayeeCityName());
        this.getDvPayeeDetail().setDisbVchrPayeeStateCode(payee.getPayeeStateCode());
        this.getDvPayeeDetail().setDisbVchrPayeeZipCode(payee.getPayeeZipCode());
        this.getDvPayeeDetail().setDisbVchrPayeeCountryCode(payee.getPayeeCountryCode());
        this.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(payee.isPayeeEmployeeCode());
        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(payee.isAlienPaymentCode());
        this.getDvPayeeDetail().setDvPayeeRevolvingFundCode(payee.isPayeeRevolvingFundCode());

        this.disbVchrPayeeTaxControlCode = payee.getPayeeTaxControlCode();
        this.disbVchrPayeeW9CompleteCode = payee.isPayeeW9CompleteCode();
    }

    /**
     * Convenience method to set dv payee detail fields based on a given Employee.
     * 
     * @param payee
     */
    public void templateEmployee(UniversalUser employee) {
        if (employee == null) {
            return;
        }

        this.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
        this.getDvPayeeDetail().setDisbVchrPayeeIdNumber(employee.getPersonUniversalIdentifier());
        this.getDvPayeeDetail().setDisbVchrPayeePersonName(employee.getPersonName());
        this.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(employee.getDeptid());
        this.getDvPayeeDetail().setDisbVchrPayeeLine2Addr("");
        this.getDvPayeeDetail().setDisbVchrPayeeCityName(employee.getCampus().getCampusName() + " CAMPUS");
        this.getDvPayeeDetail().setDisbVchrPayeeStateCode("");
        this.getDvPayeeDetail().setDisbVchrPayeeZipCode("");
        this.getDvPayeeDetail().setDisbVchrPayeeCountryCode("");
        this.getDvPayeeDetail().setDisbVchrPayeeEmployeeCode(false);
        this.getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);
        this.getDvPayeeDetail().setDvPayeeRevolvingFundCode(false);

        this.disbVchrPayeeTaxControlCode = "";
        this.disbVchrPayeeW9CompleteCode = true;
    }

    /**
     * @see org.kuali.core.document.Document#prepareForSave()
     */
    public void prepareForSave() {
        if (dvWireTransfer != null) {
            dvWireTransfer.setFinancialDocumentNumber(this.financialDocumentNumber);
        }

        if (dvNonResidentAlienTax != null) {
            dvNonResidentAlienTax.setFinancialDocumentNumber(this.financialDocumentNumber);
        }

        dvPayeeDetail.setFinancialDocumentNumber(this.financialDocumentNumber);

        if (dvNonEmployeeTravel != null) {
            dvNonEmployeeTravel.setFinancialDocumentNumber(this.financialDocumentNumber);
            dvNonEmployeeTravel.setTotalTravelAmount(dvNonEmployeeTravel.getTotalTravelAmount());
        }

        if (dvPreConferenceDetail != null) {
            dvPreConferenceDetail.setFinancialDocumentNumber(this.financialDocumentNumber);
            dvPreConferenceDetail.setDisbVchrConferenceTotalAmt(dvPreConferenceDetail.getDisbVchrConferenceTotalAmt());
        }
    }

    /**
     * Clears information that might have been entered for sub tables, but because of changes to the document is longer needed and
     * should not be persisted.
     */
    private void cleanDocumentData() {
        if (!DisbursementVoucherRuleConstants.PAYMENT_METHOD_WIRE.equals(this.getDisbVchrPaymentMethodCode())
                && !DisbursementVoucherRuleConstants.PAYMENT_METHOD_DRAFT.equals(this.getDisbVchrPaymentMethodCode())) {
            SpringServiceLocator.getBusinessObjectService().delete(dvWireTransfer);
            dvWireTransfer = null;
        }

        if (!dvPayeeDetail.isDisbVchrAlienPaymentCode()) {
            SpringServiceLocator.getBusinessObjectService().delete(dvNonResidentAlienTax);
            dvNonResidentAlienTax = null;
        }

        String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValues(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM,
                        DisbursementVoucherRuleConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);
        if (!RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(dvPayeeDetail.getDisbVchrPaymentReasonCode())) {
            SpringServiceLocator.getBusinessObjectService().delete(dvNonEmployeeTravel);
            dvNonEmployeeTravel = null;
        }

        String[] travelPrepaidPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValues(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM,
                        DisbursementVoucherRuleConstants.PREPAID_TRAVEL_PAY_REASONS_PARM_NM);
        if (!RulesUtils.makeSet(travelPrepaidPaymentReasonCodes).contains(dvPayeeDetail.getDisbVchrPaymentReasonCode())) {
            SpringServiceLocator.getBusinessObjectService().delete(dvPreConferenceDetail);
            dvPreConferenceDetail = null;
        }
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#convertIntoCopy()
     */
    public void convertIntoCopy() throws WorkflowException {
        super.convertIntoCopy();
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
        getDvPayeeDetail().setDisbVchrAlienPaymentCode(false);
        setDisbVchrPayeeTaxControlCode("");

        // clear nra
        SpringServiceLocator.getDisbursementVoucherTaxService().clearNRATaxLines(this);
        setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());

        // clear waive wire
        setDvNonEmployeeTravel(new DisbursementVoucherNonEmployeeTravel());
        setDvPreConferenceDetail(new DisbursementVoucherPreConferenceDetail());
        getDvWireTransfer().setDisbursementVoucherWireTransferFeeWaiverIndicator(false);
        
        // check payee id number to see if still valid, if so retrieve their last information and set in the detail inform.
        if (getDvPayeeDetail().isPayee() && !StringUtils.isBlank(getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            Payee payee = new Payee();
            payee.setPayeeIdNumber(getDvPayeeDetail().getDisbVchrPayeeIdNumber());
            Map keys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(payee);
            payee = (Payee) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(Payee.class, keys);
            if (payee == null) {
                getDvPayeeDetail().setDisbVchrPayeeIdNumber("");
                GlobalVariables.getMessageList().add(KeyConstants.WARNING_DV_PAYEE_NONEXISTANT_CLEARED);
            }
            else {
                templatePayee(payee);
            }
        }

        // employee
        if (getDvPayeeDetail().isEmployee() && !StringUtils.isBlank(getDvPayeeDetail().getDisbVchrPayeeIdNumber())) {
            UniversalUser employee = new UniversalUser();
            employee.setPersonUniversalIdentifier(getDvPayeeDetail().getDisbVchrPayeeIdNumber());
            employee = (UniversalUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);
            if (employee == null) {
                getDvPayeeDetail().setDisbVchrPayeeIdNumber("");
                GlobalVariables.getMessageList().add(KeyConstants.WARNING_DV_PAYEE_NONEXISTANT_CLEARED);
            }
            else {
                templateEmployee(employee);
            }
        }


    }

    /**
     * generic, shared logic used to iniate a dv document
     */
    public void initiateDocument() {
        KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
        setDisbVchrContactPersonName(currentUser.getPersonName());
        setCampusCode(currentUser.getOrganization().getOrganizationPhysicalCampusCode());

        // due date
        Calendar calendar = SpringServiceLocator.getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setDisbursementVoucherDueDate(new Date(calendar.getTimeInMillis()));

        // default doc location
        setDisbursementVoucherDocumentationLocationCode(SpringServiceLocator.getKualiConfigurationService()
                .getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM,
                        DisbursementVoucherRuleConstants.DEFAULT_DOC_LOCATION_PARM_NM));
    }

    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
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
}