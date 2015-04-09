/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.krad.datadictionary.DocumentEntry;

public class DocumentHelperServiceImpl extends org.kuali.rice.kns.service.impl.DocumentHelperServiceImpl {

    @Override
    public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
        DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(documentType);
        Class documentAuthorizerClass = documentEntry.getDocumentAuthorizerClass();
        if (documentAuthorizerClass == null) {
            if (FinancialSystemMaintenanceDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                documentAuthorizerClass = FinancialSystemMaintenanceDocumentAuthorizerBase.class;
            }
            else if (FinancialSystemTransactionalDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                if (AccountingDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                    documentAuthorizerClass = AccountingDocumentAuthorizerBase.class;
                }
                else {
                    documentAuthorizerClass = FinancialSystemTransactionalDocumentAuthorizerBase.class;
                }
            }
            else {
                return super.getDocumentAuthorizer(documentType);
            }
        }
        try {
            return (DocumentAuthorizer) documentAuthorizerClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to instantiate DocumentAuthorizer class: " + documentAuthorizerClass, e);
        }
    }

    @Override
    public DocumentPresentationController getDocumentPresentationController(String documentType) {
        org.kuali.rice.krad.datadictionary.DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(documentType);
        Class documentPresentationControllerClass = documentEntry.getDocumentPresentationControllerClass();
        if (documentPresentationControllerClass == null) {
            if (FinancialSystemMaintenanceDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                documentPresentationControllerClass = FinancialSystemMaintenanceDocumentPresentationControllerBase.class;
            }
            else if (FinancialSystemTransactionalDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                if (LedgerPostingDocument.class.isAssignableFrom(documentEntry.getDocumentClass())) {
                    documentPresentationControllerClass = LedgerPostingDocumentPresentationControllerBase.class;
                }
                else {
                    documentPresentationControllerClass = FinancialSystemTransactionalDocumentPresentationControllerBase.class;
                }
            }
            else {
                return super.getDocumentPresentationController(documentType);
            }
        }
        try {
            return (DocumentPresentationController) documentPresentationControllerClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to instantiate DocumentPresentationController class: " + documentPresentationControllerClass, e);
        }
    }
}
