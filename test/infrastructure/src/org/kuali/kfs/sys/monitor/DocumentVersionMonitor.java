/*
 * Copyright 2005 The Kuali Foundation
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

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * DocumentVersionMonitor
 */
public class DocumentVersionMonitor extends ChangeMonitor {
    private final DocumentService documentService;
    private final String documentHeaderId;
    private final Long startVersion;

    public DocumentVersionMonitor(DocumentService documentService, String documentHeaderId, Long startVersion) {
        this.documentService = documentService;
        this.documentHeaderId = documentHeaderId;
        this.startVersion = startVersion;
    }

    /**
     * Returns true if the version number of the given projectCode changes from startVersion.
     * 
     * @see org.kuali.kfs.sys.document.routing.ChangeMonitor#valueChanged()
     */
    public boolean valueChanged() throws Exception {
        boolean changed = false;

        Document hopefullyUpdatedDocument = documentService.getByDocumentHeaderId(documentHeaderId.toString());
        Long currentVersion = hopefullyUpdatedDocument.getVersionNumber();

        changed = !currentVersion.equals(this.startVersion);

        return changed;

    }
}
