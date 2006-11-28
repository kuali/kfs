/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/Bank.java,v $
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
package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
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
     * @return Returns the financialDocumentBankCode
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
     * @return Returns the financialDocumentBankName
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
     * @return Returns the financialDocumentBankShortNm
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
     * @return Returns the bankAccounts
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
     * 
     * @return Returns the bankRoutingNumber.
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    /**
     * Sets the bankRoutingNumber attribute value.
     * 
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
