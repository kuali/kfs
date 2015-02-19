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
