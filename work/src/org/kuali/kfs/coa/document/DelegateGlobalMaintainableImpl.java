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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.module.chart.bo.DelegateGlobal;
import org.kuali.module.chart.bo.DelegateGlobalDetail;
import org.kuali.module.chart.bo.OrganizationRoutingModel;
import org.kuali.module.chart.bo.OrganizationRoutingModelName;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global delegates
 * and to help with using delegate models
 * 
 * @see OrganizationRoutingModelName
 */
public class DelegateGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    /**
     * This method is used for the creation of a delegate from a {@link OrganizationRoutingModelName}
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#setupNewFromExisting()
     */
    @Override
    public void setupNewFromExisting( Map parameters ) {
        super.setupNewFromExisting( parameters );

        DelegateGlobal globalDelegate = (DelegateGlobal) this.getBusinessObject();
        // 1. if model name, chart of accounts, and org code are all present
        // then let's see if we've actually got a model record
        if (!StringUtils.isBlank(globalDelegate.getModelName()) && !StringUtils.isBlank(globalDelegate.getModelChartOfAccountsCode()) && !StringUtils.isBlank(globalDelegate.getModelOrganizationCode())) {
            Map pkMap = new HashMap();
            pkMap.put("organizationRoutingModelName", globalDelegate.getModelName());
            pkMap.put("chartOfAccountsCode", globalDelegate.getModelChartOfAccountsCode());
            pkMap.put("organizationCode", globalDelegate.getModelOrganizationCode());

            OrganizationRoutingModelName globalDelegateTemplate = (OrganizationRoutingModelName) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationRoutingModelName.class, pkMap);
            if (globalDelegateTemplate != null) {
                // 2. if there is a model record, then let's populate the global delegate
                // based on that
                for (OrganizationRoutingModel model : globalDelegateTemplate.getOrganizationRoutingModel()) {
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
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // create locking rep for each combination of account and object code
        List<MaintenanceLock> maintenanceLocks = new ArrayList();
        DelegateGlobal delegateGlobal = (DelegateGlobal) getBusinessObject();

        // hold all the locking representations in a set to make sure we don't get any duplicates
        Set<String> lockingRepresentations = new HashSet<String>();

        MaintenanceLock maintenanceLock;
        for (AccountGlobalDetail accountGlobalDetail : delegateGlobal.getAccountGlobalDetails()) {
            for (DelegateGlobalDetail delegateGlobalDetail : delegateGlobal.getDelegateGlobals()) {
                StringBuilder lockRep = new StringBuilder();
                lockRep.append(Delegate.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append("chartOfAccountsCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountNumber");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getAccountNumber());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("financialDocumentTypeCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getFinancialDocumentTypeCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountDelegateSystemId");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getAccountDelegateUniversalId());
                // FIXME above is a bit dangerous b/c it hard codes the attribute names, which could change (particularly
                // accountDelegateSystemId) - guess they should either be constants or obtained by looping through Delegate keys;
                // however, I copied this from elsewhere which had them hard-coded, so I'm leaving it for now

                if (!lockingRepresentations.contains(lockRep.toString())) {
                    maintenanceLock = new MaintenanceLock();
                    maintenanceLock.setDocumentNumber(delegateGlobal.getDocumentNumber());
                    maintenanceLock.setLockingRepresentation(lockRep.toString());
                    maintenanceLocks.add(maintenanceLock);
                    lockingRepresentations.add(lockRep.toString());
                }

                lockRep = new StringBuilder();
                lockRep.append(Delegate.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append("chartOfAccountsCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountNumber");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getAccountNumber());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("financialDocumentTypeCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getFinancialDocumentTypeCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountsDelegatePrmrtIndicator");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append("true");

                if (!lockingRepresentations.contains(lockRep.toString())) {
                    maintenanceLock = new MaintenanceLock();
                    maintenanceLock.setDocumentNumber(delegateGlobal.getDocumentNumber());
                    maintenanceLock.setLockingRepresentation(lockRep.toString());
                    maintenanceLocks.add(maintenanceLock);
                    lockingRepresentations.add(lockRep.toString());
                }
            }
        }
        return maintenanceLocks;
    }
}
