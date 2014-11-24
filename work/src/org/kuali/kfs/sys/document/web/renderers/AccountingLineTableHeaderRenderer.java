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
package org.kuali.kfs.sys.document.web.renderers;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.HiddenTag;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.taglib.html.KNSImageTag;

/**
 * Renders the header of an accounting line table
 */
public class AccountingLineTableHeaderRenderer implements Renderer {
    private int cellCount;
    private boolean hideDetails;
    private String accountingLineImportInstructionsUrl;
    private KNSImageTag showHideTag = new KNSImageTag();
    private HiddenTag hideStateTag = new HiddenTag();
    
    /**
     * Constructs a AccountingLineTableHeaderRenderer, updating the tags used by this renderer to keep constant properties
     */
    public AccountingLineTableHeaderRenderer() {
        hideStateTag.setName("KualiForm");
        hideStateTag.setProperty("hideDetails");
        
        showHideTag.setStyleClass("tinybutton");
    }

    /**
     * Clears out the mutable, changing qualities of this renderer so it can be repooled
     */
    public void clear() {
        cellCount = 0;
        hideDetails = false;
        accountingLineImportInstructionsUrl = null;
        
        showHideTag.setPageContext(null);
        showHideTag.setParent(null);
        showHideTag.setProperty(null);
        showHideTag.setAlt(null);
        showHideTag.setTitle(null);
        showHideTag.setSrc(null);
        
        hideStateTag.setPageContext(null);
        hideStateTag.setParent(null);
    }

    /**
     * Renders the header for the accounting line table to the screen
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildDivStart());
            out.write(buildTableStart());
            out.write(buildSubheadingWithDetailToggleRowBeginning());
            renderHideDetails(pageContext, parentTag);
            out.write(buildSubheadingWithDetailToggleRowEnding());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering AccountingLineTableHeader", ioe);
        }
    }
    
    /**
     * Builds the beginning of the tab-container div
     * @return the beginning of the tab-container div in HTML
     */
    protected String buildDivStart() {
        return "<div class=\"tab-container\" align=\"center\">\n";
    }

    /**
     * Builds the very start of the table
     * @return the very start of the table expressed as HTML
     */
    protected String buildTableStart() {
        return "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"datatable\">\n";
    }
    
    /**
     * Builds the start of the subheading row of the table
     * @return the start of the subheading row of the table expressed as HTML
     */
    protected String buildSubheadingWithDetailToggleRowBeginning() {
        StringBuilder row = new StringBuilder();
        row.append("\t<tr>\n");
        row.append("\t\t<td colspan=\"");
        row.append(cellCount);
        row.append("\" class=\"subhead\">\n");
        row.append("\t\t\t<span class=\"subhead-left\">");
        row.append(buildSubHeading());
        row.append("</span>\n");
        row.append("\t\t\t<span class=\"subhead-right\">\n");
        
        return row.toString();
    }
    
    /**
     * Builds the subheading for the table
     * @return the subheading for the table, expressed as HTML
     */
    protected String buildSubHeading() {
        if (StringUtils.isBlank(accountingLineImportInstructionsUrl)) return "&nbsp;";
        
        StringBuilder subheading = new StringBuilder();

        subheading.append("Accounting Lines <a href=\"");
        subheading.append(accountingLineImportInstructionsUrl);
        subheading.append("\" target=\"helpWindow\">");
        subheading.append("<img src=\"");
        subheading.append(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kr.externalizable.images.url"));
        subheading.append("my_cp_inf.gif\" title=\"Accounting Lines Help\" alt=\"Accounting Lines Help\" hspace=\"5\" border=\"0\" align=\"middle\" />");
        subheading.append("</a>");
        
        return subheading.toString();
    }
    
    /**
     * Renders the show/hide button  
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting all this rendering
     * @throws JspException thrown under terrible circumstances when the rendering failed and had to be left behind like so much refuse
     */
    protected void renderHideDetails(PageContext pageContext, Tag parentTag) throws JspException {
        hideStateTag.setPageContext(pageContext);
        hideStateTag.setParent(parentTag);

        hideStateTag.doStartTag();
        hideStateTag.doEndTag();
        
        String toggle = hideDetails ? "show" : "hide";
        
        showHideTag.setPageContext(pageContext);
        showHideTag.setParent(parentTag);
        showHideTag.setProperty("methodToCall."+toggle+"Details");
        showHideTag.setSrc(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kr.externalizable.images.url")+"det-"+toggle+".gif");
        showHideTag.setAlt(toggle+" transaction details");
        showHideTag.setTitle(toggle+" transaction details");
        
        showHideTag.doStartTag();
        showHideTag.doEndTag();
    }
    
    /**
     * Builds the ending of the toggle row
     * @return the ending of the toggle row expressed as HTML
     */
    protected String buildSubheadingWithDetailToggleRowEnding() {
        StringBuilder row = new StringBuilder();
        row.append("\t\t\t</span>\n");
        row.append("\t\t</td>\n");
        row.append("\t</tr>\n");
        return row.toString();
    }

    /**
     * Gets the accountingLineImportInstructionsUrl attribute. 
     * @return Returns the accountingLineImportInstructionsUrl.
     */
    public String getAccountingLineImportInstructionsUrl() {
        return accountingLineImportInstructionsUrl;
    }

    /**
     * Sets the accountingLineImportInstructionsUrl attribute value.
     * @param accountingLineImportInstructionsUrl The accountingLineImportInstructionsUrl to set.
     */
    public void setAccountingLineImportInstructionsUrl(String accountingLineImportInstructionsUrl) {
        this.accountingLineImportInstructionsUrl = accountingLineImportInstructionsUrl;
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
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean getHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }
}
