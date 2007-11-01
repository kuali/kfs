/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.maintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.AccountGlobal;

/**
 * 
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific
 * maintenance locks for Global accounts
 */
public class AccountGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    /**
     * This creates the particular locking representation for this global document.
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AccountGlobal accountGlobal = (AccountGlobal)getBusinessObject();
        List <MaintenanceLock> maintenanceLocks = new ArrayList();
        
        for ( AccountGlobalDetail detail : accountGlobal.getAccountGlobalDetails() ) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockrep = new StringBuffer();

            lockrep.append(Account.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockrep.append("chartOfAccountsCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getChartOfAccountsCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockrep.append("accountNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getAccountNumber());

            maintenanceLock.setDocumentNumber(accountGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockrep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
    }
}
