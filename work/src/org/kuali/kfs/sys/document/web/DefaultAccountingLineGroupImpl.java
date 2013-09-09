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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.TotalDefinition;
import org.kuali.kfs.sys.document.web.renderers.CellCountCurious;
import org.kuali.kfs.sys.document.web.renderers.CollectionPropertiesCurious;
import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;
import org.kuali.kfs.sys.document.web.renderers.GroupTitleLineRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.kfs.sys.document.web.renderers.RepresentedCellCurious;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This represents an accounting line group in renderable state
 */
public class DefaultAccountingLineGroupImpl implements AccountingLineGroup {
    protected AccountingLineGroupDefinition groupDefinition;
    protected JspFragment importLineOverride;
    protected String collectionPropertyName;
    protected List<? extends AccountingLineRenderingContext> containers;
    protected AccountingDocument accountingDocument;
    protected int cellCount = 0;
    protected int arbitrarilyHighIndex;
    protected Map<String, Object> displayedErrors;
    protected Map<String, Object> displayedWarnings;
    protected Map<String, Object> displayedInfo;
    protected boolean canEdit;
    protected String collectionItemPropertyName;
    protected List errorKeys;

    /**
     * Constructs a DefaultAccountingLineGroupImpl
     */
    public DefaultAccountingLineGroupImpl() {}

    /**
     * Initializes the DefaultAccountingLineGroupImpl
     *
     * @param groupDefinition the data dictionary group definition for this accounting line group
     * @param accountingDocument the document which owns or will own the accounting line being rendered
     * @param containers the containers within this group
     * @param collectionPropertyName the property name of the collection of accounting lines owned by this group
     * @param errors a List of errors keys for errors on the page
     * @param displayedErrors a Map of errors that have already been displayed
     * @param canEdit determines if the page can be edited or not
     */
    public void initialize(AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, List<RenderableAccountingLineContainer> containers, String collectionPropertyName, String collectionItemPropertyName, Map<String, Object> displayedErrors, Map<String, Object> displayedWarnings, Map<String, Object> displayedInfo, boolean canEdit) {
        this.groupDefinition = groupDefinition;
        this.accountingDocument = accountingDocument;
        this.containers = containers;
        this.collectionPropertyName = collectionPropertyName;
        this.collectionItemPropertyName = collectionItemPropertyName;
        this.displayedErrors = displayedErrors;
        this.displayedWarnings = displayedWarnings;
        this.displayedInfo = displayedInfo;
        this.canEdit = canEdit;
    }

    /**
     * Renders the whole of this accounting line group
     *
     * @param pageContext the page context to render to
     * @param parentTag the AccountingLinesTag that is requesting this rendering
     */
    @Override
    public void renderEverything(PageContext pageContext, Tag parentTag) throws JspException {
        if (groupDefinition.isHeaderRendering()) {
            renderGroupHeader(pageContext, parentTag);
        }
        renderAccountingLineContainers(pageContext, parentTag);

        if (shouldRenderTotals()) {
            renderTotals(pageContext, parentTag);
        }
    }

    /**
     * Checks that there are either source or target accounting lines available and that at least one totals definition has been set up for this accounting line group
     * @see org.kuali.kfs.sys.document.web.AccountingLineGroup#shouldRenderTotals()
     */
    @Override
    public boolean shouldRenderTotals() {
        boolean renderTotals = !accountingDocument.getSourceAccountingLines().isEmpty() || !accountingDocument.getTargetAccountingLines().isEmpty();
        renderTotals &= groupDefinition.getTotals() != null && groupDefinition.getTotals().size() > 0;
        return renderTotals;
    }

    /**
     * Finds the maximum number of cells in the accounting line table row
     *
     * @param rows the rows which are being rendered
     * @return the maximum number of cells to render
     */
    @Override
    public int getWidthInCells() {
        if (groupDefinition.getForceColumnCount() > 0) {
            return groupDefinition.getForceColumnCount();
        }
        if (cellCount > 0) {
            return cellCount;
        }

        int max = 0;
        for (AccountingLineRenderingContext line : containers) {
            if (line.getRenderableCellCount() > max) {
                max = line.getRenderableCellCount();
            }
        }
        cellCount = max;
        return cellCount;
    }

    /**
     * Renders the group header/import line for the accounting line group. Renders importLineOverride if present; otherwise, uses
     * ImportLineRenderer to do its dirty work
     *
     * @param accountingLineGroupDefinition the accounting line group definition
     * @param rows the rows to render
     * @throws JspException thrown if something goes wrong in rendering the header
     */
    protected void renderGroupHeader(PageContext pageContext, Tag parentTag) throws JspException {
        if (importLineOverride != null) {
            try {
                importLineOverride.invoke(pageContext.getOut());
            }
            catch (IOException ioe) {
                throw new JspException("Could not render import line override fragment", ioe);
            }
        }
        else {
            GroupTitleLineRenderer groupTitleLineRenderer = new GroupTitleLineRenderer();
            groupTitleLineRenderer.setAccountingLineGroupDefinition(groupDefinition);
            groupTitleLineRenderer.setCellCount(getWidthInCells());
            groupTitleLineRenderer.setLineCollectionProperty(collectionPropertyName);
            groupTitleLineRenderer.setAccountingDocument(accountingDocument);
            groupTitleLineRenderer.setCanEdit(canEdit);

            boolean isGroupEditable = groupDefinition.getAccountingLineAuthorizer().isGroupEditable(accountingDocument, containers, GlobalVariables.getUserSession().getPerson());
            groupTitleLineRenderer.overrideCanUpload(groupDefinition.isImportingAllowed() && isGroupEditable);
            groupTitleLineRenderer.setGroupActionsRendered(!this.isDocumentEnrouted() && isGroupEditable);

            groupTitleLineRenderer.render(pageContext, parentTag);
            groupTitleLineRenderer.clear();
        }

        renderErrors(pageContext, parentTag);
    }

