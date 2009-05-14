/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.web;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl;
import org.kuali.kfs.sys.document.web.renderers.GroupErrorsRenderer;

public class ProcurementCardAccoutingLineGroupImpl extends DefaultAccountingLineGroupImpl {




    /**
     * Renders any errors for the group
     * @param pageContext the page context where the errors will be rendered on
     * @param parentTag the parent tag requesting the rendering
     */
    @Override
    protected void renderErrors(PageContext pageContext, Tag parentTag) throws JspException {

        /*
         * We need to match up the error with the proper transaction group since we have multiple 
         * AccountingLineGroups. Without this logic, any error will be printed in the header of 
         * each AccountingLineGroup. This will attempt to find the correct Transaction to place the
         * errors in. propName should evaluate to document.transactionEntries[XX].targetAccountingLines
         * and the errorlist should evaluate to document.transactionEntries[XX].targetAccountingLines[YY]
         * 
         */
        
        String propName = getCollectionPropertyName();
        List errors = getErrors();
        for (Iterator itr = errors.iterator(); itr.hasNext();) {
            String error = (String)itr.next();
            if (error.startsWith(propName)) {

                GroupErrorsRenderer errorRenderer = new GroupErrorsRenderer();
                errorRenderer.setErrorKeyMatch(getGroupDefinition().getErrorKey());
                errorRenderer.setColSpan(getWidthInCells());
                errorRenderer.setErrorPropertyList(getErrors());
                errorRenderer.setSectionTitle(getGroupDefinition().getGroupLabel());
                errorRenderer.render(pageContext, parentTag);

                for (String displayedErrorKey : errorRenderer.getErrorsRendered()) {
                    getDisplayedErrors().put(displayedErrorKey, "true");
                }

                errorRenderer.clear();
            }
        }

    }
}