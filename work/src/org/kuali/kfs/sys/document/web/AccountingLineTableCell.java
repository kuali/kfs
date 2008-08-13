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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.core.web.ui.Field;
import org.kuali.kfs.sys.document.web.renderers.TableCellRenderer;
import org.kuali.kfs.sys.document.web.renderers.TableHeaderRenderer;

/**
 * A class which represents a number of fields held in a table cell.
 */
public class AccountingLineTableCell implements RenderableElement {
    private int rowSpan = 1;
    private int colSpan = 1;
    private List<RenderableElement> renderableElements = new ArrayList<RenderableElement>();
    private boolean rendersAsHeader = false;
    private AccountingLineRenderingContext renderingContext;
    
    /**
     * Gets the colSpan attribute. 
     * @return Returns the colSpan.
     */
    public int getColSpan() {
        return colSpan;
    }
    /**
     * Sets the colSpan attribute value.
     * @param colSpan The colSpan to set.
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * Gets the rowSpan attribute. 
     * @return Returns the rowSpan.
     */
    public int getRowSpan() {
        return rowSpan;
    }
    /**
     * Sets the rowSpan attribute value.
     * @param rowSpan The rowSpan to set.
     */
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }
    
    /**
     * Gets the renderableElement attribute. 
     * @return Returns the renderableElement.
     */
    public List<RenderableElement> getRenderableElement() {
        return renderableElements;
    }
    
    /**
     * Sets the renderableElement attribute value.
     * @param renderableElement The renderableElement to set.
     */
    public void setRenderableElement(List<RenderableElement> renderableElement) {
        this.renderableElements = renderableElement;
    }
    
    /**
     * Adds a renderable element to the elements this cell can render
     * @param element another element to render within this cell
     */
    public void addRenderableElement(RenderableElement element) {
        renderableElements.add(element);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        for (RenderableElement element : renderableElements) {
            if (!element.isHidden()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * This is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        for (RenderableElement element : renderableElements) {
            if (!element.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets the rendersAsHeader attribute. 
     * @return Returns the rendersAsHeader.
     */
    public boolean isRendersAsHeader() {
        return rendersAsHeader;
    }
    /**
     * Sets the rendersAsHeader attribute value.
     * @param rendersAsHeader The rendersAsHeader to set.
     */
    public void setRendersAsHeader(boolean rendersAsHeader) {
        this.rendersAsHeader = rendersAsHeader;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        TableCellRenderer renderer;
        if (isRendersAsHeader()) {
            renderer = new TableHeaderRenderer();
        } else {
            renderer = new TableCellRenderer();
        }
        this.renderingContext = renderingContext;
        renderer.setCell(this);
        renderer.render(pageContext, parentTag);
        renderer.clear();
        this.renderingContext = null;
    }
    
    /**
     * Requests that this cell render all of its children cells
     * @param pageContext the page context of the rendering
     * @param parentTag the parent tag requesting all this rendering
     * @param accountingLine the accounting line getting rendered
     * @param accountingLineProperty the property to get from the form to the accounting line
     * @throws JspException Oh.  Shoot.  Something went...wrong.
     */
    public void renderChildrenElements(PageContext pageContext, Tag parentTag) throws JspException {
        for (RenderableElement element : renderableElements) {
            element.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * Goes through fields, appending field names
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFields(List<Field> fields) {
        for (RenderableElement element : renderableElements) {
            element.appendFields(fields);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int[] passIndexes, int reallyHighIndex) {
        for (RenderableElement element : renderableElements) {
            element.populateWithTabIndexIfRequested(passIndexes, reallyHighIndex);
        }
    }
}
