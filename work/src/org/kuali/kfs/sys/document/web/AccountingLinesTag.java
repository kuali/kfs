/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.renderers.AccountingLineTableFooterRenderer;
import org.kuali.kfs.sys.document.web.renderers.AccountingLineTableHeaderRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This tag...how to describe what it does?  It takes these groups of accounting lines and, you know,
 * it, um...it renders them.
 */
public class AccountingLinesTag extends BodyTagSupport {
    private AccountingDocument document;
    private KualiAccountingDocumentFormBase form;
    private List<AccountingLineGroup> groupsToRender;

    /**
     * Finds the KualiForm wherever it may be hiding
     * @return the KualiForm
     */
    public KualiAccountingDocumentFormBase getForm() {
        if (form == null) {
            form = SpringContext.getBean(AccountingLineRenderingService.class).findForm(pageContext);
        }
        return form;
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
        document = null;
        groupsToRender = null;
        form = null;
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
            group.setArbitrarilyHighIndex(arbitrarilyHighIndex);
            group.renderEverything(pageContext, this);
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
