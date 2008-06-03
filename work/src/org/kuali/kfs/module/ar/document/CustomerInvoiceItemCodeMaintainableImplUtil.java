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
package org.kuali.module.ar.maintenance.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerInvoiceItemCodeMaintainableImplUtil {
    
    public final static String CHART_OF_ACCOUNTS_CODE_FIELD = "chartOfAccountsCode";
    public final static String ORGANIZATION_CODE_FIELD = "organizationCode";
    
    /**
     * This utility is used for creating the special lock for customer invoice item code to ensure that 
     * no invoice item codes are created if a organization option maint. doc is pending with the same
     * chart and organization.
     */
    public static List<MaintenanceLock> generateCustomerInvoiceItemCodeMaintenanceLocks(PersistableBusinessObject bo, String documentNumber) {

        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        StringBuilder lockRepresentation = new StringBuilder(CustomerInvoiceItemCode.class.getName());
        lockRepresentation.append(KNSConstants.Maintenance.AFTER_CLASS_DELIM);
        
        //get chart of accounts code locking representation
        String chartOfAccountsCode = String.valueOf(ObjectUtils.getPropertyValue(bo, CHART_OF_ACCOUNTS_CODE_FIELD));
        lockRepresentation.append(CHART_OF_ACCOUNTS_CODE_FIELD);
        lockRepresentation.append(KNSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
        lockRepresentation.append(chartOfAccountsCode);
        
        //get organization code locking representation
        String organizationCode = String.valueOf(ObjectUtils.getPropertyValue(bo, ORGANIZATION_CODE_FIELD));
        lockRepresentation.append(ORGANIZATION_CODE_FIELD);
        lockRepresentation.append(KNSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
        lockRepresentation.append(organizationCode);

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);
        return maintenanceLocks;
    }

}
