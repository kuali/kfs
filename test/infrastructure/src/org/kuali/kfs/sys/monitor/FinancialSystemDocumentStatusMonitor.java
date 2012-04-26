/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.sys.monitor;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * FinancialSystemDocumentStatusMonitor
 */
public class FinancialSystemDocumentStatusMonitor extends ChangeMonitor {
    final DocumentService documentService;
    final private String docHeaderId;
    final private String desiredStatus;

    public FinancialSystemDocumentStatusMonitor(DocumentService documentService, String docHeaderId, String desiredStatus) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredStatus = desiredStatus;
    }

    @Override
    public boolean valueChanged() throws Exception {
        Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());
        String currentStatus = null;
        if (d instanceof FinancialSystemTransactionalDocument) {
            currentStatus = ((FinancialSystemTransactionalDocument) d).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
        } else if (d instanceof FinancialSystemMaintenanceDocument) {
            currentStatus = ((FinancialSystemMaintenanceDocument) d).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
        } else {
            throw new IllegalArgumentException("Document with id " + docHeaderId + " is not an instance of " + FinancialSystemMaintenanceDocument.class + " or " + FinancialSystemTransactionalDocument.class);
        }

        return StringUtils.equals(desiredStatus, currentStatus);
    }
}
