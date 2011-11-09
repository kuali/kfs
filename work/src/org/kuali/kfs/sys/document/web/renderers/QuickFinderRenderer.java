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
import java.text.MessageFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.util.RendererUtil;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Renders a quick field for an element
 */
public class QuickFinderRenderer extends FieldRendererBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickFinderRenderer.class);
    
    private int tabIndex = -1;
    
    /**
     * Gets the tabIndex attribute. 
     * @return Returns the tabIndex.
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * Sets the tabIndex attribute value.
     * @param tabIndex The tabIndex to set.
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Renders the quick finder to the page context
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.rice.krad.bo.BusinessObject)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.write(buildQuickFinderHtml(pageContext));
        } catch (IOException ioe) {
            throw new JspException("Cannot render quick finder for field "+getField(), ioe);
        }
    }
    
    /**
     * Creates the HTML for a quick finder icon
     * @param businessObjectToRender the business object we're rendering
     * @return the html for the quick finder
     */
    protected String buildQuickFinderHtml(PageContext pageContext) {
        StringBuilder quickFinderHtml = new StringBuilder();
        quickFinderHtml.append("&nbsp;<input type=\"image\" ");
        //quickFinderHtml.append("tabindex=\"${tabindex}\" ");
        quickFinderHtml.append("name=\"").append(buildQuickFinderName(pageContext)).append("\" ");
        
        quickFinderHtml.append("src=\"");
        quickFinderHtml.append(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("kr.externalizable.images.url"));
        quickFinderHtml.append("searchicon.gif");
        quickFinderHtml.append("\" ");
        
        quickFinderHtml.append("border=\"0\" ");
        
        quickFinderHtml.append("class=\"tinybutton\" ");
        
        quickFinderHtml.append("valign=\"middle\" ");
        
        quickFinderHtml.append("alt=\"");
        quickFinderHtml.append(getAccessibleTitle());
        quickFinderHtml.append("\" ");
        
        quickFinderHtml.append("title=\"");
        quickFinderHtml.append(getAccessibleTitle());
        quickFinderHtml.append("\" ");
        
        if (tabIndex > -1) {
            quickFinderHtml.append(" tabIndex=\"");
            quickFinderHtml.append(getTabIndex());
            quickFinderHtml.append("\"");
        }
        
        quickFinderHtml.append("/> ");
        
        return quickFinderHtml.toString();
    }
    
    /**
     * Builds the (quite complex) name for the quick finder field
     * @return the name of the quick finder field
     */
    protected String buildQuickFinderName(PageContext pageContext) {
        StringBuilder nameBuf = new StringBuilder();
        nameBuf.append("methodToCall.performLookup.");
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL);
        nameBuf.append(getField().getQuickFinderClassNameImpl());
        nameBuf.append(KRADConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        nameBuf.append(".");
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM1_LEFT_DEL);
        nameBuf.append(getField().getFieldConversions());
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        nameBuf.append(".");
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM2_LEFT_DEL);
        nameBuf.append(getField().getLookupParameters());
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        nameBuf.append(".");
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM3_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL+"."); // hide return link
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM4_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL+"."); // extra button source
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM5_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM5_RIGHT_DEL+"."); // extra button params
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM7_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM7_RIGHT_DEL+"."); // supress actions
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM8_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM8_RIGHT_DEL+"."); // read only fields
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM10_LEFT_DEL);
        nameBuf.append(getField().getReferencesToRefresh());
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM10_RIGHT_DEL+".");
        
        nameBuf.append(KRADConstants.METHOD_TO_CALL_PARM9_LEFT_DEL+KRADConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL+"."); // auto-search
        
        nameBuf.append("anchor"); // anchor
        
        String name = nameBuf.toString();
        RendererUtil.registerEditableProperty(pageContext, name);
        return name;
    }

    /**
     * A quick finder for a quick finder?  I fear not
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }

    /**
     * Clears the tab index
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        tabIndex = -1;
    }

    /**
     * Overridden to format into message automatically, so there's a "Search" in front of the field label name
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#setAccessibleTitle(java.lang.String)
     */
    @Override
    public void setAccessibleTitle(String accessibleTitle) {
        final String messagePattern = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.LABEL_ACCOUNTING_LINE_QUICKFINDER_ACCESSIBLE_TITLE);
        final String formattedAccessibleTitle = MessageFormat.format(messagePattern, accessibleTitle);
        super.setAccessibleTitle(formattedAccessibleTitle);
    }

}
