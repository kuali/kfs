/*
 * Copyright 2009 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

import org.kuali.kfs.fp.batch.service.impl.ProcurementCardCreateDocumentServiceImpl;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;

public class PooledFundValueMaintainableImpl extends KualiMaintainableImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    private PooledFundValue newPooledFundValue = null;
    private PooledFundValue oldPooledFundValue = null;
    private Security theSecurity = null;

    /**
     * Overrides the parent method to check the status of the Pooled Fund Value Maintenance document. Business Rule 1: Saving a new
     * or changed unit value will result in changes to the Security unit values and the associated holding market values. Once the
     * document has been approved, If it is in the edit-maintenance doc mode, compare unit value between old and new pooled fund
     * value BOs. If it is in the create-new or copy maintenance doc mode, compare unit value in the new pooled fund value BO with
     * unit value in the referred Security record. When saving a new or changed unit value 1) Copy current Security Unit Value to
     * Previous Unit Value 2) Copy current Security Price Date to Previous Price dAte 3) Copy PooledFundValue.POOL_FND_UNIT_VAL to
     * current Security Unit Price 4) Copy PooledFundValue.VAL_EFF_DT to current Security Price Date 5) Update Security.Security
     * Source to Pooled Fund Value Business Rule 2:When the DSTRB_AMT is updated, the amount of the distribution is multiplied by
     * the number of times per year that the institution will distribute income to the account holders (this is an institution
     * specified parameter: DISTRIBUTION_TIMES_PER_YEAR stored in the KFS parameter table) and the result is copied to END_SEC_T:
     * SEC_RT for the Pooled Fund SEC_ID.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader header) {
        super.doRouteStatusChange(header);
        WorkflowDocument workflowDoc = header.getWorkflowDocument();

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        try {
            MaintenanceDocument maintDoc = (MaintenanceDocument) documentService.getByDocumentHeaderId(header.getDocumentNumber());

            initializeAttributes(maintDoc);

            // Use the isFinal() method so this code is only executed when the final approval occurs
            if (workflowDoc.isFinal()) {
                BigDecimal oldUnitValue = null;
                BigDecimal oldDistributionPerUnit = null;
                BigDecimal newUnitValue = newPooledFundValue.getUnitValue();
                BigDecimal newDistributionPerUnit = newPooledFundValue.getIncomeDistributionPerUnit();
                BigDecimal oldIncomeDistributionPerUnit = oldPooledFundValue.getIncomeDistributionPerUnit();
                BigDecimal newIncomeDistributionPerUnit = newPooledFundValue.getIncomeDistributionPerUnit();
                Date newValueDate = newPooledFundValue.getValueEffectiveDate();
                String newUnitValueSource = EndowParameterKeyConstants.POOLED_FUND_VALUE;
                String pooledSecurityID = newPooledFundValue.getPooledSecurityID();

                // Why the following method didn't work?
                // newPooledFundValue.refreshNonUpdateableReferences();
                // Security theSecurity = newPooledFundValue.getPooledFundControl().getSecurity();
                SecurityService securityService = SpringContext.getBean(SecurityService.class);
                Security theSecurity = securityService.getByPrimaryKey(pooledSecurityID);

                ParameterService parameterService = SpringContext.getBean(ParameterService.class);
                BigDecimal numOfDistributions = new BigDecimal(new Double(parameterService.getParameterValueAsString(PooledFundValue.class, EndowParameterKeyConstants.DISTRIBUTION_TIMES_PER_YEAR)).doubleValue());
                BigDecimal interestRate = newDistributionPerUnit.multiply(numOfDistributions);

                /*
                 * Note: There is an assumption here that the unitValue in the oldPooledFundValue is always equal to the unitValue
                 * in the referred Security object. A business rule has to be added for Security impl -- when a Security has the
                 * class code type equals to Pooled Investment, the unit value can't be modify directly through Security maintenance
                 * doc.
                 */
                if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction())) {
                    oldUnitValue = oldPooledFundValue.getUnitValue();
                    oldDistributionPerUnit = oldPooledFundValue.getIncomeDistributionPerUnit();
                    if (newUnitValue.compareTo(oldUnitValue) != 0) {
                        compareAndUpdateUnitValue(oldUnitValue, newUnitValue, theSecurity, newValueDate, newUnitValueSource);
                    }
                    if (newDistributionPerUnit.compareTo(oldDistributionPerUnit) != 0) {
                        updateInterestRate(theSecurity, interestRate);
                    }                    
                    if (newIncomeDistributionPerUnit.compareTo(oldIncomeDistributionPerUnit) != 0) {
                        updateIncomeChangeDate(theSecurity);    
                    }
                }
                else if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction()) || KRADConstants.MAINTENANCE_NEW_ACTION.equals(getMaintenanceAction())) {
                    oldUnitValue = theSecurity.getUnitValue();
                    compareAndUpdateUnitValue(oldUnitValue, newUnitValue, theSecurity, newValueDate, newUnitValueSource);
                    updateInterestRate(theSecurity, interestRate);
                    updateIncomeChangeDate(theSecurity);                    
                }
            }
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to retrieve PooledFundValueMaintenanceDocument: " + header.getDocumentNumber() + " " + we);
            throw new RuntimeException(we.getMessage());
        }

    }

    private void compareAndUpdateUnitValue(BigDecimal oldUnitValue, BigDecimal newUnitValue, Security theSecurity, Date newValueDate, String newUnitValueSource) {
        SecurityService securityService = SpringContext.getBean(SecurityService.class);
        if (newUnitValue.compareTo(oldUnitValue) != 0) {
            Security security = securityService.updateUnitValue(theSecurity, newUnitValue, newValueDate, newUnitValueSource);
            SpringContext.getBean(BusinessObjectService.class).save(security);
        }
    }

    private void updateInterestRate(Security theSecurity, BigDecimal interestRate) {
        SecurityService securityService = SpringContext.getBean(SecurityService.class);
        Security security = securityService.updateInterestRate(theSecurity, interestRate);
        SpringContext.getBean(BusinessObjectService.class).save(security);
    }
    
    /**
     * Changes IncomeChangeDate in Security to the current date
     * 
     * @param theSecurity
     */
    private void updateIncomeChangeDate(Security theSecurity) {
        SecurityService securityService = SpringContext.getBean(SecurityService.class);
        Security security = securityService.updateIncomeChangeDate(theSecurity);
        SpringContext.getBean(BusinessObjectService.class).save(security);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        initializeAttributes(document);

        // clear out all fields except unit value and income distribution per unit
        newPooledFundValue.setDistributeIncomeOnDate(null);
        newPooledFundValue.setDistributeLongTermGainLossOnDate(null);
        newPooledFundValue.setDistributeShortTermGainLossOnDate(null);
        newPooledFundValue.setIncomeDistributionComplete(false);
        newPooledFundValue.setLongTermGainLossDistributionComplete(false);
        newPooledFundValue.setLongTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        newPooledFundValue.setShortTermGainLossDistributionComplete(false);
        newPooledFundValue.setShortTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        newPooledFundValue.setValuationDate(null);
        newPooledFundValue.setValueEffectiveDate(null);

    }


    /**
     * Initializes newSecurity and oldSecurity.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newPooledFundValue == null) {
            newPooledFundValue = (PooledFundValue) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldPooledFundValue == null) {
            oldPooledFundValue = (PooledFundValue) document.getOldMaintainableObject().getBusinessObject();
        }
    }
}
