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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.web.struts.form.PurchasingAccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Purchasing Accounts Payable Action Base
 */
public class PurchasingAccountsPayableActionBase extends KualiAccountingDocumentActionBase {

    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) kualiDocumentFormBase;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument)purapForm.getDocument();
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(document);
        
        purapForm.refreshAccountSummmary();
        
        //FIXME: temporary workaround see KULPURAP-1397
        for (org.kuali.core.bo.Note note : (java.util.List<org.kuali.core.bo.Note>)document.getBoNotes()) {
            note.refreshReferenceObject("attachment");
        }
        
        updateBaseline(document,(PurchasingAccountsPayableFormBase)kualiDocumentFormBase);
    }

    /**
     * This method updates the baseline accounts on form and doc
     * @param document
     */
     private <T extends PurchasingAccountsPayableDocument, V extends KualiAccountingDocumentFormBase> void updateBaseline(T document, V form) {
        //update baseline accounts for purap

        //clear out the old lines first
        form.getBaselineSourceAccountingLines().clear();
        for (PurApItem item : document.getItems()) {
            //clear out the old lines first
            item.getBaselineSourceAccountingLines().clear();
            
            for (PurApAccountingLine sourceAccount : item.getSourceAccountingLines()) {
                PurApAccountingLine baselineAccount = (PurApAccountingLine)ObjectUtils.deepCopy(sourceAccount);
                item.getBaselineSourceAccountingLines().add(baselineAccount);
                form.getBaselineSourceAccountingLines().add(baselineAccount);
            }
        }
    }

    public ActionForward refreshAccountSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;        
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) purapForm.getDocument();
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(document);
        purapForm.refreshAccountSummmary();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // It would be preferable to find a way to genericize the KualiAccountingDocument methods but this will work for now
        PurchasingAccountsPayableFormBase purapForm = (PurchasingAccountsPayableFormBase) form;

        // index of item selected
        int itemIndex = getSelectedLine(request);
        PurApItem item = null;

        /*
         * if custom processing of an accounting line is not done then insert a line generically.
         */
        if (processCustomInsertAccountingLine(purapForm, request) == false) {

            item = (PurApItem) ((PurchasingAccountsPayableDocument) purapForm.getDocument()).getItem((itemIndex));
            PurApAccountingLine line = (PurApAccountingLine)ObjectUtils.deepCopy(item.getNewSourceLine());

            String errorPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + Integer.toString(itemIndex) + "]." + KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
            boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(errorPrefix, purapForm.getDocument(), (AccountingLine) line));

            if (rulePassed) {
                // add accountingLine
                SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
                insertAccountingLine(purapForm, item, line);

                // clear the temp account
                item.resetAccount();
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void insertAccountingLine(PurchasingAccountsPayableFormBase financialDocumentForm, PurApItem item, PurApAccountingLine line) {
        PurchasingAccountsPayableDocument preq = (PurchasingAccountsPayableDocument)financialDocumentForm.getDocument();
        // this decorator stuff should be moved out in parent class so we don't need to copy it here
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        // add it to the item
        item.getSourceAccountingLines().add(line);
        // add it to the baseline on item
        item.getBaselineSourceAccountingLines().add(line);
        
        // add it to the baseline, to prevent generation of spurious update events
        financialDocumentForm.getBaselineSourceAccountingLines().add(line);
        
        // add the decorator
        financialDocumentForm.getSourceLineDecorators().add(decorator);

    }

    /**
     * This method allows the custom processing of an accounting line during a call to insert source line. If a custom method for
     * inserting an accounting line was performed, then a value of true must be returned.
     * 
     * @param purapForm
     * @param request
     * @return
     */
    public boolean processCustomInsertAccountingLine(PurchasingAccountsPayableFormBase purapForm, HttpServletRequest request) {
        return false;
    }


    protected void insertAccountingLine(KualiAccountingDocumentFormBase financialDocumentForm, PurApItem item, PurApAccountingLine line) {
        // this decorator stuff should be moved out in parent class so we don't need to copy it here
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        // add it to the item
        item.getSourceAccountingLines().add(line);

        // add it to the baseline, to prevent generation of spurious update events
        item.getBaselineSourceAccountingLines().add(line);

        // add the decorator
        financialDocumentForm.getSourceLineDecorators().add(decorator);

    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
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
        
        //add it to the baseline, to prevent generation of spurious update events
        item.getBaselineSourceAccountingLines().remove(accountIndex);
//      TODO: Chris we should probabl also add to baselineSourceList on form
        
        
        // remove the decorator
//        financialDocumentForm.getSourceLineDecorators().remove(decorator);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#getSourceAccountingLine(org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public SourceAccountingLine getSourceAccountingLine(ActionForm form, HttpServletRequest request) {
        String[] indexes = getSelectedLineForAccounts(request);
        int itemIndex = Integer.parseInt(indexes[0]);
        int accountIndex = Integer.parseInt(indexes[1]);
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        SourceAccountingLine line;
        if (itemIndex == -2) {
            line = (SourceAccountingLine) ObjectUtils.deepCopy(purchasingForm.getAccountDistributionsourceAccountingLines().get(accountIndex));
        }
        else {
            PurApItem item = (PurApItem) ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItem((itemIndex));
            line = (SourceAccountingLine) ObjectUtils.deepCopy(item.getSourceAccountingLines().get(accountIndex));
        }
        return line;
    }

    /**
     * This method will return an array of String containing 2 indexes, the first String is the item index and the second String is
     * the account index. These are obtained by parsing the method to call parameter from the request, between the word ".line" and
     * "." The indexes are separated by a semicolon (:)
     * 
     * @param request
     * @return
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

}
