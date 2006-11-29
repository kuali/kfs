/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/DisbursementVoucherPreConferenceDetail.java,v $
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class DisbursementVoucherPreConferenceDetail extends BusinessObjectBase {

    private String documentNumber;
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
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
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
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the dvConferenceDestinationName attribute.
     * 
     * @return Returns the dvConferenceDestinationName
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
     * @return Returns the disbVchrConferenceStartDate
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
     * @return Returns the disbVchrConferenceEndDate
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
     * @return Returns the disbVchrConferenceTotalAmt
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
     * @return Returns the disbVchrExpenseCode
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
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
