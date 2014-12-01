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
