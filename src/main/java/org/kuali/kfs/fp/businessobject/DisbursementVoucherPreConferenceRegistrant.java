/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a disbursement voucher pre-conference registrant.  
 */
public class DisbursementVoucherPreConferenceRegistrant extends PersistableBusinessObjectBase {

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
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }


    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }

    /**
     * Gets the disbVchrPreConfDepartmentCd attribute.
     * 
     * @return Returns the disbVchrPreConfDepartmentCd
     */
    public String getDisbVchrPreConfDepartmentCd() {
        return disbVchrPreConfDepartmentCd;
    }


    /**
     * Sets the disbVchrPreConfDepartmentCd attribute.
     * 
     * @param disbVchrPreConfDepartmentCd The disbVchrPreConfDepartmentCd to set.
     */
    public void setDisbVchrPreConfDepartmentCd(String disbVchrPreConfDepartmentCd) {
        this.disbVchrPreConfDepartmentCd = disbVchrPreConfDepartmentCd;
    }

    /**
     * Gets the dvConferenceRegistrantName attribute.
     * 
     * @return Returns the dvConferenceRegistrantName
     */
    public String getDvConferenceRegistrantName() {
        return dvConferenceRegistrantName;
    }


    /**
     * Sets the dvConferenceRegistrantName attribute.
     * 
     * @param dvConferenceRegistrantName The dvConferenceRegistrantName to set.
     */
    public void setDvConferenceRegistrantName(String dvConferenceRegistrantName) {
        this.dvConferenceRegistrantName = dvConferenceRegistrantName;
    }

    /**
     * Gets the dvPreConferenceRequestNumber attribute.
     * 
     * @return Returns the dvPreConferenceRequestNumber
     */
    public String getDvPreConferenceRequestNumber() {
        return dvPreConferenceRequestNumber;
    }


    /**
     * Sets the dvPreConferenceRequestNumber attribute.
     * 
     * @param dvPreConferenceRequestNumber The dvPreConferenceRequestNumber to set.
     */
    public void setDvPreConferenceRequestNumber(String dvPreConferenceRequestNumber) {
        this.dvPreConferenceRequestNumber = dvPreConferenceRequestNumber;
    }

    /**
     * Gets the disbVchrExpenseAmount attribute.
     * 
     * @return Returns the disbVchrExpenseAmount
     */
    public KualiDecimal getDisbVchrExpenseAmount() {
        return disbVchrExpenseAmount;
    }


    /**
     * Sets the disbVchrExpenseAmount attribute.
     * 
     * @param disbVchrExpenseAmount The disbVchrExpenseAmount to set.
     */
    public void setDisbVchrExpenseAmount(KualiDecimal disbVchrExpenseAmount) {
        this.disbVchrExpenseAmount = disbVchrExpenseAmount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
