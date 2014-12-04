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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

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
        lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_CLASS_DELIM);
        
        //get chart of accounts code locking representation
        String chartOfAccountsCode = String.valueOf(ObjectUtils.getPropertyValue(bo, CHART_OF_ACCOUNTS_CODE_FIELD));
        lockRepresentation.append(CHART_OF_ACCOUNTS_CODE_FIELD);
        lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_FIELDNAME_DELIM);
        lockRepresentation.append(chartOfAccountsCode);
        
        //get organization code locking representation
        String organizationCode = String.valueOf(ObjectUtils.getPropertyValue(bo, ORGANIZATION_CODE_FIELD));
        lockRepresentation.append(ORGANIZATION_CODE_FIELD);
        lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_FIELDNAME_DELIM);
        lockRepresentation.append(organizationCode);

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);
        return maintenanceLocks;
    }

}
