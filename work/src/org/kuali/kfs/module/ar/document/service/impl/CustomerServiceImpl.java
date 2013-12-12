/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.dataaccess.CustomerDao;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerServiceImpl implements CustomerService {

    protected CustomerDao customerDao;
    protected SequenceAccessorService sequenceAccessorService;
    protected BusinessObjectService businessObjectService;
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected NoteService noteService;
    protected static final String CUSTOMER_NUMBER_SEQUENCE = "CUST_NBR_SEQ";

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerService#getByPrimaryKey(java.lang.String)
     */
    @Override
    public Customer getByPrimaryKey(String customerNumber) {
       return businessObjectService.findBySinglePrimaryKey(Customer.class, customerNumber);
    }

    @Override
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
    @Override
    public String getNextCustomerNumber(Customer newCustomer) {
        try {
            Long customerNumberSuffix = sequenceAccessorService.getNextAvailableSequenceNumber(
                    CUSTOMER_NUMBER_SEQUENCE, Customer.class);
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
    @Override
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

    @Override
    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(Customer customer) {
        Collection<CustomerInvoiceDocument> invoices = null;
        if(null != customer) {
            invoices = getInvoicesForCustomer(customer.getCustomerNumber());
        }
        return invoices;
    }

    @Override
    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(String customerNumber) {
        return customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber);
    }


    @Override
    public void createCustomerNote(String customerNumber, String customerNote) {
        Customer customer = getByPrimaryKey(customerNumber);
         try {
            if (StringUtils.isNotBlank(customerNote)) {
                Note newBONote = new Note();
                newBONote.setNoteText(customerNote);
                newBONote.setNotePostedTimestampToCurrent();
                newBONote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                Note note = noteService.createNote(newBONote, customer, GlobalVariables.getUserSession().getPrincipalId());
                noteService.save(note);
              }
        } catch (Exception e){
            throw new RuntimeException("Problems creating note for Customer " + customerNumber);
        }
       }

    @Override
    public List<Note> getCustomerNotes(String customerNumber) {
        Customer customer = getByPrimaryKey(customerNumber);
        List<Note> notes = new ArrayList<Note>();
       if (ObjectUtils.isNotNull(customer)) {
            notes = noteService.getByRemoteObjectId(customer.getObjectId());
        }
        return notes;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

}
