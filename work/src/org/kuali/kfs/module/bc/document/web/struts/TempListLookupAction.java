/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiLookupAction;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSelect;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.module.budget.bo.BudgetConstructionPositionSelect;
import org.kuali.module.budget.service.OrganizationBCDocumentSearchService;
import org.kuali.module.budget.service.OrganizationSalarySettingSearchService;
import org.kuali.module.budget.web.struts.form.TempListLookupForm;

/**
 * Action class to display special budget lookup screens.
 */
public class TempListLookupAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TempListLookupAction.class);

    /**
     * TempListLookupAction can be called to build and display different lists. This method determines what the requested behavior is
     * and either makes a build call for that list or sets up a message (if the list has already been built). If the request parameter
     * showInitialResults is true, an initial search will be performed before display of the screen.
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */ 
    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // determine requested lookup action
        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
                break;
       
            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildPositionSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
                break;
        
            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV:
                // Show Account above current point of view for user
                // The table was already built in OrganizationSelectionTreeAction.performReport
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_ACCOUNT_LIST);
                break;
       
            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_BUDGETED_DOCUMENTS:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) in the point of view subtree for the selected org(s)
                // The table was already built in OrganizationSelectionTreeAction.performShowBudgetDocs
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_ACCOUNT_LIST);
                break;
        }

        ActionForward forward = super.start(mapping, form, request, response);
        if (tempListLookupForm.isShowInitialResults()) {
            forward = search(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * This differs from KualiLookupAction.clearValues in that any atributes marked hidden will not be cleared. This is to support
     * BC temp tables that use personUniversalIdentifier to operate on the set of rows associated with the current user.
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#clearValues(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO) && !field.getFieldType().equals(Field.HIDDEN)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiLookupAction#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        
        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier());
                break;
       
            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanPositionSelect(tempListLookupForm.getPersonUniversalIdentifier());
                break;
        
            default:               
                SpringContext.getBean(OrganizationBCDocumentSearchService.class).cleanAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        
        return super.cancel(mapping, form, request, response);
    }

    /**
     * Continues the organization report action after viewing the account list.
     */
    public ActionForward submitReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, tempListLookupForm.getFormKey());
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + BCConstants.ORG_SEL_TREE_ACTION);
        if (tempListLookupForm.isReportConsolidation()) {
            parameters.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        parameters.put(BCConstants.Report.REPORT_MODE, tempListLookupForm.getReportMode());
        parameters.put(BCConstants.CURRENT_POINT_OF_VIEW_KEYCODE, tempListLookupForm.getCurrentPointOfViewKeyCode());

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_REPORT_SELECTION_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }
}
