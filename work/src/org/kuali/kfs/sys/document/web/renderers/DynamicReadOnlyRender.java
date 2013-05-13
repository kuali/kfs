/*
 * Copyright 2010 The Kuali Foundation.
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.HiddenTag;

/**
 * Render which displays a field that can't be input directly but could be changed when other fields change.
 * An example of such is the chartOfAccountsCode in accounting lines, when accounts can't cross charts and
 * chart code is set automatically by account number.   
 */
public class DynamicReadOnlyRender extends ReadOnlyRenderer {
    // A hidden input field to store a copy of the field value so that the field will appear read-only to users
    // but could be set by the system and read into the data object.
    private HiddenTag shadowInputTag = new HiddenTag();
    
    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();

        try {
            String value = discoverRenderValue();            
            out.write(buildBeginSpan());
            
            if (!StringUtils.isEmpty(value)) {
                if (shouldRenderInquiryLink()) {
                    out.write(buildBeginInquiryLink());
                }                
                out.write(value);                
                if (shouldRenderInquiryLink()) {
                    out.write(buildEndInquiryLink());
                }                                
            } else {
                out.write(buildNonBreakingSpace());
            }
            
            out.write(buildEndSpan());
            renderShadowInputTag(pageContext, parentTag);
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering read only field", ioe);
        }
    }

    /**
     * Generates the HTML for the opening span tag to wrap the displayed value
     * @param propertyPrefix the property path from the form the business object being rendered
     * @return the HTML for the opening span 
     */
    protected String buildBeginSpan() {
        StringBuilder beginSpan = new StringBuilder();        
        beginSpan.append("<span id=\"");
        beginSpan.append(getFieldName());
        beginSpan.append(".div\">");        
        return beginSpan.toString();
    }    

    /**
     * Renders the value of the field in the hidden input tag so it can be read into the data object.
     * @param pageContext the page context to render to
     * @param parentTag the tag requesting all this rendering
     */
    protected void renderShadowInputTag(PageContext pageContext, Tag parentTag) throws JspException {
        shadowInputTag.setPageContext(pageContext);
        shadowInputTag.setParent(parentTag);
        shadowInputTag.setProperty(getFieldName());
        shadowInputTag.setValue(getField().getPropertyValue());        
        shadowInputTag.doStartTag();
        shadowInputTag.doEndTag();
    }
    
}
