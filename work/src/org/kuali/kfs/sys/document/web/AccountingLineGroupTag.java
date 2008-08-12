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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.datadictionary.TotalDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.renderers.CellCountCurious;
import org.kuali.kfs.sys.document.web.renderers.ImportLineRenderer;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * Tag that is responsible for rendering an accounting line group
 */
public class AccountingLineGroupTag extends TagSupport {
    private String collectionPropertyName;
    private String collectionItemPropertyName;
    private String newLinePropertyName;
    private String attributeGroupName;
    private JspFragment importLineOverride;
    private AccountingLineGroupDefinition groupDefinition;
    private AccountingLineGroup group;
    
    /**
     * Gets the attributeGroupName attribute. 
     * @return Returns the attributeGroupName.
     */
    public String getAttributeGroupName() {
        return attributeGroupName;
    }
    /**
     * Sets the attributeGroupName attribute value.
     * @param attributeGroupName The attributeGroupName to set.
     */
    public void setAttributeGroupName(String attributeGroup) {
        this.attributeGroupName = attributeGroup;
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
    public void setCollectionPropertyName(String collectionProperties) {
        this.collectionPropertyName = collectionProperties;
    }
    /**
     * Gets the newLinePropertyName attribute. 
     * @return Returns the newLinePropertyName.
     */
    public String getNewLinePropertyName() {
        return newLinePropertyName;
    }
    /**
     * Sets the newLinePropertyName attribute value.
     * @param newLinePropertyName The newLinePropertyName to set.
     */
    public void setNewLinePropertyName(String newLineProperty) {
        this.newLinePropertyName = newLineProperty;
    }
    
    /**
     * Gets the importLineOverride attribute. 
     * @return Returns the importLineOverride.
     */
    public JspFragment getImportLineOverride() {
        return importLineOverride;
    }
    /**
     * Sets the importLineOverride attribute value.
     * @param importLineOverride The importLineOverride to set.
     */
    public void setImportLineOverride(JspFragment importLineOverride) {
        this.importLineOverride = importLineOverride;
    }
    /**
     * Gets the collectionItemPropertyName attribute. 
     * @return Returns the collectionItemPropertyName.
     */
    public String getCollectionItemPropertyName() {
        return collectionItemPropertyName;
    }
    /**
     * Sets the collectionItemPropertyName attribute value.
     * @param collectionItemPropertyName The collectionItemPropertyName to set.
     */
    public void setCollectionItemPropertyName(String collectionItemPropertyName) {
        if (StringUtils.isBlank(collectionItemPropertyName)) {
            collectionItemPropertyName = generateItemPropertyFromCollectionNameTheDumbWay(collectionPropertyName);
        }
        this.collectionItemPropertyName = collectionItemPropertyName;
    }
    
    /**
     * Automagically generates teh name of the collection item property from the collection property by taking any extra "s" off the end.  Note:
     * I never claimed this method was smart.  You'd be surprised at how often it'll work, though.
     * @param collectionName the collection name to generate the collection item name from
     * @return the collection item property name
     */
    protected String generateItemPropertyFromCollectionNameTheDumbWay(String collectionName) {
        int subStringEnd = collectionName.length();
        if (collectionName.endsWith("s")) {
            subStringEnd -= 1;
        }
        return collectionName.substring(0, subStringEnd);
    }
    
    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();
        List<RenderableAccountingLineContainer> containers = generateContainersForAllLines();
        group = new AccountingLineGroup(groupDefinition, containers, collectionPropertyName, ((AccountingLineTag)getParent()).getEditModes());
        if (getParent() instanceof AccountingLineTag) {
            ((AccountingLineTag)getParent()).addGroupToRender(group);
            resetTag();
        }
        return Tag.SKIP_BODY;
    }
    
    /**
     * If our parent isn't AccountingLineTag, then we should render all the group
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();
        if (!(getParent() instanceof AccountingLineTag)) {
            group.renderEverything(pageContext, getParent());
            resetTag();
        }
        return Tag.EVAL_PAGE;
    }
    
    /**
     * Clears out any state variables on the tag
     */
    protected void resetTag() {
        collectionPropertyName = null;
        collectionItemPropertyName = null;
        newLinePropertyName = null;
        attributeGroupName = null;
        importLineOverride = null;
        groupDefinition = null;
        group = null;
    }
    
