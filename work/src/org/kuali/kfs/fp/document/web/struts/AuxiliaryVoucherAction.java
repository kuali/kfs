/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.web.struts.form.AuxiliaryVoucherForm;


/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. The Auxiliary Voucher is unique in that it defines several fields that aren't typically used by the other financial
 * transaction processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 * 
 * 
 */
public class AuxiliaryVoucherAction extends VoucherAction {
    /**
     * Overrides the parent and then calls the super method after checking to see if the user just changed the voucher type.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
     *      HttpServletResponse response)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuxiliaryVoucherForm avForm = (AuxiliaryVoucherForm) form;

        // now check to see if the voucher type was changed and if so, we want to
        // set the method to call so that the appropriate action can be invoked
        // did it this way b/c the changing of the type causes the page to re-submit
        // and we need to process some stuff if it's changed
        ActionForward returnForward;
        if (StringUtils.isNotBlank(avForm.getOriginalVoucherType()) && !avForm.getAuxiliaryVoucherDocument().getTypeCode().equals(avForm.getOriginalVoucherType())) {
            returnForward = super.dispatchMethod(mapping, form, request, response, KFSConstants.AuxiliaryVoucher.CHANGE_VOUCHER_TYPE);
            // must call this here, because execute in the super method will never have control for this particular action
            // this is called in the parent by super.execute()
            Document document = avForm.getDocument();
            DocumentAuthorizer documentAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(document);
            avForm.populateAuthorizationFields(documentAuthorizer);
        }
        else { // otherwise call the super
            returnForward = super.execute(mapping, avForm, request, response);
        }
        return returnForward;
    }

    /**
     * This action method is responsible for clearing the GLPEs for an AV after the voucher type changes, since a voucher type
     * change makes any previously generated GLPEs inaccurate.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward changeVoucherType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuxiliaryVoucherForm avForm = (AuxiliaryVoucherForm) form;

        AuxiliaryVoucherDocument avDoc = avForm.getAuxiliaryVoucherDocument();

        // clear the glpes now
        avDoc.getGeneralLedgerPendingEntries().clear();

        // make sure to set the original type to the new one now
        avForm.setOriginalVoucherType(avDoc.getTypeCode());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        
        // Fix for KULEDOCS-1701, update the original voucher type so that the execute method in
        // this class will call the right block of code
        AuxiliaryVoucherForm avForm = (AuxiliaryVoucherForm) form;
        AuxiliaryVoucherDocument avDoc = avForm.getAuxiliaryVoucherDocument();
        avForm.setOriginalVoucherType(avDoc.getTypeCode());
        
        return forward;
    }
}