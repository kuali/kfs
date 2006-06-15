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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.lookup.keyvalues.PayeeTypeValuesFinder;
import org.kuali.core.lookup.keyvalues.PaymentReasonValuesFinder;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherPayeeDetail extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String disbVchrPaymentReasonCode;
    private boolean disbVchrAlienPaymentCode;
    private String disbVchrPayeeIdNumber;
    private String disbVchrPayeePersonName;
    private String disbVchrPayeeLine1Addr;
    private String disbVchrPayeeLine2Addr;
    private String disbVchrPayeeCityName;
    private String disbVchrPayeeStateCode;
    private String disbVchrPayeeZipCode;
    private String disbVchrPayeeCountryCode;
    private String disbVchrRemitPersonName;
    private String disbVchrRemitLine1Addr;
    private String disbVchrRemitLine2Addr;
    private String disbVchrRemitCityName;
    private String disbVchrRemitStateCode;
    private String disbVchrRemitZipCode;
    private String disbVchrRemitCountryCode;
    private boolean disbVchrPayeeEmployeeCode;
    private boolean dvPayeeRevolvingFundCode;
    private String disbursementVoucherPayeeTypeCode;

    private PaymentReasonCode disbVchrPaymentReason;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherPayeeDetail() {

    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     * 
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }


    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     * 
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }

    /**
     * Gets the disbVchrPaymentReasonCode attribute.
     * 
     * @return - Returns the disbVchrPaymentReasonCode
     * 
     */
    public String getDisbVchrPaymentReasonCode() {
        return disbVchrPaymentReasonCode;
    }


    /**
     * Sets the disbVchrPaymentReasonCode attribute.
     * 
     * @param disbVchrPaymentReasonCode The disbVchrPaymentReasonCode to set.
     * 
     */
    public void setDisbVchrPaymentReasonCode(String disbVchrPaymentReasonCode) {
        this.disbVchrPaymentReasonCode = disbVchrPaymentReasonCode;
    }

    /**
     * Gets the disbVchrAlienPaymentCode attribute.
     * 
     * @return - Returns the disbVchrAlienPaymentCode
     * 
     */
    public boolean isDisbVchrAlienPaymentCode() {
        return disbVchrAlienPaymentCode;
    }


    /**
     * Sets the disbVchrAlienPaymentCode attribute.
     * 
     * @param disbVchrAlienPaymentCode The disbVchrAlienPaymentCode to set.
     * 
     */
    public void setDisbVchrAlienPaymentCode(boolean disbVchrAlienPaymentCode) {
        this.disbVchrAlienPaymentCode = disbVchrAlienPaymentCode;
    }

    /**
     * Gets the disbVchrPayeeIdNumber attribute.
     * 
     * @return - Returns the disbVchrPayeeIdNumber
     * 
     */
    public String getDisbVchrPayeeIdNumber() {
        return disbVchrPayeeIdNumber;
    }


    /**
     * Sets the disbVchrPayeeIdNumber attribute.
     * 
     * @param disbVchrPayeeIdNumber The disbVchrPayeeIdNumber to set.
     * 
     */
    public void setDisbVchrPayeeIdNumber(String disbVchrPayeeIdNumber) {
        this.disbVchrPayeeIdNumber = disbVchrPayeeIdNumber;
    }

    /**
     * Gets the disbVchrPayeePersonName attribute.
     * 
     * @return - Returns the disbVchrPayeePersonName
     * 
     */
    public String getDisbVchrPayeePersonName() {
        return disbVchrPayeePersonName;
    }


    /**
     * Sets the disbVchrPayeePersonName attribute.
     * 
     * @param disbVchrPayeePersonName The disbVchrPayeePersonName to set.
     * 
     */
    public void setDisbVchrPayeePersonName(String disbVchrPayeePersonName) {
        this.disbVchrPayeePersonName = disbVchrPayeePersonName;
    }

    /**
     * Gets the disbVchrPayeeLine1Addr attribute.
     * 
     * @return - Returns the disbVchrPayeeLine1Addr
     * 
     */
    public String getDisbVchrPayeeLine1Addr() {
        return disbVchrPayeeLine1Addr;
    }


    /**
     * Sets the disbVchrPayeeLine1Addr attribute.
     * 
     * @param disbVchrPayeeLine1Addr The disbVchrPayeeLine1Addr to set.
     * 
     */
    public void setDisbVchrPayeeLine1Addr(String disbVchrPayeeLine1Addr) {
        this.disbVchrPayeeLine1Addr = disbVchrPayeeLine1Addr;
    }

    /**
     * Gets the disbVchrPayeeLine2Addr attribute.
     * 
     * @return - Returns the disbVchrPayeeLine2Addr
     * 
     */
    public String getDisbVchrPayeeLine2Addr() {
        return disbVchrPayeeLine2Addr;
    }


    /**
     * Sets the disbVchrPayeeLine2Addr attribute.
     * 
     * @param disbVchrPayeeLine2Addr The disbVchrPayeeLine2Addr to set.
     * 
     */
    public void setDisbVchrPayeeLine2Addr(String disbVchrPayeeLine2Addr) {
        this.disbVchrPayeeLine2Addr = disbVchrPayeeLine2Addr;
    }

    /**
     * Gets the disbVchrPayeeCityName attribute.
     * 
     * @return - Returns the disbVchrPayeeCityName
     * 
     */
    public String getDisbVchrPayeeCityName() {
        return disbVchrPayeeCityName;
    }


    /**
     * Sets the disbVchrPayeeCityName attribute.
     * 
     * @param disbVchrPayeeCityName The disbVchrPayeeCityName to set.
     * 
     */
    public void setDisbVchrPayeeCityName(String disbVchrPayeeCityName) {
        this.disbVchrPayeeCityName = disbVchrPayeeCityName;
    }

    /**
     * Gets the disbVchrPayeeStateCode attribute.
     * 
     * @return - Returns the disbVchrPayeeStateCode
     * 
     */
    public String getDisbVchrPayeeStateCode() {
        return disbVchrPayeeStateCode;
    }


    /**
     * Sets the disbVchrPayeeStateCode attribute.
     * 
     * @param disbVchrPayeeStateCode The disbVchrPayeeStateCode to set.
     * 
     */
    public void setDisbVchrPayeeStateCode(String disbVchrPayeeStateCode) {
        this.disbVchrPayeeStateCode = disbVchrPayeeStateCode;
    }

    /**
     * Gets the disbVchrPayeeZipCode attribute.
     * 
     * @return - Returns the disbVchrPayeeZipCode
     * 
     */
    public String getDisbVchrPayeeZipCode() {
        return disbVchrPayeeZipCode;
    }


    /**
     * Sets the disbVchrPayeeZipCode attribute.
     * 
     * @param disbVchrPayeeZipCode The disbVchrPayeeZipCode to set.
     * 
     */
    public void setDisbVchrPayeeZipCode(String disbVchrPayeeZipCode) {
        this.disbVchrPayeeZipCode = disbVchrPayeeZipCode;
    }

    /**
     * Gets the disbVchrPayeeCountryCode attribute.
     * 
     * @return - Returns the disbVchrPayeeCountryCode
     * 
     */
    public String getDisbVchrPayeeCountryCode() {
        return disbVchrPayeeCountryCode;
    }


    /**
     * Sets the disbVchrPayeeCountryCode attribute.
     * 
     * @param disbVchrPayeeCountryCode The disbVchrPayeeCountryCode to set.
     * 
     */
    public void setDisbVchrPayeeCountryCode(String disbVchrPayeeCountryCode) {
        this.disbVchrPayeeCountryCode = disbVchrPayeeCountryCode;
    }

    /**
     * Gets the disbVchrRemitPersonName attribute.
     * 
     * @return - Returns the disbVchrRemitPersonName
     * 
     */
    public String getDisbVchrRemitPersonName() {
        return disbVchrRemitPersonName;
    }


    /**
     * Sets the disbVchrRemitPersonName attribute.
     * 
     * @param disbVchrRemitPersonName The disbVchrRemitPersonName to set.
     * 
     */
    public void setDisbVchrRemitPersonName(String disbVchrRemitPersonName) {
        this.disbVchrRemitPersonName = disbVchrRemitPersonName;
    }

    /**
     * Gets the disbVchrRemitLine1Addr attribute.
     * 
     * @return - Returns the disbVchrRemitLine1Addr
     * 
     */
    public String getDisbVchrRemitLine1Addr() {
        return disbVchrRemitLine1Addr;
    }


    /**
     * Sets the disbVchrRemitLine1Addr attribute.
     * 
     * @param disbVchrRemitLine1Addr The disbVchrRemitLine1Addr to set.
     * 
     */
    public void setDisbVchrRemitLine1Addr(String disbVchrRemitLine1Addr) {
        this.disbVchrRemitLine1Addr = disbVchrRemitLine1Addr;
    }

    /**
     * Gets the disbVchrRemitLine2Addr attribute.
     * 
     * @return - Returns the disbVchrRemitLine2Addr
     * 
     */
    public String getDisbVchrRemitLine2Addr() {
        return disbVchrRemitLine2Addr;
    }


    /**
     * Sets the disbVchrRemitLine2Addr attribute.
     * 
     * @param disbVchrRemitLine2Addr The disbVchrRemitLine2Addr to set.
     * 
     */
    public void setDisbVchrRemitLine2Addr(String disbVchrRemitLine2Addr) {
        this.disbVchrRemitLine2Addr = disbVchrRemitLine2Addr;
    }

    /**
     * Gets the disbVchrRemitCityName attribute.
     * 
     * @return - Returns the disbVchrRemitCityName
     * 
     */
    public String getDisbVchrRemitCityName() {
        return disbVchrRemitCityName;
    }


    /**
     * Sets the disbVchrRemitCityName attribute.
     * 
     * @param disbVchrRemitCityName The disbVchrRemitCityName to set.
     * 
     */
    public void setDisbVchrRemitCityName(String disbVchrRemitCityName) {
        this.disbVchrRemitCityName = disbVchrRemitCityName;
    }

    /**
     * Gets the disbVchrRemitStateCode attribute.
     * 
     * @return - Returns the disbVchrRemitStateCode
     * 
     */
    public String getDisbVchrRemitStateCode() {
        return disbVchrRemitStateCode;
    }


    /**
     * Sets the disbVchrRemitStateCode attribute.
     * 
     * @param disbVchrRemitStateCode The disbVchrRemitStateCode to set.
     * 
     */
    public void setDisbVchrRemitStateCode(String disbVchrRemitStateCode) {
        this.disbVchrRemitStateCode = disbVchrRemitStateCode;
    }

    /**
     * Gets the disbVchrRemitZipCode attribute.
     * 
     * @return - Returns the disbVchrRemitZipCode
     * 
     */
    public String getDisbVchrRemitZipCode() {
        return disbVchrRemitZipCode;
    }


    /**
     * Sets the disbVchrRemitZipCode attribute.
     * 
     * @param disbVchrRemitZipCode The disbVchrRemitZipCode to set.
     * 
     */
    public void setDisbVchrRemitZipCode(String disbVchrRemitZipCode) {
        this.disbVchrRemitZipCode = disbVchrRemitZipCode;
    }

    /**
     * Gets the disbVchrRemitCountryCode attribute.
     * 
     * @return - Returns the disbVchrRemitCountryCode
     * 
     */
    public String getDisbVchrRemitCountryCode() {
        return disbVchrRemitCountryCode;
    }


    /**
     * Sets the disbVchrRemitCountryCode attribute.
     * 
     * @param disbVchrRemitCountryCode The disbVchrRemitCountryCode to set.
     * 
     */
    public void setDisbVchrRemitCountryCode(String disbVchrRemitCountryCode) {
        this.disbVchrRemitCountryCode = disbVchrRemitCountryCode;
    }

    /**
     * Gets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @return - Returns the disbVchrPayeeEmployeeCode
     * 
     */
    public boolean isDisbVchrPayeeEmployeeCode() {
        return disbVchrPayeeEmployeeCode;
    }


    /**
     * Sets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @param disbVchrPayeeEmployeeCode The disbVchrPayeeEmployeeCode to set.
     * 
     */
    public void setDisbVchrPayeeEmployeeCode(boolean disbVchrPayeeEmployeeCode) {
        this.disbVchrPayeeEmployeeCode = disbVchrPayeeEmployeeCode;
    }

    /**
     * Gets the dvPayeeRevolvingFundCode attribute.
     * 
     * @return - Returns the dvPayeeRevolvingFundCode
     * 
     */
    public boolean isDvPayeeRevolvingFundCode() {
        return dvPayeeRevolvingFundCode;
    }


    /**
     * Sets the dvPayeeRevolvingFundCode attribute.
     * 
     * @param dvPayeeRevolvingFundCode The dvPayeeRevolvingFundCode to set.
     * 
     */
    public void setDvPayeeRevolvingFundCode(boolean dvPayeeRevolvingFundCode) {
        this.dvPayeeRevolvingFundCode = dvPayeeRevolvingFundCode;
    }

    /**
     * Gets the disbVchrPaymentReason attribute.
     * 
     * @return - Returns the disbVchrPaymentReason
     * 
     */
    public PaymentReasonCode getDisbVchrPaymentReason() {
        return disbVchrPaymentReason;
    }


    /**
     * Sets the disbVchrPaymentReason attribute.
     * 
     * @param disbVchrPaymentReason The disbVchrPaymentReason to set.
     * @deprecated
     */
    public void setDisbVchrPaymentReason(PaymentReasonCode disbVchrPaymentReason) {
        this.disbVchrPaymentReason = disbVchrPaymentReason;
    }

    /**
     * Checks the payee type code for vendor type
     * 
     * @return
     */
    public boolean isVendor() {
        return DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR.equals(disbursementVoucherPayeeTypeCode);
    }

    /**
     * Checks the payee type code for dv payee type
     * 
     * @return
     */
    public boolean isPayee() {
        return DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_PAYEE.equals(disbursementVoucherPayeeTypeCode);
    }

    /**
     * Checks the payee type code for employee type
     * 
     * @return
     */
    public boolean isEmployee() {
        return DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE.equals(disbursementVoucherPayeeTypeCode);
    }

    /**
     * @return Returns the disbursementVoucherPayeeTypeCode.
     */
    public String getDisbursementVoucherPayeeTypeCode() {
        return disbursementVoucherPayeeTypeCode;
    }

    /**
     * @param disbursementVoucherPayeeTypeCode The disbursementVoucherPayeeTypeCode to set.
     */
    public void setDisbursementVoucherPayeeTypeCode(String disbursementVoucherPayeeTypeCode) {
        this.disbursementVoucherPayeeTypeCode = disbursementVoucherPayeeTypeCode;
    }

    /**
     * returns the payee type name
     */
    public String getDisbursementVoucherPayeeTypeName() {
        return new PayeeTypeValuesFinder().getKeyLabel(disbursementVoucherPayeeTypeCode);
    }

    public void setDisbursementVoucherPayeeTypeName(String name) {
    }

    /**
     * Returns the name associated with the payment reason name
     * 
     * @return
     */
    public String getDisbVchrPaymentReasonName() {
        return new PaymentReasonValuesFinder().getKeyLabel(disbVchrPaymentReasonCode);
    }

    public void setDisbVchrPaymentReasonName(String name) {
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}