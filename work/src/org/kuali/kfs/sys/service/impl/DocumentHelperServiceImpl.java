/*
 * Copyright 2008-2009 The Kuali Foundation
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
