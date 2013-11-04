/*
 * Copyright 2010 The Kuali Foundation.
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