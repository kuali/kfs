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
package org.kuali.kfs.coa.document;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.A21SubAccountService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class SubAccountMaintainableImpl extends FinancialSystemMaintainable {
    private static final Logger LOG = Logger.getLogger(SubAccountMaintainableImpl.class);

    // account fields that are PKs of nested reference accounts but don't exist in the Sub-Account BO as FKs.
    public static final String[] COA_CODE_NAMES = {        
        KFSPropertyConstants.A21_SUB_ACCOUNT + "." + KFSPropertyConstants.COST_SHARE_SOURCE_CHART_OF_ACCOUNTS_CODE, 
    };
    public static final String[] ACCOUNT_NUMBER_NAMES = {        
        KFSPropertyConstants.A21_SUB_ACCOUNT + "." + KFSPropertyConstants.COST_SHARE_SOURCE_ACCOUNT_NUMBER, 
    };

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        Person person = GlobalVariables.getUserSession().getPerson();
        MaintenanceDocumentRestrictions restrictions = getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, person);
        boolean canEdit = !restrictions.isHiddenSectionId(KFSConstants.SUB_ACCOUNT_EDIT_CG_ICR_SECTION_ID) && !restrictions.isReadOnlySectionId(KFSConstants.SUB_ACCOUNT_EDIT_CG_ICR_SECTION_ID);
        
        // after account lookup, refresh the CG ICR account fields
        if (refreshCaller != null && (refreshCaller.toUpperCase().endsWith(KFSConstants.LOOKUPABLE_SUFFIX.toUpperCase())) && fieldValues.containsKey("document.newMaintainableObject.accountNumber") && canEdit) {
            SubAccount subAccount = (SubAccount) this.getBusinessObject();
            this.populateCGIcrFields(subAccount);
        }
    }

    // populate the CG ICR fields if any
    private void populateCGIcrFields(SubAccount subAccount) {
        A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
        String chartOfAccountsCode = subAccount.getChartOfAccountsCode();
        String accountNumber = subAccount.getAccountNumber();
        
        //populate ICR fields if subAccount COA or Acct does not match a21SubAccount COA/Acct
        if (ObjectUtils.isNotNull(a21SubAccount) && (!StringUtils.equals(chartOfAccountsCode, a21SubAccount.getChartOfAccountsCode()) || !StringUtils.equals(accountNumber, a21SubAccount.getAccountNumber()))) {                  
            A21SubAccountService a21SubAccountService = SpringContext.getBean(A21SubAccountService.class);
            a21SubAccountService.populateCgIcrAccount(a21SubAccount, chartOfAccountsCode, accountNumber);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#populateChartOfAccountsCodeFields()
     * 
     * Special treatment is needed to populate chart code fields from account number fields 
     * in nested reference accounts a21SubAccount.costShareAccount 
     * when accounts can't cross charts.
     */
    @Override
    protected void populateChartOfAccountsCodeFields() {
        super.populateChartOfAccountsCodeFields();
        PersistableBusinessObject bo = getBusinessObject();        
        AccountService acctService = SpringContext.getBean(AccountService.class);

        for (int i=0; i<COA_CODE_NAMES.length; i++) {
            String coaCodeName = COA_CODE_NAMES[i];            
            String acctNumName = ACCOUNT_NUMBER_NAMES[i];        
            String accountNumber = (String)ObjectUtils.getPropertyValue(bo, acctNumName);
            String coaCode = null;
            Account account = acctService.getUniqueAccountForAccountNumber(accountNumber);            
            if (ObjectUtils.isNotNull(account)) {
                coaCode = account.getChartOfAccountsCode();
            }
            try {
                ObjectUtils.setObjectProperty(bo, coaCodeName, coaCode); 
            }
            catch (Exception e) {
                LOG.error("Error in setting property value for " + coaCodeName);
            } 
        }
    }
    
}
