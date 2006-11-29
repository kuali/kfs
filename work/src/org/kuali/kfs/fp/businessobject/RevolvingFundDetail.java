/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/RevolvingFundDetail.java,v $
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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RevolvingFundDetail extends BusinessObjectBase {

    private String documentNumber;
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
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
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
     * @return Returns the financialDocumentColumnTypeCode
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
     * Gets the financialDocumentRevolvingFundDate attribute.
     * 
     * @return Returns the financialDocumentRevolvingFundDate
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
     * @return Returns the financialDocumentRevolvingFundReferenceNumber
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
     * @return Returns the financialDocumentRevolvingFundDescription
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
     * @return Returns the financialDocumentRevolvingFundAmount
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
     * Gets the financialDocumentBankAccountNumber attribute.
     * 
     * @return Returns the financialDocumentBankAccountNumber
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
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialDocumentColumnTypeCode", this.financialDocumentColumnTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
