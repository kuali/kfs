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
