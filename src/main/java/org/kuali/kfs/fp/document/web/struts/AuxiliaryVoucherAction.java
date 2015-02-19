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
package org.kuali.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;


/**
 * This class piggy backs on all of the functionality in the FinancialSystemTransactionalDocumentActionBase but is necessary for this document
 * type. The Auxiliary Voucher is unique in that it defines several fields that aren't typically used by the other financial
 * transaction processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 */
public class AuxiliaryVoucherAction extends VoucherAction {
    /**
     * Overrides the parent and then calls the super method after checking to see if the user just changed the voucher type.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
