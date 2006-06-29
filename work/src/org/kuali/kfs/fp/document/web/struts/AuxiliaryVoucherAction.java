/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.DocumentAuthorizer;
import org.kuali.core.document.Document;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.web.struts.form.AuxiliaryVoucherForm;


/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. The Auxiliary Voucher is unique in that it defines several fields that aren't typically used by the other financial
 * transaction processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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
            returnForward = super.dispatchMethod(mapping, form, request, response, Constants.AuxiliaryVoucher.CHANGE_VOUCHER_TYPE);
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
     * This action method is responsible for clearing the GLPEs for an AV after the voucher type changes, since a voucher type change makes any 
     * previously generated GLPEs inaccurate.
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

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}