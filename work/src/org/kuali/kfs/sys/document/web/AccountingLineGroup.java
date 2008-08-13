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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.TotalDefinition;
import org.kuali.kfs.sys.document.web.renderers.CellCountCurious;
import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;
import org.kuali.kfs.sys.document.web.renderers.ImportLineRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;

/**
 * This represents an accounting line group in renderable state
 */
public class AccountingLineGroup {
    private AccountingLineGroupDefinition groupDefinition;
    private JspFragment importLineOverride;
    private Map editModes;
    private String collectionPropertyName;
    private List<RenderableAccountingLineContainer> containers;
    private int cellCount = 0;
    private int renderTabIndex;
    private int arbitrarilyHighIndex;
    private List errors;
    private Map displayedErrors;

    /**
     * Constructs a AccountingLineGroup
     * @param groupDefinition the data dictionary group definition for this accounting line group
     * @param containers the containers within this group
     * @param collectionPropertyName the property name of the collection of accounting lines owned by this group
     * @param editModes the Map of edit modes
     * @param errors a List of errors keys for errors on the page
     * @param displayedErrors a Map of errors that have already been displayed
     */
    public AccountingLineGroup(AccountingLineGroupDefinition groupDefinition, List<RenderableAccountingLineContainer> containers, String collectionPropertyName, Map editModes, List errors, Map displayedErrors) {
        this.groupDefinition = groupDefinition;
        this.containers = containers;
        this.collectionPropertyName = collectionPropertyName;
        this.editModes = editModes;
        this.errors = errors;
        this.displayedErrors = displayedErrors;
    }
    
    /**
     * Renders the whole of this accounting line group
     * @param pageContext the page context to render to
     * @param parentTag the AccountingLineTag that is requesting this rendering
     */
    public void renderEverything(PageContext pageContext, Tag parentTag) throws JspException {
        renderGroupHeader(pageContext, parentTag);
        renderAccountingLineContainers(pageContext, parentTag);
        renderTotals(pageContext, parentTag);
    }
    
    /**
     * Finds the maximum number of cells in the accounting line table row
     * @param rows the rows which are being rendered
     * @return the maximum number of cells to render
     */
    public int getWidthInCells() {
        if (groupDefinition.getForceColumnCount() > 0) return groupDefinition.getForceColumnCount();
        if (cellCount > 0) return cellCount;
        
        int max = 0;
        for (RenderableAccountingLineContainer line : containers) {
            if (line.getCellCount() > max) {
                max = line.getCellCount();
            }
        }
        cellCount = max;
        return cellCount;
    }
    
    /**
     * Renders the group header/import line for the accounting line group.  Renders importLineOverride if present; otherwise, uses ImportLineRenderer to do its dirty work 
     * @param accountingLineGroupDefinition the accounting line group definition
     * @param rows the rows to render
     * @throws JspException thrown if something goes wrong in rendering the header
     */
    protected void renderGroupHeader(PageContext pageContext, Tag parentTag) throws JspException {
        if (importLineOverride != null) {
            try {
                importLineOverride.invoke(pageContext.getOut());
            } catch (IOException ioe) {
                throw new JspException("Could not render import line override fragment", ioe);
            }
        } else {
            ImportLineRenderer importLineRenderer = new ImportLineRenderer();
            importLineRenderer.setAccountingLineGroupDefinition(groupDefinition);
            importLineRenderer.setCellCount(getWidthInCells());
            importLineRenderer.setEditModes(editModes);
            importLineRenderer.setLineCollectionProperty(collectionPropertyName);
            importLineRenderer.render(pageContext, parentTag);
            importLineRenderer.clear();
        }
        
        if (errors != null && errors.size() > 0) {
            GroupErrorsRenderer errorRenderer = new GroupErrorsRenderer();
            errorRenderer.setErrorKeyMatch(groupDefinition.getErrorKey());
            errorRenderer.setColSpan(getWidthInCells());
            errorRenderer.setErrorPropertyList(errors);
            errorRenderer.setSectionTitle(groupDefinition.getGroupLabel());
            errorRenderer.render(pageContext, parentTag);
            
            for (String displayedErrorKey : errorRenderer.getErrorsRendered()) {
                displayedErrors.put(displayedErrorKey, "true");
            }
            
            errorRenderer.clear();
        }
    }
    
    /**
     * Renders the accounting line containers
     * @param containers the containers to render
     * @throws JspException thrown if rendering goes badly
     */
    protected void renderAccountingLineContainers(PageContext pageContext, Tag parentTag) throws JspException {
        for (RenderableAccountingLineContainer container : containers) {
            container.populateValuesForFields();
            int[] passIndexes = generateNextTabIndexes();
            container.populateWithTabIndexIfRequested(passIndexes, arbitrarilyHighIndex);
            container.renderElement(pageContext, parentTag, container);
            setStartingTabIndex(passIndexes[passIndexes.length - 1] + 1);
        }
    }
    
    /**
     * Based on the renderTabIndex and the number of passes needed, defined in the group definition
     * @return an array of pass indexes
     */
    public int[] generateNextTabIndexes() {
        int[] result = new int[groupDefinition.getTabIndexPasses()];
        int i = 0;
        while (i < result.length) {
            result[i] = renderTabIndex + i;
            i += 1;
        }
        return result;
    }
    
    /**
     * Renders all of the totals required by the group total definition
     * @param groupDefinition the accounting line view group definition
     * @param lines the lines that will be rendered - so we can count how many cells we're rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderTotals(PageContext pageContext, Tag parentTag) throws JspException {
        int cellCount = getWidthInCells();
        for (TotalDefinition definition : groupDefinition.getTotals()) {
            Renderer renderer = definition.getTotalRenderer();
            if (renderer instanceof CellCountCurious) {
                ((CellCountCurious)renderer).setCellCount(cellCount);
            }
            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }

    /**
     * Sets the cellCount attribute value.
     * @param cellCount The cellCount to set.
     */
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    /**
     * Sets the importLineOverride attribute value.
     * @param importLineOverride The importLineOverride to set.
     */
    public void setImportLineOverride(JspFragment importLineOverride) {
        this.importLineOverride = importLineOverride;
    }
    
    /**
     * Sets the tab index before rendering
     * @param startingTabIndex the tab index starting with the first usuable tab index
     */
    public void setStartingTabIndex(int startingTabIndex) {
        renderTabIndex = startingTabIndex;
    }
    
    /**
     * @return the next available tab index after all rendering has been accomplished 
     */
    public int getEndingTabIndex() {
        return renderTabIndex;
    }
    
    public void setArbitrarilyHighIndex(int arbitrarilyHighIndex) {
        this.arbitrarilyHighIndex = arbitrarilyHighIndex;
    }

    /**
     * Gets the errors attribute. 
     * @return Returns the errors.
     */
    public List getErrorKeys() {
        return errors;
    }

    /**
     * Sets the errors attribute value.
     * @param errors The errors to set.
     */
    public void setErrorKeys(List errors) {
        this.errors = errors;
    }
    
}
