/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/DisbursementVoucherNonEmployeeExpense.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class DisbursementVoucherNonEmployeeExpense extends BusinessObjectBase {

    private String documentNumber;
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
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
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
     * @return Returns the disbVchrExpenseCode
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
     * @return Returns the disbVchrExpenseCompanyName
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
     * Gets the disbVchrExpenseCompanyName attribute.
     * 
     * @return Returns the disbVchrExpenseCompanyName
     * 
     */
    public String getDisbVchrPrePaidExpenseCompanyName() {
        return disbVchrExpenseCompanyName;
    }


    /**
     * Sets the disbVchrExpenseCompanyName attribute.
     * 
     * @param disbVchrExpenseCompanyName The disbVchrExpenseCompanyName to set.
     * 
     */
    public void setDisbVchrPrePaidExpenseCompanyName(String disbVchrExpenseCompanyName) {
        this.disbVchrExpenseCompanyName = disbVchrExpenseCompanyName;
    }

    /**
     * Gets the disbVchrExpenseAmount attribute.
     * 
     * @return Returns the disbVchrExpenseAmount
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
     * @return Returns the disbVchrExpense
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
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}