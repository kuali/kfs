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
 * This class...
 */
public class TempListLookupAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TempListLookupAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // TODO use switch here
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionIntendedIncumbentSelect.class.getName())) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionPositionSelect.class.getName())) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildPositionSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        // TODO may need to pass another parameter for building variations of AccountSelect
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionAccountSelect.class.getName())) {
            
            // ORG Report mode
            if (tempListLookupForm.getReportMode() != null) {
                String[] flds = tempListLookupForm.getCurrentPointOfViewKeyCode().split("[-]");
                SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildBudgetedAccountsAbovePointsOfView(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear(), flds[0], flds[1]);
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_ACCOUNT_LIST);   
            } else {
                SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_ACCOUNT_LIST);    
            }
        }

        forward = super.start(mapping, form, request, response);
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

        // TODO use switch here
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionIntendedIncumbentSelect.class.getName())) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionPositionSelect.class.getName())) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanPositionSelect(tempListLookupForm.getPersonUniversalIdentifier());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals(BudgetConstructionAccountSelect.class.getName())) {
            SpringContext.getBean(OrganizationBCDocumentSearchService.class).cleanAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }

        return super.cancel(mapping, form, request, response);
    }
    
    public ActionForward submitReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        //String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        // build out base path for return location, using config service
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        
        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KFSConstants.DOC_FORM_KEY, tempListLookupForm.getFormKey());
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + BCConstants.ORG_SEL_TREE_ACTION);
        parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        if (tempListLookupForm.isReportConsolidation()) {
            parameters.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        parameters.put(BCConstants.Report.REPORT_MODE, tempListLookupForm.getReportMode());
        if (tempListLookupForm.isBuildControlList()){
            parameters.put(BCConstants.Report.BUILD_CONTROL_LIST, "true");
        }
        
        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_REPORT_SELECTION_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }
}
