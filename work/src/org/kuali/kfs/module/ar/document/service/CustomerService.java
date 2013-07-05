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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.krad.bo.Note;

public interface CustomerService {

    /**
     * Return customer by customerNumber
     * @param customerNumber
     * @return
     */
    public Customer getByPrimaryKey(String customerNumber);
    
    /**
     * Return customer by taxNumber
     * @param customerNumber
     * @return
     */
    public Customer getByTaxNumber(String taxNumber);
    
    /**
     * This method builds the new customer number
     * @param newCustomer the new customer
     * @return the new customer number
     */
    public String getNextCustomerNumber(Customer newCustomer);
    
    /**
     * This method gets a customer given his name
     * @param customerName
     * @return the customer with the given name
     */
    public Customer getCustomerByName(String customerName);
    
    /**
     * @param customer
     * @return
     */
    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(Customer customer);
    
    /**
     * @param customerNumber
     * @return
     */
    public Collection<CustomerInvoiceDocument> getInvoicesForCustomer(String customerNumber);
    
    /**
     * This method create customer notes from customer notes for customer number.
     * 
     * @param customerNumber
     * @param customerNote
     */
    public void createCustomerNote( String customerNumber, String customerNote );

    /**
     * Gets list of notes for customer
     * 
     * @param customerNumber
     * @return list of Notes.
     */
    public List<Note> getCustomerNotes(String customerNumber) ;
}
