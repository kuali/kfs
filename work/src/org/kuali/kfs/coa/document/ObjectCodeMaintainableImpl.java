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
package org.kuali.kfs.coa.document;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

public class ObjectCodeMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();
        ObjectCode maintainedObjectCode = (ObjectCode) getBusinessObject();
        if (isInactivatingObjectCode()) {
            maintenanceLocks.addAll(SpringContext.getBean(SubObjectTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((ObjectCode) getBusinessObject(), documentNumber));
        }
        return maintenanceLocks;
    }

    
    @Override
    public void saveBusinessObject() {
        boolean isInactivatingObjectCode = isInactivatingObjectCode();
        
        super.saveBusinessObject();
        
        if (isInactivatingObjectCode) {
            SpringContext.getBean(SubObjectTrickleDownInactivationService.class).trickleDownInactivateSubObjects((ObjectCode) getBusinessObject(), documentNumber);
        }
    }
    
    protected boolean isInactivatingObjectCode() {
        // the object code has to be inactive on the new side during an edit for it to be possible that we are inactivating an object code
        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((ObjectCode) getBusinessObject()).isActive()) {
            // then check if the object code was originally active.  If so, then we are inactivating.
            ObjectCode objectCodeFromDB = retrieveObjectCodeFromDB();
            if (ObjectUtils.isNotNull(objectCodeFromDB)) {
                if (objectCodeFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected ObjectCode retrieveObjectCodeFromDB() {
        ObjectCode maintainedObjectCode = (ObjectCode) getBusinessObject();
        ObjectCode oldObjectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(maintainedObjectCode.getUniversityFiscalYear(), maintainedObjectCode.getChartOfAccountsCode(), maintainedObjectCode.getFinancialObjectCode());
        return oldObjectCode;
    }
}
