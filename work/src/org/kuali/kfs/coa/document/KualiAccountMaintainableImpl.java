/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.sql.Timestamp;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;

/**
 * 
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account
 * 
 * 
 * @link KualiPostProcessor
 */
public class KualiAccountMaintainableImpl extends KualiMaintainableImpl {
    /**
     * 
     * @see org.kuali.core.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        // make sure we save account first
        super.saveBusinessObject();
        BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
        Account acct = (Account) businessObject;

        // deactivate any indicated BOs
        List<PersistableBusinessObject> bosToDeactivate = acct.generateDeactivationsToPersist();
        if (bosToDeactivate != null) {
            if (!bosToDeactivate.isEmpty()) {
                boService.save(bosToDeactivate);
            }
        }
    }

    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy() {
        Account account = (Account) this.getBusinessObject();
        account.setAccountEffectiveDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
        account.setAccountClosedIndicator(false);
        super.processAfterCopy();
    }


}
