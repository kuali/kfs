package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherInvoiceNumberEnteredValidation extends AccountingLineAccessibleValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument accountingDocument = (DisbursementVoucherDocument) getAccountingDocumentForValidation();

        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        final AccountingLineAuthorizer accountingLineAuthorizer = lookupAccountingLineAuthorizer();
        final boolean lineIsAccessible = accountingLineAuthorizer.hasEditPermissionOnAccountingLine(accountingDocument, accountingLineForValidation, getAccountingLineCollectionProperty(), financialSystemUser, true);
        boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocument, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.EXTENSION_INVOICE_NUMBER, lineIsAccessible, true, financialSystemUser);

        if (isAccessible) {
            DisbursementVoucherSourceAccountingLineExtension accountingLineExtension = (DisbursementVoucherSourceAccountingLineExtension) accountingLineForValidation.getExtension();
            if (StringUtils.isBlank(accountingLineExtension.getInvoiceNumber())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.EXTENSION_INVOICE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, KFSConstants.INVOICE_NUMBER);
                return false;
            }
        }
        return true;
    }

}