    /**
     * Gets the correct accounting line group definition for this 
     * @return the accounting line group definition to be used by this tag
     */
    protected AccountingLineGroupDefinition getGroupDefinition() {
        if (groupDefinition == null) {
            final String documentTypeClassName = ((AccountingLineTag)this.getParent()).getDocument().getClass().getName();
            final FinancialSystemTransactionalDocumentEntry documentEntry = (FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(documentTypeClassName);
            final Map<String, AccountingLineGroupDefinition> groupDefinitions = documentEntry.getAccountingLineGroups();
            groupDefinition = groupDefinitions.get(attributeGroupName);
        }
        return groupDefinition;
    }
    
    /**
     * Creates a List of renderable elements, with all rendering transformations accomplished, to be rendered 
     * @param groupDefinition the data dictionary definition of the accounting line group this line is in
     * @param accountingLine the accounting line itself
     * @param newLine true if the line is new and not added yet to the document, false otherwise
     * @return a List of AccountingLineTableRows to render
     */
    protected List<AccountingLineTableRow> getRenderableElementsForLine(AccountingLineGroupDefinition groupDefinition, AccountingLine accountingLine, boolean newLine) {
        List<TableJoining> layoutElements = groupDefinition.getAccountingLineView().getAccountingLineLayoutElements(accountingLine.getClass());
        AccountingLineRenderingService renderingService = SpringContext.getBean(AccountingLineRenderingService.class);
        renderingService.performPreTablificationTransformations(layoutElements, groupDefinition, ((AccountingLineTag)getParent()).getDocument(), accountingLine, newLine, ((AccountingLineTag)getParent()).getForm().getUnconvertedValues());
        List<AccountingLineTableRow> renderableElements = renderingService.tablify(layoutElements);
        renderingService.performPostTablificationTransformations(renderableElements, groupDefinition, ((AccountingLineTag)getParent()).getDocument(), accountingLine, newLine);
        return renderableElements;
    }
    
    /**
     * @return the new accounting line from the form
     */
    protected AccountingLine getNewAccountingLine() {
        return (AccountingLine)ObjectUtils.getPropertyValue(((AccountingLineTag)getParent()).getForm(), newLinePropertyName);
    }
    
    /**
     * @return the collection of accounting lines that this tag is supposed to render
     */
    protected List<AccountingLine> getAccountingLineCollection() {
        return (List<AccountingLine>)ObjectUtils.getPropertyValue(((AccountingLineTag)getParent()).getForm(), collectionPropertyName);
    }
    
    /**
     * @return a List of accounting line table rows to be rendered for all the accounting lines available for rendering within the group
     */
    protected List<RenderableAccountingLineContainer> generateContainersForAllLines() {
        List<RenderableAccountingLineContainer> containers = new ArrayList<RenderableAccountingLineContainer>();
        
        final AccountingLineGroupDefinition groupDefinition = getGroupDefinition();
        final AccountingDocument document = ((AccountingLineTag)getParent()).getDocument();
        final FinancialSystemUser currentUser = GlobalVariables.getUserSession().getFinancialSystemUser();
        
        if (!shouldRenderAsReadOnly() && !StringUtils.isBlank(newLinePropertyName)) {
            containers.add(buildContainerForLine(groupDefinition, document, getNewAccountingLine(), currentUser, null));
        }
        int count = 0;
        for (AccountingLine accountingLine : getAccountingLineCollection()) {
            containers.add(buildContainerForLine(groupDefinition, document, accountingLine, currentUser, new Integer(count)));
            count += 1;
        }
        return containers;
    }
    
    /**
     * Builds an accounting line container for a given accounting line to render
     * @param groupDefinition the data dictionary definition of the group this line is a part of
     * @param accountingDocument the accounting document the line is or will someday live on
     * @param accountingLine the accounting line to render
     * @param currentUser the currently logged in user
     * @param count the count of this line within the collection represented by the group; null if this is a new line for the group
     * @return the container created
     */
    protected RenderableAccountingLineContainer buildContainerForLine(AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, FinancialSystemUser currentUser, Integer count) {
        RenderableAccountingLineContainer container = new RenderableAccountingLineContainer();
        container.setAccountingLine(accountingLine);
        String accountingLinePropertyName = count == null ? newLinePropertyName : collectionItemPropertyName+"["+count.toString()+"]";
        container.setAccountingLineProperty(accountingLinePropertyName);
        container.setActions(groupDefinition.getAccountingLineAuthorizer().getActions(accountingDocument, accountingLine, accountingLinePropertyName, (count == null ? -1 : count.intValue()), currentUser, ((AccountingLineTag)getParent()).getEditModes()));
        container.setNewLine(count == null);
        container.setRows(getRenderableElementsForLine(groupDefinition, accountingLine, (count == null)));
        KualiAccountingDocumentFormBase form = (KualiAccountingDocumentFormBase)((AccountingLineTag)getParent()).getForm();
        container.setRenderHelp(form.isFieldLevelHelpEnabled());
        container.setShowDetails(!form.isHideDetails());
        return container;
    }
    
    /**
     * Determines if this group should be rendered as read only 
     * @return true if the group should be rendered as read only, false otherwise
     */
    protected boolean shouldRenderAsReadOnly() {
        return ((AccountingLineTag)getParent()).getEditModes().containsKey(AuthorizationConstants.EditMode.VIEW_ONLY);
    }

    /**
     * Clean up state held by this tag
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    @Override
    public void release() {
        super.release();
        resetTag();
    }
}
