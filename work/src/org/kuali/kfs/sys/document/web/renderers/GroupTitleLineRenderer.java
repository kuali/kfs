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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewActionDefinition;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.taglib.html.KNSFileTag;
import org.kuali.rice.kns.web.taglib.html.KNSImageTag;

/**
 * Renders the standard group header/import line
 */
public class GroupTitleLineRenderer implements Renderer, CellCountCurious {
    private int titleCellSpan = 4;
    private int cellCount = 1;
    private AccountingLineGroupDefinition accountingLineGroupDefinition;
    private AccountingDocument accountingDocument;
    private String lineCollectionProperty;
    private KNSFileTag scriptFileTag = new KNSFileTag();
    private KNSFileTag noscriptFileTag = new KNSFileTag();
    private KNSImageTag uploadButtonTag = new KNSImageTag();
    private KNSImageTag cancelButtonTag = new KNSImageTag();
    private boolean shouldUpload = true;
    private boolean canEdit = false;

    private boolean groupActionsRendered = false;

    /**
     * Constructs a ImportLineRenderer, setting defaults on the tags that will always exist
     */
    public GroupTitleLineRenderer() {
        scriptFileTag.setSize("30");
        noscriptFileTag.setSize("30");
        noscriptFileTag.setStyle("font:10px;height:16px;");
        uploadButtonTag.setSrc(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("externalizable.images.url") + "tinybutton-add1.gif");
        uploadButtonTag.setStyleClass("tinybutton");
        // The double setting of the property is to handle the non-standard browser, you can guess which one _that_ is...
        uploadButtonTag.setOnclick("this.form.enctype=\\'multipart/form-data\\'; this.form.encoding=\\'multipart/form-data\\'; return true;");
        cancelButtonTag.setProperty("methodToCall.cancel");
        cancelButtonTag.setSrc(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("externalizable.images.url") + "tinybutton-cancelimport.gif");
        cancelButtonTag.setStyleClass("tinybutton");
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        cellCount = 1;
        accountingLineGroupDefinition = null;
        titleCellSpan = 4;
        lineCollectionProperty = null;
        accountingDocument = null;
        shouldUpload = true;
        canEdit = false;

        // clean script file tag
        scriptFileTag.setPageContext(null);
        scriptFileTag.setParent(null);
        scriptFileTag.setProperty(null);

        // clean noscript file tag
        noscriptFileTag.setPageContext(null);
        noscriptFileTag.setParent(null);
        noscriptFileTag.setProperty(null);

        // clean upload button tag
        uploadButtonTag.setPageContext(null);
        uploadButtonTag.setParent(null);
        uploadButtonTag.setProperty(null);
        uploadButtonTag.setAlt(null);
        uploadButtonTag.setTitle(null);

        // clean cancel import tag
        cancelButtonTag.setPageContext(null);
        cancelButtonTag.setParent(null);
        cancelButtonTag.setAlt(null);
        cancelButtonTag.setTitle(null);
        cancelButtonTag.setOnclick(null);
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag,
     *      org.kuali.core.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            pageContext.getOut().write(buildRowBeginning());
            
            pageContext.getOut().write(buildTitleCell());
            this.renderGroupLevelActions(pageContext, parentTag);
            
            pageContext.getOut().write(buildRowEnding());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty in rendering import/group header line", ioe);
        }
    }

    /**
     * Builds a tag for the row beginning
     * 
     * @returns the String with the HTML for the row opening
     */
    protected String buildRowBeginning() {
        return "<tr>";
    }

    /**
     * Builds the tag for the row beginning
     * 
     * @returns the String with the HTML for the row beginning
     */
    protected String buildRowEnding() {
        return "</tr>";
    }
    
    protected void renderGroupLevelActions(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(this.buildGroupActionsBeginning());
            
            this.renderGroupActions(pageContext, parentTag);
            
            this.renderUploadCell(pageContext, parentTag);
            
            out.write(this.buildGroupActionsColumnEnding());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering group level actions", ioe);
        }
    }

    /**
     * Builds a tag for the row beginning
     * 
     * @returns the String with the HTML for the row opening
     */
    protected String buildGroupActionsBeginning() { 
        if (this.canUpload() || this.isGroupActionsRendered()) { 
            StringBuilder groupActionsBeginning = new StringBuilder();
            final int width = cellCount - titleCellSpan;
            
            groupActionsBeginning.append("<td ");
            groupActionsBeginning.append("colspan=\"");
            groupActionsBeginning.append(Integer.toString(width));
            groupActionsBeginning.append("\" ");

            groupActionsBeginning.append("class=\"tab-subhead-import\" ");
            groupActionsBeginning.append("align=\"right\" ");
            groupActionsBeginning.append("nowrap=\"nowrap\" ");
            groupActionsBeginning.append("style=\"border-right: none;\"");
            groupActionsBeginning.append(">");
            
            return groupActionsBeginning.toString();
        }

        return StringUtils.EMPTY;
    }

    /**
     * Builds the tag for the row beginning
     * 
     * @returns the String with the HTML for the row beginning
     */
    protected String buildGroupActionsColumnEnding() {
        return this.canUpload() || this.isGroupActionsRendered() ? "</td>" : StringUtils.EMPTY;
    }

    /**
     * Builds the tags for the title cell of the import line
     * 
     * @return the String with the HTML for the title cell
     */
    protected String buildTitleCell() {
        StringBuilder titleCell = new StringBuilder();
        int colSpan = (this.canUpload() || this.isGroupActionsRendered()) ? titleCellSpan : cellCount;

        titleCell.append("<td ");

        titleCell.append("colspan=\"");
        titleCell.append(colSpan);
        titleCell.append("\" ");

        titleCell.append("class=\"tab-subhead\" ");

        titleCell.append("style=\"border-right: none;\"");

        titleCell.append(">");

        titleCell.append(buildGroupAnchor());

        titleCell.append(accountingLineGroupDefinition.getGroupLabel());

        titleCell.append("</td>");

        return titleCell.toString();
    }

    /**
     * Builds the unique anchor for this group
     * 
     * @return the unique anchor for this group
     */
    protected String buildGroupAnchor() {
        return "<a name=\"accounting" + getGroupInfix() + "Anchor\"></a>";
    }

    protected void renderGroupActions(PageContext pageContext, Tag parentTag) throws JspException {
        List<? extends AccountingLineViewActionDefinition> accountingLineGroupActions = accountingLineGroupDefinition.getAccountingLineGroupActions();        
        if (!this.isGroupActionsRendered() || accountingLineGroupActions == null || accountingLineGroupActions.isEmpty()) {
            return;
        }

        List<AccountingLineViewAction> viewActions = new ArrayList<AccountingLineViewAction>();
        for (AccountingLineViewActionDefinition action : accountingLineGroupActions) {
            String actionMethod = action.getActionMethod();
            String actionLabel = action.getActionLabel();
            String imageName = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("externalizable.images.url") + action.getImageName();

            AccountingLineViewAction viewAction = new AccountingLineViewAction(actionMethod, actionLabel, imageName);
            viewActions.add(viewAction);
        }

        if (!viewActions.isEmpty()) {
            ActionsRenderer actionsRenderer = new ActionsRenderer();
            actionsRenderer.setTagBeginning(" ");
            actionsRenderer.setTagEnding(" ");
            actionsRenderer.setPostButtonSpacing(" ");
            actionsRenderer.setActions(viewActions);
            actionsRenderer.render(pageContext, parentTag);
            actionsRenderer.clear();
        }
    }

    /**
     * A dumb way to get the group infix that tries to figure out if it's dealing with a source or target line
     * 
     * @return the String "source" or "target" to populate the buildGroupAnchor
     */
    protected String getGroupInfix() {
        Class accountingLineClass = accountingLineGroupDefinition.getAccountingLineClass();
        return (accountingLineClass.isAssignableFrom(SourceAccountingLine.class) ? "source" : "target");
    }

    /**
     * Oy, the big one...this one actually renders instead of returning the HTML in a String. This is because it's kind of complex
     * (and a likely target for future refactoring)
     * 
     * @param pageContext the page contex to render to
     * @param parentTag the tag that is requesting all the rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderUploadCell(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();

        if (canUpload()) {
            try {
                String hideImport = getHideImportName();
                String showImport = getShowImportName();
                String showLink = getShowLinkName();
                String uploadDiv = getUploadDivName();

                out.write("\n<SCRIPT type=\"text/javascript\">\n");
                out.write("<!--\n");
                out.write("\tfunction " + hideImport + "() {\n");
                out.write("\t\tdocument.getElementById(\"" + showLink + "\").style.display=\"inline\";\n");
                out.write("\t\tdocument.getElementById(\"" + uploadDiv + "\").style.display=\"none\";\n");
                out.write("\t}\n");
                out.write("\tfunction " + showImport + "() {\n");
                out.write("\t\tdocument.getElementById(\"" + showLink + "\").style.display=\"none\";\n");
                out.write("\t\tdocument.getElementById(\"" + uploadDiv + "\").style.display=\"inline\";\n");
                out.write("\t}\n");
                out.write("\tdocument.write(\n");
                out.write("\t\t'<a id=\"" + showLink + "\" href=\"#\" onclick=\"" + showImport + "();return false;\">' +\n");
                out.write("\t\t'<img src=\"" + SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("externalizable.images.url") + "tinybutton-importlines.gif\" title=\"import file\" alt=\"import file\"' +\n");
                out.write("\t\t'width=\"72\" border=\"0\">' +\n");
                out.write("\t\t'</a>' +\n");
                out.write("\t\t'<div id=\"" + uploadDiv + "\" style=\"display:none;\" >' +\n");

                out.write("\t\t'");

                scriptFileTag.setPageContext(pageContext);
                scriptFileTag.setParent(parentTag);
                scriptFileTag.setProperty(accountingLineGroupDefinition.getImportedLinePropertyPrefix() + "File");
                scriptFileTag.doStartTag();
                scriptFileTag.doEndTag();

                out.write("' +\n");
                out.write("\t\t'");

                uploadButtonTag.setPageContext(pageContext);
                uploadButtonTag.setParent(parentTag);
                uploadButtonTag.setProperty("methodToCall.upload" + StringUtils.capitalize(accountingLineGroupDefinition.getImportedLinePropertyPrefix()) + "Lines");
                uploadButtonTag.setAlt("insert " + accountingLineGroupDefinition.getGroupLabel() + " accounting lines");
                uploadButtonTag.setTitle("insert " + accountingLineGroupDefinition.getGroupLabel() + " accounting lines");
                uploadButtonTag.doStartTag();
                uploadButtonTag.doEndTag();

                out.write("' +\n");

                out.write("\t\t'");

                cancelButtonTag.setPageContext(pageContext);
                cancelButtonTag.setParent(parentTag);
                cancelButtonTag.setAlt("Cancel import of " + accountingLineGroupDefinition.getGroupLabel() + " accounting lines");
                cancelButtonTag.setTitle("Cancel import of " + accountingLineGroupDefinition.getGroupLabel() + " accounting lines");
                cancelButtonTag.setOnclick(getHideImportName() + "();return false;");
                cancelButtonTag.doStartTag();
                cancelButtonTag.doEndTag();

                out.write("' +\n");

                out.write("\t'</div>');\n");
                out.write("\t//-->\n");
                out.write("</SCRIPT>\n");
                out.write("<NOSCRIPT>\n");
                out.write("\tImport " + accountingLineGroupDefinition.getGroupLabel() + " lines\n");

                noscriptFileTag.setPageContext(pageContext);
                noscriptFileTag.setParent(parentTag);
                noscriptFileTag.setProperty(accountingLineGroupDefinition.getImportedLinePropertyPrefix() + "File");
                noscriptFileTag.doStartTag();
                noscriptFileTag.doEndTag();

                uploadButtonTag.doStartTag();
                uploadButtonTag.doEndTag();

                out.write("</NOSCRIPT>\n");
            }
            catch (IOException ioe) {
                throw new JspException("Difficulty rendering accounting lines import upload", ioe);
            }
        }
    }

    /**
     * @return the name of the line collection property, but in a form that is okay for javascript variable/function naming
     */
    protected String getVariableFriendlyLineCollectionProperty() {
        return lineCollectionProperty.replaceAll("[^A-Za-z0-9]", "_");
    }

    /**
     * @return the name of the hide import function
     */
    protected String getHideImportName() {
        return "hide" + getVariableFriendlyLineCollectionProperty() + "Import";
    }

    /**
     * @return the name of the show import function
     */
    protected String getShowImportName() {
        return "show" + getVariableFriendlyLineCollectionProperty() + "Import";
    }

    /**
     * @return the name of the show link element
     */
    protected String getShowLinkName() {
        return lineCollectionProperty + "ShowLink";
    }

    /**
     * @return the name of the upload div
     */
    protected String getUploadDivName() {
        return "upload" + lineCollectionProperty + "Div";
    }

    /**
     * Determines if an upload can proceed for the accounting line group
     * 
     * @return true if upload is possible, false otherwise
     */
    protected boolean canUpload() {
        return (canEdit && accountingDocument.getAccountingLineParser() != null && shouldUpload);
    }

    /**
     * Allows overriding of whether something can be uploaded - though this serves only to turn uploading more off, never more on
     * 
     * @param allowUpload should we be allowed to upload?
     */
    public void overrideCanUpload(boolean allowUpload) {
        this.shouldUpload = allowUpload;
    }

    /**
     * Gets the cellCount attribute.
     * 
     * @return Returns the cellCount.
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Sets the cellCount attribute value.
     * 
     * @param cellCount The cellCount to set.
     */
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    /**
     * Gets the accountingDocument attribute.
     * 
     * @return Returns the accountingDocument.
     */
    public AccountingDocument getAccountingDocument() {
        return accountingDocument;
    }

    /**
     * Sets the accountingDocument attribute value.
     * 
     * @param accountingDocument The accountingDocument to set.
     */
    public void setAccountingDocument(AccountingDocument accountingDocument) {
        this.accountingDocument = accountingDocument;
    }

    /**
     * Gets the accountingLineGroupDefinition attribute.
     * 
     * @return Returns the accountingLineGroupDefinition.
     */
    public AccountingLineGroupDefinition getAccountingLineGroupDefinition() {
        return accountingLineGroupDefinition;
    }

    /**
     * Sets the accountingLineGroupDefinition attribute value.
     * 
     * @param accountingLineGroupDefinition The accountingLineGroupDefinition to set.
     */
    public void setAccountingLineGroupDefinition(AccountingLineGroupDefinition accountingLineGroupDefinition) {
        this.accountingLineGroupDefinition = accountingLineGroupDefinition;
    }

    /**
     * Gets the titleCellSpan attribute.
     * 
     * @return Returns the titleCellSpan.
     */
    public int getTitleCellSpan() {
        return titleCellSpan;
    }

    /**
     * Sets the titleCellSpan attribute value.
     * 
     * @param titleCellSpan The titleCellSpan to set.
     */
    public void setTitleCellSpan(int titleCellSpan) {
        this.titleCellSpan = titleCellSpan;
    }

    /**
     * Gets the lineCollectionProperty attribute.
     * 
     * @return Returns the lineCollectionProperty.
     */
    public String getLineCollectionProperty() {
        return lineCollectionProperty;
    }

    /**
     * Sets the lineCollectionProperty attribute value.
     * 
     * @param lineCollectionProperty The lineCollectionProperty to set.
     */
    public void setLineCollectionProperty(String lineCollectionProperty) {
        this.lineCollectionProperty = lineCollectionProperty;
    }

    /**
     * Gets the groupActionsRendered attribute.
     * 
     * @return Returns the groupActionsRendered.
     */
    public boolean isGroupActionsRendered() {
        return groupActionsRendered;
    }

    /**
     * Sets the groupActionsRendered attribute value.
     * 
     * @param groupActionsRendered The groupActionsRendered to set.
     */
    public void setGroupActionsRendered(boolean groupActionsRenderred) {
        this.groupActionsRendered = groupActionsRenderred;
    }

    /**
     * Sets the canEdit attribute value.
     * @param canEdit The canEdit to set.
     */
    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

}
