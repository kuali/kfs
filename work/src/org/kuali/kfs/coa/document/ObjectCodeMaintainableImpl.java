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
