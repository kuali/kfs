package edu.arizona.kfs.fp.document.validation.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.document.Document;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLine;
import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.service.DisbursementVoucherInvoiceService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

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

        result &= checkInvoiceNumberDuplicate((DisbursementVoucherDocument) document);

        return result;
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
    
    @Override
    protected boolean checkNonEmployeeTravelTabState(DisbursementVoucherDocument dvDocument) {
    	boolean tabStatesOK = true;
    	
    	DisbursementVoucherNonEmployeeTravel dvNonEmplTrav = dvDocument.getDvNonEmployeeTravel();
    	if (!hasNonEmployeeTravelValues(dvNonEmplTrav)) {
    		return true;
    	}
    	
    	String paymentReasonCode = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
    	List<String> nonEmpltravelPaymentReasonCodes = new ArrayList<String>(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(DisbursementVoucherDocument.class, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM));
    	
    	if (nonEmpltravelPaymentReasonCodes == null || !nonEmpltravelPaymentReasonCodes.contains(paymentReasonCode)) {
    		String nonEmplTravReasonStr = getValidPaymentReasonsAsString(nonEmpltravelPaymentReasonCodes);
    		
    		String paymentReasonName = dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonName();
    		Object[] args = { "payment reason", "'" + paymentReasonName + "'", "Non-Employee Travel", nonEmplTravReasonStr };
    		
    		String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.QUESTION_CLEAR_UNNEEDED_TAB);
    		questionText = MessageFormat.format(questionText, args);
    		
    		boolean clearTab = super.askOrAnalyzeYesNoQuestion(KFSConstants.DisbursementVoucherDocumentConstants.CLEAR_NON_EMPLOYEE_TAB_QUESTION_ID, questionText);
    		
    		if (clearTab) {
    			DisbursementVoucherNonEmployeeTravel blankDvNonEmplTrav = new DisbursementVoucherNonEmployeeTravel();
    			blankDvNonEmplTrav.setDocumentNumber(dvNonEmplTrav.getDocumentNumber());
    			blankDvNonEmplTrav.setVersionNumber(dvNonEmplTrav.getVersionNumber());
    			dvDocument.setDvNonEmployeeTravel(blankDvNonEmplTrav);
    		}
    		else {
    			//return to document if the user doesn't want to clear the Non Employee Travel tab
    			super.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
    			tabStatesOK = false;
    		}
    	}
    	
    	return tabStatesOK;
    }

}
