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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.rice.kns.web.ui.Field;

/**
 * Renders a set of read only fields to a table cell
 */
public class MultipleReadOnlyFieldsRenderer implements Renderer {
    private List<Field> fields;
    private ReadOnlyRenderer readOnlyRenderer = new ReadOnlyRenderer();

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        fields = null;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     * KRAD Conversion: Customization of render with fieldsand inquiry urls.
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            if (fields != null && !fields.isEmpty()) {
                out.write(beginReadOnlyLayout());
                for (Field field : fields) {
                    out.write(beginReadOnlyLabel());
                    out.write(renderLabel(field));
                    out.write(endReadOnlyLabel());
                    out.write(beginReadOnlyValue());
                    readOnlyRenderer.setField(field);
                    if (field.getInquiryURL() != null) {
                        readOnlyRenderer.setShouldRenderInquiry(true);
                    }
                    readOnlyRenderer.render(pageContext, parentTag);
                    readOnlyRenderer.clear();
                    out.write(endReadOnlyValue());
                }
                out.write(endReadOnlyLayout());
            } else {
                out.write(renderEmptyCell());
            }
        }
        catch (IOException ioe) {
            throw new JspException("Could not render MultipleReadOnlyFields", ioe);
        }
    }
    
    /**
     * @return the value to render for an empty cell
     */
    protected String renderEmptyCell() {
        return "&nbsp;";
    }
    
    protected String beginReadOnlyLayout() {
        return "<table>";
    }
    
    protected String beginReadOnlyLabel() {
        return "<tr><td width=\"50%\">";
    }
    
    protected String endReadOnlyLabel() {
        return "</td>";
    }
    
    protected String beginReadOnlyValue() {
        return "<td width=\"50%\">";
    }
    
    protected String endReadOnlyValue() {
        return "</td></tr>";
    }

    /**
     * KRAD Conversion: getting field label
     */
    protected String renderLabel(Field field) {
        return field.getFieldLabel();
    }
    
    protected String endReadOnlyLayout() {
        return "</table>";
    }

    /**
     * @return the current list of fields to render through this render pass
     *
     * KRAD Conversion: getting fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Associate fields with this render pass of the renderer
     * @param fields the fields to render through this render pass
     *
     * KRAD Conversion: setting fields
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
