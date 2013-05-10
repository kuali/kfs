/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.vnd.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.batch.service.VendorExcludeService;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;

public class VendorExclusionAction extends KualiAction {

    public ActionForward confirmDebarredVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        VendorExclusionForm vendorExclusionForm = (VendorExclusionForm) form;

        Map<String, String> fields = new HashMap<String, String>();
        fields.put("confirmStatusCode", vendorExclusionForm.getConfirmStatusCode());
        fields.put("vendorExclusionStatus", vendorExclusionForm.getVendorExclusionStatus());
        fields.put("vendorType", vendorExclusionForm.getVendorType());
        vendorExclusionForm.setFields(fields);
        SpringContext.getBean(VendorExcludeService.class).confirmDebarredVendor(Integer.parseInt(vendorExclusionForm.getDebarredVendorId()));
        return new ActionForward(getBackUrl(vendorExclusionForm), true);
    }

    public ActionForward denyDebarredVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        VendorExclusionForm vendorExclusionForm = (VendorExclusionForm) form;

        Map<String, String> fields = new HashMap<String, String>();
        fields.put("confirmStatusCode", vendorExclusionForm.getConfirmStatusCode());
        fields.put("vendorExclusionStatus", vendorExclusionForm.getVendorExclusionStatus());
        fields.put("vendorType", vendorExclusionForm.getVendorType());
        vendorExclusionForm.setFields(fields);
        SpringContext.getBean(VendorExcludeService.class).denyDebarredVendor(Integer.parseInt(vendorExclusionForm.getDebarredVendorId()));
        return new ActionForward(getBackUrl(vendorExclusionForm), true);
    }

    protected String getBackUrl(VendorExclusionForm form) {
        return form.getBackLocation() + "?methodToCall=search&docFormKey=88888888&businessObjectClassName=" + form.getBusinessObjectClassName()+ "&confirmStatusCode=" +  form.getConfirmStatusCode()
        + "&vendorExclusionStatus=" +  form.getVendorExclusionStatus() + "&vendorType=" +  form.getVendorType() + "&suppressActions=No&showMaintenanceLinks=Yes";
    }

    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
    }

}
