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
