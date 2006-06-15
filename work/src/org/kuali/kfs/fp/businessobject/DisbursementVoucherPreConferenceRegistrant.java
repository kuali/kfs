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
public class DisbursementVoucherPreConferenceRegistrant extends BusinessObjectBase {

    private String financialDocumentNumber;
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
     * Gets the disbVchrPreConfDepartmentCd attribute.
     * 
     * @return - Returns the disbVchrPreConfDepartmentCd
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
     * @return - Returns the dvConferenceRegistrantName
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
     * @return - Returns the dvPreConferenceRequestNumber
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}