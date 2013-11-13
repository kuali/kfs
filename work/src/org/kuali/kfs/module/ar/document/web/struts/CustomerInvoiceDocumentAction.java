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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.DiscountCustomerInvoiceDetailEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerInvoiceDetailEvent;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

public class CustomerInvoiceDocumentAction extends KualiAccountingDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentAction.class);
    
    /**
     * Overriding to make it easier to distinguish discount lines and lines that are associated to discounts
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        if(StringUtils.isBlank(customerInvoiceDocument.getDocumentNumber())) {
            String docId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
            customerInvoiceDocument.setDocumentNumber(docId);
            customerInvoiceDocument.refresh();
        }
        customerInvoiceDocument.updateAccountReceivableObjectCodes();
        try {
            // proceed as usual
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
            ActionForward result = super.execute(mapping, form, request, response);
            return result;
        }
        finally {
            // update it again for display purposes
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        }
    }

    /**
     * Called when customer invoice document is initiated.
     * 
     * Makes a call to parent's createDocument method, but also defaults values for customer invoice document. Line which inserts
     * Customer Invoice Detail (i.e. insertSourceLine) has its values defaulted by
     * CustomerInvoiceDocumentForm.createNewSourceAccountingLine()
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        // set up the default values for customer invoice document
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
    }

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CustomerInvoiceDocumentForm form = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        form.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();

    }

    /**
     * Method that will take the current document, copy it, replace all references to doc header id with a new one, clear pending
     * entries, clear notes, and reset version numbers
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        
        // perform discount check
        ActionForward forward = performInvoiceWithDiscountsCheck(mapping, form, request, response, customerInvoiceDocument);
        if (forward != null) {
            return forward;
        }

        forward = super.copy(mapping, form, request, response);
        // KFSCNTRB-1737- We don't want to copy the closed date if the (copied) invoice isn't closed.
        if (customerInvoiceDocument.isOpenInvoiceIndicator()) {
                customerInvoiceDocument.setClosedDate(null);
        }
        return forward;
    }


    /**
     * This method checks if the user wants to copy a document that contains a discount line.  If yes, this method returns null. If no,
     * this method returns the "basic" forward. 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param customerInvoiceDocument
     * @return
     * @throws Exception
     */
    protected ActionForward performInvoiceWithDiscountsCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, CustomerInvoiceDocument customerInvoiceDocument) throws Exception {
        ActionForward forward = null;

        if( customerInvoiceDocument.hasAtLeastOneDiscount() ){
            
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                return this.performQuestionWithoutInput(mapping, form, request, response, ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION, 
                        "This document contains a discount line.  Are you sure you want to copy this document?", KFSConstants.CONFIRMATION_QUESTION,
                        KFSConstants.ROUTE_METHOD, "");
            }
    
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION.equals(question) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * This method is the action for refreshing the added source line (or customer invoice detail) based off a provided invoice item
     * code.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshNewSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail newCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail loadedCustomerInvoiceDetail = customerInvoiceDetailService.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(newCustomerInvoiceDetail.getInvoiceItemCode(), customerInvoiceDocument.getBillByChartOfAccountCode(), customerInvoiceDocument.getBilledByOrganizationCode());
        if (loadedCustomerInvoiceDetail == null) {
            loadedCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();
        }

        customerInvoiceDocumentForm.setNewSourceLine(loadedCustomerInvoiceDetail);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is the action for recalculating the amount added line assuming that the unit price or quantity has changed
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculateSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerInvoiceDetailEvent(errorPath, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);
            customerInvoiceDetailService.updateAccountsForCorrespondingDiscount(customerInvoiceDetail);
        }

        // Update the doc total
        ((FinancialSystemDocumentHeader) customerInvoiceDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is used for inserting a discount line based on a selected source line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward discountSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail parentCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        // document.sourceAccountingLine[0].invoiceItemUnitPrice
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DiscountCustomerInvoiceDetailEvent(errorPath, customerInvoiceDocumentForm.getDocument(), parentCustomerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetail discountCustomerInvoiceDetail = SpringContext.getBean(CustomerInvoiceDetailService.class).getDiscountCustomerInvoiceDetailForCurrentYear(parentCustomerInvoiceDetail, customerInvoiceDocument);
            discountCustomerInvoiceDetail.refreshNonUpdateableReferences();
            insertAccountingLine(true, customerInvoiceDocumentForm, discountCustomerInvoiceDetail);

            // also set parent customer invoice detail line to have discount line seq number
            parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(discountCustomerInvoiceDetail.getSequenceNumber());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Removed salesTax checking. Need to verify if this check has be moved out later of the KualiAccountingDocumentActionBase
     * class. If so just use the parent class' insertSourceLine method.
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();

        // populate chartOfAccountsCode from account number if accounts cant cross chart and Javascript is turned off
        //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(customerInvoiceDetail);
        
        // make sure amount is up to date before rules
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));

        if (rulePassed) {

            // add accountingLine
            customerInvoiceDetail.refreshNonUpdateableReferences();
            service.prepareCustomerInvoiceDetailForAdd(customerInvoiceDetail, customerInvoiceDocument);
            insertAccountingLine(true, customerInvoiceDocumentForm, customerInvoiceDetail);

            // clear the used newTargetLine
            customerInvoiceDocumentForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Overrides method to delete accounting line. If line to be deleted has a corresponding discount line, the corresponding
     * discount line is also deleted. If the line to be delete is a discount line, set the reference for the parent to null
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {

        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) financialDocumentForm).getCustomerInvoiceDocument();

        // if line to delete is a discount parent discountLine, remove discount line too
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(deleteIndex);
        if (customerInvoiceDetail.isDiscountLineParent()) {
            customerInvoiceDocument.removeDiscountLineBasedOnParentLineIndex(deleteIndex);
        }
        else if (customerInvoiceDocument.isDiscountLineBasedOnSequenceNumber(customerInvoiceDetail.getSequenceNumber())) {

            // if line to delete is a discount line, set discount line reference for parent to null
            CustomerInvoiceDetail parentCustomerInvoiceDetail = customerInvoiceDetail.getParentDiscountCustomerInvoiceDetail();
            if (ObjectUtils.isNotNull(parentCustomerInvoiceDetail)) {
                parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(null);
            }
        }

        // delete line like normal
        super.deleteAccountingLine(isSource, financialDocumentForm, deleteIndex);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        refreshBillToAddress(mapping, form, request, response);
        refreshShipToAddress(mapping, form, request, response);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refresh the ShipToAddress CustomerAddress object
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshBillToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) form).getCustomerInvoiceDocument();
        
        CustomerAddress customerBillToAddress = null;
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerBillToAddressIdentifier())) {
            int customerBillToAddressIdentifier = customerInvoiceDocument.getCustomerBillToAddressIdentifier();
           
            customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerBillToAddressIdentifier);
            if (ObjectUtils.isNotNull(customerBillToAddress)) {
                customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddressIdentifier);
            } else {
                customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());

                if (ObjectUtils.isNotNull(customerBillToAddress)) {
                    customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
                } else {
                    customerInvoiceDocument.setCustomerBillToAddress(null);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(null);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(null);
                }
            }
            
        } else {
            customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());

            if (ObjectUtils.isNotNull(customerBillToAddress)) {
                customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
            } else {
                customerInvoiceDocument.setCustomerBillToAddress(null);
                customerInvoiceDocument.setCustomerBillToAddressOnInvoice(null);
                customerInvoiceDocument.setCustomerBillToAddressIdentifier(null);
            }
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method refresh the ShipToAddress CustomerAddress object
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshShipToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) form).getCustomerInvoiceDocument();
        
        CustomerAddress customerShipToAddress = null;
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier())) {
            int customerShipToAddressIdentifier = customerInvoiceDocument.getCustomerShipToAddressIdentifier();
            
            customerShipToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerShipToAddressIdentifier);
            if (ObjectUtils.isNotNull(customerShipToAddress)) {
                customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
                customerInvoiceDocument.setCustomerShipToAddressOnInvoice(customerShipToAddress);
                customerInvoiceDocument.setCustomerShipToAddressIdentifier(customerShipToAddressIdentifier);
            }
        }
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier()) | ObjectUtils.isNull(customerShipToAddress)) {
            customerInvoiceDocument.setCustomerShipToAddress(null);
            customerInvoiceDocument.setCustomerShipToAddressOnInvoice(null);
            customerInvoiceDocument.setCustomerShipToAddressIdentifier(null);
        }
        

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    

    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        String docId = ((CustomerInvoiceDocumentForm) form).getCustomerInvoiceDocument().getDocumentNumber();
        String methodToCallPrintInvoicePDF = "printInvoicePDF";
        String methodToCallDocHandler = "docHandler";
        String printInvoicePDFUrl = getUrlForPrintInvoice(basePath, docId, methodToCallPrintInvoicePDF);
        String displayInvoiceTabbedPageUrl = getUrlForPrintInvoice(basePath, docId, methodToCallDocHandler);
        
        request.setAttribute("printPDFUrl", printInvoicePDFUrl);
        request.setAttribute("displayTabbedPageUrl", displayInvoiceTabbedPageUrl);
        request.setAttribute(KFSConstants.PARAMETER_DOC_ID, docId);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_INVOICE);
        request.setAttribute("printLabel", label);
        return mapping.findForward("arPrintPDF");
        
    }
    
    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printInvoicePDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String invoiceDocId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(invoiceDocId);
        
        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        File report = reportService.generateInvoice(customerInvoiceDocument);
        
        StringBuilder fileName = new StringBuilder();
        fileName.append(customerInvoiceDocument.getOrganizationInvoiceNumber());
        fileName.append("-");
        fileName.append(customerInvoiceDocument.getDocumentNumber());
        fileName.append(".pdf");
        
        if (report.length() == 0) {
            //csForm.setMessage("No Report Generated");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
            
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String contentDisposition = "";
        try {
            ArrayList master = new ArrayList();
            PdfCopy writer = null;

            // create a reader for the document
            String reportName = report.getAbsolutePath();
            PdfReader reader = new PdfReader(reportName);
            reader.consolidateNamedDestinations();
            
            // retrieve the total number of pages
            int n = reader.getNumberOfPages();
            List bookmarks = SimpleBookmark.getBookmark(reader);
            if (bookmarks != null) {
                master.addAll(bookmarks);
            }

            // step 1: create a document-object
            Document document = new Document(reader.getPageSizeWithRotation(1));
            // step 2: create a writer that listens to the document
            writer = new PdfCopy(document, baos);
            // step 3: open the document
            document.open();
            // step 4: add content
            PdfImportedPage page;
            for (int i = 0; i < n; ) {
                ++i;
                page = writer.getImportedPage(reader, i);
                writer.addPage(page);
            }
            writer.freeReader(reader);
            if (!master.isEmpty()) {
                writer.setOutlines(master);
            }
            // step 5: we close the document
            document.close();

            StringBuffer sbContentDispValue = new StringBuffer();
            String useJavascript = request.getParameter("useJavascript");
            if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                sbContentDispValue.append("attachment");
            }
            else {
                sbContentDispValue.append("inline");
            }
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(fileName);
            
            contentDisposition = sbContentDispValue.toString();
            
        }
        catch(Exception e) {
            LOG.error("problem during CustomerInvoiceDocumentAction.printInvoicePDF()", e);
        } 
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", contentDisposition);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(baos.size());

        // write to output
        ServletOutputStream sos;
        sos = response.getOutputStream();
        baos.writeTo(sos);
        sos.flush();
        sos.close();
        
        return null;
    }
    
    /**
     * Creates a URL to be used in printing the customer invoice document.
     * 
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintInvoice(String basePath, String docId, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/arCustomerInvoiceDocument.do?methodToCall=");
        result.append(methodToCall);
        result.append("&docId=");
        result.append(docId);
        result.append("&command=displayDocSearchView");

        return result.toString();
    }

    
    
}
