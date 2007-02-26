/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PhoneNumberService;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 */
public class PurchaseOrderAction extends PurchasingActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();

        //Handling lookups for alternate vendor for escrow payment that are only specific to Purchase Order
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null &&
            request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
            Integer alternateVendorDetailAssignedId = document.getAlternateVendorDetailAssignedIdentifier();
            Integer alternateVendorHeaderGeneratedId = document.getAlternateVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(alternateVendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail)businessObjectService.retrieve(refreshVendorDetail);
            document.templateAlternateVendor(refreshVendorDetail);
        } 
        
        String newStipulation = request.getParameter("document.vendorStipulationDescription");
        if (StringUtils.isNotEmpty(newStipulation)) {
            poForm.getNewPurchaseOrderVendorStipulationLine().setVendorStipulationDescription(newStipulation);
        }
        PhoneNumberService phoneNumberService = SpringServiceLocator.getPhoneNumberService();

        // Format phone numbers
        document.setInstitutionContactPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getInstitutionContactPhoneNumber()));    
        document.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getRequestorPersonPhoneNumber()));    
        document.setDeliveryToPhoneNumber(phoneNumberService.formatNumberIfPossible(document.getDeliveryToPhoneNumber()));    
 
        return super.refresh(mapping, form, request, response);
    }

    public ActionForward viewRelatedDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewRelatedDocuments() enter action");

        //TODO add code

        return mapping.findForward("viewRelatedDocuments");
    }

    public ActionForward viewPaymentHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewPaymentHistory() enter action");

        //TODO add code

        return mapping.findForward("viewPaymentHistory");
    }

    /**
     * Add a stipulation to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        // TODO: should this be calling the Rule class?
        if (StringUtils.isBlank(poForm.getNewPurchaseOrderVendorStipulationLine().getVendorStipulationDescription())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
        }
        else {
            PurchaseOrderVendorStipulation newStipulation = poForm.getAndResetNewPurchaseOrderVendorStipulationLine();
            document.getPurchaseOrderVendorStipulations().add(newStipulation);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Delete a stipulation from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorStipulations().remove(getSelectedLine(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

}