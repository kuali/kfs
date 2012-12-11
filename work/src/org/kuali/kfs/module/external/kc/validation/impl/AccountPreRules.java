/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.validation.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.kfs.module.external.kc.businessobject.CfdaDTO;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class AccountPreRules extends org.kuali.kfs.coa.document.validation.impl.AccountPreRules {

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.AccountPreRules#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        this.setCfdaNumber();
        return super.doCustomPreRules(document);
    }

    private void setCfdaNumber() {
        ContractsAndGrantsCfda cfda =  (CfdaDTO) newAccount.getCfda();    
        if (cfda != null) newAccount.setAccountCfdaNumber(cfda.getCfdaNumber());
    }

}
