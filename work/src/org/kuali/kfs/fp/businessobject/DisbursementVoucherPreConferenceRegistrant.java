/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/DisbursementVoucherPreConferenceRegistrant.java,v $
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
public class DisbursementVoucherPreConferenceRegistrant extends BusinessObjectBase {

    private String documentNumber;
    private Integer financialDocumentLineNumber;
    private String disbVchrPreConfDepartmentCd;
    private String dvConferenceRegistrantName;
    private String dvPreConferenceRequestNumber;
    private KualiDecimal disbVchrExpenseAmount;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherPreConferenceRegistrant() {

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
     * Gets the disbVchrPreConfDepartmentCd attribute.
     * 
     * @return Returns the disbVchrPreConfDepartmentCd
     * 
     */
    public String getDisbVchrPreConfDepartmentCd() {
        return disbVchrPreConfDepartmentCd;
    }


    /**
     * Sets the disbVchrPreConfDepartmentCd attribute.
     * 
     * @param disbVchrPreConfDepartmentCd The disbVchrPreConfDepartmentCd to set.
     * 
     */
    public void setDisbVchrPreConfDepartmentCd(String disbVchrPreConfDepartmentCd) {
        this.disbVchrPreConfDepartmentCd = disbVchrPreConfDepartmentCd;
    }

    /**
     * Gets the dvConferenceRegistrantName attribute.
     * 
     * @return Returns the dvConferenceRegistrantName
     * 
     */
    public String getDvConferenceRegistrantName() {
        return dvConferenceRegistrantName;
    }


    /**
     * Sets the dvConferenceRegistrantName attribute.
     * 
     * @param dvConferenceRegistrantName The dvConferenceRegistrantName to set.
     * 
     */
    public void setDvConferenceRegistrantName(String dvConferenceRegistrantName) {
        this.dvConferenceRegistrantName = dvConferenceRegistrantName;
    }

    /**
     * Gets the dvPreConferenceRequestNumber attribute.
     * 
     * @return Returns the dvPreConferenceRequestNumber
     * 
     */
    public String getDvPreConferenceRequestNumber() {
        return dvPreConferenceRequestNumber;
    }


    /**
     * Sets the dvPreConferenceRequestNumber attribute.
     * 
     * @param dvPreConferenceRequestNumber The dvPreConferenceRequestNumber to set.
     * 
     */
    public void setDvPreConferenceRequestNumber(String dvPreConferenceRequestNumber) {
        this.dvPreConferenceRequestNumber = dvPreConferenceRequestNumber;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}