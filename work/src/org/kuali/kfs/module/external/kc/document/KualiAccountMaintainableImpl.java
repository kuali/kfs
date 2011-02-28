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
package org.kuali.kfs.module.external.kc.document;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.SubAccountTrickleDownInactivationService;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.kfs.integration.cg.businessobject.CFDA;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.module.external.kc.businessobject.Cfda;
import org.kuali.kfs.module.external.kc.service.CfdaService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends org.kuali.kfs.coa.document.KualiAccountMaintainableImpl {
 
    /**
     * After a copy is done set specific fields on {@link Account} to default values
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        Account account = (Account) this.getBusinessObject();
        super.processAfterCopy(document, parameters);
     //   account.setAccountCfdaNumber(lookupAccountCfda( account.getAccountNumber(), account.getAccountCfdaNumber()));
    }
  
    /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#lookupAccountCfda(java.lang.String, java.lang.String)
     */
    @Override
    protected String lookupAccountCfda(String accountNumber, String currentCfda) {
        ContractsAndGrantsCfda contractsAndGrantsCfda = this.lookupAccountCfda(accountNumber);
        return contractsAndGrantsCfda.getCfdaNumber();
     }
 
    protected ContractsAndGrantsCfda lookupAccountCfda (String accountNumber) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String enableResearchAdminObjectCodeAttributeInd = parameterService.getParameterValue(ObjectCode.class, KFSConstants.ObjectCodeConstants.PARAMETER_KC_ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND);
        if (! enableResearchAdminObjectCodeAttributeInd.equals("Y")) return null;
        CfdaService cfdaService = (CfdaService) SpringContext.getService("cfdaService");
        return cfdaService.getByPrimaryId(accountNumber);
     }

     /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        // TODO Auto-generated method stub
        super.saveBusinessObject();
    }

  

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        // TODO Auto-generated method stub
        Account newAccount = (Account) getBusinessObject();
        ContractsAndGrantsCfda cfda = lookupAccountCfda(newAccount.getAccountNumber());
        
        newAccount.setAccountCfdaNumber( cfda.getCfdaNumber());
    //    newAccount.setAccountCfdaNumber(lookupAccountCfda( newAccount.getAccountNumber(), newAccount.getAccountCfdaNumber()));
        super.processAfterEdit(document, parameters);
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // TODO Auto-generated method stub
        super.processAfterRetrieve();
    }


}
