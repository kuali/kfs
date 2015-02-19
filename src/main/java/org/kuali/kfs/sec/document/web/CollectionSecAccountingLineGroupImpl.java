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
package org.kuali.kfs.sec.document.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;


/**
 * Integrates with access security module to check security on accounting lines before rendering
 */
public class CollectionSecAccountingLineGroupImpl extends SecAccountingLineGroupImpl {

    /**
     * Constructs a SecAccountingLineGroupImpl
     */
    public CollectionSecAccountingLineGroupImpl() {
        super();
    }

    /**
     * Adds info message if we have restricted view of any accounting lines and matches only messages for collection
     * 
     * @see org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl#renderErrors(javax.servlet.jsp.PageContext,
     *      javax.servlet.jsp.tagext.Tag)
     */
    @Override
    protected void renderErrors(PageContext pageContext, Tag parentTag) throws JspException {
        renderSecurityMessage(pageContext, parentTag);

        GroupErrorsRenderer errorRenderer = new GroupErrorsRenderer();
        List errors = errorRenderer.getErrorPropertyList(pageContext);
        if (errors != null && !errors.isEmpty()) {
            for (Iterator itr = errors.iterator(); itr.hasNext();) {
                String error = (String) itr.next();
                if (error.startsWith(collectionItemPropertyName)) {
                    renderMessages(pageContext, parentTag, error);
                }
            }
        }
    }

}
