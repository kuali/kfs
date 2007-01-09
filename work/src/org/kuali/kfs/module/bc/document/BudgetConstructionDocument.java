/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.budget.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;

public class BudgetConstructionDocument extends TransactionalDocumentBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDocument.class);

    private String documentNumber;
    private Integer universityFiscalYear = new Integer(2008);
    private String chartOfAccountsCode = "BA";
    private String accountNumber = "6044906";
    private String subAccountNumber = "-----";
    private String financialObjectTypeCode = "IN";

    private Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedger;
    
    public BudgetConstructionDocument(){

        Map fieldValues = new HashMap();
        fieldValues.put("UNIV_FISCAL_YR", universityFiscalYear);
        fieldValues.put("FIN_COA_CD", chartOfAccountsCode);
        fieldValues.put("ACCOUNT_NBR", accountNumber);
        fieldValues.put("SUB_ACCT_NBR", subAccountNumber);
        fieldValues.put("FIN_OBJ_TYP_CD", financialObjectTypeCode);
        
        pendingBudgetConstructionGeneralLedger = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(PendingBudgetConstructionGeneralLedger.class, fieldValues, "FIN_OBJECT_CD", true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("pendingBudgetConstructionGeneralLedger is: "+pendingBudgetConstructionGeneralLedger);
        }
    }

    public Collection<PendingBudgetConstructionGeneralLedger> getPendingBudgetConstructionGeneralLedger() {
        return pendingBudgetConstructionGeneralLedger;
    }

    public void setPendingBudgetConstructionGeneralLedger(Collection<PendingBudgetConstructionGeneralLedger> pendingBudgetConstructionGeneralLedger) {
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
    }

}
