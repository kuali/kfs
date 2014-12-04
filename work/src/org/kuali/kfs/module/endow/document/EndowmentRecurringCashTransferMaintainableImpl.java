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
package org.kuali.kfs.module.endow.document;

import java.util.Map;

import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class EndowmentRecurringCashTransferMaintainableImpl extends FinancialSystemMaintainable {
    
    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument arg0, Map<String, String[]> arg1) {
        
        //EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) this.getBusinessObject();
        EndowmentRecurringCashTransfer newEndowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) arg0.getNewMaintainableObject().getBusinessObject();
        EndowmentRecurringCashTransfer oldEndowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) arg0.getOldMaintainableObject().getBusinessObject();
        claenAllTargets(newEndowmentRecurringCashTransfer);
        claenAllTargets(oldEndowmentRecurringCashTransfer);
        
        super.processAfterCopy(arg0, arg1);
    }
    
    protected void claenAllTargets(EndowmentRecurringCashTransfer endowmentRecurringCashTransfer){
        //endowmentRecurringCashTransfer.refreshNonUpdateableReferences();
        endowmentRecurringCashTransfer.getKemidTarget().clear();
        endowmentRecurringCashTransfer.getGlTarget().clear();
        
    }

}
