/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.web.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.cams.bo.BarcodeInventoryErrorDetail;
import org.kuali.module.cams.document.BarcodeInventoryErrorDocument;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

public class BarcodeInventoryErrorForm extends KualiTransactionalDocumentFormBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorForm.class);

    private BarcodeInventoryErrorDetail inventoryUploadErrorDetail;

    /**
     * Constructs a CashControlDocumentForm.java.
     */
    public BarcodeInventoryErrorForm() {
        super();
        setDocument(new BarcodeInventoryErrorDocument());
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     *
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);

        CashControlDocument ccDoc = getCashControlDocument();

        if (hasDocumentId()) {

            // apply populate to PaymentApplicationDocuments
            for (CashControlDetail cashControlDetail : ccDoc.getCashControlDetails()) {

                // populate workflowDocument in documentHeader, if needed
                try {
                    KualiWorkflowDocument workflowDocument = null;
                    if (GlobalVariables.getUserSession().getWorkflowDocument(cashControlDetail.getReferenceFinancialDocumentNumber()) != null) {
                        workflowDocument = GlobalVariables.getUserSession().getWorkflowDocument(cashControlDetail.getReferenceFinancialDocumentNumber());
                    }
                    else {
                        // gets the workflow document from doc service, doc service will also set the workflow document in the
                        // user's session
                        Document retrievedDocument = KNSServiceLocator.getDocumentService().getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
                        if (retrievedDocument == null) {
                            throw new WorkflowException("Unable to get retrieve document # " + cashControlDetail.getReferenceFinancialDocumentNumber() + " from document service getByDocumentHeaderId");
                        }
                        workflowDocument = retrievedDocument.getDocumentHeader().getWorkflowDocument();
                    }

                    cashControlDetail.getReferenceFinancialDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
                }
                catch (WorkflowException e) {
                    LOG.warn("Error while instantiating workflowDoc", e);
                    throw new RuntimeException("error populating documentHeader.workflowDocument", e);
                }
            }
        }

    }*/

    /**
     * This method gets the cash control document
     * 
     * @return the CashControlDocument
     */
    public BarcodeInventoryErrorDocument getInventoryUploadErrorDocument() {
        return (BarcodeInventoryErrorDocument) getDocument();
    }



 
}
