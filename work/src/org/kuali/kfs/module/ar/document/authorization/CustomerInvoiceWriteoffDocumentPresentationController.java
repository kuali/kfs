/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.krad.document.Document;

public class CustomerInvoiceWriteoffDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {

        Set<String> editModes = super.getEditModes(document);
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) document;

        if (StringUtils.equals(customerInvoiceWriteoffDocument.getStatusCode(), ArConstants.CustomerInvoiceWriteoffStatuses.INITIATE)) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        
        return editModes;
    }

    @Override
    public boolean canCancel(Document document) {
        return !isDocStatusCodeInitiated(document);
    }

    @Override
    public boolean canClose(Document document) {
        return isDocStatusCodeInitiated(document);
    }

    @Override
    public boolean canSave(Document document) {
        return !isDocStatusCodeInitiated(document);
    }
    
    /**
     * Returns true if the document passed in is in initiated status.
     */
    protected boolean isDocStatusCodeInitiated(Document document) {
        CustomerInvoiceWriteoffDocument writeoffDoc = (CustomerInvoiceWriteoffDocument) document;
        return (StringUtils.equals(writeoffDoc.getStatusCode(), ArConstants.CustomerInvoiceWriteoffStatuses.INITIATE));
    }

}
