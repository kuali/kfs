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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.struts.taglib.html.HiddenTag;

/**
 * A renderer which renders a String - but also renders a hidden tag to persist the value 
 * from the String (or an overriden persisting string)
 */
public class PersistingTagRenderer extends StringRenderer {
    private String persistingProperty;
    private String valueToPersist;
    private HiddenTag persistingTag = new HiddenTag();
    
    /**
     * Gets the persistingProperty attribute. 
     * @return Returns the persistingProperty.
     */
    public String getPersistingProperty() {
        return persistingProperty;
    }
    /**
     * Sets the persistingProperty attribute value.
     * @param persistingProperty The persistingProperty to set.
     */
    public void setPersistingProperty(String persistingProperty) {
        this.persistingProperty = persistingProperty;
    }
    
    /**
     * Gets the valueToPersist attribute. 
     * @return Returns the valueToPersist.
     */
    public String getValueToPersist() {
        return valueToPersist;
    }
    /**
     * Sets the valueToPersist attribute value.
     * @param valueToPersist The valueToPersist to set.
     */
    public void setValueToPersist(String valueToPersist) {
        this.valueToPersist = valueToPersist;
    }
    /**
     * @see org.kuali.kfs.sys.document.web.renderers.StringRenderer#clear()
     */
    @Override
    public void clear() {
        super.clear();
        
        persistingTag.setPageContext(null);
        persistingTag.setParent(null);
        persistingTag.setProperty(null);
        persistingTag.setValue(null);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.renderers.StringRenderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    @Override
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        super.render(pageContext, parentTag);

    }
}
