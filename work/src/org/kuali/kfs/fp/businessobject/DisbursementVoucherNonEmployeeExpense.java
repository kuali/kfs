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
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherNonEmployeeExpense extends BusinessObjectBase {

    private String financialDocumentNumber;
    private Integer financialDocumentLineNumber;
    private String disbVchrExpenseCode;
    private String disbVchrExpenseCompanyName;
    private KualiDecimal disbVchrExpenseAmount;

    private TravelExpenseTypeCode disbVchrExpense;

    private boolean isPrepaid;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherNonEmployeeExpense() {

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
     * Gets the disbVchrExpenseCode attribute.
     * 
     * @return - Returns the disbVchrExpenseCode
     * 
     */
    public String getDisbVchrExpenseCode() {
        return disbVchrExpenseCode;
    }

    /**
     * Dummy field so we can have different select options.
     * 
     * @return String
     */
    public String getDisbVchrPrePaidExpenseCode() {
        return disbVchrExpenseCode;
    }


    /**
     * Sets the disbVchrExpenseCode attribute.
     * 
     * @param disbVchrExpenseCode The disbVchrExpenseCode to set.
     * 
     */
    public void setDisbVchrExpenseCode(String disbVchrExpenseCode) {
        this.disbVchrExpenseCode = disbVchrExpenseCode;
        this.refresh();
    }

    /**
     * Dummy field so we can have different select options.
     * 
     * @param disbVchrExpenseCode
     */
    public void setDisbVchrPrePaidExpenseCode(String disbVchrExpenseCode) {
        this.disbVchrExpenseCode = disbVchrExpenseCode;
    }

    /**
     * Gets the disbVchrExpenseCompanyName attribute.
     * 
     * @return - Returns the disbVchrExpenseCompanyName
     * 
     */
    public String getDisbVchrExpenseCompanyName() {
        return disbVchrExpenseCompanyName;
    }


    /**
     * Sets the disbVchrExpenseCompanyName attribute.
     * 
     * @param disbVchrExpenseCompanyName The disbVchrExpenseCompanyName to set.
     * 
     */
    public void setDisbVchrExpenseCompanyName(String disbVchrExpenseCompanyName) {
        this.disbVchrExpenseCompanyName = disbVchrExpenseCompanyName;
    }

    /**
     * Gets the disbVchrExpenseAmount attribute.
     * 
     * @return - Returns the disbVchrExpenseAmount
     * 
     */
    public KualiDecimal getDisbVchrExpenseAmount() {
        return disbVchrExpenseAmount;
    }


    /**
     * Sets the disbVchrExpenseAmount attribute.
     * 
     * @param disbVchrExpenseAmount The disbVchrExpenseAmount to set.
     * 
     */
    public void setDisbVchrExpenseAmount(KualiDecimal disbVchrExpenseAmount) {
        this.disbVchrExpenseAmount = disbVchrExpenseAmount;
    }

    /**
     * Gets the disbVchrExpense attribute.
     * 
     * @return - Returns the disbVchrExpense
     * 
     */
    public TravelExpenseTypeCode getDisbVchrExpense() {
        return disbVchrExpense;
    }

    /**
     * Sets the disbVchrExpense attribute.
     * 
     * @param disbVchrExpense The disbVchrExpense to set.
     * @deprecated
     */
    public void setDisbVchrExpense(TravelExpenseTypeCode disbVchrExpense) {
        this.disbVchrExpense = disbVchrExpense;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}