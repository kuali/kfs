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
