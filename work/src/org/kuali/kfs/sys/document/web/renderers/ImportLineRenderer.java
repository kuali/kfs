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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.taglib.html.FileTag;
import org.apache.struts.taglib.html.ImageTag;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * Renders the standard group header/import line
 */
public class ImportLineRenderer implements Renderer, CellCountCurious {
    private int titleCellSpan = 4;
    private int cellCount = 1;
    private AccountingLineGroupDefinition accountingLineGroupDefinition;
    private Map editModes;
    private String lineCollectionProperty;
    private FileTag scriptFileTag = new FileTag();
    private FileTag noscriptFileTag = new FileTag();
    private ImageTag uploadButtonTag = new ImageTag();
    private ImageTag cancelButtonTag = new ImageTag();
    
    /**
     * Constructs a ImportLineRenderer, setting defaults on the tags that will always exist
     */
    public ImportLineRenderer() {
        scriptFileTag.setSize("30");
        noscriptFileTag.setSize("30");
        noscriptFileTag.setStyle("font:10px;height:16px;");
        uploadButtonTag.setSrc(KNSServiceLocator.getKualiConfigurationService().getPropertyString("externalizable.images.url")+"tinybutton-add1.gif");
        uploadButtonTag.setStyleClass("tinybutton");
        cancelButtonTag.setProperty("methodToCall.cancel");
        cancelButtonTag.setSrc(KNSServiceLocator.getKualiConfigurationService().getPropertyString("externalizable.images.url")+"tinybutton-cancelimport.gif");
        cancelButtonTag.setStyleClass("tinybutton");
    }

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        cellCount = 1;
        accountingLineGroupDefinition = null;
        titleCellSpan = 4;
        editModes = null;
        lineCollectionProperty = null;
        
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
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.core.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            pageContext.getOut().write(buildRowBeginning());
            pageContext.getOut().write(buildTitleCell());
            renderUploadCell(pageContext, parentTag);
            pageContext.getOut().write(buildRowEnding());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty in rendering import/group header line", ioe);
        }
    }
    
    /**
     * Builds a tag for the row beginning
     * @returns the String with the HTML for the row opening
     */
    protected String buildRowBeginning() {
        return "<tr>";
    }
    
    /**
     * Builds the tag for the row beginning
     * @returns the String with the HTML for the row beginning
     */
    protected String buildRowEnding() {
        return "</tr>";
    }
    
    /**
     * Builds the tags for the title cell of the import line
     * @return the String with the HTML for the title cell
     */
    protected String buildTitleCell() {
        StringBuilder titleCell = new StringBuilder();
        int colSpan = canUpload() ? titleCellSpan : cellCount;
        
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
     * @return the unique anchor for this group
     */
    protected String buildGroupAnchor() {
        return "<a name=\"accounting"+getGroupInfix()+"Anchor\"></a>";
    }
    
    /**
     * A dumb way to get the group infix that tries to figure out if it's dealing with a source or target line
     * @return the String "source" or "target" to populate the buildGroupAnchor
     */
    protected String getGroupInfix() {
        Class accountingLineClass = accountingLineGroupDefinition.getAccountingLineClass();
        return (accountingLineClass.isAssignableFrom(SourceAccountingLine.class) ? "source" : "target");
    }
    
    /**
     * Oy, the big one...this one actually renders instead of returning the HTML in a String.  This is because it's kind of complex (and a likely target for future refactoring)
     * @param pageContext the page contex to render to
     * @param parentTag the tag that is requesting all the rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderUploadCell(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        if (canUpload()) {
            try {
                out.write("<td ");
                
                out.write("colspan=\"");
                final int width = cellCount - titleCellSpan;
                out.write(Integer.toString(width));
                out.write("\" ");
                
                out.write("class=\"tab-subhead-import\" ");
                
                out.write("align=\"right\" ");
                
                out.write("nowrap=\"nowrap\" ");
                
                out.write("style=\"border-right: none;\"");
                
                out.write(">");
                
                String hideImport = getHideImportName();
                String showImport = getShowImportName();
                String showLink = getShowLinkName();
                String uploadDiv = getUploadDivName();
                
                out.write("\n<SCRIPT type=\"text/javascript\">\n");
                out.write("<!--\n");
                out.write("\tfunction "+hideImport+"() {\n");
                out.write("\t\tdocument.getElementById(\""+showLink+"\").style.display=\"inline\";\n");
                out.write("\t\tdocument.getElementById(\""+uploadDiv+"\").style.display=\"none\";\n");
                out.write("\t}\n");
                out.write("\tfunction "+showImport+"() {\n");
                out.write("\t\tdocument.getElementById(\""+showLink+"\").style.display=\"none\";\n");
                out.write("\t\tdocument.getElementById(\""+uploadDiv+"\").style.display=\"inline\";\n");
                out.write("\t}\n");
                out.write("\tdocument.write(\n");
                out.write("\t\t'<a id=\""+showLink+"\" href=\"#\" onclick=\""+showImport+"();return false;\">' +\n");
                out.write("\t\t'<img src=\""+KNSServiceLocator.getKualiConfigurationService().getPropertyString("externalizable.images.url")+"tinybutton-importlines.gif\" title=\"import file\" alt=\"import file\"' +\n");
                out.write("\t\t'width=\"72\" height=\"15\" border=\"0\" align=\"middle\" class=\"det-button\">' +\n");
                out.write("\t\t'</a>' +\n");
                out.write("\t\t'<div id=\""+uploadDiv+"\" style=\"display:none;\" >' +\n");
                
                out.write("\t\t'");
                
                scriptFileTag.setPageContext(pageContext);
                scriptFileTag.setParent(parentTag);
                scriptFileTag.setProperty(accountingLineGroupDefinition.getImportedLinePropertyPrefix()+"File");
                scriptFileTag.doStartTag();
                scriptFileTag.doEndTag();
                
                out.write("' +\n");
                out.write("\t\t'");
                
                uploadButtonTag.setPageContext(pageContext);
                uploadButtonTag.setParent(parentTag);
                uploadButtonTag.setProperty("methodToCall.upload"+accountingLineGroupDefinition.getImportedLinePropertyPrefix()+"Lines");
                uploadButtonTag.setAlt("insert "+accountingLineGroupDefinition.getGroupLabel()+" accounting lines");
                uploadButtonTag.setTitle("insert "+accountingLineGroupDefinition.getGroupLabel()+" accounting lines");
                uploadButtonTag.doStartTag();
                uploadButtonTag.doEndTag();
                
                out.write("' +\n");
                
                out.write("\t\t'");
                
                cancelButtonTag.setPageContext(pageContext);
                cancelButtonTag.setParent(parentTag);
                cancelButtonTag.setAlt("Cancel import of "+accountingLineGroupDefinition.getGroupLabel()+" accounting lines");
                cancelButtonTag.setTitle("Cancel import of "+accountingLineGroupDefinition.getGroupLabel()+" accounting lines");
                cancelButtonTag.setOnclick(getHideImportName()+"();return false;");
                cancelButtonTag.doStartTag();
                cancelButtonTag.doEndTag();
                
                out.write("' +\n");
                
                out.write("\t'</div>');\n");
                out.write("\t//-->\n");
                out.write("</SCRIPT>\n");
                out.write("<NOSCRIPT>\n");
                out.write("\tImport "+accountingLineGroupDefinition.getGroupLabel()+" lines\n");
                
                noscriptFileTag.setPageContext(pageContext);
                noscriptFileTag.setParent(parentTag);
                noscriptFileTag.setProperty(accountingLineGroupDefinition.getImportedLinePropertyPrefix()+"File");
                noscriptFileTag.doStartTag();
                noscriptFileTag.doEndTag();
                
                uploadButtonTag.doStartTag();
                uploadButtonTag.doEndTag();
                                
                out.write("</NOSCRIPT>\n");
                
                out.write("</td>");
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
        return lineCollectionProperty.replaceAll("[^A-Za-z]", "_");
    }
    
    /**
     * @return the name of the hide import function
     */
    protected String getHideImportName() {
        return "hide"+getVariableFriendlyLineCollectionProperty()+"Import";
    }
    
    /**
     * @return the name of the show import function
     */
    protected String getShowImportName() {
        return "show"+getVariableFriendlyLineCollectionProperty()+"Import";
    }
    
    /**
     * @return the name of the show link element
     */
    protected String getShowLinkName() {
        return lineCollectionProperty+"ShowLink";
    }
    
    /**
     * @return the name of the upload div
     */
    protected String getUploadDivName() {
        return "upload"+lineCollectionProperty+"Div";
    }
    
    /**
     * Determines if an upload can proceed on this 
     * @return
     */
    protected boolean canUpload() {
        return (getEditModes().containsKey(AuthorizationConstants.EditMode.FULL_ENTRY) && getAccountingLineGroupDefinition().getImportedLineParser() != null);
    }

    /**
     * Gets the cellCount attribute. 
     * @return Returns the cellCount.
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Sets the cellCount attribute value.
     * @param cellCount The cellCount to set.
     */
    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }

    /**
     * Gets the accountingLineGroupDefinition attribute. 
     * @return Returns the accountingLineGroupDefinition.
     */
    public AccountingLineGroupDefinition getAccountingLineGroupDefinition() {
        return accountingLineGroupDefinition;
    }

    /**
     * Sets the accountingLineGroupDefinition attribute value.
     * @param accountingLineGroupDefinition The accountingLineGroupDefinition to set.
     */
    public void setAccountingLineGroupDefinition(AccountingLineGroupDefinition accountingLineGroupDefinition) {
        this.accountingLineGroupDefinition = accountingLineGroupDefinition;
    }

    /**
     * Gets the titleCellSpan attribute. 
     * @return Returns the titleCellSpan.
     */
    public int getTitleCellSpan() {
        return titleCellSpan;
    }

    /**
     * Sets the titleCellSpan attribute value.
     * @param titleCellSpan The titleCellSpan to set.
     */
    public void setTitleCellSpan(int titleCellSpan) {
        this.titleCellSpan = titleCellSpan;
    }

    /**
     * Gets the editModes attribute. 
     * @return Returns the editModes.
     */
    public Map getEditModes() {
        return editModes;
    }

    /**
     * Sets the editModes attribute value.
     * @param editModes The editModes to set.
     */
    public void setEditModes(Map editModes) {
        this.editModes = editModes;
    }

    /**
     * Gets the lineCollectionProperty attribute. 
     * @return Returns the lineCollectionProperty.
     */
    public String getLineCollectionProperty() {
        return lineCollectionProperty;
    }

    /**
     * Sets the lineCollectionProperty attribute value.
     * @param lineCollectionProperty The lineCollectionProperty to set.
     */
    public void setLineCollectionProperty(String lineCollectionProperty) {
        this.lineCollectionProperty = lineCollectionProperty;
    }
    
}
