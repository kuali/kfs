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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.taglib.bean.WriteTag;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * The standard renderer of totals for an accounting line group
 */
public class GroupTotalRenderer implements Renderer, CellCountCurious {
    private String totalProperty;
    private WriteTag writeTag = new WriteTag();
    private int cellCount = 0;
    
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
     * Clears out the totalProperty
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        totalProperty = null;
        cellCount = 0;
        
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
            
            out.write("<td  class=\"total-line\" colspan=\"");
            final int longEmptyCellSpan = cellCount - 2;
            out.write(Integer.toString(longEmptyCellSpan));
            out.write("\">&nbsp;</td>");
            
            out.write("<td class=\"total-line\" style=\"border-left: 0px;\">");
            
            out.write("<strong>");
            
            out.write(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(totalLabelProperty));
            out.write("&nbsp;");
            
            writeTag.setPageContext(pageContext);
            writeTag.setParent(parentTag);
            writeTag.setProperty(totalProperty);
            writeTag.doStartTag();
            writeTag.doEndTag();
            
            out.write("</strong>");
            
            out.write("</td>");
            
            out.write("<td class=\"total-line\" style=\"border-left: 0px;\">&nbsp;</td>");
            
            out.write("</tr>");
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering group total", ioe);
        }
    }
}
