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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.fp.document.PreEncumbranceDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI.
 */
public class PreEncumbranceForm extends KualiAccountingDocumentFormBase {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a PreEncumbranceForm.java.
     */
    public PreEncumbranceForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "PE";
    }
    
    /**
     * @return Returns the preEncumbranceDocument.
     */
    public PreEncumbranceDocument getPreEncumbranceDocument() {
        return (PreEncumbranceDocument) getDocument();
    }

    /**
     * @param preEncumbranceDocument The preEncumbranceDocument to set.
     */
    public void setPreEncumbranceDocument(PreEncumbranceDocument preEncumbranceDocument) {
        setDocument(preEncumbranceDocument);
    }

    /**
     * This method returns the reversal date in the format MMM d, yyyy.
     * 
     * @return String
     */
    public String getFormattedReversalDate() {
        return formatReversalDate(getPreEncumbranceDocument().getReversalDate());
    }
}
