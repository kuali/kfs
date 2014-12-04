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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.Customer;
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

public class CustomerServiceImpl implements CustomerService {

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
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("customerTaxNbr", taxNumber);


        Collection<Customer> customers = getBusinessObjectService().findMatching(Customer.class, fieldValues);
        Customer customer = null;
        for (Customer c: customers) {
            customer = c;
        }

        return customer;
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
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("customerName", customerName);

        Collection<Customer> customers = getBusinessObjectService().findMatching(Customer.class, fieldValues);
        Customer customer = null;
        for (Customer c: customers) {
            customer = c;
        }

        return customer;
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
        if (StringUtils.isNotBlank(customerNote)) {
            Note newBONote = new Note();
            newBONote.setNoteText(customerNote);
            newBONote.setNotePostedTimestampToCurrent();
            newBONote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
            Note note = noteService.createNote(newBONote, customer, GlobalVariables.getUserSession().getPrincipalId());
            noteService.save(note);
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
