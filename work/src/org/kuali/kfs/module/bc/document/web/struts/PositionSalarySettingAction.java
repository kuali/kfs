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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.web.struts.form.PositionSalarySettingForm;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class...
 */
public class PositionSalarySettingAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        ActionForward forward = super.execute(mapping, form, request, response);

        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;
        
        //TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        //since BC sets locks up front, but need to verify this

        //TODO form.populateAuthorizationFields(budgetConstructionDocumentAuthorizer) would normally happen here
        //but each line needs to be authorized, so need to figure out how to implement this

        //TODO should probably use service locator and call
        //DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer("<BCDoctype>");
        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        positionSalarySettingForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), bcAuthorizationType ) ){
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()) );
        }
    }

    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
        
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

        // use the passed url parms to get the record from DB 
        Map fieldValues = new HashMap();
        fieldValues.put("universityFiscalYear", positionSalarySettingForm.getUniversityFiscalYear());
        fieldValues.put("positionNumber", positionSalarySettingForm.getPositionNumber());

        BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionPosition.class,fieldValues);
        if (budgetConstructionPosition == null){
            //TODO this is an RI error need to report it
        }
        positionSalarySettingForm.setBudgetConstructionPosition(budgetConstructionPosition);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;
        BudgetConstructionPosition budgetConstructionPosition = positionSalarySettingForm.getBudgetConstructionPosition();

        // TODO validate and store changes

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(Constants.DOC_FORM_KEY, positionSalarySettingForm.getReturnFormKey());
        parameters.put(Constants.ANCHOR, positionSalarySettingForm.getReturnAnchor());
        parameters.put(Constants.REFRESH_CALLER, BCConstants.POSITION_SALARY_SETTING_REFRESH_CALLER);
        
        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.SALARY_SETTING_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered,
     * the UI knows to show all of the descriptions and labels for each of the pbgl line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PositionSalarySettingForm tForm = (PositionSalarySettingForm) form;
        tForm.setHideDetails(false);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This action toggles the value of the hide field in the user interface to "hide" so that when the page is rendered,
     * the UI displays values without all of the descriptions and labels for each of the pbgl lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PositionSalarySettingForm tForm = (PositionSalarySettingForm) form;
        tForm.setHideDetails(true);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
}
