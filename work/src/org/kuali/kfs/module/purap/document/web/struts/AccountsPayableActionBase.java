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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.web.struts.form.AccountsPayableFormBase;
import org.kuali.module.purap.web.struts.form.PaymentRequestForm;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;

/**
 * This class handles specific Actions requests for the AP.
 */
public class AccountsPayableActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableActionBase.class);

    /**
     * Performs refresh of objects after a lookup.
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase baseForm = (AccountsPayableFormBase) form;
        AccountsPayableDocumentBase document = (AccountsPayableDocumentBase)baseForm.getDocument();
        
        if (StringUtils.equals(baseForm.getRefreshCaller(), VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotBlank(request.getParameter(PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                Integer vendorAddressGeneratedId = document.getVendorAddressGeneratedIdentifier();
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(vendorAddressGeneratedId);
                refreshVendorAddress = (VendorAddress)SpringServiceLocator.getBusinessObjectService().retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }
        
        document.refreshAllReferences();
        
        return super.refresh(mapping, form, request, response);
    }

    /**
     * Add a new item to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call add line event/rules here
        PurchasingApItem item = purchasingForm.getAndResetNewPurchasingItemLine();
        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.addItem(item);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete an item from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;
        // TODO: should call delete line event/rules here

        PurchasingDocument purDocument = (PurchasingDocument) purchasingForm.getDocument();
        purDocument.deleteItem(getSelectedLine(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Perform calculation on item line.
     */
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) apForm.getDocument();

        customCalculate(apDoc);

        // doesn't really matter what happens above we still reset the calculate flag
        apForm.setCalculated(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is an overridable area to do calculate specific tasks
     * 
     * @param apDoc
     */
    protected void customCalculate(AccountsPayableDocument apDoc) {
        //do nothing by default
    }

    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        if(preqForm.isCalculated()) {
            return super.approve(mapping, form, request, response);
        }
        GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        if(preqForm.isCalculated()) {
            return super.save(mapping, form, request, response);
        }
        GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_SAVE_REQUIRES_CALCULATE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }
    
}