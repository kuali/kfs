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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.kfs.web.ui.AccountingLineDecorator;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.web.struts.form.PurchasingAccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Purchasing Accounts Payable Action Base
 */
public class PurchasingAccountsPayableActionBase extends KualiAccountingDocumentActionBase {

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward superForward = super.save(mapping, form, request, response);
        ((KualiAccountingDocumentFormBase) form).getDocument().refreshNonUpdateableReferences();
        ActionForward forward = refreshAccountSummary(mapping, form, request, response);
        return superForward;
    }

    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {

        super.loadDocument(kualiDocumentFormBase);

        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) kualiDocumentFormBase.getDocument();
        this.refreshAccountSummary(document);
    }

    public ActionForward refreshAccountSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        KualiAccountingDocumentFormBase baseForm = (KualiAccountingDocumentFormBase) form;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) baseForm.getDocument();
        this.refreshAccountSummary(document);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public void refreshAccountSummary(PurchasingAccountsPayableDocument document) {
        List<PurchasingApItem> items = document.getItems();
        List<SourceAccountingLine> summaryAccountingLines = SpringServiceLocator.getPurapService().generateSummary(items);
        document.setSummaryAccounts(summaryAccountingLines);
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
        PurchasingApItem item = null;

        /*
         * if custom processing of an accounting line is not done then insert a line generically.
         */
        if (processCustomInsertAccountingLine(purapForm, request) == false) {

            item = (PurchasingApItem) ((PurchasingAccountsPayableDocument) purapForm.getDocument()).getItem((itemIndex));
            PurApAccountingLine line = item.getNewSourceLine();

            boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINES_PROPERTY_NAME + "[" + Integer.toString(itemIndex) + "]", purapForm.getDocument(), (AccountingLine) line));

            if (rulePassed) {
                // add accountingLine
                SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
                insertAccountingLine(purapForm, item, line);

                // clear the temp account
                item.resetAccount();
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void insertAccountingLine(PurchasingAccountsPayableFormBase financialDocumentForm, PurchasingApItem item, PurApAccountingLine line) {
        // this decorator stuff should be moved out in parent class so we don't need to copy it here
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        // add it to the item
        item.getSourceAccountingLines().add(line);

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


    protected void insertAccountingLine(KualiAccountingDocumentFormBase financialDocumentForm, PurchasingApItem item, PurApAccountingLine line) {
        // this decorator stuff should be moved out in parent class so we don't need to copy it here
        // create and init a decorator
        AccountingLineDecorator decorator = new AccountingLineDecorator();
        decorator.setRevertible(false);

        // add it to the item
        item.getSourceAccountingLines().add(line);

        // add it to the baseline, to prevent generation of spurious update events
        financialDocumentForm.getBaselineSourceAccountingLines().add(line);

        // add the decorator
        financialDocumentForm.getSourceLineDecorators().add(decorator);

    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        String[] indexes = getSelectedLineForAccounts(request);
        int itemIndex = Integer.parseInt(indexes[0]);
        int accountIndex = Integer.parseInt(indexes[1]);
        if (itemIndex == -2) {
            purchasingForm.getAccountDistributionsourceAccountingLines().remove(accountIndex);
        }
        else {
            PurchasingApItem item = (PurchasingApItem) ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItem((itemIndex));
            item.getSourceAccountingLines().remove(accountIndex);
        }
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
            PurchasingApItem item = (PurchasingApItem) ((PurchasingAccountsPayableDocument) purchasingForm.getDocument()).getItem((itemIndex));
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
    private String[] getSelectedLineForAccounts(HttpServletRequest request) {
        String accountString = new String();
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            accountString = StringUtils.substringBetween(parameterName, ".line", ".");
        }
        String[] result = StringUtils.split(accountString, ":");
        return result;
    }

}
