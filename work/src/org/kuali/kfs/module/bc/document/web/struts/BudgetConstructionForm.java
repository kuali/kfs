/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.dao.ojb.BudgetConstructionDaoOjb;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.core.util.SpringServiceLocator;


public class BudgetConstructionForm extends KualiTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionForm.class);

    BudgetConstructionDaoOjb bcHeaderDao;
    String chartOfAccountsCode = "BA";
    String accountNumber = "6044906" ;
    String subAccountNumber = "-----";
    Integer universityFiscalYear = new Integer(2008);
    

    public BudgetConstructionForm() {
        super();
        LOG.debug("creating BudgetConstructionForm");
//        setDocument(new BudgetConstructionDocument());

        bcHeaderDao = new BudgetConstructionDaoOjb();
        BudgetConstructionHeader budgetConstructionHeader = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        Map fieldValues = new HashMap();
        fieldValues.put("FDOC_NBR", budgetConstructionHeader.getDocumentNumber());
        fieldValues.put("UNIV_FISCAL_YR", budgetConstructionHeader.getUniversityFiscalYear());
        fieldValues.put("FIN_COA_CD", budgetConstructionHeader.getChartOfAccountsCode());
        fieldValues.put("ACCOUNT_NBR", budgetConstructionHeader.getAccountNumber());
        fieldValues.put("SUB_ACCT_NBR", budgetConstructionHeader.getSubAccountNumber());

//        BudgetConstructionDocument budgetConstructionDocument = new BudgetConstructionDocument();
        BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionDocument.class, fieldValues);
//      BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) getDocumentService().getByDocumentHeaderId("258750");
        setDocument(budgetConstructionDocument);
        
        budgetConstructionDocument.initiateDocument(budgetConstructionHeader);
    }

}
