package edu.arizona.kfs.module.purap.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedPaymentRequestForEInvoiceEvent;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceOrderHolder;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.util.AutoPopulatingList;

/**
 * This is a helper service to parse electronic invoice file, match it with a PO and create PREQs based on the eInvoice. Also, it
 * provides helper methods to the reject document to match it with a PO and create PREQ.
 */

public class ElectronicInvoiceHelperServiceImpl extends org.kuali.kfs.module.purap.service.impl.ElectronicInvoiceHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceHelperServiceImpl.class);

    protected DocumentService documentService;
    protected WorkflowDocumentService workflowDocumentService;    
    protected KualiRuleService kualiRuleService;

    protected PaymentRequestDocument createPaymentRequest(ElectronicInvoiceOrderHolder orderHolder){

        if (LOG.isInfoEnabled()){
            LOG.info("Creating Payment Request document");
        }

        KNSGlobalVariables.getMessageList().clear();

        validateInvoiceOrderValidForPREQCreation(orderHolder);

        if (LOG.isInfoEnabled()){
            if (orderHolder.isInvoiceRejected()){
                LOG.info("Not possible to convert einvoice details into payment request");
            }else{
                LOG.info("Payment request document creation validation succeeded");
            }
        }

        if (orderHolder.isInvoiceRejected()){
            return null;
        }

        PaymentRequestDocument preqDoc = null;
        try {
            preqDoc = (PaymentRequestDocument) SpringContext.getBean(DocumentService.class).getNewDocument("PREQ");
        }
        catch (WorkflowException e) {
            String extraDescription = "Error=" + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_WORKLOW_EXCEPTION,
                                                                                            extraDescription,
                                                                                            orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            LOG.error("Error creating Payment request document - " + e.getMessage());
            return null;
        }

        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        if (poDoc == null){
            throw new RuntimeException("Purchase Order document (POId=" + poDoc.getPurapDocumentIdentifier() + ") does not exist in the system");
        }

        preqDoc.getDocumentHeader().setDocumentDescription(generatePREQDocumentDescription(poDoc));
        try {
            preqDoc.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_IN_PROCESS);
        } catch (WorkflowException we) {
            throw new RuntimeException("Unable to save route status data for document: " + preqDoc.getDocumentNumber(), we);
        }

        preqDoc.setInvoiceDate(orderHolder.getInvoiceDate());
        preqDoc.setInvoiceNumber(orderHolder.getInvoiceNumber());
        preqDoc.setVendorInvoiceAmount(new KualiDecimal(orderHolder.getInvoiceNetAmount()));
        preqDoc.setAccountsPayableProcessorIdentifier("E-Invoice");
        preqDoc.setVendorCustomerNumber(orderHolder.getCustomerNumber());
        preqDoc.setPaymentRequestElectronicInvoiceIndicator(true);

        if (orderHolder.getAccountsPayablePurchasingDocumentLinkIdentifier() != null){
            preqDoc.setAccountsPayablePurchasingDocumentLinkIdentifier(orderHolder.getAccountsPayablePurchasingDocumentLinkIdentifier());
        }

        //Copied from PaymentRequestServiceImpl.populatePaymentRequest()
        //set bank code to default bank code in the system parameter
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(preqDoc.getClass());
        if (defaultBank != null) {
            preqDoc.setBankCode(defaultBank.getBankCode());
            preqDoc.setBank(defaultBank);
        }

        RequisitionDocument reqDoc = SpringContext.getBean(RequisitionService.class).getRequisitionById(poDoc.getRequisitionIdentifier());
        String reqDocInitiator = reqDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        try {
            Person user = KimApiServiceLocator.getPersonService().getPerson(reqDocInitiator);

            setProcessingCampus(preqDoc, user.getCampusCode());

        }catch(Exception e){
            String extraDescription = "Error setting processing campus code - " + e.getMessage();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList = SpringContext.getBean(AccountsPayableService.class).expiredOrClosedAccountsList(poDoc);
        if (expiredOrClosedAccountList == null){
            expiredOrClosedAccountList = new HashMap();
        }

        if (LOG.isInfoEnabled()){
             LOG.info(expiredOrClosedAccountList.size() + " accounts has been found as Expired or Closed");
        }

        preqDoc.populatePaymentRequestFromPurchaseOrder(orderHolder.getPurchaseOrderDocument(),expiredOrClosedAccountList);

        populateItemDetails(preqDoc,orderHolder);

        /**
         * Validate totals,paydate
         */
        kualiRuleService.applyRules(new AttributedCalculateAccountsPayableEvent(preqDoc));

        paymentRequestService.calculatePaymentRequest(preqDoc,true);

        processItemsForDiscount(preqDoc,orderHolder);

        if (orderHolder.isInvoiceRejected()){
            return null;
        }

        paymentRequestService.calculatePaymentRequest(preqDoc,false);
        /**
         * PaymentRequestReview
         */
        kualiRuleService.applyRules(new AttributedPaymentRequestForEInvoiceEvent(preqDoc));

        if(GlobalVariables.getMessageMap().hasErrors()){
            if (LOG.isInfoEnabled()){
                LOG.info("***************Error in rules processing - " + GlobalVariables.getMessageMap());
            }
            Map<String, AutoPopulatingList<ErrorMessage>> errorMessages = GlobalVariables.getMessageMap().getErrorMessages();

            String errors = errorMessages.toString();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, errors, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        if(KNSGlobalVariables.getMessageList().size() > 0){
            if (LOG.isInfoEnabled()){
                LOG.info("Payment request contains " + KNSGlobalVariables.getMessageList().size() + " warning message(s)");
                for (int i = 0; i < KNSGlobalVariables.getMessageList().size(); i++) {
                    LOG.info("Warning " + i + "  - " +KNSGlobalVariables.getMessageList().get(i));
                }
            }
        }

        addShipToNotes(preqDoc,orderHolder);

        String routingAnnotation = null;
        if (!orderHolder.isRejectDocumentHolder()){
            routingAnnotation = "Routed by electronic invoice batch job";
        }

        try {
            documentService.saveDocument(preqDoc);
            documentService.prepareWorkflowDocument(preqDoc);
            DocumentBase docBase = (DocumentBase) preqDoc;
            workflowDocumentService.route(docBase.getDocumentHeader().getWorkflowDocument(), routingAnnotation, null);            
        }
        catch (WorkflowException e) {
            e.printStackTrace();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_FAILURE, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }catch(ValidationException e){
            String extraDescription = GlobalVariables.getMessageMap().toString();
            ElectronicInvoiceRejectReason rejectReason = matchingService.createRejectReason(PurapConstants.ElectronicInvoice.PREQ_ROUTING_VALIDATION_ERROR, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return null;
        }

        return preqDoc;
    }

    protected void populateItemDetails(PaymentRequestDocument preqDocument, ElectronicInvoiceOrderHolder orderHolder) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Populating invoice order items into the payment request document");
        }

        List<PurApItem> preqItems = preqDocument.getItems();

        //process all preq items and apply amounts from order holder
        for (int i = 0; i < preqItems.size(); i++) {
            PaymentRequestItem preqItem = (PaymentRequestItem) preqItems.get(i);
            processInvoiceItem(preqItem, orderHolder);
        }

        //as part of a clean up, remove any preq items that have zero or null unit/extended price
        removeEmptyItems(preqItems);

        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully populated the invoice order items");
        }
    }
   
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
    
    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }
}

