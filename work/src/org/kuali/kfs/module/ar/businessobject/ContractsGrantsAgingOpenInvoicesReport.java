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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * This class is used to See the open invoices for the customers from the CGAging Report.
 */
public class ContractsGrantsAgingOpenInvoicesReport extends CustomerOpenItemReportDetail {

    private static final String COLLECTION_ACTIVITY_TITLE_PROPERTY = ArKeyConstants.CollectionActivityDocumentConstants.COLLECTION_ACTIVITY_TITLE_PROPERTY;
    private String collectionActivityInquiryTitle;
    private String finalInvoice;
    private String agencyNumber;
    private String accountNumber;
    private String proposalNumber;

    /**
     * Gets the collectionActivityInquiryTitle attribute.
     * 
     * @return Returns the collectionActivityInquiryTitle.
     */
    public String getCollectionActivityInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(COLLECTION_ACTIVITY_TITLE_PROPERTY);
    }

    /**
     * Sets the collectionActivityInquiryTitle attribute value.
     * 
     * @param collectionActivityInquiryTitle The collectionActivityInquiryTitle to set.
     */
    public void setCollectionActivityInquiryTitle(String collectionActivityInquiryTitle) {
        this.collectionActivityInquiryTitle = collectionActivityInquiryTitle;
    }

    /**
     * Gets the finalInvoice attribute.
     * 
     * @return Returns the finalInvoice.
     */
    public String getFinalInvoice() {
        return finalInvoice;
    }

    /**
     * Sets the finalInvoice attribute value.
     * 
     * @param finalInvoice The finalInvoice to set.
     */
    public void setFinalInvoice(String finalInvoice) {
        this.finalInvoice = finalInvoice;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute value.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


}
