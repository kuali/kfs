/*
 * Copyright 2009 The Kuali Foundation.
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
