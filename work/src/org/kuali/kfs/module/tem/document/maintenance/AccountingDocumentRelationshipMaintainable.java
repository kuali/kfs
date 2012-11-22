/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.GlobalVariables;

public class AccountingDocumentRelationshipMaintainable extends KualiMaintainableImpl {

    /**
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareForSave()
     * Add principalId to AccountingDocumentRelationship.
     */
    @Override
    public void prepareForSave() {
        AccountingDocumentRelationship adr = (AccountingDocumentRelationship) super.getBusinessObject();
        if(adr.getPrincipalId() == null){
            Person user = GlobalVariables.getUserSession().getPerson();
            adr.setPrincipalId(user.getPrincipalId());
        }

        super.prepareForSave();
    }

    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // prevent locking
        return new ArrayList<MaintenanceLock>();
    }

}
