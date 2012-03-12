/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.document;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubAccountTrickleDownInactivationService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends FinancialSystemMaintainable {
    private static final Logger LOG = Logger.getLogger(KualiAccountMaintainableImpl.class);
    private static final String ACCOUNT_GUIDE_LINE_PROPERTY = "accountGuideline";
    
    /**
     * Automatically deactivates {@link SubAccount}s after saving the {@link Account}
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        boolean isClosingAccount = isClosingAccount();

        // make sure we save account first
        super.saveBusinessObject();

        // if we're closing the account, then rely on the trickle-down inactivation services to trickle-down inactivate the
        // sub-accounts
        if (isClosingAccount) {
            SpringContext.getBean(SubAccountTrickleDownInactivationService.class).trickleDownInactivateSubAccounts((Account) getBusinessObject(), getDocumentNumber());
            SpringContext.getBean(SubObjectTrickleDownInactivationService.class).trickleDownInactivateSubObjects((Account) getBusinessObject(), getDocumentNumber());
        }
    }

    /**
     * After a copy is done set specific fields on {@link Account} to default values
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        Account account = (Account) this.getBusinessObject();
        account.setAccountCreateDate(null); // account's pre-rules will fill this field in
        account.setAccountEffectiveDate(new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
        account.setActive(true);
        super.processAfterCopy(document, parameters);
    }
    
 
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();
        boolean isClosingAccount = false;

        if (isClosingAccount()) {
            maintenanceLocks.addAll(SpringContext.getBean(SubAccountTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((Account) getBusinessObject(), getDocumentNumber()));
            maintenanceLocks.addAll(SpringContext.getBean(SubObjectTrickleDownInactivationService.class).generateTrickleDownMaintenanceLocks((Account) getBusinessObject(), getDocumentNumber()));
        }
        return maintenanceLocks;
    }

    protected Account retrieveExistingAccountFromDB() {
        Account newAccount = (Account) getBusinessObject();
         Account oldAccount = SpringContext.getBean(AccountService.class).getByPrimaryId(newAccount.getChartOfAccountsCode(), newAccount.getAccountNumber());
        return oldAccount;
    }

    protected boolean isClosingAccount() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((Account) getBusinessObject()).isActive()) {
            Account existingAccountFromDB = retrieveExistingAccountFromDB();
            if (ObjectUtils.isNotNull(existingAccountFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (existingAccountFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines who should be FYI'd as the account supervisor for the routing of the account maintenance document. If there is an
     * existing account, it uses the account supervisor from that; otherwise, it uses the account supervisor from the business
     * object of this maintainable
     * 
     * @return an appropriate account supervisor to FYI during account maintenance document routing
     */
    public String getRoutingAccountsSupervisorySystemsIdentifier() {
        final Account existingAccountFromDB = retrieveExistingAccountFromDB();
        if (ObjectUtils.isNull(existingAccountFromDB)) {
            return ((Account) getBusinessObject()).getAccountsSupervisorySystemsIdentifier();
        }
        return existingAccountFromDB.getAccountsSupervisorySystemsIdentifier();
    }

    /**
     * Had to override this method because account guideline data was lost when copied and then a lookup is performed
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        Account newAcct = (Account) document.getNewMaintainableObject().getBusinessObject();
        Account oldAcct = (Account) document.getOldMaintainableObject().getBusinessObject();
        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            if (ObjectUtils.isNull(newAcct.getAccountGuideline())) {
                newAcct.setAccountGuideline(oldAcct.getAccountGuideline());
            }
        }

    }

    @Override
    protected void refreshReferences(String referencesToRefresh) {        
        //make call to super
        super.refreshReferences( removeReferenceFromString(referencesToRefresh, ACCOUNT_GUIDE_LINE_PROPERTY) );
    }
    
    /**
     * Removes a named reference from a referencesToRefresh string
     */
    protected String removeReferenceFromString(String referencesToRefresh, String referenceToRemove){
        String newReference = referencesToRefresh;
        
        if(ObjectUtils.isNotNull(newReference)){
            int index = newReference.indexOf(referenceToRemove);        
            if(index != -1){  
                //remove from beginning
                if(index == 0){
                    
                    String suffix = "";
                    //add comma at end since there is more after this word
                    if(newReference.length() != referenceToRemove.length()){
                        suffix = ",";
                    }                    
                    newReference = referencesToRefresh.replaceAll(ACCOUNT_GUIDE_LINE_PROPERTY + suffix, "");
                    
                }else{
                    //removing from middle to end... either way, comma will be in front
                    newReference = referencesToRefresh.replaceAll("," + ACCOUNT_GUIDE_LINE_PROPERTY, "");
                }
            }
        }
        
        return newReference;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#populateChartOfAccountsCodeFields()
     * 
     * Special treatment is needed when a new Account is created, the chartCode-accountNumber fields in the document can use the new account 
     * that's being created; in which case chart code shall be populated from the PK chart code in the document instead of retrieving it from DB
     * using the account number, as the new account doesn't exist in the DB yet.
     */
    @Override
    protected void populateChartOfAccountsCodeFields() {
        // super method is not called because the logic there wouldn't apply here        
        AccountService acctService = SpringContext.getBean(AccountService.class);        
        AccountPersistenceStructureService apsService = SpringContext.getBean(AccountPersistenceStructureService.class);
        PersistableBusinessObject bo = getBusinessObject();        
        Iterator<Map.Entry<String, String>> chartAccountPairs = apsService.listChartCodeAccountNumberPairs(bo).entrySet().iterator();        
 
        // all reference accounts could possibly use the same new accounting being created in the current document 
        while (chartAccountPairs.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>)chartAccountPairs.next();
            String coaCodeName = entry.getKey();            
            String acctNumName = entry.getValue(); 
            String accountNumber = (String)ObjectUtils.getPropertyValue(bo, acctNumName);
            String coaCode = null;
            String coaCodePK = (String)ObjectUtils.getPropertyValue(bo, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            String accountNumberPK = (String)ObjectUtils.getPropertyValue(bo, KFSPropertyConstants.ACCOUNT_NUMBER);
            
            // if reference account number is same as the primary key accountNumber, copy the primary key chart code to reference chart Code
            if (StringUtils.equalsIgnoreCase(accountNumber, accountNumberPK)) {
                coaCode = coaCodePK;
            }
            // otherwise retrieve chart code from account as usual
            else {
                Account account = acctService.getUniqueAccountForAccountNumber(accountNumber);            
                if (ObjectUtils.isNotNull(account)) {
                    coaCode = account.getChartOfAccountsCode();
                }
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
