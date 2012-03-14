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
package org.kuali.kfs.module.purap.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.PhoneNumberService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class BulkReceivingAction extends KualiTransactionalDocumentActionBase {
    protected static final Logger LOG = Logger.getLogger(BulkReceivingAction.class);

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        BulkReceivingForm blkForm = (BulkReceivingForm) kualiDocumentFormBase;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkForm.getDocument();

        blkRecDoc.setPurchaseOrderIdentifier(blkForm.getPurchaseOrderId());

        blkRecDoc.initiateDocument();
    }

    public ActionForward continueBulkReceiving(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkReceivingForm blkForm = (BulkReceivingForm) form;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkForm.getDocument();

        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(blkRecDoc.getPurchaseOrderIdentifier());
        if (ObjectUtils.isNotNull(po)) {
            // TODO figure out a more straightforward way to do this. ailish put this in so the link id would be set and the perm
            // check would work
            blkRecDoc.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

            // TODO hjs-check to see if user is allowed to initiate doc based on PO sensitive data (add this to all other docs
            // except acm doc)
            if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(blkRecDoc).isAuthorizedByTemplate(blkRecDoc, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
                throw buildAuthorizationException("initiate document", blkRecDoc);
            }
        }

        // perform duplicate check
        ActionForward forward = isDuplicateDocumentEntry(mapping, form, request, response, blkRecDoc);
        if (forward != null) {
            return forward;
        }

        // populate and save bulk Receiving Document from Purchase Order
        SpringContext.getBean(BulkReceivingService.class).populateAndSaveBulkReceivingDocument(blkRecDoc);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkReceivingForm blkRecForm = (BulkReceivingForm) form;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkRecForm.getDocument();

        blkRecDoc.clearInitFields();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected ActionForward isDuplicateDocumentEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, BulkReceivingDocument bulkReceivingDocument) throws Exception {
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringContext.getBean(BulkReceivingService.class).bulkReceivingDuplicateMessages(bulkReceivingDocument);

        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION, duplicateMessages.get(PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    public ActionForward printReceivingTicket(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String blkDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();

        try {
            // will throw validation exception if errors occur
            SpringContext.getBean(BulkReceivingService.class).performPrintReceivingTicketPDF(blkDocId, baosPDF);

            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            String useJavascript = request.getParameter("useJavascript");
            if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                sbContentDispValue.append("attachment");
            }
            else {
                sbContentDispValue.append("inline");
            }
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_RECEIVING_TICKET_");
            sbFilename.append(blkDocId);
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos = response.getOutputStream();
            baosPDF.writeTo(sos);
            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }

    public ActionForward printReceivingTicketPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkReceivingForm blkRecForm = (BulkReceivingForm) form;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkRecForm.getDocument();

        String basePath = getApplicationBaseUrl();
        String docId = blkRecDoc.getDocumentNumber();
        String methodToCallPrintPurchaseOrderPDF = "printReceivingTicket";
        String methodToCallDocHandler = "docHandler";
        String printReceivingTicketPDFUrl = getUrlForPrintReceivingTicket(basePath, docId, methodToCallPrintPurchaseOrderPDF);
        String displayReceivingDocTabbedPageUrl = getUrlForPrintReceivingTicket(basePath, docId, methodToCallDocHandler);
        request.setAttribute("printReceivingTicketPDFUrl", printReceivingTicketPDFUrl);
        request.setAttribute("displayReceivingDocTabbedPageUrl", displayReceivingDocTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.BULK_RECEIVING);
        request.setAttribute("receivingDocLabel", label);

        return mapping.findForward("printReceivingTicketPDF");
    }

    protected String getUrlForPrintReceivingTicket(String basePath, String docId, String methodToCall) {

        StringBuffer result = new StringBuffer(basePath);
        result.append("/purapBulkReceiving.do?methodToCall=");
        result.append(methodToCall);
        result.append("&docId=");
        result.append(docId);
        result.append("&command=displayDocSearchView");

        return result.toString();
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkReceivingForm blkRecForm = (BulkReceivingForm) form;

        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkRecForm.getDocument();
        String refreshCaller = blkRecForm.getRefreshCaller();
        PhoneNumberService phoneNumberService = SpringContext.getBean(PhoneNumberService.class);

        // Format phone numbers
        blkRecDoc.setInstitutionContactPhoneNumber(phoneNumberService.formatNumberIfPossible(blkRecDoc.getInstitutionContactPhoneNumber()));
        blkRecDoc.setRequestorPersonPhoneNumber(phoneNumberService.formatNumberIfPossible(blkRecDoc.getRequestorPersonPhoneNumber()));
        blkRecDoc.setDeliveryToPhoneNumber(phoneNumberService.formatNumberIfPossible(blkRecDoc.getDeliveryToPhoneNumber()));

        // Refreshing the fields after returning from a vendor lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_LOOKUPABLE_IMPL) && blkRecDoc.getVendorDetailAssignedIdentifier() != null && blkRecDoc.getVendorHeaderGeneratedIdentifier() != null) {

            // retrieve vendor based on selection from vendor lookup
            blkRecDoc.refreshReferenceObject("vendorDetail");
            blkRecDoc.setVendorName(blkRecDoc.getVendorDetail().getVendorName());

            // populate default address based on selected vendor
            VendorAddress defaultAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(blkRecDoc.getVendorDetail().getVendorAddresses(), blkRecDoc.getVendorDetail().getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
            if (ObjectUtils.isNotNull(defaultAddress)) {
                blkRecDoc.setVendorLine1Address(defaultAddress.getVendorLine1Address());
                blkRecDoc.setVendorLine2Address(defaultAddress.getVendorLine2Address());
                blkRecDoc.setVendorCityName(defaultAddress.getVendorCityName());
                blkRecDoc.setVendorStateCode(defaultAddress.getVendorStateCode());
                blkRecDoc.setVendorPostalCode(defaultAddress.getVendorZipCode());
                blkRecDoc.setVendorCountryCode(defaultAddress.getVendorCountryCode());
            }
        }

        // Refreshing the fields after returning from an address lookup in the vendor tab
        if (StringUtils.equals(refreshCaller, VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotEmpty(request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                // retrieve address based on selection from address lookup
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(blkRecDoc.getVendorAddressGeneratedIdentifier());
                refreshVendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorAddress);
                if (ObjectUtils.isNotNull(refreshVendorAddress)) {
                    blkRecDoc.setVendorLine1Address(refreshVendorAddress.getVendorLine1Address());
                    blkRecDoc.setVendorLine2Address(refreshVendorAddress.getVendorLine2Address());
                    blkRecDoc.setVendorCityName(refreshVendorAddress.getVendorCityName());
                    blkRecDoc.setVendorStateCode(refreshVendorAddress.getVendorStateCode());
                    blkRecDoc.setVendorPostalCode(refreshVendorAddress.getVendorZipCode());
                    blkRecDoc.setVendorCountryCode(refreshVendorAddress.getVendorCountryCode());
                }
            }
        }

        // Refreshing corresponding fields after returning from various kuali lookups
        if (StringUtils.equals(refreshCaller, KFSConstants.KUALI_LOOKUPABLE_IMPL)) {
            if (request.getParameter("document.deliveryCampusCode") != null) {
                // returning from a building or campus lookup on the delivery tab

                if (request.getParameter("document.deliveryBuildingName") == null) {
                    // came from campus lookup not building, so clear building
                    blkRecDoc.setDeliveryBuildingCode("");
                    blkRecDoc.setDeliveryBuildingLine1Address("");
                    blkRecDoc.setDeliveryBuildingLine2Address("");
                    blkRecDoc.setDeliveryBuildingRoomNumber("");
                    blkRecDoc.setDeliveryCityName("");
                    blkRecDoc.setDeliveryStateCode("");
                    blkRecDoc.setDeliveryPostalCode("");
                    blkRecDoc.setDeliveryCountryCode("");
                }
                else {
                    // came from building lookup then turn off "OTHER" and clear room and line2address
                    blkRecDoc.setDeliveryBuildingOtherIndicator(false);
                    blkRecDoc.setDeliveryBuildingRoomNumber("");
                    blkRecDoc.setDeliveryBuildingLine2Address("");
                }
            }
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Setup document to use "OTHER" building
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request A HttpServletRequest
     * @param response A HttpServletResponse
     * @return An ActionForward
     * @throws Exception
     */
    public ActionForward useOtherDeliveryBuilding(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkReceivingForm baseForm = (BulkReceivingForm) form;
        BulkReceivingDocument document = (BulkReceivingDocument) baseForm.getDocument();

        document.setDeliveryBuildingOtherIndicator(true);
        document.setDeliveryBuildingName("");
        document.setDeliveryBuildingCode("");
        document.setDeliveryBuildingLine1Address("");
        document.setDeliveryBuildingLine2Address("");
        document.setDeliveryBuildingRoomNumber("");
        document.setDeliveryCityName("");
        document.setDeliveryStateCode("");
        document.setDeliveryCountryCode("");
        document.setDeliveryPostalCode("");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    @Override
    protected void populateAdHocActionRequestCodes(KualiDocumentFormBase formBase){
        Document document = formBase.getDocument();
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        Map<String,String> adHocActionRequestCodes = new HashMap<String,String>();

        if (documentAuthorizer.canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_FYI_REQ, GlobalVariables.getUserSession().getPerson())) {
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL);
        }
        if ( (document.getDocumentHeader().getWorkflowDocument().isInitiated()
              || document.getDocumentHeader().getWorkflowDocument().isSaved()
              || document.getDocumentHeader().getWorkflowDocument().isEnroute()
              )&& documentAuthorizer.canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, GlobalVariables.getUserSession().getPerson())) {
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
        } 
        formBase.setAdHocActionRequestCodes(adHocActionRequestCodes);

    }

}
