/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.dataaccess.CustomerDao;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao;
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private NoteService noteService;
    private static final String CUSTOMER_NUMBER_SEQUENCE = "CUST_NBR_SEQ";
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerService#getByPrimaryKey(java.lang.String)
     */
    public Customer getByPrimaryKey(String customerNumber) {
       return customerDao.getByPrimaryId(customerNumber);
    }

    public Customer getByTaxNumber(String taxNumber) {
        return customerDao.getByTaxNumber(taxNumber);
    }
    
    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerService#getNextCustomerNumber(org.kuali.kfs.module.ar.businessobject.Customer)
     */
    public String getNextCustomerNumber(Customer newCustomer) {
        try {
            Long customerNumberSuffix = sequenceAccessorService.getNextAvailableSequenceNumber(CUSTOMER_NUMBER_SEQUENCE);
            String customerNumberPrefix = newCustomer.getCustomerName().substring(0, 3);
            String customerNumber = customerNumberPrefix + String.valueOf(customerNumberSuffix);
    
            return customerNumber;
        } catch(StringIndexOutOfBoundsException sibe) {
            // The customer number generation expects all customer names to be at least three characters long.
            throw new StringIndexOutOfBoundsException("Customer name is less than three characters in length.");
        }
    }

    /**
     * This method gets the sequenceAccessorService
     * 
     * @return the sequenceAccessorService
     */
    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    /**
     * This method sets the sequenceAccessorService
     * 
     * @param sequenceAccessorService
     */
    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerService#getCustomerByName(java.lang.String)
     */
    public Customer getCustomerByName(String customerName) {
        return customerDao.getByName(customerName);
	}
	
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(Customer customer) {
        Collection<CustomerInvoiceDocument> invoices = null;
        if(null != customer) {
            invoices = getInvoicesForCustomer(customer.getCustomerNumber());
        }
        return invoices;
    }

    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(String customerNumber) {
        return customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber);
    }

    public void createCustomerNote(String customerNumber, String customerNote) {
        Customer customer = getByPrimaryKey(customerNumber);
        Note note = new Note();
        note.setNoteText(customerNote);
        try {
            note = noteService.createNote(note, customer);
            noteService.save(note);
        } catch (Exception e){
            throw new RuntimeException("Problems creating note for Customer " + customerNumber);
        }
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

}
