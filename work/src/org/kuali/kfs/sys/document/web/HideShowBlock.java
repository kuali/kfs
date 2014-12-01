/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.web;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewHideShowLinesDefinition;
import org.kuali.kfs.sys.document.web.renderers.HideShowBlockRenderer;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.Field;

/**
 * A renderable element that renders child elements within a div that can be hidden or displayed
 */
public class HideShowBlock implements RenderableElement {
    private List<AccountingLineTableRow> contentRows; 
    private AccountingLineViewHideShowLinesDefinition definition;
    private AccountingLineRenderingContext renderingContext;
    private String tabKey;

    /**
     * Has child table rows add any fields they know about to the List
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFields(java.util.List)
     * 
     * KRAD Conversion: Customization of adding the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
       for (AccountingLineTableRow row : contentRows) {
           row.appendFields(fields);
       }
    }

    /**
     * This is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }

    /**
     * Checks if all of the child rows are empty or not; if one isn't empty, then this isn't empty
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        for (AccountingLineTableRow row : contentRows) {
            if (!row.isEmpty()) return false;
        }
        return true;
    }

    /**
     * Checks if all the child rows are hidden; if so, then no point in showing this...
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        for (AccountingLineTableRow row : contentRows) {
            if (!row.isHidden()) return false;
        }
        return true;
    }

    /**
     * Has child rows populate with the tab index
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        for (AccountingLineTableRow row : contentRows) {
            row.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }

    /**
     * Uses a HideShowBlockRenderer to render this element
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        this.renderingContext = renderingContext;
        
        HideShowBlockRenderer renderer = new HideShowBlockRenderer();
        renderer.setHideShowBlock(this);
        renderer.render(pageContext, parentTag);
        
        this.renderingContext = null;
    }
    
    /**
     * Forces all children rows to render themselves
     * @param pageContext the pageContext to render to
     * @param parentTag the tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    public void renderChildRows(PageContext pageContext, Tag parentTag) throws JspException {
        for (AccountingLineTableRow row : contentRows) {
            row.renderElement(pageContext, parentTag, renderingContext);
        }
    }

    /**
     * Gets the contentRows attribute. 
     * @return Returns the contentRows.
     */
    public List<AccountingLineTableRow> getContentRows() {
        return contentRows;
    }

    /**
     * Sets the contentRows attribute value.
     * @param contentRows The contentRows to set.
     */
    public void setContentRows(List<AccountingLineTableRow> contentRows) {
        this.contentRows = contentRows;
    }

    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewHideShowLinesDefinition definition) {
        this.definition = definition;
    }

    /** 
     * @return the tab key for this hide/show block
     */
    public String getTabKey() {
        if (tabKey == null) {
            tabKey = WebUtils.generateTabKey(renderingContext.getGroupLabel()+definition.getLabel()) + "-" + renderingContext.getAccountingLinePropertyPath().replaceAll("\\.","-").replaceAll("\\[", "(").replaceAll("\\]", ")");
        }
        return tabKey;
    }
    
    /**
     * @return the current tab state
     */
    public String getTabState() {
        String tabState = renderingContext.getTabState(getTabKey());
        return StringUtils.isNotBlank(tabState) ? tabState : "CLOSE";
    }
    
    /**
     * Determines if this tab should currently be showing or not
     * @return true if the tab shows its contents; false otherwise
     */
    public boolean isShowing() {
        return getTabState().equals("OPEN");
    }
    
    /**
     * @return the label for this hide/show block
     */
    public String getLabel() {
        return definition.getLabel();
    }
    
    /**
     * @return the concatenation of the group label and this block's label
     */
    public String getFullLabel() {
        return renderingContext.getGroupLabel()+(!StringUtils.isBlank(definition.getLabel()) ? " "+definition.getLabel() : " Hide/Show Block");
    }
}