    /**
     * Renders any errors for the group
     * @param pageContext the page context where the errors will be rendered on
     * @param parentTag the parent tag requesting the rendering
     */
    protected void renderErrors(PageContext pageContext, Tag parentTag) throws JspException {
        GroupErrorsRenderer errorRenderer = getErrorRenderer();
        errorRenderer.setErrorKeyMatch(groupDefinition.getErrorKey());
        errorRenderer.setColSpan(getWidthInCells());
        errorRenderer.render(pageContext, parentTag);

        moveListToMap(errorRenderer.getErrorsRendered(), getDisplayedErrors());
        moveListToMap(errorRenderer.getWarningsRendered(), getDisplayedWarnings());
        moveListToMap(errorRenderer.getInfoRendered(), getDisplayedInfo());

        errorRenderer.clear();
    }

    /**
     * Moves all of the members of theList into theMap as a key with the value always being the String "true"
     * @param theList the List of Strings to be keys
     * @param theMap the Map of keys and values
     */
    protected void moveListToMap(List<String> theList, Map theMap) {
        for (String s : theList) {
            theMap.put(s, "true");
        }
    }

    /**
     * @return get a GroupErrorsRenderer in a way which can be overridden
     */
    protected GroupErrorsRenderer getErrorRenderer() {
        return new GroupErrorsRenderer();
    }

    /**
     * Renders the accounting line containers
     *
     * @param containers the containers to render
     * @throws JspException thrown if rendering goes badly
     */
    protected void renderAccountingLineContainers(PageContext pageContext, Tag parentTag) throws JspException {
        for (AccountingLineRenderingContext container : containers) {
            container.populateValuesForFields();
            container.populateWithTabIndexIfRequested(arbitrarilyHighIndex);
            container.renderElement(pageContext, parentTag, container);
        }
    }

