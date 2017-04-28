package edu.arizona.kfs.fp.document.authorization;

import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.identity.Person;

public class ProcurementCardAccountingLineAuthorizer extends org.kuali.kfs.fp.document.authorization.ProcurementCardAccountingLineAuthorizer {
    
    /*
     * The delivered code is incorrect. A Fiscal Officer should be able to change any of their own accounts, but not other Fiscal Officers accounts.
     * This is a copy of org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionByFieldName(AccountingDocument,AccountingLine,String,Person)
     */
    @Override
    protected boolean determineEditPermissionByFieldName(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser) {
        Map<String,String> roleQualifiers = getRoleQualifiers(accountingDocument, accountingLine);
        Map<String,String> permissionDetail = getPermissionDetails( accountingDocument, fieldName);

        return this.hasEditPermission(accountingDocument, currentUser, permissionDetail, roleQualifiers);
    }
}
