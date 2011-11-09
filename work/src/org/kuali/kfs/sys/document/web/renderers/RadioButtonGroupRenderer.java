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
 * Renders a control as a group of radio buttons
 */
public class RadioButtonGroupRenderer extends FieldRendererBase {

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        
        try {
            out.write(buildRadioButtons());
            renderQuickFinderIfNecessary(pageContext, parentTag);
            if (isShowError()) {
                renderErrorIcon(pageContext);
            }
            RendererUtil.registerEditableProperty(pageContext, getFieldName());
        }
        catch (IOException ioe) {
            throw new JspException("Difficulty rendering radio buttons", ioe);
        }
    }

    /**
     * Builds radio buttons for all the valid values on the field
     * @param propertyPrefix the property path from the form to the business object being rendered
     * @return a String containing the HTML for all the radio buttons
     */
    protected String buildRadioButtons() {
        StringBuilder radioButtons = new StringBuilder();
        for (Object keyLabelPairAsObject : getField().getFieldValidValues()) {
            radioButtons.append(buildRadioButton((KeyValue)keyLabelPairAsObject));
        }
        return radioButtons.toString();
    }
    
    /**
     * Given a KeyValue, generates a radio buttion representing it
     * @param keyLabelPair the key label pair to turn into a radio button
     * @param propertyPrefix the property path from the form to the business object being rendered
     * @return the HTML for the represented radio button
     */
    protected String buildRadioButton(KeyValue keyLabelPair) {
        StringBuilder radioButton = new StringBuilder();
        
        radioButton.append("<input type=\"radio\"");
        
        if (getField().getPropertyValue().equalsIgnoreCase(keyLabelPair.getKey().toString())) {
            radioButton.append(" checked=\"checked\"");
        }
        
        radioButton.append(" title=\"");
        radioButton.append(this.getAccessibleTitle());
        radioButton.append("\"");
        
        radioButton.append(" name=\"");
        radioButton.append(getFieldName());
        radioButton.append("\"");
        
        radioButton.append(" id=\"");
        radioButton.append(getFieldName()+"_"+keyLabelPair.getKey().toString().replaceAll("\\W", "_"));
        radioButton.append("\"");
        
        radioButton.append(" value=\"");
        radioButton.append(keyLabelPair.getKey());
        radioButton.append("\"");
        
        String onBlur = buildOnBlur();
        if (!StringUtils.isBlank(onBlur)) {
            radioButton.append(" ");
            radioButton.append(onBlur);
        }
        
        radioButton.append(" /> ");
        radioButton.append(keyLabelPair.getValue());
        radioButton.append(" ");
        
        return radioButton.toString();
    }

    /**
     * No quickfinder
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }
    
}
