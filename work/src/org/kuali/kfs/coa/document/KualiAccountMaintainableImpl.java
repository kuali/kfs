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

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends KualiMaintainableImpl {
    /**
     * Automatically deactivates {@link SubAccount}s after saving the {@link Account}
     * 
     * @see org.kuali.core.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        // make sure we save account first
        super.saveBusinessObject();
        Account acct = (Account) businessObject;

        // deactivate any indicated BOs
        List<PersistableBusinessObject> bosToDeactivate = acct.generateDeactivationsToPersist();
        if (bosToDeactivate != null) {
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            if (!bosToDeactivate.isEmpty()) {
                boService.save(bosToDeactivate);
            }
        }
    }

    /**
     * After a copy is done set specific fields on {@link Account} to default values
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        Account account = (Account) this.getBusinessObject();
        account.setAccountCreateDate(null); // account's pre-rules will fill this field in
        account.setAccountEffectiveDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        account.setAccountClosedIndicator(false);
        super.processAfterCopy( document, parameters );
    }


}
