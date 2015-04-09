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
package org.kuali.kfs.sys.document;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public abstract class FinancialSystemGlobalMaintainable extends KualiGlobalMaintainableImpl {
    private static final Logger LOG = Logger.getLogger(FinancialSystemGlobalMaintainable.class);

    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("FinancialSystemGlobalMaintainable does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName); 
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refreshReferences(String)
     */
    @Override
    protected void refreshReferences(String referencesToRefresh) {
        // if accounts can't cross charts, populate chart code fields according to corresponding account number fields
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            populateChartOfAccountsCodeFields();            
        }
        
        super.refreshReferences(referencesToRefresh);
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterAddLine(String)
     */
    @Override
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject bo) {
        super.processBeforeAddLine(colName, colClass, bo);

        // if accounts can't cross charts, populate chart code fields according to corresponding account number fields
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            populateChartOfAccountsCodeFields();            
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterPost(String)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);

        // if accounts can't cross charts, populate chart code fields according to corresponding account number fields
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            populateChartOfAccountsCodeFields();            
        }
    }
    
    /**
     * Populates all chartOfAccountsCode fields according to corresponding accountNumber fields in this BO.  
     * The chartOfAccountsCode-accountNumber pairs are (part of) the FKs for the reference accounts in this BO.    
     */
    protected void populateChartOfAccountsCodeFields() {
        AccountService acctService = SpringContext.getBean(AccountService.class);
        AccountPersistenceStructureService apsService = SpringContext.getBean(AccountPersistenceStructureService.class);
 
        // non-collection reference accounts 
        PersistableBusinessObject bo = getBusinessObject();        
        Iterator<Map.Entry<String, String>> chartAccountPairs = apsService.listChartCodeAccountNumberPairs(bo).entrySet().iterator();        
        while (chartAccountPairs.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>)chartAccountPairs.next();
            String coaCodeName = entry.getKey();            
            String acctNumName = entry.getValue(); 
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
                LOG.error("Error in setting property value for " + coaCodeName, e);
            }
        }
        
        // collection reference accounts         
        Iterator<Map.Entry<String, Class>> accountColls = apsService.listCollectionAccountFields(bo).entrySet().iterator();        
        while (accountColls.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)accountColls.next();
            String accountCollName = entry.getKey();
            PersistableBusinessObject newAccount = getNewCollectionLine(accountCollName);
            
            // here we can use hard-coded chartOfAccountsCode and accountNumber field name 
            // since all reference account types do follow the standard naming pattern        
            String accountNumber = (String)ObjectUtils.getPropertyValue(newAccount, KFSPropertyConstants.ACCOUNT_NUMBER);            
            String coaCode = null;
            Account account = acctService.getUniqueAccountForAccountNumber(accountNumber);            
            if (ObjectUtils.isNotNull(account)) {
                coaCode = account.getChartOfAccountsCode();
                try {
                    ObjectUtils.setObjectProperty(newAccount, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode); 
                }
                catch (Exception e) {
                    LOG.error("Error in setting chartOfAccountsCode property value in account collection " + accountCollName, e);
                }
            }
        }
    }
    
}
