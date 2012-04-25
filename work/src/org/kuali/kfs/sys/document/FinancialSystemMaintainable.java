/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class FinancialSystemMaintainable extends KualiMaintainableImpl {
    private static final Logger LOG = Logger.getLogger(FinancialSystemMaintainable.class);

    /**
     * Constructs a FinancialSystemMaintainable
     */
    public FinancialSystemMaintainable() {
        super();
    }

    /**
     * Constructs a FinancialSystemMaintainable, allowing the PersistableBusinessObject from KualiMaintainableImpl
     * to be inherited
     * @param businessObject a business object to set
     */
    public FinancialSystemMaintainable(PersistableBusinessObject businessObject) {
        super(businessObject);
    }

    /**
     *
     * @param nodeName
     * @return
     * @throws UnsupportedOperationException
     */
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("FinancialSystemMaintainable does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName);

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
    //public void processAfterAddLine(String colName, Class colClass) {
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject bo) {
        //super.processAfterAddLine(colName, colClass);
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
            Map.Entry<String, String> entry = chartAccountPairs.next();
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
                LOG.error("Error in setting property value for " + coaCodeName,e);
            }
        }

        // collection reference accounts
        Iterator<Map.Entry<String, Class>> accountColls = apsService.listCollectionAccountFields(bo).entrySet().iterator();
        while (accountColls.hasNext()) {
            Map.Entry<String, Class> entry = accountColls.next();
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
                    LOG.error("Error in setting chartOfAccountsCode property value in account collection " + accountCollName,e);
                }
            }
        }
    }

    @Override
    public void saveBusinessObject() {
        super.saveBusinessObject();
        // clear any caches for the selected business object (as long as they are using the normal conventions)
        SpringContext.getBean(CacheService.class).clearKfsBusinessObjectCache(getBoClass());
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(MaintenanceDocument,Maintainable)
     *
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        // if accounts can't cross charts, populate chart code fields according to corresponding account number fields
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            populateChartOfAccountsCodeFields();
        }

        return super.getSections(document, oldMaintainable);
    }
    */

}
