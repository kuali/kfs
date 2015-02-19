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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class VendorCreditMemoAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        return true;
    }

}
