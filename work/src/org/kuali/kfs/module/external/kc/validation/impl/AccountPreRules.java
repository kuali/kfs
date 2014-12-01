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
