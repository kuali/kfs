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
package org.kuali.kfs.module.external.kc.document;

import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends org.kuali.kfs.coa.document.KualiAccountMaintainableImpl {

    /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#lookupAccountCfda(java.lang.String, java.lang.String)
     */

    protected String lookupAccountCfda(String accountNumber, String currentCfda) {
        Account account = (Account) this.getBusinessObject();
        ContractsAndGrantsCfda cfda = account.getCfda();
        if (cfda != null) {
        	return cfda.getCfdaNumber();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        Account account = (Account) this.getBusinessObject();
        account.setAccountCfdaNumber(lookupAccountCfda( account.getAccountNumber(), account.getAccountCfdaNumber()));
        super.processAfterCopy(document, parameters);
    }

    /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#retrieveExistingAccountFromDB()
     */
    @Override
    protected Account retrieveExistingAccountFromDB() {
        Account newAccount = (Account) getBusinessObject();
        newAccount.setAccountCfdaNumber(lookupAccountCfda( newAccount.getAccountNumber(), newAccount.getAccountCfdaNumber()));
        return super.retrieveExistingAccountFromDB();
    }
}
