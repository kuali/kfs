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
