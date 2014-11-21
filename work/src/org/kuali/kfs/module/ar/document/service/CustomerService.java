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
    
    
    public void createCustomerNote( String customerNumber, String customerNote );

    public List<Note> getCustomerNotes(String customerNumber) ;
}
