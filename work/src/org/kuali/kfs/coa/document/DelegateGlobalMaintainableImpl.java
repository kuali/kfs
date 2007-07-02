/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.bo.DelegateGlobal;
import org.kuali.module.chart.bo.DelegateGlobalDetail;
import org.kuali.module.chart.bo.OrganizationRoutingModel;
import org.kuali.module.chart.bo.OrganizationRoutingModelName;

public class DelegateGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    /**
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#setupNewFromExisting()
     */
    @Override
    public void setupNewFromExisting() {
        super.setupNewFromExisting();
        
        DelegateGlobal globalDelegate = (DelegateGlobal)this.getBusinessObject();
        // 1. if model name, chart of accounts, and org code are all present
        //    then let's see if we've actually got a model record
        if (!StringUtils.isBlank(globalDelegate.getModelName()) && !StringUtils.isBlank(globalDelegate.getModelChartOfAccountsCode()) && !StringUtils.isBlank(globalDelegate.getModelOrganizationCode())) {
            Map pkMap = new HashMap();
            pkMap.put("organizationRoutingModelName", globalDelegate.getModelName());
            pkMap.put("chartOfAccountsCode", globalDelegate.getModelChartOfAccountsCode());
            pkMap.put("organizationCode", globalDelegate.getModelOrganizationCode());
            
            OrganizationRoutingModelName globalDelegateTemplate = (OrganizationRoutingModelName)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(OrganizationRoutingModelName.class, pkMap);
            if (globalDelegateTemplate != null) {
                // 2. if there is a model record, then let's populate the global delegate
                //    based on that
                for (OrganizationRoutingModel model: globalDelegateTemplate.getOrganizationRoutingModel()) {
                    if (model.isActive()) { // only populate with active models
                        DelegateGlobalDetail newDelegate = new DelegateGlobalDetail(model);
                        // allow deletion of the new delegate from the global delegate
                        newDelegate.setNewCollectionRecord(true);
                        globalDelegate.getDelegateGlobals().add(newDelegate);
                    }
                }
            }
        }
    }

    /**
     * This creates the particular locking representation for this global document.
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // create locking rep for each combination of account and object code
        List <MaintenanceLock> maintenanceLocks = new ArrayList();
        DelegateGlobal delegateGlobal = (DelegateGlobal)getBusinessObject();
        
        for ( AccountGlobalDetail accountGlobalDetail: delegateGlobal.getAccountGlobalDetails()) {
            for ( DelegateGlobalDetail delegateGlobalDetail : delegateGlobal.getDelegateGlobals() ) {
                MaintenanceLock maintenanceLock = new MaintenanceLock();
                maintenanceLock.setDocumentNumber(delegateGlobal.getDocumentNumber());

                StringBuffer lockrep = new StringBuffer();
                lockrep.append(Delegate.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockrep.append("chartOfAccountsCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(accountGlobalDetail.getChartOfAccountsCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("accountNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(accountGlobalDetail.getAccountNumber() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("financialDocumentTypeCode" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(delegateGlobalDetail.getFinancialDocumentTypeCode() + KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockrep.append("accountDelegateSystemId" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockrep.append(delegateGlobalDetail.getAccountDelegateUniversalId());
//FIXME above is a bit dangerous b/c it hard codes the attribute names, which could change (particularly accountDelegateSystemId) - guess they should either be constants or obtained by looping through Delegate keys; however, I copied this from elsewhere which had them hard-coded, so I'm leaving it for now

                maintenanceLock.setLockingRepresentation(lockrep.toString());
                maintenanceLocks.add(maintenanceLock);
            }
        }
        return maintenanceLocks;
    }
}
