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
package org.kuali.kfs.integration.cg;

import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;

/**
 * Methods which return information needed about Contracts and Grants agencies by other modules.
 */
public interface ContractsAndGrantsBillingAgency extends ContractsAndGrantsAgency {
    /**
     * Returns the agency number for this agency
     *
     * @return the agency number for the given agency
     */
    @Override
    public abstract String getAgencyNumber();


    /**
     * Gets the fullName attribute.
     *
     * @return Returns the fullName
     */
    public String getFullName();


    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber();


    /**
     * Gets the reportingName attribute.
     *
     * @return Returns the reportingName
     */
    public String getReportingName();


    /**
     * Gets the customerTypeCode attribute.
     *
     * @return Returns the customerTypeCode.
     */
    public String getCustomerTypeCode();


    /**
     * Gets the dunsPlusFourNumber attribute.
     *
     * @return Returns the dunsPlusFourNumber.
     */
    public String getDunsPlusFourNumber();

    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer.
     */
    public AccountsReceivableCustomer getCustomer();

    /**
     * Gets the stateAgency attribute.
     *
     * @return Returns the stateAgency.
     */
    public boolean isStateAgencyIndicator();

    /**
     * Gets the agencyAddresses attribute
     *
     * @return
     */
    public List<? extends ContractsAndGrantsAgencyAddress> getAgencyAddresses();
}
