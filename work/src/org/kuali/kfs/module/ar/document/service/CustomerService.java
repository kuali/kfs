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
package org.kuali.module.ar.service;

import org.kuali.module.ar.bo.Customer;

public interface CustomerService {

    /**
     * Return customer by customerNumber
     * 
     * @param customerNumber
     * @return
     */
    public Customer getByPrimaryKey(String customerNumber);

    /**
     * This method builds the new customer number
     * 
     * @param newCustomer the new customer
     * @return the new customer number
     */
    public String getNextCustomerNumber(Customer newCustomer);

    /**
     * This method gets a customer given his name
     * 
     * @param customerName
     * @return the customer with the given name
     */
    public Customer getCustomerByName(String customerName);

}
