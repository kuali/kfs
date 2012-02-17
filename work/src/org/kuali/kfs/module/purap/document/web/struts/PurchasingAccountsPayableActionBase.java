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
package org.kuali.kfs.module.purap.document.web.struts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineParser;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Struts Action for Purchasing and Accounts Payable documents
 */
public class PurchasingAccountsPayableActionBase extends KualiAccountingDocumentActionBase {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) kualiDocumentFormBase;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) purapForm.getDocument();

        // refresh the account summary (note this also updates the account amounts)
        purapForm.refreshAccountSummmary();

        for (org.kuali.rice.krad.bo.Note note : (java.util.List<org.kuali.rice.krad.bo.Note>) document.getNotes()) {
            note.refreshReferenceObject("attachment");
        }

        // sort the below the line
        SpringContext.getBean(PurapService.class).sortBelowTheLine(document);

        updateBaseline(document, (PurchasingAccountsPayableFormBase) kualiDocumentFormBase);
    }
    
    /**
     * Updates the baseline accounts on form and doc.
     * 
     * @param document A descendant of PurchasingAccountsPayableDocument
     */
    protected <T extends PurchasingAccountsPayableDocument, V extends KualiAccountingDocumentFormBase> void updateBaseline(T document, V form) {
        // clear out the old lines first
        for (PurApItem item : document.getItems()) {
            // clear out the old lines first
            item.getBaselineSourceAccountingLines().clear();

            for (PurApAccountingLine sourceAccount : item.getSourceAccountingLines()) {
                // JHK: KFSMI-287 - removed deep copy since this object will be thrown away after the page renders, we just need a
                // different path to have them stored on the form
                // ESPECIALLY since PURAP does not allow lines to be reverted (see calls to setRevertible)
                item.getBaselineSourceAccountingLines().add(sourceAccount);
            }
        }
    }

    /**
     * Invokes a service method to refresh the account summary.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward refreshAccountSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) purapForm.getDocument();
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(document);
        purapForm.refreshAccountSummmary();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#uploadAccountingLines(boolean,org.apache.struts.action.ActionForm)
     */
    @Override
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;
        PurchasingAccountsPayableDocumentBase purapDocument = (PurchasingAccountsPayableDocumentBase)purapForm.getFinancialDocument();        
        PurApAccountingLineParser accountingLineParser = (PurApAccountingLineParser)purapDocument.getAccountingLineParser();
        List importedLines = null;
        String errorPathPrefix = PurapConstants.ACCOUNT_DISTRIBUTION_ERROR_KEY;
        //String errorPathPrefix = "accountDistributionnewSourceLine";

        // import the lines
        try {
             FormFile sourceFile = purapForm.getSourceFile();
             checkUploadFile(sourceFile);
             GlobalVariables.getMessageMap().clearErrorPath();
             GlobalVariables.getMessageMap().addToErrorPath(errorPathPrefix);                
             importedLines = accountingLineParser.importSourceAccountingLines(sourceFile.getFileName(), sourceFile.getInputStream(), purapDocument);
             GlobalVariables.getMessageMap().removeFromErrorPath(errorPathPrefix);            
        }
        catch (AccountingLineParserException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add to list those lines successfully imported
        if (importedLines != null) {
            for (Iterator iter = importedLines.iterator(); iter.hasNext();) {
                PurApAccountingLine importedLine = (PurApAccountingLine) iter.next();
                //boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(errorPathPrefix, purapForm.getDocument(), (AccountingLine) importedLine));
                //if (rulePassed) {
                // add accountingLine
                SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(importedLine);
                ((PurchasingFormBase)purapForm).addAccountDistributionsourceAccountingLine(importedLine);
                //}
            }
        }
    }    

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // It would be preferable to find a way to genericize the KualiAccountingDocument methods but this will work for now
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;
        
        // index of item selected
        int itemIndex = getSelectedLine(request);
        PurApItem item = null;
        
        // if custom processing of an accounting line is not done then insert a line generically.
        if (processCustomInsertAccountingLine(purapForm, request) == false) {
            String errorPrefix = null;
            PurApAccountingLine line = null;
            boolean rulePassed = false;
            if (itemIndex >= 0) {
                item = (PurApItem) ((PurchasingAccountsPayableDocument) purapForm.getDocument()).getItem((itemIndex));
                line = (PurApAccountingLine) ObjectUtils.deepCopy(item.getNewSourceLine());
                //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(line);
                errorPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(itemIndex) + "]." + KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
                rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(errorPrefix, purapForm.getDocument(), (AccountingLine) line));
            }
            else if (itemIndex == -2){
                //corrected: itemIndex == -2 is the only case for distribute account
                //This is the case when we're inserting an accounting line for distribute account.
                line = ((PurchasingFormBase)purapForm).getAccountDistributionnewSourceLine();
                //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(line);
                errorPrefix = PurapPropertyConstants.ACCOUNT_DISTRIBUTION_NEW_SRC_LINE;
                rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(errorPrefix, purapForm.getDocument(), (AccountingLine) line));
            }

            if (rulePassed) {
                // add accountingLine
                SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
                if (itemIndex >=0) {
                    insertAccountingLine(purapForm, item, line);
                    // clear the temp account
                    item.resetAccount();
                }
                else if (itemIndex == -2) {
                    //this is the case for distribute account
                    ((PurchasingFormBase)purapForm).addAccountDistributionsourceAccountingLine(line);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Insert the given Accounting Line in several appropriate places in the given item and given form.
     * 
     * @param financialDocumentForm A form that inherits from PurchasingAccountsPaybleFormBase
     * @param item A PurApItem
     * @param line A PurApAccountingLine
     */
    protected void insertAccountingLine(PurchasingAccountsPayableFormBase financialDocumentForm, PurApItem item, PurApAccountingLine line) {
        PurchasingAccountsPayableDocument preq = (PurchasingAccountsPayableDocument) financialDocumentForm.getDocument();

        Integer index = item.getSourceAccountingLines().size() + 1;
        line.setSequenceNumber(index);
        // add it to the item
        item.getSourceAccountingLines().add(line);
    }

    /**
     * Allows the custom processing of an accounting line during a call to insert source line. If a custom method for inserting an
     * accounting line was performed, then a value of true must be returned.
     * 
     * @param purapForm
     * @param request
     * @return boolean indicating if validation succeeded
     */
    public boolean processCustomInsertAccountingLine(PurchasingAccountsPayableFormBase purapForm, HttpServletRequest request) {
        return false;
    }

    /**
     * Insert the given Accounting Line in several appropriate places in the given item and given form.
     * 
     * @param financialDocumentForm A form that inherits from KualiAccountingDocumentFormBase
     * @param item A PurApItem
     * @param line A PurApAccountingLine
     */
    protected void insertAccountingLine(KualiAccountingDocumentFormBase financialDocumentForm, PurApItem item, PurApAccountingLine line) {
        // add it to the item
        item.getSourceAccountingLines().add(line);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;

        String[] indexes = getSelectedLineForAccounts(request);
        int itemIndex = Integer.parseInt(indexes[0]);
        int accountIndex = Integer.parseInt(indexes[1]);

        PurApItem item = (PurApItem) ((PurchasingAccountsPayableDocument) purapForm.getDocument()).getItem((itemIndex));
        item.getSourceAccountingLines().remove(accountIndex);

        // remove the decorator
        // financialDocumentForm.getSourceLineDecorators().remove(decorator);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#getSourceAccountingLine(org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public SourceAccountingLine getSourceAccountingLine(ActionForm form, HttpServletRequest request) {
        String[] indexes = getSelectedLineForAccounts(request);
        int itemIndex = Integer.parseInt(indexes[0]);
        int accountIndex = Integer.parseInt(indexes[1]);
        PurchasingAccountsPayableFormBase purchasingAccountsPayableForm = (PurchasingAccountsPayableFormBase) form;
        SourceAccountingLine line;
        if (itemIndex == -2) {
            line = customAccountRetrieval(accountIndex, purchasingAccountsPayableForm);
        }
        else {
            PurApItem item = (PurApItem) ((PurchasingAccountsPayableDocument) purchasingAccountsPayableForm.getDocument()).getItem((itemIndex));
            line = (SourceAccountingLine) ObjectUtils.deepCopy(item.getSourceAccountingLines().get(accountIndex));
        }
        return line;
    }

    /**
     * Perform custom processing on accounting lines. See <code>getSelectedLineForAccounts</code>.
     * 
     * @param accountIndex The index of the account into the request parameter
     * @param purchasingAccountsPayableForm A form which inherits from PurchasingAccountsPayableFormBase
     * @return A SourceAccountingLine
     */
    protected SourceAccountingLine customAccountRetrieval(int accountIndex, PurchasingAccountsPayableFormBase purchasingAccountsPayableForm) {
        // default impl returns null
        return null;
    }

    /**
     * Will return an array of Strings containing 2 indexes, the first String is the item index and the second String is the account
     * index. These are obtained by parsing the method to call parameter from the request, between the word ".line" and "." The
     * indexes are separated by a semicolon (:)
     * 
     * @param request The HttpServletRequest
     * @return An array of Strings containing pairs of two indices, an item index and a account index
     */
    protected String[] getSelectedLineForAccounts(HttpServletRequest request) {
        String accountString = new String();
        String parameterName = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            accountString = StringUtils.substringBetween(parameterName, ".line", ".");
        }
        String[] result = StringUtils.split(accountString, ":");

        return result;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#downloadBOAttachment(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward downloadBOAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) ((PurchasingAccountsPayableFormBase) form).getDocument();

        for (org.kuali.rice.krad.bo.Note note : (java.util.List<org.kuali.rice.krad.bo.Note>) document.getNotes()) {
            note.refreshReferenceObject("attachment");
        }

        return super.downloadBOAttachment(mapping, form, request, response);
    }

    @Override
    protected void processAccountingLineOverrides(List accountingLines) {
        //do nothing purap handles these differently
    }
    
    /**
     * Perform calculation on item line.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return An ActionForward
     */
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clearAllTaxes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    protected void customCalculate(PurchasingAccountsPayableDocument purapDoc) {
        // do nothing by default
    }
    
    /**
     * Toggles all specific tabs to open
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward showAllAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        String accountingLineTab = "AccountingLines";
        String value = null;
        
        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        for (Entry<String, String> tabEntry: tabStates.entrySet()) {
            if(tabEntry.getKey().startsWith(accountingLineTab)){
                newTabStates.put(tabEntry.getKey(), "OPEN");
            }else{                        
                if (tabEntry.getValue() instanceof String) {
                    value = tabEntry.getValue();
                }
                else {
                    //This is the case where the value is an Array of String,
                    //so we'll have to get the first element
                    Object result = tabEntry.getValue();
                    result.getClass();
                    value = ((String[])result)[0];
                }
                newTabStates.put(tabEntry.getKey(), value);                
            }
        }
        kualiForm.setTabStates(newTabStates);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Toggles all specific tabs to closed
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hideAllAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        String accountingLineTab = "AccountingLines";
        String value = null;
        
        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        for (Entry<String, String> tabEntry: tabStates.entrySet()) {
            if(tabEntry.getKey().startsWith(accountingLineTab)){
                newTabStates.put(tabEntry.getKey(), "CLOSE");
            }else{
                if (tabEntry.getValue() instanceof String) {
                    value = tabEntry.getValue();
                }
                else {
                    //This is the case where the value is an Array of String,
                    //so we'll have to get the first element
                    Object result = tabEntry.getValue();
                    result.getClass();
                    value = ((String[])result)[0];
                }
                newTabStates.put(tabEntry.getKey(), value);                
            }
        }
        kualiForm.setTabStates(newTabStates);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Override to verify the document has been saved before the note is inserted. This will assure the correct parent object id is
     * associated with the note.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#insertBONote(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertBONote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) ((PurchasingAccountsPayableFormBase) form).getDocument();
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isInitiated()) {
            SpringContext.getBean(DocumentService.class).saveDocument(document);
        }

        return super.insertBONote(mapping, form, request, response);
    }

}
