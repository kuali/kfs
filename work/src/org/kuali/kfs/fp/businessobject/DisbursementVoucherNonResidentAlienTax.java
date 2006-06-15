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
import org.kuali.core.lookup.keyvalues.TaxIncomeClassValuesFinder;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherNonResidentAlienTax extends BusinessObjectBase {

    private String financialDocumentNumber;
    private KualiDecimal federalIncomeTaxPercent;
    private KualiDecimal stateIncomeTaxPercent;
    private String incomeClassCode;
    private String postalCountryCode;
    private boolean incomeTaxTreatyExemptCode;
    private boolean foreignSourceIncomeCode;
    private boolean incomeTaxGrossUpCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private String financialDocumentAccountingLineText;

    private TaxIncomeClassCode incomeClass;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherNonResidentAlienTax() {

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
     * Gets the federalIncomeTaxPercent attribute.
     * 
     * @return - Returns the federalIncomeTaxPercent
     * 
     */
    public KualiDecimal getFederalIncomeTaxPercent() {
        return federalIncomeTaxPercent;
    }


    /**
     * Sets the federalIncomeTaxPercent attribute.
     * 
     * @param federalIncomeTaxPercent The federalIncomeTaxPercent to set.
     * 
     */
    public void setFederalIncomeTaxPercent(KualiDecimal federalIncomeTaxPercent) {
        this.federalIncomeTaxPercent = federalIncomeTaxPercent;
    }

    /**
     * Gets the stateIncomeTaxPercent attribute.
     * 
     * @return - Returns the stateIncomeTaxPercent
     * 
     */
    public KualiDecimal getStateIncomeTaxPercent() {
        return stateIncomeTaxPercent;
    }


    /**
     * Sets the stateIncomeTaxPercent attribute.
     * 
     * @param stateIncomeTaxPercent The stateIncomeTaxPercent to set.
     * 
     */
    public void setStateIncomeTaxPercent(KualiDecimal stateIncomeTaxPercent) {
        this.stateIncomeTaxPercent = stateIncomeTaxPercent;
    }

    /**
     * Gets the incomeClassCode attribute.
     * 
     * @return - Returns the incomeClassCode
     * 
     */
    public String getIncomeClassCode() {
        return incomeClassCode;
    }


    /**
     * Sets the incomeClassCode attribute.
     * 
     * @param incomeClassCode The incomeClassCode to set.
     * 
     */
    public void setIncomeClassCode(String incomeClassCode) {
        this.incomeClassCode = incomeClassCode;
    }

    /**
     * Gets the postalCountryCode attribute.
     * 
     * @return - Returns the postalCountryCode
     * 
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }


    /**
     * Sets the postalCountryCode attribute.
     * 
     * @param postalCountryCode The postalCountryCode to set.
     * 
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }

    /**
     * Gets the incomeTaxTreatyExemptCode attribute.
     * 
     * @return - Returns the incomeTaxTreatyExemptCode
     * 
     */
    public boolean isIncomeTaxTreatyExemptCode() {
        return incomeTaxTreatyExemptCode;
    }


    /**
     * Sets the incomeTaxTreatyExemptCode attribute.
     * 
     * @param incomeTaxTreatyExemptCode The incomeTaxTreatyExemptCode to set.
     * 
     */
    public void setIncomeTaxTreatyExemptCode(boolean incomeTaxTreatyExemptCode) {
        this.incomeTaxTreatyExemptCode = incomeTaxTreatyExemptCode;
    }

    /**
     * Gets the foreignSourceIncomeCode attribute.
     * 
     * @return - Returns the foreignSourceIncomeCode
     * 
     */
    public boolean isForeignSourceIncomeCode() {
        return foreignSourceIncomeCode;
    }


    /**
     * Sets the foreignSourceIncomeCode attribute.
     * 
     * @param foreignSourceIncomeCode The foreignSourceIncomeCode to set.
     * 
     */
    public void setForeignSourceIncomeCode(boolean foreignSourceIncomeCode) {
        this.foreignSourceIncomeCode = foreignSourceIncomeCode;
    }

    /**
     * Gets the incomeTaxGrossUpCode attribute.
     * 
     * @return - Returns the incomeTaxGrossUpCode
     * 
     */
    public boolean isIncomeTaxGrossUpCode() {
        return incomeTaxGrossUpCode;
    }


    /**
     * Sets the incomeTaxGrossUpCode attribute.
     * 
     * @param incomeTaxGrossUpCode The incomeTaxGrossUpCode to set.
     * 
     */
    public void setIncomeTaxGrossUpCode(boolean incomeTaxGrossUpCode) {
        this.incomeTaxGrossUpCode = incomeTaxGrossUpCode;
    }

    /**
     * Gets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @return - Returns the referenceFinancialSystemOriginationCode
     * 
     */
    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }


    /**
     * Sets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     * 
     */
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return - Returns the referenceFinancialDocumentNumber
     * 
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }


    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     * 
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * @return Returns the financialDocumentAccountingLineText.
     */
    public String getFinancialDocumentAccountingLineText() {
        return financialDocumentAccountingLineText;
    }

    /**
     * @param financialDocumentAccountingLineText The financialDocumentAccountingLineText to set.
     */
    public void setFinancialDocumentAccountingLineText(String financialDocumentAccountingLineText) {
        this.financialDocumentAccountingLineText = financialDocumentAccountingLineText;
    }

    /**
     * Gets the incomeClass attribute.
     * 
     * @return - Returns the incomeClass
     * 
     */
    public TaxIncomeClassCode getIncomeClass() {
        return incomeClass;
    }


    /**
     * Sets the incomeClass attribute.
     * 
     * @param incomeClass The incomeClass to set.
     * @deprecated
     */
    public void setIncomeClass(TaxIncomeClassCode incomeClass) {
        this.incomeClass = incomeClass;
    }

    /**
     * Return select read-only label for income class
     * 
     * @return
     */
    public String getIncomeClassName() {
        return new TaxIncomeClassValuesFinder().getKeyLabel(incomeClassCode);
    }

    public void setincomeClassName(String name) {
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
