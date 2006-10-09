/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.financial.document.PreEncumbranceDocument;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI.
 * 
 */
public class PreEncumbranceForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a PreEncumbranceForm.java.
     */
    public PreEncumbranceForm() {
        super();
        setDocument(new PreEncumbranceDocument());
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
