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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class CustomerTypeRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerTypeRule.class);

    protected CustomerType newCustomerType;

    @Override
    public void setupConvenienceObjects() {
        newCustomerType = (CustomerType) super.getNewBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success = validateCustomerTypeDescription(newCustomerType);

        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method checks if there is another customer type in the database with the same description
     * 
     * @param customerType
     * @return true if there is no other customer type in the database with the same description, false otherwise
     */
    public boolean validateCustomerTypeDescription(CustomerType customerType) {
        boolean success = true;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        List<CustomerType> dataToValidateList = new ArrayList<CustomerType>(businessObjectService.findAll(CustomerType.class));
        List<String> errorMessages = new ArrayList<String>();

        Map<String, Account> retrievedAccounts = new HashMap<String, Account>();

        for (CustomerType record : dataToValidateList) {
            if (customerType.getCustomerTypeDescription() != null && customerType.getCustomerTypeDescription().equalsIgnoreCase(record.getCustomerTypeDescription())) {
                if (customerType.getCustomerTypeCode() != null && !customerType.getCustomerTypeCode().equalsIgnoreCase(record.getCustomerTypeCode())) {
                    putFieldError(ArPropertyConstants.CustomerTypeFields.CUSTOMER_TYPE_DESC, ArKeyConstants.CustomerTypeConstants.ERROR_CUSTOMER_TYPE_DUPLICATE_VALUE);
                    success = false;
                    break;
                }
            }
        }
        return success;
    }
}
