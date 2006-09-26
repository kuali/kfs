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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;

/**
 * @author Kuali Financial Transactions Team ()
 */
public class DisbursementVoucherPreConferenceDetail extends BusinessObjectBase {

    private String financialDocumentNumber;
    private String dvConferenceDestinationName;
    private Date disbVchrConferenceStartDate;
    private Date disbVchrConferenceEndDate;
    private KualiDecimal disbVchrConferenceTotalAmt;
    private String disbVchrExpenseCode;


    private List dvPreConferenceRegistrants;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherPreConferenceDetail() {
        dvPreConferenceRegistrants = new TypedArrayList(DisbursementVoucherPreConferenceRegistrant.class);
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
     * Gets the dvPreConferenceRegistrants attribute.
     * 
     * @return Returns the dvPreConferenceRegistrants.
     */
    public List getDvPreConferenceRegistrants() {
        return dvPreConferenceRegistrants;
    }

    /**
     * Sets the dvPreConferenceRegistrants attribute value.
     * 
     * @param dvPreConferenceRegistrants The dvPreConferenceRegistrants to set.
     */
    public void setDvPreConferenceRegistrants(List dvPreConferenceRegistrants) {
        this.dvPreConferenceRegistrants = dvPreConferenceRegistrants;
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
     * Gets the dvConferenceDestinationName attribute.
     * 
     * @return - Returns the dvConferenceDestinationName
     * 
     */
    public String getDvConferenceDestinationName() {
        return dvConferenceDestinationName;
    }


    /**
     * Sets the dvConferenceDestinationName attribute.
     * 
     * @param dvConferenceDestinationName The dvConferenceDestinationName to set.
     * 
     */
    public void setDvConferenceDestinationName(String dvConferenceDestinationName) {
        this.dvConferenceDestinationName = dvConferenceDestinationName;
    }

    /**
     * Gets the disbVchrConferenceStartDate attribute.
     * 
     * @return - Returns the disbVchrConferenceStartDate
     * 
     */
    public Date getDisbVchrConferenceStartDate() {
        return disbVchrConferenceStartDate;
    }


    /**
     * Sets the disbVchrConferenceStartDate attribute.
     * 
     * @param disbVchrConferenceStartDate The disbVchrConferenceStartDate to set.
     * 
     */
    public void setDisbVchrConferenceStartDate(Date disbVchrConferenceStartDate) {
        this.disbVchrConferenceStartDate = disbVchrConferenceStartDate;
    }

    /**
     * Gets the disbVchrConferenceEndDate attribute.
     * 
     * @return - Returns the disbVchrConferenceEndDate
     * 
     */
    public Date getDisbVchrConferenceEndDate() {
        return disbVchrConferenceEndDate;
    }


    /**
     * Sets the disbVchrConferenceEndDate attribute.
     * 
     * @param disbVchrConferenceEndDate The disbVchrConferenceEndDate to set.
     * 
     */
    public void setDisbVchrConferenceEndDate(Date disbVchrConferenceEndDate) {
        this.disbVchrConferenceEndDate = disbVchrConferenceEndDate;
    }

    /**
     * Gets the disbVchrConferenceTotalAmt attribute.
     * 
     * @return - Returns the disbVchrConferenceTotalAmt
     * 
     */
    public KualiDecimal getDisbVchrConferenceTotalAmt() {
        KualiDecimal totalConferenceAmount = new KualiDecimal(0);

        if (dvPreConferenceRegistrants != null) {
            for (Iterator iter = dvPreConferenceRegistrants.iterator(); iter.hasNext();) {
                DisbursementVoucherPreConferenceRegistrant registrantLine = (DisbursementVoucherPreConferenceRegistrant) iter.next();
                totalConferenceAmount = totalConferenceAmount.add(registrantLine.getDisbVchrExpenseAmount());
            }
        }

        return totalConferenceAmount;
    }


    /**
     * Sets the disbVchrConferenceTotalAmt attribute.
     * 
     * @param disbVchrConferenceTotalAmt The disbVchrConferenceTotalAmt to set.
     * 
     */
    public void setDisbVchrConferenceTotalAmt(KualiDecimal disbVchrConferenceTotalAmt) {
        this.disbVchrConferenceTotalAmt = disbVchrConferenceTotalAmt;
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
     * Sets the disbVchrExpenseCode attribute.
     * 
     * @param disbVchrExpenseCode The disbVchrExpenseCode to set.
     * 
     */
    public void setDisbVchrExpenseCode(String disbVchrExpenseCode) {
        this.disbVchrExpenseCode = disbVchrExpenseCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }
}
