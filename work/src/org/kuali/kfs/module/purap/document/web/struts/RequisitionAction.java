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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.VendorContract;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.web.struts.form.RequisitionForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 * @author Kuali PURAP Team (kualidev@oncourse.iu.edu)
 */
public class RequisitionAction extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionAction.class);

    /**
     * Do initialization for a new requisition
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.createDocument(kualiDocumentFormBase);
        
        ((RequisitionDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
        
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequisitionForm rqForm = (RequisitionForm) form;
        RequisitionDocument document = (RequisitionDocument) rqForm.getDocument();

        /* refresh from requisition vendor lookup */
        if (document.getVendorDetail() == null &&
            document.getVendorDetailAssignedIdentifier() != null &&
            document.getVendorHeaderGeneratedIdentifier() != null)  {

            Integer vendorDetailAssignedId = ((RequisitionDocument) rqForm.getDocument()).getVendorDetailAssignedIdentifier();
            Integer vendorHeaderGeneratedId = ((RequisitionDocument) rqForm.getDocument()).getVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(vendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail) SpringServiceLocator.getBusinessObjectService().retrieve(refreshVendorDetail);
            ((RequisitionDocument) rqForm.getDocument()).templateVendorDetail(refreshVendorDetail);
        } 
        if (Constants.KUALI_LOOKUPABLE_IMPL.equals(rqForm.getRefreshCaller()) &&
            request.getParameter("document.vendorContractGeneratedIdentifier") != null) {
            Integer vendorContractGeneratedId = ((RequisitionDocument) rqForm.getDocument()).getVendorContractGeneratedIdentifier();
            VendorContract refreshVendorContract = new VendorContract();
            refreshVendorContract.setVendorContractGeneratedIdentifier(vendorContractGeneratedId);
            refreshVendorContract = (VendorContract)SpringServiceLocator.getBusinessObjectService().retrieve(refreshVendorContract);
            ((RequisitionDocument) rqForm.getDocument()).templateVendorContract(refreshVendorContract);
        }

        //TODO add code to retrieve new building list        
        return super.refresh(mapping, form, request, response);
    }
}