/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * This class represents the different types of credit card types
 */
public class CreditCardType extends PersistableBusinessObjectBase {

    private String financialDocumentCreditCardTypeCode;
    private String financialDocumentCreditCardCompanyName;

    /**
     * Default constructor.
     */
    public CreditCardType() {

    }

    /**
     * Gets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @return Returns the financialDocumentCreditCardTypeCode
     */
    public String getFinancialDocumentCreditCardTypeCode() {
        return financialDocumentCreditCardTypeCode;
    }

    /**
     * Sets the financialDocumentCreditCardTypeCode attribute.
     * 
     * @param financialDocumentCreditCardTypeCode The financialDocumentCreditCardTypeCode to set.
     */
    public void setFinancialDocumentCreditCardTypeCode(String financialDocumentCreditCardTypeCode) {
        this.financialDocumentCreditCardTypeCode = financialDocumentCreditCardTypeCode;
    }


    /**
     * Gets the financialDocumentCreditCardCompanyName attribute.
     * 
     * @return Returns the financialDocumentCreditCardCompanyName
     */
    public String getFinancialDocumentCreditCardCompanyName() {
        return financialDocumentCreditCardCompanyName;
    }

    /**
     * Sets the financialDocumentCreditCardCompanyName attribute.
     * 
     * @param financialDocumentCreditCardCompanyName The financialDocumentCreditCardCompanyName to set.
     */
    public void setFinancialDocumentCreditCardCompanyName(String financialDocumentCreditCardCompanyName) {
        this.financialDocumentCreditCardCompanyName = financialDocumentCreditCardCompanyName;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentCreditCardTypeCode", this.financialDocumentCreditCardTypeCode);
        return m;
    }
}
