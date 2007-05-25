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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Purchasing Accounts Payable Action Base
 */
public class PurchasingAccountsPayableActionBase extends KualiAccountingDocumentActionBase {
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ActionForward superForward = super.save(mapping, form, request, response);
        ActionForward forward = refreshAccountSummary(mapping, form, request, response);
        return superForward;
    }
    
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.loadDocument(kualiDocumentFormBase);
        
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument)kualiDocumentFormBase.getDocument();
        this.refreshAccountSummary(document);
    }
    
    public ActionForward refreshAccountSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        KualiAccountingDocumentFormBase baseForm = (KualiAccountingDocumentFormBase) form;
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument)baseForm.getDocument();
        this.refreshAccountSummary(document);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    private void refreshAccountSummary(PurchasingAccountsPayableDocument document) {
        List<PurchasingApItem> items = document.getItems();
        List<SourceAccountingLine> summaryAccountingLines = SpringServiceLocator.getPurapService().generateSummary(items);
        document.setSummaryAccounts(summaryAccountingLines);
    }

}
