/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.test.monitor;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;

/**
 * DocumentStatusMonitor
 */
public class DocumentStatusMonitor extends ChangeMonitor {
    final DocumentService documentService;
    final private String docHeaderId;
    final private String desiredStatus;

    public DocumentStatusMonitor(DocumentService documentService, String docHeaderId, String desiredStatus) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredStatus = desiredStatus;
    }

    public boolean valueChanged() throws Exception {
        Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());

        String currentStatus = d.getDocumentHeader().getFinancialDocumentStatusCode();
        return StringUtils.equals(desiredStatus, currentStatus);
    }
}