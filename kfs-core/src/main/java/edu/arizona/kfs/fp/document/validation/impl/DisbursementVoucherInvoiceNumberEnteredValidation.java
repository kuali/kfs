package edu.arizona.kfs.fp.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherInvoiceNumberEnteredValidation extends AccountingLineAccessibleValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument accountingDocument = (DisbursementVoucherDocument) getAccountingDocumentForValidation();
        DisbursementVoucherNonResidentAlienTax nonResidentAlienTax = accountingDocument.getDvNonResidentAlienTax();
        
        // tax accounting lines don't need to be validated
        if (nonResidentAlienTax != null) {
        	List<String> taxLineNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(nonResidentAlienTax.getFinancialDocumentAccountingLineText());
        	
        	if (taxLineNumbers.contains(this.getAccountingLineForValidation().getSequenceNumber())) {
        		return true;
        	}
        }

        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        final AccountingLineAuthorizer accountingLineAuthorizer = lookupAccountingLineAuthorizer();
        final boolean lineIsAccessible = accountingLineAuthorizer.hasEditPermissionOnAccountingLine(accountingDocument, accountingLineForValidation, getAccountingLineCollectionProperty(), financialSystemUser, true);
        boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocument, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.EXTENSION_INVOICE_NUMBER, lineIsAccessible, true, financialSystemUser);

        if (isAccessible) {
            DisbursementVoucherSourceAccountingLineExtension accountingLineExtension = (DisbursementVoucherSourceAccountingLineExtension) accountingLineForValidation.getExtension();
            if (isInvoiceNumberRequired(accountingDocument, event) && StringUtils.isBlank(accountingLineExtension.getInvoiceNumber())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.EXTENSION_INVOICE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, KFSConstants.INVOICE_NUMBER);
                return false;
            }
        }
        return true;
    }
    
    protected boolean isInvoiceNumberRequired(DisbursementVoucherDocument document, AttributedDocumentEvent event) {
    	WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
    	
    	if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
    		return true;
    	}
    	
    	if (event instanceof UpdateAccountingLineEvent && workflowDocument.isEnroute() && workflowDocument.isApprovalRequested()) {
    		// the FO is not allowed to blank out the invoice number, but he isn't required to fill one in
    		UpdateAccountingLineEvent uale = (UpdateAccountingLineEvent) event;
    		String origInvoiceNumber = ((DisbursementVoucherSourceAccountingLineExtension) uale.getAccountingLine().getExtension()).getInvoiceNumber();
    		return StringUtils.isNotBlank(origInvoiceNumber);
    	}
    	
    	return false;
    }

}