    /**
     * Renders all of the totals required by the group total definition
     *
     * @param groupDefinition the accounting line view group definition
     * @param lines the lines that will be rendered - so we can count how many cells we're rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderTotals(PageContext pageContext, Tag parentTag) throws JspException {
        int cellCount = getWidthInCells();

        List<? extends TotalDefinition> groupTotals = groupDefinition.getTotals();
        for (TotalDefinition definition : groupTotals) {
            if (definition instanceof NestedFieldTotaling) {
                NestedFieldTotaling nestedFieldTotaling = definition;

                if (nestedFieldTotaling.isNestedProperty()) {
                    int index = groupTotals.indexOf(definition);
                    AccountingLineRenderingContext container = this.containers.get(index);
                    String containingObjectPropertyName = container.getAccountingLineContainingObjectPropertyName();
                    nestedFieldTotaling.setContainingPropertyName(containingObjectPropertyName);
                }
            }

            Renderer renderer = definition.getTotalRenderer();
            if (renderer instanceof CellCountCurious) {
                ((CellCountCurious) renderer).setCellCount(cellCount);
            }

            if (renderer instanceof RepresentedCellCurious) {
                RepresentedCellCurious representedCellCurious = ((RepresentedCellCurious) renderer);
                int columnNumberOfRepresentedCell = this.getRepresentedColumnNumber(representedCellCurious.getRepresentedCellPropertyName());
                representedCellCurious.setColumnNumberOfRepresentedCell(columnNumberOfRepresentedCell);
            }

            if (renderer instanceof CollectionPropertiesCurious) {
                ((CollectionPropertiesCurious)renderer).setCollectionProperty(this.collectionPropertyName);
                ((CollectionPropertiesCurious)renderer).setCollectionItemProperty(this.collectionItemPropertyName);
            }

            renderer.render(pageContext, parentTag);
            renderer.clear();
        }
    }

    /**
     * get the column number of the tabel cell with the given property name in an accounting line table
     *
     * @param propertyName the given property name that is associated with the column
     * @return the column number of the tabel cell with the given property name in an accounting line table
     */
    protected int getRepresentedColumnNumber(String propertyName) {
        for (AccountingLineRenderingContext container : containers) {
            List<AccountingLineTableRow> tableRows = container.getRows();

            for (AccountingLineTableRow row : tableRows) {
                List<AccountingLineTableCell> tableCells = row.getCells();
                int cumulativeDisplayCellCount = 0;

                for (AccountingLineTableCell cell : tableCells) {
                    cumulativeDisplayCellCount += cell.getColSpan();
                    List<RenderableElement> fields = cell.getRenderableElement();

                    for (RenderableElement field : fields) {
                        if (field instanceof ElementNamable == false) {
                            continue;
                        }

                        if (((ElementNamable) field).getName().equals(propertyName)) {
                            return cumulativeDisplayCellCount;
                        }
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Sets the cellCount attribute value.
     *
     * @param cellCount The cellCount to set.
     */
    @Override
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    /**
     * Sets the importLineOverride attribute value.
     *
     * @param importLineOverride The importLineOverride to set.
     */
    @Override
    public void setImportLineOverride(JspFragment importLineOverride) {
        this.importLineOverride = importLineOverride;
    }

    /**
     * Sets the form's arbitrarily high tab index
     *
     * @param arbitrarilyHighIndex the index to set
     */
    @Override
    public void setArbitrarilyHighIndex(int arbitrarilyHighIndex) {
        this.arbitrarilyHighIndex = arbitrarilyHighIndex;
    }

    /**
     * Gets the displayedWarnings attribute.
     * @return Returns the displayedWarnings.
     */
    public Map getDisplayedWarnings() {
        return displayedWarnings;
    }

    /**
     * Gets the displayedInfo attribute.
     * @return Returns the displayedInfo.
     */
    public Map getDisplayedInfo() {
        return displayedInfo;
    }

    /**
     * Gets the errorKeys attribute.
     * @return Returns the errorKeys.
     */
    @Override
    public List getErrorKeys() {
        return errorKeys;
    }

    /**
     * Sets the errorKeys attribute value.
     * @param errorKeys The errorKeys to set.
     */
    @Override
    public void setErrorKeys(List errorKeys) {
        this.errorKeys = errorKeys;
    }

    /**
     * Determines if the current document is enrouted
     */
    private boolean isDocumentEnrouted() {
        WorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();

        return !workflowDocument.isInitiated() && !workflowDocument.isSaved();
    }

    /**
     * Determines if there is more than one editable line in this group; if so, then it allows deleting
     */
    @Override
    public void updateDeletabilityOfAllLines() {
        if (this.isDocumentEnrouted()) {
            if (hasEnoughAccountingLinesForDelete()) {
                for (AccountingLineRenderingContext accountingLineRenderingContext : containers) {
                    if (accountingLineRenderingContext.isEditableLine()) {
                        accountingLineRenderingContext.makeDeletable();
                    }
                }
            }
        } else {
            // we're pre-route - everybody is deletable!
            for (AccountingLineRenderingContext accountingLineRenderingContext : containers) {
                accountingLineRenderingContext.makeDeletable();
            }
        }
    }

    /**
     * Determines if there are enough accounting lines in this group for delete buttons to be present
     * @return true if there are enough accounting lines for a delete, false otherwise
     */
    protected boolean hasEnoughAccountingLinesForDelete() {
        // 1. get the count of how many accounting lines are editable
        int editableLineCount = 0;
        for (AccountingLineRenderingContext accountingLineRenderingContext : containers) {
            if (!accountingLineRenderingContext.isNewLine() && accountingLineRenderingContext.isEditableLine()) {
                editableLineCount += 1;
            }
            if (editableLineCount == 2)
             {
                return true; // we know we're good...skip out early
            }
        }
        return false;
    }

    /**
     * Gets the collectionItemPropertyName attribute.
     * @return Returns the collectionItemPropertyName.
     */
    @Override
    public String getCollectionItemPropertyName() {
        return collectionItemPropertyName;
    }

    /**
     * Gets the groupDefinition attribute.
     * @return Returns the groupDefinition.
     */
    public AccountingLineGroupDefinition getGroupDefinition() {
        return groupDefinition;
    }

    /**
     * Sets the groupDefinition attribute value.
     * @param groupDefinition The groupDefinition to set.
     */
    public void setGroupDefinition(AccountingLineGroupDefinition groupDefinition) {
        this.groupDefinition = groupDefinition;
    }

    /**
     * Gets the displayedErrors attribute.
     * @return Returns the displayedErrors.
     */
    public Map getDisplayedErrors() {
        return displayedErrors;
    }

    /**
     * Sets the displayedErrors attribute value.
     * @param displayedErrors The displayedErrors to set.
     */
    public void setDisplayedErrors(Map displayedErrors) {
        this.displayedErrors = displayedErrors;
    }

    /**
     * Gets the collectionPropertyName attribute.
     * @return Returns the collectionPropertyName.
     */
    public String getCollectionPropertyName() {
        return collectionPropertyName;
    }

    /**
     * Sets the collectionPropertyName attribute value.
     * @param collectionPropertyName The collectionPropertyName to set.
     */
    public void setCollectionPropertyName(String collectionPropertyName) {
        this.collectionPropertyName = collectionPropertyName;
    }

    /**
     * Sets the collectionItemPropertyName attribute value.
     * @param collectionItemPropertyName The collectionItemPropertyName to set.
     */
    public void setCollectionItemPropertyName(String collectionItemPropertyName) {
        this.collectionItemPropertyName = collectionItemPropertyName;
    }


    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }
}
