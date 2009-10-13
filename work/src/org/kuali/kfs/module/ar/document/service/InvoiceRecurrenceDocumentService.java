/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.service;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface InvoiceRecurrenceDocumentService {
        
    /**
     * This method returns true if customer invoice detail amount can be taxed.
     * 
     * @param customer
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailTaxable( CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method returns the appropriate postal code for taxation
     * @param document
     * @return
     */
    public String getPostalCodeForTaxation( CustomerInvoiceDocument document );

    /**
     * This method returns true if the invoice has an approved status.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isInvoiceApproved( String invoiceNumber ) ;

    /**
     * This method returns true if the bein date is valid.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidRecurrenceBeginDate( Date beginDate ) ;

    /**
     * This method returns true if the end date is valid.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidRecurrenceEndDate( Date beginDate, Date endDate ) ;

    /**
     * This method returns true if the end date and number of recurrences are valid if entered together.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidEndDateAndTotalRecurrenceNumber( Date beginDate, Date endDate, Integer totalRecurrenceNumber, String intervalCode ) ;

    /**
     * This method returns true if one of the end date or the number of recurrences is entered.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidEndDateOrTotalRecurrenceNumber( Date endDate, Integer totalRecurrenceNumber ) ;

    /**
     * This method returns true if the number of recurrences is not more than the maximum allowed.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidMaximumNumberOfRecurrences( Integer totalRecurrenceNumber, String intervalCode ) ;

    /**
     * This method returns true if the initiator is valid.
     * 
     * @param invoiceNumber
     * @return
     */
    public boolean isValidInitiator( String initiator ) ;

    
}
