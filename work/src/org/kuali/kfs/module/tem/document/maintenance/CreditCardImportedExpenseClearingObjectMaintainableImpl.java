/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportedExpenseClearingDetail;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportedExpenseClearingObject;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global accounts
 */
public class CreditCardImportedExpenseClearingObjectMaintainableImpl extends KualiGlobalMaintainableImpl {
    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        CreditCardImportedExpenseClearingObject cardImportedExpenseClearingObject =  (CreditCardImportedExpenseClearingObject) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        for (CreditCardImportedExpenseClearingDetail detail : cardImportedExpenseClearingObject.getExpenses()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockrep = new StringBuffer();

            lockrep.append(HistoricalTravelExpense.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockrep.append("id" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockrep.append(detail.getCreditCardStagingDataId() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);

            maintenanceLock.setDocumentNumber(cardImportedExpenseClearingObject.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockrep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Account.class;
    }
}
