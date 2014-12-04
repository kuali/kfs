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
package org.kuali.kfs.module.ld.document.web.struts;

import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * Base Struts Action Form class for Labor Forms.
 */
public class LaborDocumentFormBase extends KualiAccountingDocumentFormBase {

    /**
     * Constructs a LaborDocumentFormBase instance.
     */
    public LaborDocumentFormBase() {
        super();
    }

    /**
     * @return Returns the LaborDocument.
     */
    public LaborLedgerPostingDocument getLaborDocument() {
        return (LaborLedgerPostingDocument) getDocument();
    }

}
