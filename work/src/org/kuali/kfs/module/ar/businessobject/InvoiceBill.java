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
package org.kuali.kfs.module.ar.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class extends the Bills BO to be used exclusively for Contracts Grants Invoice Document with document Number as its key.
 */

public class InvoiceBill extends PersistableBusinessObjectBase implements ContractsAndGrantsBill {

    private String documentNumber;
    private Long proposalNumber;
    private Long billNumber;
    private String billDescription;
    private Long billIdentifier;
    private Date billDate;
    private boolean billedIndicator;
    private KualiDecimal estimatedAmount;



    private ContractsGrantsInvoiceDocument invoiceDocument;


    /**
     * Constructs a Bills.java.
     */
    public InvoiceBill() {

    }

    public boolean isBilledIndicator() {
        return billedIndicator;
    }

    public void setBilledIndicator(boolean billedIndicator) {
        this.billedIndicator = billedIndicator;
    }

    /**
     * Gets the billDate attribute.
     *
     * @return Returns the billDate.
     */
    @Override
    public Date getBillDate() {
        return billDate;
    }


    /**
     * Gets the billDescription attribute.
     *
     * @return Returns the billDescription.
     */
    @Override
    public String getBillDescription() {
        return billDescription;
    }

    /**
     * Gets the billIdentifier attribute.
     *
     * @return Returns the billIdentifier.
     */
    @Override
    public Long getBillIdentifier() {
        return billIdentifier;
    }

    /**
     * Gets the billNumber attribute.
     *
     * @return Returns the billNumber.
     */
    @Override
    public Long getBillNumber() {
        return billNumber;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Gets the estimatedAmount attribute.
     *
     * @return Returns the estimatedAmount.
     */
    @Override
    public KualiDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    /**
     * Gets the invoiceDocument attribute.
     *
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    @Override
    public Long getProposalNumber() {
        return proposalNumber;
    }


    /**
     * Sets the billDate attribute value.
     *
     * @param billDate The billDate to set.
     */
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    /**
     * Sets the billDescription attribute value.
     *
     * @param billDescription The billDescription to set.
     */
    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    /**
     * Sets the billIdentifier attribute value.
     *
     * @param billIdentifier The billIdentifier to set.
     */
    public void setBillIdentifier(Long billIdentifier) {
        this.billIdentifier = billIdentifier;
    }



    /**
     * Sets the billNumber attribute value.
     *
     * @param billNumber The billNumber to set.
     */
    public void setBillNumber(Long billNumber) {
        this.billNumber = billNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Sets the estimatedAmount attribute value.
     *
     * @param estimatedAmount The estimatedAmount to set.
     */
    public void setEstimatedAmount(KualiDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }


    /**
     * Sets the invoiceDocument attribute value.
     *
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put("billDescription", this.billDescription);

        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }

        if (this.billNumber != null) {
            m.put("billNumber", this.billNumber.toString());
        }

        if (this.billIdentifier != null) {
            m.put("billIdentifier", this.billIdentifier.toString());
        }

        if (this.billDate != null) {
            m.put("billDate", this.billDate.toString());
        }

        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }

        m.put("isBilledIndicator", this.billedIndicator);

        if (this.estimatedAmount != null) {
            m.put("estimatedAmount", this.estimatedAmount.toString());
        }

        return m;
    }
}
