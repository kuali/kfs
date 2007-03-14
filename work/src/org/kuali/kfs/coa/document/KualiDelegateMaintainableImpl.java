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

import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.Constants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Delegate;

/**
 * 
 * This class is a special implementation of Maintainable specifically for Account Delegates.  It was created to correctly update the
 * default Start Date on edits and copies, ala JIRA #KULRNE-62.
 */
public class KualiDelegateMaintainableImpl extends KualiMaintainableImpl {
    /**
     * This method will reset AccountDelegate's Start Date to the current timestamp on edits and copies
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    public void processAfterCopy() {
        this.setStartDateDefault();
        super.processAfterCopy();
    }
    
    public void processAfterEdit() {
        this.setStartDateDefault();
        super.processAfterEdit();
    }
    
    private void setStartDateDefault() {
        if (this.businessObject != null && this.businessObject instanceof Delegate) {
            Delegate delegate = (Delegate)this.businessObject;
            delegate.setAccountDelegateStartDate(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
        }
    }
}
