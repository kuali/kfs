/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.impl.PersistenceStructureServiceImpl;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.beans.factory.InitializingBean;

public class AccountPersistenceStructureServiceImpl extends PersistenceStructureServiceImpl implements AccountPersistenceStructureService, InitializingBean {
    
    protected List<AccountReferencePersistenceExemption> accountReferencePersistenceExemptions;
    protected Map<Class<?>, List<AccountReferencePersistenceExemption>> accountReferencePersistenceExemptionsMap;
    
    /* 
     * The following list is commented out as it's not used in code anymore, but still can server as a reference for testing. 
     * The list causes problems when referencing AwardAccount.class, a class in optional module;
     * also it's not a good practice to hard-code the list as it may have to be expanded when new sub/classes are added.
     * Instead of using "if (ACCOUNT_CLASSES.contains(clazz))" to judge whether whether a class is account-related,  
     * we now judge by whether the PKs of the class contain chartOfAccountsCode-accountNumber.
     * 
     *      
    // List of account-related BO classes (and all their subclasses) which have chartOfAccountsCode and accountNumber as (part of) the primary keys,
    // i.e. the complete list of all possible referenced BO classes with chart code and account number as (part of) the foreign keys. 
    protected static final HashSet<Class<? extends BusinessObject>> ACCOUNT_CLASSES = new HashSet<Class<? extends BusinessObject>>();    
    static {
        ACCOUNT_CLASSES.add(Account.class);
        ACCOUNT_CLASSES.add(SubAccount.class);
        ACCOUNT_CLASSES.add(A21SubAccount.class);
        ACCOUNT_CLASSES.add(AwardAccount.class); // this class can't be referenced by core code
        ACCOUNT_CLASSES.add(IndirectCostRecoveryExclusionAccount.class);
        ACCOUNT_CLASSES.add(PriorYearAccount.class);
        ACCOUNT_CLASSES.add(AccountDelegate.class);
        ACCOUNT_CLASSES.add(AccountDescription.class);
        ACCOUNT_CLASSES.add(AccountGlobalDetail.class);
        ACCOUNT_CLASSES.add(AccountGuideline.class);
        ACCOUNT_CLASSES.add(SubObjectCode.class);
        ACCOUNT_CLASSES.add(SubObjectCodeCurrent.class);
    }     
    */
    
    public boolean isAccountRelatedClass(Class clazz) {
        List<String> pks = listPrimaryKeyFieldNames(clazz);
        
        if (pks.contains(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) && pks.contains(KFSPropertyConstants.ACCOUNT_NUMBER )) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Class> listCollectionAccountFields(PersistableBusinessObject bo) {
        Map<String, Class> accountFields = new HashMap<String, Class>(); 
        Iterator<Map.Entry<String, Class>> collObjs = listCollectionObjectTypes(bo).entrySet().iterator();
        
        while (collObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)collObjs.next();
            String accountCollName = entry.getKey();
            Class accountCollType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses) 
            if (isAccountRelatedClass(accountCollType)) {
                // exclude non-maintainable account collection
                String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
                if (maintenanceDocumentDictionaryService.getMaintainableCollection(docTypeName, accountCollName) == null)
                    continue;
                
                // otherwise include the account field
                accountFields.put(accountCollName, accountCollType);                
            }
        }
        
