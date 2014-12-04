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
