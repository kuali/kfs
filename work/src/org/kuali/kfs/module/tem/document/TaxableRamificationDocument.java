/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.sql.Date;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * if the cash advance is not cleared on an expense report, the system will generate a tax ramification document showing all taxable
 * income of travelers/hosts.
 */
public class TaxableRamificationDocument extends FinancialSystemTransactionalDocumentBase {
    private final static Logger LOG = Logger.getLogger(TaxableRamificationDocument.class);

    private String arInvoiceDocNumber;
    private Date dueDate;

    private KualiDecimal invoiceAmount;
    private KualiDecimal openAmount;
    
    private String taxableRamificationNotice;

    private String travelDocumentIdentifier;
    private Integer travelerDetailId;
    private Integer travelAdvanceId;

    private TravelerDetail travelerDetail;
    private TravelAdvance travelAdvance;

    /**
     * Gets the arInvoiceDocNumber attribute.
     * 
     * @return Returns the arInvoiceDocNumber.
     */
    public String getArInvoiceDocNumber() {
        return arInvoiceDocNumber;
    }

    /**
     * Sets the arInvoiceDocNumber attribute value.
     * 
     * @param arInvoiceDocNumber The arInvoiceDocNumber to set.
     */
    public void setArInvoiceDocNumber(String arInvoiceDocNumber) {
        this.arInvoiceDocNumber = arInvoiceDocNumber;
    }

    /**
     * Gets the dueDate attribute.
     * 
     * @return Returns the dueDate.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the dueDate attribute value.
     * 
     * @param dueDate The dueDate to set.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the invoiceAmount attribute.
     * 
     * @return Returns the invoiceAmount.
     */
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     * 
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the openAmount attribute.
     * 
     * @return Returns the openAmount.
     */
    public KualiDecimal getOpenAmount() {
        return openAmount;
    }

    /**
     * Sets the openAmount attribute value.
     * 
     * @param openAmount The openAmount to set.
     */
    public void setOpenAmount(KualiDecimal openAmount) {
        this.openAmount = openAmount;
    }

    /**
     * Gets the travelerDetail attribute.
     * 
     * @return Returns the travelerDetail.
     */
    public TravelerDetail getTravelerDetail() {
        return travelerDetail;
    }

    /**
     * Sets the travelerDetail attribute value.
     * 
     * @param travelerDetail The travelerDetail to set.
     */
    public void setTravelerDetail(TravelerDetail travelerDetail) {
        this.travelerDetail = travelerDetail;
    }

    /**
     * Gets the travelAdvance attribute.
     * 
     * @return Returns the travelAdvance.
     */
    public TravelAdvance getTravelAdvance() {
        return travelAdvance;
    }

    /**
     * Sets the travelAdvance attribute value.
     * 
     * @param travelAdvance The travelAdvance to set.
     */
    public void setTravelAdvance(TravelAdvance travelAdvance) {
        this.travelAdvance = travelAdvance;
    }

    /**
     * Gets the travelDocumentIdentifier attribute.
     * 
     * @return Returns the travelDocumentIdentifier.
     */
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Sets the travelDocumentIdentifier attribute value.
     * 
     * @param travelDocumentIdentifier The travelDocumentIdentifier to set.
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

    /**
     * Gets the travelerDetailId attribute.
     * 
     * @return Returns the travelerDetailId.
     */
    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    /**
     * Sets the travelerDetailId attribute value.
     * 
     * @param travelerDetailId The travelerDetailId to set.
     */
    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    /**
     * Gets the travelAdvanceId attribute.
     * 
     * @return Returns the travelAdvanceId.
     */
    public Integer getTravelAdvanceId() {
        return travelAdvanceId;
    }

    /**
     * Sets the travelAdvanceId attribute value.
     * 
     * @param travelAdvanceId The travelAdvanceId to set.
     */
    public void setTravelAdvanceId(Integer travelAdvanceId) {
        this.travelAdvanceId = travelAdvanceId;
    }

    /**
     * Gets the taxableRamificationNotice attribute. 
     * @return Returns the taxableRamificationNotice.
     */
    public String getTaxableRamificationNotice() {
        return taxableRamificationNotice;
    }

    /**
     * Sets the taxableRamificationNotice attribute value.
     * @param taxableRamificationNotice The taxableRamificationNotice to set.
     */
    public void setTaxableRamificationNotice(String taxableRamificationNotice) {
        this.taxableRamificationNotice = taxableRamificationNotice;
    }
}
