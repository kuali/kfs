package edu.arizona.kfs.fp.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.KFSConstants;

public class DisbursementVoucherIncomeTypeTotalsValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherIncomeTypeTotalsValidation.class);

    public static final String ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT = "errors.document.ap.incomeTypesTotalAmount.invalid";
    private AccountingDocument accountingDocumentForValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        boolean hasIncomeTypes = false;
        DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument) accountingDocumentForValidation;

        if (is1099TabVisible(disbursementVoucherDocument)) {
            MessageMap errors = GlobalVariables.getMessageMap();
            errors.clearErrorPath();
            errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
    
            KualiDecimal incomeTypesTotal = KualiDecimal.ZERO;
            
            for (DisbursementVoucherIncomeType incomeType : disbursementVoucherDocument.getIncomeTypes()) {
                if (incomeType.getAmount() != null) {
                    incomeTypesTotal = incomeTypesTotal.add(incomeType.getAmount());
                    hasIncomeTypes = true;
                }
            }
            
            if (hasIncomeTypes) {
                if (disbursementVoucherDocument.getDisbVchrCheckTotalAmount().compareTo(incomeTypesTotal) != 0) {
                    errors.putError(KFSPropertyConstants.DOCUMENT + "incomeTypes", ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT);           
                    isValid = false;
                }
            }
    
            errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT); 
        }
        
        return isValid;
    }

    private boolean is1099TabVisible(DisbursementVoucherDocument disbursementVoucherDocument) {
        boolean retval = true;
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        DocumentAuthorizer docAuth = documentHelperService.getDocumentAuthorizer(disbursementVoucherDocument);

        Map<String, String> permDetails = new HashMap<String, String>();
        
        permDetails.put(KimConstants.AttributeConstants.EDIT_MODE, KFSConstants.IncomeTypeConstants.IncomeTypesAuthorization.VIEW_INCOME_TYPES_EDIT_MODE);
        permDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, KFSConstants.DOCUWARE_DV_DOC_TYPE);
        
        retval = docAuth.isAuthorizedByTemplate(
                disbursementVoucherDocument, 
                KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT, 
                GlobalVariables.getUserSession().getPrincipalId(),
                permDetails, 
                null);

        
        if (LOG.isDebugEnabled()) {
            LOG.debug("user: " +  GlobalVariables.getUserSession().getPrincipalId() + ", retval: " + retval);
        }
        
        return retval;
    }
    
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}
