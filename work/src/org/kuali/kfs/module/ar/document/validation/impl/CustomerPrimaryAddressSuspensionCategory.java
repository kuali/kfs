/*
 * Copyright 2014 The Kuali Foundation.
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
