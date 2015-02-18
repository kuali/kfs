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

import org.apache.struts.taglib.bean.WriteTag;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * The standard renderer of totals for an accounting line group
 */
public class GroupTotalRenderer extends TotalRendererBase {
    private String totalProperty;
    private WriteTag writeTag = new WriteTag();
    
    private String totalLabelProperty = "accounting.line.group.total.label";
    private String formName = "KualiForm";
    
    /**
     * Constructs a GroupTotalRenderer, setting permanent values on the writeTag tag
     */
    public GroupTotalRenderer() {
        writeTag.setName(formName);
    }

    /**
     * Gets the totalProperty attribute. 
     * @return Returns the totalProperty.
     */
    public String getTotalProperty() {
        return totalProperty;
    }

    /**
     * Sets the totalProperty attribute value.
     * @param totalProperty The totalProperty to set.
     */
    public void setTotalProperty(String totalProperty) {
        this.totalProperty = totalProperty;
    }

    /**
     * Gets the totalLabelProperty attribute. 
     * @return Returns the totalLabelProperty.
     */
    public String getTotalLabelProperty() {
        return totalLabelProperty;
    }

    /**
     * Sets the totalLabelProperty attribute value.
     * @param totalLabelProperty The totalLabelProperty to set.
     */
    public void setTotalLabelProperty(String totalLabelProperty) {
        this.totalLabelProperty = totalLabelProperty;
    }

    /**
     * Clears out the totalProperty
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        super.clear();
        totalProperty = null;
        
        writeTag.setPageContext(null);
        writeTag.setParent(null);
        writeTag.setProperty(null);
    }

    /**
     * Uses a Struts write tag to dump out the total
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write("<tr>");
            
            final int emptyCellSpanBefore = this.getColumnNumberOfRepresentedCell() - 1;
            out.write("<td  class=\"total-line\" colspan=\"");
            out.write(Integer.toString(emptyCellSpanBefore));
            out.write("\">&nbsp;</td>");
            
            out.write("<td class=\"total-line\" style=\"border-left: 0px;\">");
            
            out.write("<strong>");
            
            out.write(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(totalLabelProperty));
            out.write("&nbsp;");
            
            writeTag.setPageContext(pageContext);
            writeTag.setParent(parentTag);
            writeTag.setProperty(getTotalProperty());
            writeTag.doStartTag();
            writeTag.doEndTag();
            
            out.write("</strong>");
            
            out.write("</td>");
            
            final int emptyCellSpanAfter = this.getCellCount() - this.getColumnNumberOfRepresentedCell();
            if(emptyCellSpanAfter > 0) {
                out.write("<td class=\"total-line\" style=\"border-left: 0px;\" colspan=\"");
                out.write(Integer.toString(emptyCellSpanAfter));
                out.write("\">&nbsp;</td>");
            }
            
            out.write("</tr>");
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering group total", ioe);
        }
    }
}
