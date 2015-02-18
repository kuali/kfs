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
