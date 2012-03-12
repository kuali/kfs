/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;

/**
 * Extension to override rendering of errors to first verify the error key starts with the give collection name.
 */
public class CollectionAccountingLineGroupImpl extends DefaultAccountingLineGroupImpl {

    /**
     * Renders any errors for the group
     * 
     * @param pageContext the page context where the errors will be rendered on
     * @param parentTag the parent tag requesting the rendering
     */
    @Override
    protected void renderErrors(PageContext pageContext, Tag parentTag) throws JspException {
        String propName = getCollectionPropertyName();
        GroupErrorsRenderer errorRenderer = new GroupErrorsRenderer();
        List errors = errorRenderer.getErrorPropertyList(pageContext);
        if ( errors != null && !errors.isEmpty()) { 
            for (Iterator itr = errors.iterator(); itr.hasNext();) {
                String error = (String) itr.next();
                if (error.startsWith(propName)) {
                    errorRenderer.setErrorKeyMatch(error);
                    errorRenderer.setColSpan(getWidthInCells());
                    errorRenderer.render(pageContext, parentTag);
    
                    moveListToMap(errorRenderer.getErrorsRendered(), getDisplayedErrors());
                    moveListToMap(errorRenderer.getWarningsRendered(), getDisplayedWarnings());
                    moveListToMap(errorRenderer.getInfoRendered(), getDisplayedInfo());
    
                    errorRenderer.clear();
                }
            }
        }
    }
}
