/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.document;

import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.A21SubAccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class...
 */
public class SubAccountMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        Person person = GlobalVariables.getUserSession().getPerson();
        MaintenanceDocumentRestrictions restrictions = getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, person);
        if (!restrictions.isHiddenSectionId(KFSConstants.SUB_ACCOUNT_EDIT_CG_ICR_SECTION_ID) && !restrictions.isReadOnlySectionId(KFSConstants.SUB_ACCOUNT_EDIT_CG_ICR_SECTION_ID)) {
            SubAccount subAccount = (SubAccount) this.getBusinessObject();
            this.populateCGIcrFields(subAccount);
        }
    }

    // populate the CG ICR fields if any
    private void populateCGIcrFields(SubAccount subAccount) {
        A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
        String chartOfAccountsCode = subAccount.getChartOfAccountsCode();
        String accountNumber = subAccount.getAccountNumber();
               
        A21SubAccountService a21SubAccountService = SpringContext.getBean(A21SubAccountService.class);
        a21SubAccountService.populateCgIcrAccount(a21SubAccount, chartOfAccountsCode, accountNumber);
    }
}
