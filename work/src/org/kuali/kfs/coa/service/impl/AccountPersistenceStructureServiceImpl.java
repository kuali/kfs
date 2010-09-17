/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.AccountDescription;
import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.coa.businessobject.AccountGuideline;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCodeCurrent;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.impl.PersistenceStructureServiceImpl;
import org.kuali.rice.kns.util.KNSConstants;

public class AccountPersistenceStructureServiceImpl extends PersistenceStructureServiceImpl implements AccountPersistenceStructureService {
    
    // List of account-related BO classes which have chartOfAccountsCode and accountNumber as (part of) the primary keys.
    // This list shall include all subclasses of such classes as well.
    public static final Class[] ACCOUNT_CLASSES = { Account.class, SubAccount.class, A21SubAccount.class, PriorYearAccount.class,
        AccountDelegate.class, AccountDescription.class, AccountGlobalDetail.class, AccountGuideline.class, SubObjectCode.class, SubObjectCodeCurrent.class};
    public static HashSet<Class> accountClasses;    
    static {
        accountClasses = new HashSet<Class>();
        for (int i=0; i<ACCOUNT_CLASSES.length; i++) {
            accountClasses.add(ACCOUNT_CLASSES[i]);
        }
    }    
    
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    
    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public Map<String, Class> listCollectionAccountFields(PersistableBusinessObject bo) {
        Map<String, Class> accountFields = new HashMap<String, Class>(); 
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        Iterator<Map.Entry<String, Class>> collObjs = listCollectionObjectTypes(bo).entrySet().iterator();
        
        while (collObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)collObjs.next();
            String accountCollName = entry.getKey();
            Class accountCollType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses) 
            if (accountClasses.contains(accountCollType)) {
                // exclude non-maintainable account collection
                if (maintenanceDocumentDictionaryService.getMaintainableCollection(docTypeName, accountCollName) == null)
                    continue;
                
                // otherwise include the account field
                accountFields.put(accountCollName, accountCollType);                
            }
        }
        
        return accountFields;
    }
    
    public Set<String> listCollectionChartOfAccountsCodeNames(PersistableBusinessObject bo) {
        Set<String> coaCodeNames = new HashSet<String>();
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        Iterator<Map.Entry<String, Class>> collObjs = listCollectionObjectTypes(bo).entrySet().iterator();
        
        while (collObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)collObjs.next();
            String accountCollName = entry.getKey();
            Class accountCollType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses) 
            if (accountClasses.contains(accountCollType)) {
                // exclude non-maintainable account collection
                if (maintenanceDocumentDictionaryService.getMaintainableCollection(docTypeName, accountCollName) == null)
                    continue;

                // otherwise include the account field
                String coaCodeName = KNSConstants.ADD_PREFIX + "." + accountCollName + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                coaCodeNames.add(coaCodeName);
            }
        }

        return coaCodeNames;
    }
    
    public Map<String, Class> listReferenceAccountFields(PersistableBusinessObject bo) {
        Map<String, Class> accountFields = new HashMap<String, Class>();       
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (accountClasses.contains(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // exclude the case when chartOfAccountsCode-accountNumber are also the PKs of this BO, 
                // for ex, in SubAccount; as these fields shall be dealt by the framework separately  
                if (pks.contains(coaCodeName) || pks.contains(acctNumName)) 
                    continue;
                
                // exclude non-maintainable account field
                if (maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, coaCodeName) == null ||
                    maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, acctNumName) == null)
                    continue;
                
                // otherwise include the account field
                accountFields.put(accountName, accountType);                
            }
        }
        
        return accountFields;
    }
    
    public Map<String, String> listChartCodeAccountNumberPairs(PersistableBusinessObject bo) {
        Map<String, String> chartAccountPairs = new HashMap<String, String>();       
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (accountClasses.contains(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // exclude the case when chartOfAccountsCode-accountNumber are also the PKs of this BO, 
                // for ex, in SubAccount; as these fields shall be dealt by the framework separately  
                if (pks.contains(coaCodeName) || pks.contains(acctNumName)) 
                    continue;
                
                // exclude non-maintainable account field
                if (maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, coaCodeName) == null ||
                    maintenanceDocumentDictionaryService.getMaintainableField(docTypeName, acctNumName) == null)
                    continue;
                
                // otherwise include the account field PKs
                chartAccountPairs.put(coaCodeName, acctNumName);                
            }
        }
        
        return chartAccountPairs;
    }
    
    public Map<String, String> listAccountNumberChartCodePairs(PersistableBusinessObject bo) {
        Map<String, String> accountChartPairs = new HashMap<String, String>(); 
        String docTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(bo.getClass());
        List<String> pks = listPrimaryKeyFieldNames(bo.getClass());
        Iterator<Map.Entry<String, Class>> refObjs = listReferenceObjectFields(bo).entrySet().iterator();
        
        while (refObjs.hasNext()) {
            Map.Entry<String, Class> entry = (Map.Entry<String, Class>)refObjs.next();
            String accountName = entry.getKey();
            Class accountType = entry.getValue();
            
            // if the reference object is of Account or Account-involved BO class (including all subclasses)            
            if (accountClasses.contains(accountType)) {
                String coaCodeName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
                String acctNumName = getForeignKeyFieldName(bo.getClass(), accountName, KFSPropertyConstants.ACCOUNT_NUMBER);
                
                // exclude the case when chartOfAccountsCode-accountNumber don't exist as foreign keys in the BO:
                // for ex, in SubAccount, a21SubAccount is a reference object but its PKs don't exist as FKs in SubAccount;
                // rather, A21SubAccount has a nested reference account - costShareAccount, 
                // whose PKs exists in A21SubAccount as FKs, and are used in SubAccount maint doc as nested reference
                // special treatment outside this method is needed for this case
                if (StringUtils.isEmpty(coaCodeName) || StringUtils.isEmpty(acctNumName)) 
                    continue;
                
                // exclude the case when chartOfAccountsCode-accountNumber are also the PKs of this BO, 
                // for ex, in SubAccount; as these fields shall be dealt by the framework separately  
                if (pks.contains(coaCodeName) || pks.contains(acctNumName)) 
                    continue;
                
                // exclude non-maintainable account field
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
}
