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

