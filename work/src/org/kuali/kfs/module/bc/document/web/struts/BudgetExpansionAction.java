/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionForm;

/**
 * Handles close action to implement Budget return to caller (expansion screen) flow.
 */
public class BudgetExpansionAction extends KualiAction {

    /**
     * Handling for screen close. Default action is return to caller.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return returnToCaller(mapping, form, request, response);
    }

    /**
     * Return to form's back location (usually previous screen). Returns back the form key that was passed in for the previous form
     * and any previous anchor position. Default refresh method is executed.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetExpansionForm budgetExpansionForm = (BudgetExpansionForm) form;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, budgetExpansionForm.getReturnFormKey());

        if (StringUtils.isNotEmpty(budgetExpansionForm.getReturnAnchor())) {
            parameters.put(KFSConstants.ANCHOR, budgetExpansionForm.getReturnAnchor());
        }

        parameters.put(KFSConstants.REFRESH_CALLER, this.getClass().getName());

        String backUrl = UrlFactory.parameterizeUrl(budgetExpansionForm.getBackLocation(), parameters);

        return new ActionForward(backUrl, true);
    }
}
