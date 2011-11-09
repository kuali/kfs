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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.util.RendererUtil;
import org.kuali.rice.core.api.util.KeyValue;

/**
 * This renders a drop down field to JSP
 */
public class DropDownRenderer extends FieldRendererBase {

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildSelectControl());
            renderQuickFinderIfNecessary(pageContext, parentTag);
            if (isShowError()) {
                renderErrorIcon(pageContext);
            }
            RendererUtil.registerEditableProperty(pageContext, getFieldName());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering drop down control", ioe);
        }
    }

    /**
     * Builds a drop down control, based on the field
     * @param propertyPrefix the prefix of the property from the form to the business object
     * @return a String containing the HTML for the drop down control/select box
     */
    protected String buildSelectControl() {
        StringBuilder selectControl = new StringBuilder();
        selectControl.append("<select");
        
        selectControl.append(" id=\"");
        selectControl.append(getFieldName());
        selectControl.append("\" ");
        
        selectControl.append(" name=\"");
        selectControl.append(getFieldName());
        selectControl.append("\" ");
        
        selectControl.append(" title=\"");
        selectControl.append(this.getAccessibleTitle());
        selectControl.append("\"");
        
        final String onBlur = buildOnBlur();
        if (!StringUtils.isBlank(onBlur)) {
            selectControl.append(" onblur=\"");
            selectControl.append(onBlur);
            selectControl.append("\"");
        }
        
        selectControl.append(">");
        
        selectControl.append(buildOptions());
        
        selectControl.append("</select>");
        
        return selectControl.toString();
    }
    
    /**
     * Builds the options for the select box, given the valid values in the field
     * @return a String containing all the option tags for this drop down control
     */
    protected String buildOptions() {
        StringBuilder options = new StringBuilder();
        
        for (Object keyLabelPairAsObj : getField().getFieldValidValues()) {
            options.append(buildOption((KeyValue)keyLabelPairAsObj));
        }
        
        return options.toString();
    }
    
    /**
     * Builds an option tag for the given key label pair
     * @param keyLabelPair the key label pair to create an option tag from
     * @return the String with the option tag in it
     */
    protected String buildOption(KeyValue keyLabelPair) {
        StringBuilder option = new StringBuilder();
        
        option.append("<option value=\"");
        option.append(keyLabelPair.getKey());
        option.append("\"");
        if (getField().getPropertyValue().equalsIgnoreCase(keyLabelPair.getKey().toString())) {
            option.append(" selected=\"selected\"");
        }
        option.append(">");
        
        option.append(keyLabelPair.getValue());
        
        option.append("</options>");
        
        return option.toString();
    }

    /**
     * No quickfinder for us, thanks
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }
    
}
