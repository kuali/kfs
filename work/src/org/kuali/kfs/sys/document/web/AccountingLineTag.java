/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer;
import org.kuali.kfs.sys.document.web.renderers.AccountingLineTableFooterRenderer;
import org.kuali.kfs.sys.document.web.renderers.AccountingLineTableHeaderRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This tag...how to describe what it does?  It takes these groups of accounting lines and, you know,
 * it, um...it renders them.
 */
public class AccountingLineTag extends BodyTagSupport {
    private final String KUALI_FORM_NAME = "KualiForm";
    private AccountingDocument document;
    private List<AccountingLineGroup> groupsToRender;

    /**
     * Finds the KualiForm wherever it may be hiding
     * @return the KualiForm
     */
    public KualiDocumentFormBase getForm() {
        if (pageContext.getRequest().getAttribute(KUALI_FORM_NAME) != null) {
            return (KualiDocumentFormBase)pageContext.getRequest().getAttribute(KUALI_FORM_NAME);
        }
        
        if (pageContext.getSession().getAttribute(KUALI_FORM_NAME) != null) {
            return (KualiDocumentFormBase)pageContext.getSession().getAttribute(KUALI_FORM_NAME);
        }
        
        return (KualiDocumentFormBase)GlobalVariables.getKualiForm();
    }
    
    /**
     * Retrieves the document
     * @return the document which is currently being operated on
     */
    public AccountingDocument getDocument() {
        if (document == null) {
            document = (AccountingDocument)getForm().getDocument();
        }
        return document;
    }
    
    /**
     * Retrieves all the edit modes for the document
     * @param document the document to get edit modes for
     * @return a Map of all the edit modes
     */
    protected Map getEditModes() {
        AccountingDocument document = getDocument();
        AccountingDocumentAuthorizer documentAuthorizer = getDocumentAuthorizer(document);
        Map editModes = documentAuthorizer.getEditMode(document, GlobalVariables.getUserSession().getFinancialSystemUser());
        editModes.putAll(documentAuthorizer.getEditMode(document, GlobalVariables.getUserSession().getFinancialSystemUser(), document.getSourceAccountingLines(), document.getTargetAccountingLines()));
        return editModes;
    }
 
    /**
     * Creates an accounting document authorizer for the given accounting document
     * @param document the document to get an authorizer for
     * @return an authorizer for the document
     */
    protected AccountingDocumentAuthorizer getDocumentAuthorizer(AccountingDocument document) {
        final Class documentAuthorizerClass = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(document.getClass().getName()).getDocumentAuthorizerClass();
        AccountingDocumentAuthorizer documentAuthorizer = null;
        try {
            documentAuthorizer = (AccountingDocumentAuthorizer)documentAuthorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new IllegalArgumentException("InstantionException while initiating Document Authorizer", ie);
        }
        catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("IllegalAccessException while initiating Document Authorizer", iae);
        }
        return documentAuthorizer;
    }
    
    /**
     * Adds a group to render for this go-round
     * @param group the group to render
     */
    public void addGroupToRender(AccountingLineGroup group) {
        if (groupsToRender == null) {
            groupsToRender = new ArrayList<AccountingLineGroup>();
        }
        groupsToRender.add(group);
    }
    
    /**
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    /**
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        int cellCount = getEnforcedCellCount();
        enforceCellCount(cellCount);
        renderHeader(cellCount);
        renderGroups();
        renderFooter();
        resetTag();
        return EVAL_PAGE;
    }
    
    /**
     * Clears out all state variables in this tag
     */
    protected void resetTag() {
        document = null; // reset state variables for this renderer
        groupsToRender = null;
    }
    
    /**
     * Determines the cell count we're going to enforce, which is the maximum width in cell of any child group
     * @return the cell count we'll enforce
     */
    protected int getEnforcedCellCount() {
        int count = 0;
        for (AccountingLineGroup group : groupsToRender) {
            if (group.getWidthInCells() > count) {
                count = group.getWidthInCells();
            }
        }
        return count;
    }
    
    /**
     * Oh yeah, we're enforcing!  Oh yes we are!  We're going through all the accounting line groups and we're saying to them,
     * Hey!  Hey you group!  Are you going to render the number of cells that we've decided to enforce?  And if they're like: Oh, no,
     * I don't have that many cells, then we'll reset that group to render that many of cells!
     * @param cellCount the number of cells to uphold
     */
    protected void enforceCellCount(int cellCount) {
        for (AccountingLineGroup group : groupsToRender) {
            if (group.getWidthInCells() < cellCount) {
                group.setCellCount(cellCount);
            }
        }
    }
    
    /**
     * Renders the beginning of the accounting line table, including the tab-container div
     * @param cellCount the number of cells this table is wide
     * @throws JspException thrown if rendering fails
     */
    protected void renderHeader(int cellCount) throws JspException {
        AccountingLineTableHeaderRenderer renderer = new AccountingLineTableHeaderRenderer();
        renderer.setCellCount(cellCount);
        KualiAccountingDocumentFormBase form = (KualiAccountingDocumentFormBase)getForm();
        renderer.setHideDetails(form.getHideDetails());
        renderer.setAccountingLineImportInstructionsUrl(form.getAccountingLineImportInstructionsUrl());
        renderer.render(pageContext, this);
    }
    
    /**
     * Renders all the accounting line groups within this accounting line tag
     * @throws JspException thrown if something goes wrong
     */
    protected void renderGroups() throws JspException {
        final KualiDocumentFormBase form = getForm();
        final int startingTabIndex = form.getCurrentTabIndex();
        final int arbitrarilyHighIndex = form.getNextArbitrarilyHighIndex();
        for (AccountingLineGroup group : groupsToRender) {
            group.setStartingTabIndex(startingTabIndex);
            group.setArbitrarilyHighIndex(arbitrarilyHighIndex);
            group.renderEverything(pageContext, this);
            updateCurrentTabIndex(form, group.getEndingTabIndex());
        }
    }
    
    /**
     * Updates the form's current tab index to whatever was after the last group
     * @param form the form to update
     * @param tabIndex the tab index to update to
     */
    protected void updateCurrentTabIndex(KualiDocumentFormBase form, int tabIndex) {
        while (form.getCurrentTabIndex() < tabIndex) {
            form.incrementTabIndex();
        }
    }
    
    /**
     * Renders the footer for the accounting line table
     * @throws JspException thrown if something mysterious failed
     */
    protected void renderFooter() throws JspException {
        Renderer renderer = new AccountingLineTableFooterRenderer();
        renderer.render(pageContext, this);
    }

    /**
     * Removes the document from the tag
     * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
     */
    @Override
    public void release() {
        super.release();
        resetTag();
    }
}
