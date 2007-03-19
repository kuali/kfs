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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
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
        String accountNumber = "6044900" ;
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
        
// from kualiDocumentActionBase.loadDocument()
//        kualiDocumentFormBase.setDocument(doc);
//        KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
//        kualiDocumentFormBase.setDocTypeName(workflowDoc.getDocumentType());
//        // KualiDocumentFormBase.populate() needs this updated in the session
//        GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        //return super.save(mapping, form, request, response);
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        DocumentService documentService = SpringServiceLocator.getDocumentService();
        
        // TODO for now just do trivial save eventually need to add validation, routelog stuff, etc
        documentService.updateDocument(bcDocument);

        // TODO this should eventually be set to return to AccountSelect
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward performMonthlyBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String docNumber;
        
        // TODO do validate, save, etc first then goto the monthly screen or redisplay if errors
        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        BudgetConstructionDocument bcDocument = (BudgetConstructionDocument) budgetConstructionForm.getDocument();
        DocumentService documentService = SpringServiceLocator.getDocumentService();
        
        // TODO for now just save
        documentService.updateDocument(bcDocument);
        
        PendingBudgetConstructionGeneralLedger pbglLine = bcDocument.getPendingBudgetConstructionGeneralLedgerExpenditureLines().get(this.getSelectedLine(request));


        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, BCConstants.MONTHLY_BUDGET_METHOD);
        parameters.put("documentNumber", pbglLine.getDocumentNumber());
        parameters.put("universityFiscalYear", pbglLine.getUniversityFiscalYear().toString());
        parameters.put("chartOfAccountsCode", pbglLine.getChartOfAccountsCode());
        parameters.put("accountNumber", pbglLine.getAccountNumber());
        parameters.put("subAccountNumber", pbglLine.getSubAccountNumber());
        parameters.put("financialObjectCode", pbglLine.getFinancialObjectCode());
        parameters.put("financialSubObjectCode", pbglLine.getFinancialSubObjectCode());
        parameters.put("financialBalanceTypeCode", pbglLine.getFinancialBalanceTypeCode());
        parameters.put("financialObjectTypeCode", pbglLine.getFinancialObjectTypeCode());

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
            
        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.MONTHLY_BUDGET_ACTION, parameters);
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
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) form;
        
        // do specific refresh stuff here based on refreshCaller parameter
        // typical refresh callers would be monthlyBudget or salarySetting
        // need to look at optmistic locking problems since we will be storing the values in the form before hand
        // this locking problem may workout if we store first then put the form in session
        String refreshCaller = request.getParameter(Constants.REFRESH_CALLER);

        // for now, this re-populates pbgl revenue-expenditure lines
        // and rehooks the budgetConstructionMonthly referenced objects
        // we should be able to just refresh needed references by iterating the current set of expenditure lines???

        if (refreshCaller.equalsIgnoreCase(BCConstants.MONTHLY_BUDGET_REFRESH_CALLER)){
            
            // do things specific to returning from MonthlyBudget

            // need to get current state of monthly budgets
            for (PendingBudgetConstructionGeneralLedger line :
                budgetConstructionForm.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerExpenditureLines()){

                line.refreshReferenceObject("budgetConstructionMonthly");
            }
            for (PendingBudgetConstructionGeneralLedger line :
                budgetConstructionForm.getBudgetConstructionDocument().getPendingBudgetConstructionGeneralLedgerRevenueLines()){

                line.refreshReferenceObject("budgetConstructionMonthly");
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

}
