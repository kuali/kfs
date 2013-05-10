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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.sql.Date;

import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.document.service.PooledFundControlService;
import org.kuali.kfs.module.endow.document.service.PooledFundValueService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

public class PooledFundValuePreRule extends MaintenancePreRulesBase {

    private PooledFundValue newPooledFundValue;
    private PooledFundControl pooledFundControl;

    /**
     * Set value for newCashSweepModel.
     * 
     * @param document the CashSweepModel Maintenance document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newCashSweepModel convenience objects, make sure all possible sub-objects are populated
        newPooledFundValue = (PooledFundValue) document.getNewMaintainableObject().getBusinessObject();
        newPooledFundValue.refreshNonUpdateableReferences();
        pooledFundControl = newPooledFundValue.getPooledFundControl();
        if (!ObjectUtils.isNotNull(pooledFundControl)) {
            PooledFundControlService pooledFundControlService = SpringContext.getBean(PooledFundControlService.class);
            pooledFundControl = pooledFundControlService.getByPrimaryKey(newPooledFundValue.getPooledSecurityID());
        }
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {

        setupConvenienceObjects(maintenanceDocument);
        if (ObjectUtils.isNotNull(pooledFundControl)) {
            // Set the date of last sweep model change -- the date is the time when the maintenance doc is submitted, not
            // the time when that maintenance doc gets approved.
            PooledFundValueService pooledFundValueService = SpringContext.getBean(PooledFundValueService.class);
            Date valuationDate = newPooledFundValue.getValuationDate();
            String pooledSecurityID = newPooledFundValue.getPooledSecurityID();

            if (valuationDate != null && pooledSecurityID != null) {

                Date valueEffectiveDate = pooledFundValueService.calculateValueEffectiveDate(valuationDate, pooledSecurityID);
                newPooledFundValue.setValueEffectiveDate(valueEffectiveDate);
                return true;
            }
        }

        return true;
    }

}
