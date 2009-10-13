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

import org.kuali.kfs.module.ar.businessobject.CustomerAddress;

public interface CustomerAddressService {

    /**
     * This method returns a customer address by primary key
     * 
     * @param customerNumber
     * @param customerAddressIdentifier
     * @return
     */
    public CustomerAddress getByPrimaryKey(String customerNumber, Integer customerAddressIdentifier);

    /**
     * This method returns true if customer address exists
     * 
     * @param customerNumber
     * @param customerAddressIdentifier
     * @return
     */
    public boolean customerAddressExists(String customerNumber, Integer customerAddressIdentifier);

    /**
     * This method gets the next address identifier
     * @return
     */
    public Integer getNextCustomerAddressIdentifier();
    
    /**
     * 
     * This method returns the CustomerAddress specified as the primary address for a Customer.
     * @param customerNumber
     * @return
     */
    
    public CustomerAddress getPrimaryAddress(String customerNumber);

    /**
     * This method returns true if customer address is active
     * 
     * @param customerNumber
     * @param customerAddressIdentifier
     * @return
     */
    public boolean customerAddressActive(String customerNumber, Integer customerAddressIdentifier);
}