        return accountFields;
    }
    
    @SuppressWarnings("rawtypes")
    public Set<String> listCollectionChartOfAccountsCodeNames(PersistableBusinessObject bo) {
        Set<String> coaCodeNames = new HashSet<String>();
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        Iterator<Map.Entry<String, Class>> collObjs = listCollectionObjectTypes(bo).entrySet().iterator();
        
        while (collObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)collObjs.next();
            String accountCollName = entry.getKey();
            Class accountCollType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses) 
            if (isAccountRelatedClass(accountCollType)) {
                // exclude non-maintainable account collection
                if (maintenanceDocumentDictionaryService.getMaintainableCollection(docTypeName, accountCollName) == null)
                    continue;

                // otherwise include the account field
                String coaCodeName = KRADConstants.ADD_PREFIX + "." + accountCollName + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                coaCodeNames.add(coaCodeName);
            }
        }

        return coaCodeNames;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String, Class> listReferenceAccountFields(PersistableBusinessObject bo) {
        Map<String, Class> accountFields = new HashMap<String, Class>();       
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (isAccountRelatedClass(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference;
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // in general we do want to have chartOfAccountsCode fields readOnly/auto-populated even when they are part of PKs,  
                // (such as in SubAccount), as the associated account shall only be chosen from existing accounts; 
                // however, when the BO is Account itself, we don't want to make the PK chartOfAccountsCode field readOnly, 
                // as it shall be editable when a new Account is being created; so we shall exclude such case 
                List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
                if (bo instanceof Account && pks.contains(coaCodeName) && pks.contains(acctNumName )) 
                    continue;                
                
                // exclude non-maintainable account field
                String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
                if (maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, coaCodeName) == null ||
                    maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, acctNumName) == null)
                    continue;
                
                // otherwise include the account field
                accountFields.put(accountName, accountType);                
            }
        }
        
        return accountFields;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String, String> listChartCodeAccountNumberPairs(PersistableBusinessObject bo) {
        Map<String, String> chartAccountPairs = new HashMap<String, String>();       
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (isAccountRelatedClass(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // in general we do want to have chartOfAccountsCode fields readOnly/auto-populated even when they are part of PKs,  
                // (such as in SubAccount), as the associated account shall only be chosen from existing accounts; 
                // however, when the BO is Account itself, we don't want to make the PK chartOfAccountsCode field readOnly, 
                // as it shall be editable when a new Account is being created; so we shall exclude such case 
                List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
                if (bo instanceof Account && pks.contains(coaCodeName) && pks.contains(acctNumName )) 
                    continue;
                
                // if this relationship is specifically exempted then exempt it
                if (isExemptedFromAccountsCannotCrossChartsRules(bo.getClass(), coaCodeName, acctNumName)) {
                    continue;
                }
                                
                // exclude non-maintainable account field
                String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
                if (maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, coaCodeName) == null ||
                    maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, acctNumName) == null)
                    continue;
                
                // otherwise include the account field PKs
                chartAccountPairs.put(coaCodeName, acctNumName);                
            }
        }
        
        return chartAccountPairs;
    }
    
    @SuppressWarnings("rawtypes")
    public Map<String, String> listAccountNumberChartCodePairs(PersistableBusinessObject bo) {
        Map<String, String> accountChartPairs = new HashMap<String, String>(); 
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (isAccountRelatedClass(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // in general we do want to have chartOfAccountsCode fields readOnly/auto-populated even when they are part of PKs,  
                // (such as in SubAccount), as the associated account shall only be chosen from existing accounts; 
                // however, when the BO is Account itself, we don't want to make the PK chartOfAccountsCode field readOnly, 
                // as it shall be editable when a new Account is being created; so we shall exclude such case 
                List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
                if (bo instanceof Account && pks.contains(coaCodeName) && pks.contains(acctNumName )) 
                    continue;                

                // if this relationship is specifically exempted then exempt it
                if (isExemptedFromAccountsCannotCrossChartsRules(bo.getClass(), coaCodeName, acctNumName)) {
                    continue;
                }

                // exclude non-maintainable account field
                String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
                if (maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, coaCodeName) == null ||
                    maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, acctNumName) == null)
                    continue;
                
                // otherwise include the account field PKs
                accountChartPairs.put(acctNumName, coaCodeName);
            }
        }
        
        return accountChartPairs;
    }

    public Set<String> listChartOfAccountsCodeNames(PersistableBusinessObject bo) {;
        return listChartCodeAccountNumberPairs(bo).keySet();        
    }

    public Set<String> listAccountNumberNames(PersistableBusinessObject bo) {
        return listAccountNumberChartCodePairs(bo).keySet();     
    }
    
    public String getChartOfAccountsCodeName(PersistableBusinessObject bo, String accountNumberName) {
        return listAccountNumberChartCodePairs(bo).get(accountNumberName);        
    }
    
    public String getAccountNumberName(PersistableBusinessObject bo, String chartOfAccountsCodeName) {
        return listChartCodeAccountNumberPairs(bo).get(chartOfAccountsCodeName);        
    }
    
    /** 
     * Need to stop this method from running for objects which are not bound into the ORM layer (OJB),
     * for ex. OrgReviewRole is not persistable. In this case, we can just return an empty list.
     * 
     * @see org.kuali.rice.krad.service.impl.PersistenceStructureServiceImpl#listReferenceObjectFields(org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Class> listReferenceObjectFields(PersistableBusinessObject bo) {
        if ( isPersistable(bo.getClass() ) ) {
            return super.listReferenceObjectFields(bo);
        }
        return Collections.emptyMap();
    }

    /**
     * Determines if the relationship to an Account or Account-like business object, with keys of chartOfAccountsCodePropertyName and accountNumberPropertyName,
     * is exempted from accounts cannot cross charts roles
     * @param relationshipOwningClass the business object which possibly has an exempted relationship to Account
     * @param chartOfAccountsCodePropertyName the property name of the relationshipOwningClass which represents the chart of accounts code part of the foreign key
     * @param accountNumberPropertyName the property name of the relationshipOwningClass which represents the account number part of the foreign key
     * @return true if the relationship is exempted, false otherwise
     */
    public boolean isExemptedFromAccountsCannotCrossChartsRules(Class<?> relationshipOwningClass, String chartOfAccountsCodePropertyName, String accountNumberPropertyName) {
        final List<AccountReferencePersistenceExemption> exemptionList = accountReferencePersistenceExemptionsMap.get(relationshipOwningClass);
        if (exemptionList != null) {
            for (AccountReferencePersistenceExemption exemption : exemptionList) {
                if (exemption.matches(chartOfAccountsCodePropertyName, accountNumberPropertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the list of classes and relationships which are exempted from the accounts can't cross charts rules
     * @param accountReferencePersistenceExemptions the list of classes and relationships which are exempted from the accounts can't cross charts rules
     */
    public void setAccountReferencePersistenceExemptions(List<AccountReferencePersistenceExemption> accountReferencePersistenceExemptions) {
        this.accountReferencePersistenceExemptions = accountReferencePersistenceExemptions;
    }

    /**
     * Implemented to build the AccountReferencePersistenceExemptionsMap from the AccoutnReferencePersistenceExemptions List after intialization
     * @throws Exception well, we're not going to throw an exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        accountReferencePersistenceExemptionsMap = new HashMap<Class<?>, List<AccountReferencePersistenceExemption>>();
        if (accountReferencePersistenceExemptions != null) {
            for (AccountReferencePersistenceExemption exemption : accountReferencePersistenceExemptions) {
                List<AccountReferencePersistenceExemption> exemptionList = accountReferencePersistenceExemptionsMap.get(exemption.getParentBusinessObjectClass());
                if (exemptionList == null) {
                    exemptionList = new ArrayList<AccountReferencePersistenceExemption>();
                }
                exemptionList.add(exemption);
                accountReferencePersistenceExemptionsMap.put(exemption.getParentBusinessObjectClass(), exemptionList);
            }
        }
    }
    
}
