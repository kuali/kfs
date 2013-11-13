/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.ContinueCustomerCreditMemoDocumentEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerCreditMemoDetailEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerCreditMemoDocumentEvent;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

public class CustomerCreditMemoDocumentAction extends KualiTransactionalDocumentActionBase {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoDocumentAction.class);
    
    public CustomerCreditMemoDocumentAction() {
        super();
    }
    
    /**
     * Do initialization for a new customer credit memo.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }
    
    /**
     * This method loads the document by its provided document header id. This has been abstracted out so that it can be overridden
     * in children if the need arises.
     *
     * @param kualiDocumentFormBase
     * @throws WorkflowException
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
       super.loadDocument(kualiDocumentFormBase);
       ((CustomerCreditMemoDocument)kualiDocumentFormBase.getDocument()).populateCustomerCreditMemoDetailsAfterLoad();
    }
    
    /**
     * Clears out init tab.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        customerCreditMemoDocument.clearInitFields();
        
        return super.refresh(mapping, form, request, response);
    }   
    
    /**
     * Handles continue request. This request comes from the initial screen which gives ref. invoice number.
     * Based on that, the customer credit memo is initially populated.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new ContinueCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument));
        if (rulePassed)
            customerCreditMemoDocument.populateCustomerCreditMemoDetails();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Based on user input this method recalculates a customer credit memo detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();
        
        int indexOfLineToRecalculate = getSelectedLine(request);
        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRecalculate);
     
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "[" + indexOfLineToRecalculate + "]";

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDetailEvent(errorPath, customerCreditMemoDocument, customerCreditMemoDetail));
        if (rulePassed) {
            CustomerCreditMemoDetailService customerCreditMemoDetailService = SpringContext.getBean(CustomerCreditMemoDetailService.class);
            customerCreditMemoDetailService.recalculateCustomerCreditMemoDetail(customerCreditMemoDetail,customerCreditMemoDocument);
        } else {
            customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method refreshes a customer credit memo detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
        int indexOfLineToRefresh = getSelectedLine(request);
        
        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRefresh);
        
        customerCreditMemoDetail.setCreditMemoItemQuantity(null);
        customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
        customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
        
        customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
 
    /**
     * This method refreshes customer credit memo details and line with totals
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
    
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        for( CustomerCreditMemoDetail customerCreditMemoDetail : customerCreditMemoDetails ){
            customerCreditMemoDetail.setCreditMemoItemQuantity(null);
            customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
            customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
        }
        customerCreditMemoDocument.setCrmTotalItemAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalAmount(KualiDecimal.ZERO);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Based on user input this method recalculates customer credit memo document <=> all customer credit memo details
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument,false));
        if (rulePassed) {
            CustomerCreditMemoDocumentService customerCreditMemoDocumentService = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument,false);
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
        String docId = ((CustomerCreditMemoDocumentForm) form).getDocument().getDocumentNumber();
        String methodToCallPrintCreditMemoPDF = "printCreditMemoPDF";
        String methodToCallDocHandler = "docHandler";
        String printCreditMemoPDFUrl = getUrlForPrintCreditMemo(basePath, docId, methodToCallPrintCreditMemoPDF);
        String displayInvoiceTabbedPageUrl = getUrlForPrintCreditMemo(basePath, docId, methodToCallDocHandler);
        
        request.setAttribute("printPDFUrl", printCreditMemoPDFUrl);
        request.setAttribute("displayTabbedPageUrl", displayInvoiceTabbedPageUrl);
        request.setAttribute("docId", docId);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.CUSTOMER_CREDIT_MEMO);
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
    public ActionForward printCreditMemoPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String creditMemoDocId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(creditMemoDocId);
        
        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        File report = reportService.generateCreditMemo(customerCreditMemoDocument);
        
        StringBuilder fileName = new StringBuilder();
        fileName.append(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        fileName.append("-");
        fileName.append(customerCreditMemoDocument.getDocumentNumber());
        fileName.append(".pdf");
        
        if (report.length() == 0) {
            //csForm.setMessage("No Report Generated");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
            
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String contentDisposition = "";
        try {
            ArrayList master = new ArrayList();
            PdfCopy  writer = null;
                    
            // create a reader for the document
            String reportName = report.getAbsolutePath();
            PdfReader reader = new PdfReader(reportName);
            reader.consolidateNamedDestinations();
            
            // retrieve the total number of pages
            int n = reader.getNumberOfPages();
            List bookmarks = SimpleBookmark.getBookmark(reader);
            if (bookmarks != null)
                master.addAll(bookmarks);

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
            if (!master.isEmpty())
                writer.setOutlines(master);
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
            LOG.error("problem during lockboxService.processLockboxes()", e);
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
     * Creates a URL to be used in printing the customer credit memo.
     * 
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintCreditMemo(String basePath, String docId, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/arCustomerCreditMemoDocument.do?methodToCall=");
        result.append(methodToCall);
        result.append("&docId=");
        result.append(docId);
        result.append("&command=displayDocSearchView");

        return result.toString();
    }

    
}
