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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportedExpenseClearingDetail;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportedExpenseClearingObject;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global accounts
 */
public class CreditCardImportedExpenseClearingObjectMaintainableImpl extends FinancialSystemGlobalMaintainable {
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
