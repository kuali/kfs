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
package org.kuali.module.budget.web.struts.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.dao.ojb.BudgetConstructionDaoOjb;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.web.struts.form.BudgetConstructionForm;

import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * 
 * need to figure out if this should extend KualiAction, KualiDocumentActionBase or
 * KualiTransactionDocumentActionBase
 */
public class BudgetConstructionAction extends KualiTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingAction.class);
    
    /**
     * added this to be similar to KRA - remove if not needed
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        return super.execute(mapping, form, request, response);
    }

    /**
     * gwp - no call to super, need to work through command we will use
     * 
     * randall - This method might be unnecessary, but putting it here allows URL to be consistent with Document URLs
     * 
     * gwp - i think we still want this method, just need to figure out if we use command initiate or
     * displayDocSearchView or something else.
     * i expect we will get the account/subaccount or docnumber from the previous form and assume the
     * document will already exist regardless of creation by genesis or 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        String command = budgetConstructionForm.getCommand();
        
//        if (IDocHandler.INITIATE_COMMAND.equals(command)){
            loadDocument(budgetConstructionForm);
//        }
            
            return mapping.findForward(Constants.MAPPING_BASIC);
        
/** from KualiDocumentActionBase,docHandler()
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        String command = kualiDocumentFormBase.getCommand();

        // in all of the following cases we want to load the document
//        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && kualiDocumentFormBase.getDocId() != null) {
            loadDocument(kualiDocumentFormBase);
        }
        else if (IDocHandler.INITIATE_COMMAND.equals(command)) {
            createDocument(kualiDocumentFormBase);
        }
        else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("kualiDocumentFormBase.getAdditionalScriptFile(): " + kualiDocumentFormBase.getAdditionalScriptFile());
        }
        if (kualiDocumentFormBase.getAdditionalScriptFile() == null || kualiDocumentFormBase.getAdditionalScriptFile().equals("")) {
            DocumentEntry docEntry = SpringServiceLocator.getDataDictionaryService().getDataDictionary().getDocumentEntry(kualiDocumentFormBase.getDocument().getClass());
            kualiDocumentFormBase.setAdditionalScriptFile(docEntry.getWebScriptFile());
            if (LOG.isDebugEnabled()) {
                LOG.debug("set kualiDocumentFormBase.getAdditionalScriptFile() to: " + kualiDocumentFormBase.getAdditionalScriptFile());
            }
        }
        if (IDocHandler.SUPERUSER_COMMAND.equalsIgnoreCase(command)) {
            kualiDocumentFormBase.setSuppressAllButtons(true);
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
**/
    }

/**
 * 
 * Coded this to look like KualiDocumentActionBase.loadDocument()  
 * @param budgetConstructionForm
 * @throws WorkflowException
 */
    private void loadDocument(BudgetConstructionForm budgetConstructionForm) throws WorkflowException {

        BudgetConstructionDaoOjb bcHeaderDao;
        String chartOfAccountsCode = "BA";
        String accountNumber = "6044906" ;
        String subAccountNumber = "-----";
        Integer universityFiscalYear = new Integer(2008);

        bcHeaderDao = new BudgetConstructionDaoOjb();
        BudgetConstructionHeader budgetConstructionHeader = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        Map fieldValues = new HashMap();
        fieldValues.put("FDOC_NBR", budgetConstructionHeader.getDocumentNumber());
        fieldValues.put("UNIV_FISCAL_YR", budgetConstructionHeader.getUniversityFiscalYear());
        fieldValues.put("FIN_COA_CD", budgetConstructionHeader.getChartOfAccountsCode());
        fieldValues.put("ACCOUNT_NBR", budgetConstructionHeader.getAccountNumber());
        fieldValues.put("SUB_ACCT_NBR", budgetConstructionHeader.getSubAccountNumber());

//        BudgetConstructionDocument budgetConstructionDocument = new BudgetConstructionDocument();
//        BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionDocument.class, fieldValues);
      BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(budgetConstructionHeader.getDocumentNumber());
        budgetConstructionForm.setDocument(budgetConstructionDocument);

        KualiWorkflowDocument workflowDoc = budgetConstructionDocument.getDocumentHeader().getWorkflowDocument();
        budgetConstructionForm.setDocTypeName(workflowDoc.getDocumentType());
        // KualiDocumentFormBase.populate() needs this updated in the session
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        
        // for now, this populates pbgl revenue-expenditure lines
        // we need to changeover to using accountinglines instead
        budgetConstructionDocument.initiateDocument();
        
// from kualiDocumentActionBase.loadDocument()
//        kualiDocumentFormBase.setDocument(doc);
//        KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
//        kualiDocumentFormBase.setDocTypeName(workflowDoc.getDocumentType());
//        // KualiDocumentFormBase.populate() needs this updated in the session
//        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        
    }

    public ActionForward performMonthlyBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;
        
        // do validate, save, etc first then goto the monthly screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        
        int selectedIndex = this.getSelectedLine(request);


        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        Properties parameters = new Properties();
//        parameters.put("methodToCall", "view");
        parameters.put("documentNumber", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getDocumentNumber());
        parameters.put("universityFiscalYear", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getChartOfAccountsCode());
        parameters.put("accountNumber", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getAccountNumber());
        parameters.put("subAccountNumber", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getSubAccountNumber());
        parameters.put("financialObjectCode", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getFinancialObjectCode());
        parameters.put("financialSubObjectCode", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getFinancialSubObjectCode());
        parameters.put("financialBalanceTypeCode", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getFinancialBalanceTypeCode());
        parameters.put("financialObjectTypeCode", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getFinancialObjectTypeCode());
//        request.setAttribute("accountNumber", bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditure().get(selectedIndex).getAccountNumber());
        
//        String bcMonthParmsKey = "bcMonthParmsKey"; 
        GlobalVariables.getUserSession().addObject("bcMonthParmsKey",parameters);
        Properties parms = new Properties();
        parms.put("methodToCall", "view");
        parms.put("bcMonthParmsKey","bcMonthParmsKey");


        String lookupUrl = UrlFactory.parameterizeUrl("/" + "budgetMonthlyBudget.do", parms);
//        String lookupUrl = UrlFactory.parameterizeUrl("/" + "budgetMonthlyBudget.do", parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward returnFromMonthly(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;

        String documentNumber = request.getParameter("documentNumber");
        BudgetConstructionDocument budgetConstructionDocument = (BudgetConstructionDocument) SpringServiceLocator.getDocumentService().getByDocumentHeaderId(documentNumber);
        budgetConstructionForm.setDocument(budgetConstructionDocument);

        KualiWorkflowDocument workflowDoc = budgetConstructionDocument.getDocumentHeader().getWorkflowDocument();
        budgetConstructionForm.setDocTypeName(workflowDoc.getDocumentType());
        // KualiDocumentFormBase.populate() needs this updated in the session
        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        
        // for now, this populates pbgl revenue-expenditure lines
        // we need to changeover to using accountinglines instead
        budgetConstructionDocument.initiateDocument();

        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
}

