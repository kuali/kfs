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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RevolvingFundDetail extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String financialDocumentTypeCode;
    private String financialDocumentColumnTypeCode;
    private Integer financialDocumentLineNumber;
    private Date financialDocumentRevolvingFundDate;
    private String financialDocumentRevolvingFundReferenceNumber;
    private String financialDocumentRevolvingFundDescription;
    private KualiDecimal financialDocumentRevolvingFundAmount;
    private String financialDocumentBankCode;
    private String financialDocumentBankAccountNumber;

    /**
     * Default constructor.
     */
    public RevolvingFundDetail() {

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
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return - Returns the financialDocumentTypeCode
     * 
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     * 
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the financialDocumentColumnTypeCode attribute.
     * 
     * @return - Returns the financialDocumentColumnTypeCode
     * 
     */
    public String getFinancialDocumentColumnTypeCode() {
        return financialDocumentColumnTypeCode;
    }

    /**
     * Sets the financialDocumentColumnTypeCode attribute.
     * 
     * @param financialDocumentColumnTypeCode The financialDocumentColumnTypeCode to set.
     * 
     */
    public void setFinancialDocumentColumnTypeCode(String financialDocumentColumnTypeCode) {
        this.financialDocumentColumnTypeCode = financialDocumentColumnTypeCode;
    }


    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return - Returns the financialDocumentLineNumber
     * 
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     * 
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }


    /**
     * Gets the financialDocumentRevolvingFundDate attribute.
     * 
     * @return - Returns the financialDocumentRevolvingFundDate
     * 
     */
    public Date getFinancialDocumentRevolvingFundDate() {
        return financialDocumentRevolvingFundDate;
    }

    /**
     * Sets the financialDocumentRevolvingFundDate attribute.
     * 
     * @param financialDocumentRevolvingFundDate The financialDocumentRevolvingFundDate to set.
     * 
     */
    public void setFinancialDocumentRevolvingFundDate(Date financialDocumentRevolvingFundDate) {
        this.financialDocumentRevolvingFundDate = financialDocumentRevolvingFundDate;
    }


    /**
     * Gets the financialDocumentRevolvingFundReferenceNumber attribute.
     * 
     * @return - Returns the financialDocumentRevolvingFundReferenceNumber
     * 
     */
    public String getFinancialDocumentRevolvingFundReferenceNumber() {
        return financialDocumentRevolvingFundReferenceNumber;
    }

    /**
     * Sets the financialDocumentRevolvingFundReferenceNumber attribute.
     * 
     * @param financialDocumentRevolvingFundReferenceNumber The financialDocumentRevolvingFundReferenceNumber to set.
     * 
     */
    public void setFinancialDocumentRevolvingFundReferenceNumber(String financialDocumentRevolvingFundReferenceNumber) {
        this.financialDocumentRevolvingFundReferenceNumber = financialDocumentRevolvingFundReferenceNumber;
    }


    /**
     * Gets the financialDocumentRevolvingFundDescription attribute.
     * 
     * @return - Returns the financialDocumentRevolvingFundDescription
     * 
     */
    public String getFinancialDocumentRevolvingFundDescription() {
        return financialDocumentRevolvingFundDescription;
    }

    /**
     * Sets the financialDocumentRevolvingFundDescription attribute.
     * 
     * @param financialDocumentRevolvingFundDescription The financialDocumentRevolvingFundDescription to set.
     * 
     */
    public void setFinancialDocumentRevolvingFundDescription(String financialDocumentRevolvingFundDescription) {
        this.financialDocumentRevolvingFundDescription = financialDocumentRevolvingFundDescription;
    }


    /**
     * Gets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @return - Returns the financialDocumentRevolvingFundAmount
     * 
     */
    public KualiDecimal getFinancialDocumentRevolvingFundAmount() {
        return financialDocumentRevolvingFundAmount;
    }

    /**
     * Sets the financialDocumentRevolvingFundAmount attribute.
     * 
     * @param financialDocumentRevolvingFundAmount The financialDocumentRevolvingFundAmount to set.
     * 
     */
    public void setFinancialDocumentRevolvingFundAmount(KualiDecimal financialDocumentRevolvingFundAmount) {
        this.financialDocumentRevolvingFundAmount = financialDocumentRevolvingFundAmount;
    }


    /**
     * Gets the financialDocumentBankCode attribute.
     * 
     * @return - Returns the financialDocumentBankCode
     * 
     */
    public String getFinancialDocumentBankCode() {
        return financialDocumentBankCode;
    }

    /**
     * Sets the financialDocumentBankCode attribute.
     * 
     * @param financialDocumentBankCode The financialDocumentBankCode to set.
     * 
     */
    public void setFinancialDocumentBankCode(String financialDocumentBankCode) {
        this.financialDocumentBankCode = financialDocumentBankCode;
    }


    /**
     * Gets the financialDocumentBankAccountNumber attribute.
     * 
     * @return - Returns the financialDocumentBankAccountNumber
     * 
     */
    public String getFinancialDocumentBankAccountNumber() {
        return financialDocumentBankAccountNumber;
    }

    /**
     * Sets the financialDocumentBankAccountNumber attribute.
     * 
     * @param financialDocumentBankAccountNumber The financialDocumentBankAccountNumber to set.
     * 
     */
    public void setFinancialDocumentBankAccountNumber(String financialDocumentBankAccountNumber) {
        this.financialDocumentBankAccountNumber = financialDocumentBankAccountNumber;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialDocumentColumnTypeCode", this.financialDocumentColumnTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
