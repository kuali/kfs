package org.kuali.module.financial.bo;

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

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class Bank extends BusinessObjectBase {

    private static final long serialVersionUID = 6091563911993138998L;
    private String financialDocumentBankCode;
    private String financialDocumentBankName;
    private String financialDocumentBankShortNm;
    private String bankRoutingNumber;
    private List bankAccounts;

    /**
     * Default no-arg constructor.
     */

    public Bank() {
        super();
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
     * Gets the financialDocumentBankName attribute.
     * 
     * @return - Returns the financialDocumentBankName
     * 
     */
    public String getFinancialDocumentBankName() {
        return financialDocumentBankName;
    }

    /**
     * Sets the financialDocumentBankName attribute.
     * 
     * @param financialDocumentBankName The financialDocumentBankName to set.
     * 
     */
    public void setFinancialDocumentBankName(String financialDocumentBankName) {
        this.financialDocumentBankName = financialDocumentBankName;
    }

    /**
     * Gets the financialDocumentBankShortNm attribute.
     * 
     * @return - Returns the financialDocumentBankShortNm
     * 
     */
    public String getFinancialDocumentBankShortNm() {
        return financialDocumentBankShortNm;
    }

    /**
     * Sets the financialDocumentBankShortNm attribute.
     * 
     * @param financialDocumentBankShortNm The financialDocumentBankShortNm to set.
     * 
     */
    public void setFinancialDocumentBankShortNm(String financialDocumentBankShortNm) {
        this.financialDocumentBankShortNm = financialDocumentBankShortNm;
    }

    /**
     * Gets the bankAccounts attribute.
     * 
     * @return - Returns the bankAccounts
     * 
     */
    public List getBankAccounts() {
        return bankAccounts;
    }

    /**
     * Sets the bankAccounts attribute.
     * 
     * @param bankAccounts The bankAccounts to set.
     * 
     */
    public void setBankAccounts(List bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    /**
     * Gets the bankRoutingNumber attribute. 
     * @return Returns the bankRoutingNumber.
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    /**
     * Sets the bankRoutingNumber attribute value.
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("bankCode", getFinancialDocumentBankCode());

        return m;
    }
    
}
