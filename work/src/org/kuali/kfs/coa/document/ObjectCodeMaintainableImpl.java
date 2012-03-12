/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.document;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class ObjectCodeMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();
        ObjectCode maintainedObjectCode = (ObjectCode) getBusinessObject();
        if (isInactivatingObjectCode()) {
            maintenanceLocks.addAll(SpringContext.getBean(SubObjectTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((ObjectCode) getBusinessObject(), getDocumentNumber()));
        }
        return maintenanceLocks;
    }

    
    @Override
    public void saveBusinessObject() {
        boolean isInactivatingObjectCode = isInactivatingObjectCode();
        
        super.saveBusinessObject();
        
        if (isInactivatingObjectCode) {
            SpringContext.getBean(SubObjectTrickleDownInactivationService.class).trickleDownInactivateSubObjects((ObjectCode) getBusinessObject(), getDocumentNumber());
        }
    }
    
    protected boolean isInactivatingObjectCode() {
        // the object code has to be inactive on the new side during an edit for it to be possible that we are inactivating an object code
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((ObjectCode) getBusinessObject()).isActive()) {
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

    /**
     * Refreshes the Reports to Chart of Accounts code if needed
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        refreshReportsToChartOfAccountsCodeIfNecessary(document);
    }
    
    /**
     * Insures that the reports to chart of accounts code on the document is populated by the chosen chart of account's reports to chart code
     * @param document the MaintenanceDocument to get the ObjectCode to update from
     */
    protected void refreshReportsToChartOfAccountsCodeIfNecessary(MaintenanceDocument document) {
        final ObjectCode newObjectCode = (ObjectCode)document.getNewMaintainableObject().getBusinessObject();
        if (!StringUtils.isBlank(newObjectCode.getChartOfAccountsCode())) {
            newObjectCode.refreshReferenceObject("chartOfAccounts");
            final Chart newChart = newObjectCode.getChartOfAccounts();
            
            if (!ObjectUtils.isNull(newChart) && (StringUtils.isBlank(newObjectCode.getReportsToChartOfAccountsCode()) || !newObjectCode.getReportsToChartOfAccountsCode().equalsIgnoreCase(newChart.getReportsToChartOfAccountsCode()))) {
                newObjectCode.setReportsToChartOfAccountsCode(newChart.getReportsToChartOfAccountsCode());
            }
        }
    }
}
