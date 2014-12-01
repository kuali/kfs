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
package org.kuali.kfs.fp.document.service;

import java.util.Map;

import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.api.identity.Person;

/**
 * define a set of service methods related to disbursement payee
 */
public interface DisbursementVoucherPayeeService {

    /**
     * find the payee type description corresponding to the given payee type code
     *
     * @param payeeTypeCode the given payee type code
     * @return the payee type description corresponding to the given payee type code
     */
    public String getPayeeTypeDescription(String payeeTypeCode);

    /**
     * determine whether the given payee is an employee
     *
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is an employee; otherwise, false
     */
    public boolean isEmployee(DisbursementVoucherPayeeDetail dvPayeeDetail);

    /**
     * determine whether the given payee is an employee
     *
     * @param payee the given payee
     * @return true if the given payee is an employee; otherwise, false
     */
    public boolean isEmployee(DisbursementPayee payee);

    /**
     * determine whether the given payee is a vendor
     *
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is a vendor; otherwise, false
     */
    public boolean isVendor(DisbursementVoucherPayeeDetail dvPayeeDetail);

    /**
     * determine whether the given payee is a vendor
     *
     * @param payee the given payee
     * @return true if the given payee is a vendor; otherwise, false
     */
    public boolean isVendor(DisbursementPayee payee);

    /**
     * determine whether the given payee is an individual vendor
     *
     * @param dvPayeeDetail the given payee
     * @return true if the given payee is an individual vendor; otherwise, false
     */
    public boolean isPayeeIndividualVendor(DisbursementVoucherPayeeDetail dvPayeeDetail);

    /**
     * determine whether the given payee is an individual vendor
     *
     * @param payee the given payee
     * @return true if the given payee is an individual vendor; otherwise, false
     */
    public boolean isPayeeIndividualVendor(DisbursementPayee payee);

    public void checkPayeeAddressForChanges(DisbursementVoucherDocument dvDoc);

    /**
     * get the ownership type code if the given payee is a vendor
     * @param payee the given payee
     * @return the ownership type code if the given payee is a vendor; otherwise, return null
     */
    public String getVendorOwnershipTypeCode(DisbursementPayee payee);

    /**
     * convert the field names between Payee and Vendor
     *
     * @return a field name map of Payee and Vendor. The map key is a field name of Payee, and its value is a field name of Vendor
     */
    public Map<String, String> getFieldConversionBetweenPayeeAndVendor();

    /**
     * convert the field names between Payee and Person
     *
     * @return a field name map of Payee and Person. The map key is a field name of Payee, and its value is a field name of Person
     */
    public Map<String, String> getFieldConversionBetweenPayeeAndPerson();

    /**
     * build a payee object from the given vendor object
     *
     * @param vendorDetail the given vendor object
     * @return a payee object built from the given vendor object
     */
    public DisbursementPayee getPayeeFromVendor(VendorDetail vendorDetail);

    /**
     * build a payee object from the given person object
     *
     * @param person the given person object
     * @return a payee object built from the given person object
     */
    public DisbursementPayee getPayeeFromPerson(Person person);
}
