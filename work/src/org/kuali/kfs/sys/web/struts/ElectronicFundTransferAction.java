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
package org.kuali.kfs.sys.web.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicFundTransferActionHelper;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;

public class ElectronicFundTransferAction extends KualiAction {
    private final static String START_BEAN = "electronicFundTransferStartAction";
    private final static String REFRESH_BEAN = "electronicFundTransferRefreshAction";
    private final static String CLAIM_BEAN = "electronicFundTransferClaimAction";
    private final static String CANCEL_BEAN = "electronicFundTransferCancelAction";
    
    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ElectronicFundTransferForm eftForm = (ElectronicFundTransferForm)form;
        eftForm.setAvailableClaimingDocumentStrategies(SpringContext.getBean(ElectronicPaymentClaimingService.class).getClaimingDocumentChoices(GlobalVariables.getUserSession().getPerson()));
        return super.execute(mapping, form, request, response);
    }
    
    /**
     * The action that sends the user to the correct lookup for them
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getActionHelpers().get(ElectronicFundTransferAction.START_BEAN).performAction((ElectronicFundTransferForm)form, mapping, request.getParameterMap(), getApplicationBaseUrl());
    }
 
    /**
     * The action that is called when a document is loaded, after returning from the multivalue select
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getActionHelpers().get(ElectronicFundTransferAction.REFRESH_BEAN).performAction((ElectronicFundTransferForm)form, mapping, request.getParameterMap(), getApplicationBaseUrl());
    }
    
    /**
     * The response to the "claim" request
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward claim(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getActionHelpers().get(ElectronicFundTransferAction.CLAIM_BEAN).performAction((ElectronicFundTransferForm)form, mapping, request.getParameterMap(), getApplicationBaseUrl());
    }
    
    /**
     * The response to the "cancel" request
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getActionHelpers().get(ElectronicFundTransferAction.CANCEL_BEAN).performAction((ElectronicFundTransferForm)form, mapping, request.getParameterMap(), getApplicationBaseUrl());
    }
    
    /**
     * @return all of the beans that act as ElectronicFundTransferActionHelper
     */
    private Map<String, ElectronicFundTransferActionHelper> getActionHelpers() {
        return SpringContext.getBeansOfType(ElectronicFundTransferActionHelper.class);
    }
}

