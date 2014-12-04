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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Suspension Category that checks to see if the Customer Primary Address is completed.
 */
public class CustomerPrimaryAddressSuspensionCategory extends CustomerAddressSuspensionCategoryBase {

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        for (InvoiceAddressDetail addressDetail : contractsGrantsInvoiceDocument.getInvoiceAddressDetails()) {
            if (StringUtils.equals(ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE, addressDetail.getCustomerAddressTypeCode())) {
                if (ObjectUtils.isNull(addressDetail.getCustomerAddress())) {
                    addressDetail.refreshReferenceObject(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS);
                }
                return !isCustomerAddressComplete(addressDetail.getCustomerAddress());
            }
        }
        return true;
    }

}
