package edu.arizona.kfs.fp.document.validation.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLine;
import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.service.DisbursementVoucherInvoiceService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherDocumentPreRules extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherDocumentPreRules {

    private static transient volatile DisbursementVoucherInvoiceService disbursementVoucherInvoiceService;
    private static transient volatile ConfigurationService configurationService;
    private static transient volatile PurchasingAccountsPayableModuleService purapModuleService;

    private static DisbursementVoucherInvoiceService getDisbursementVoucherInvoiceService() {
        if (disbursementVoucherInvoiceService == null) {
            disbursementVoucherInvoiceService = SpringContext.getBean(DisbursementVoucherInvoiceService.class);
        }
        return disbursementVoucherInvoiceService;
    }

    private static ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }

    @Override
    public boolean doPrompts(Document document) {
        boolean result = super.doPrompts(document);

        result &= checkDisbursementVoucherInvoiceNumberRequired((DisbursementVoucherDocument) document);
        result &= checkInvoiceNumberDuplicate((DisbursementVoucherDocument) document);

        return result;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    private boolean checkDisbursementVoucherInvoiceNumberRequired(DisbursementVoucherDocument document) {
        boolean results = true;
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) document;
        List<DisbursementVoucherSourceAccountingLine> accountingLines = disbursementVoucherDocument.getSourceAccountingLines();
        for (DisbursementVoucherSourceAccountingLine accountingLine : accountingLines) {
            DisbursementVoucherSourceAccountingLineExtension accountingLineExtension = accountingLine.getExtension();
            if (StringUtils.isBlank(accountingLineExtension.getInvoiceNumber())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_REQUIRED, KFSConstants.INVOICE_NUMBER);
                super.abortRulesCheck();
                results = false;
            }
        }

        return results;
    }

    @SuppressWarnings("deprecation")
    private boolean checkInvoiceNumberDuplicate(DisbursementVoucherDocument document) {

        ArrayList<String> matchingDVs = new ArrayList<String>();
        ArrayList<String> matchingPreqs = new ArrayList<String>();
        for (DisbursementVoucherSourceAccountingLine sourceAccountingLine : (List<DisbursementVoucherSourceAccountingLine>) document.getSourceAccountingLines()) {
            DisbursementVoucherSourceAccountingLineExtension extension = sourceAccountingLine.getExtension();
            String invoiceNumber = extension.getInvoiceNumber();
            if (StringUtils.isNotBlank(invoiceNumber)) {
                ArrayList<String> listDisbursementVouchers = findDisbursementVouchersWithInvoiceNumber(document, invoiceNumber);
                for (String documentNumber : listDisbursementVouchers) {
                    if (!documentNumber.equals(document.getDocumentNumber()) && !matchingDVs.contains(documentNumber)) {
                        matchingDVs.add(documentNumber);
                    }
                }
                
                List<String> listPaymentRequests = findPaymentRequestsWithInvoiceNumber(document, invoiceNumber);
                if (listPaymentRequests != null && listPaymentRequests.size() > 0) {
                	for(String documentNumber : listPaymentRequests) {
                		if(!documentNumber.equals(document.getDocumentNumber()) && !matchingPreqs.contains(documentNumber)) {
                			matchingPreqs.add(documentNumber);
                		}
                	}
                }
            }
        }
        
        if (!matchingDVs.isEmpty() || !matchingPreqs.isEmpty()) {
            String questionText = getConfigurationService().getPropertyValueAsString(KFSKeyConstants.MESSAGE_DV_DUPLICATE_INVOICE);

            Object[] args = { toCommaDelimitedString(matchingDVs), toCommaDelimitedString(matchingPreqs) };
            questionText = MessageFormat.format(questionText, args);

            boolean okToProceed = super.askOrAnalyzeYesNoQuestion(KFSConstants.DUPLICATE_INVOICE_QUESTION_ID, questionText);

            if (!okToProceed) {
                super.abortRulesCheck();
            }
        }
        return true;
    }

    private String toCommaDelimitedString(ArrayList<String> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return KFSConstants.NOT_AVAILABLE_STRING;
        }
        return StringUtils.join(documentIds, KFSConstants.COMMA);
    }

    private ArrayList<String> findDisbursementVouchersWithInvoiceNumber(DisbursementVoucherDocument document, String invoiceNumber) {
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        ArrayList<String> listDisbursementVouchers = (ArrayList<String>) getDisbursementVoucherInvoiceService().findDisbursementVouchersWithInvoiceNumber(payeeDetail.getDisbVchrPayeeIdNumber(), payeeDetail.getDisbursementVoucherPayeeTypeCode(), invoiceNumber);
        return listDisbursementVouchers;
    }
    
    private List<String> findPaymentRequestsWithInvoiceNumber(DisbursementVoucherDocument document, String invoiceNumber) {
    	DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
    	List<String> listPaymentRequests = (List<String>) getPurchasingAccountsPayableModuleService().findPaymentRequestsByVendorNumberInvoiceNumber(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger(), invoiceNumber);
    	return listPaymentRequests;
    }
    
    
    public static PurchasingAccountsPayableModuleService getPurchasingAccountsPayableModuleService() {
    	if(purapModuleService == null) {
    		purapModuleService = SpringContext.getBean(PurchasingAccountsPayableModuleService.class);
    	}
    	
    	return purapModuleService;
    }
    	
}
