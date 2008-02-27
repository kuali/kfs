/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.form;

import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * Action form for Effort Certification Document.
 */
public class EffortCertificationForm extends KualiTransactionalDocumentFormBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationForm.class);

    private EffortCertificationDocument effortCertificationDocument;
    private EffortCertificationDetail newDetailLine;

    /**
     * Constructs a EffortCertificationForm.java.
     */
    public EffortCertificationForm() {
        super();

        this.setEffortCertificationDocument(new EffortCertificationDocument());
        this.setDocument(this.getEffortCertificationDocument());

        newDetailLine = new EffortCertificationDetail();
    }

    /**
     * @return new detail line
     */
    public EffortCertificationDetail getNewDetailLine() {
        return newDetailLine;
    }

    /**
     * Sets the new detail line
     * 
     * @param newDetailLine
     */
    public void setNewDetailLine(EffortCertificationDetail newDetailLine) {
        this.newDetailLine = newDetailLine;
    }

    /**
     * Gets the effortCertificationDocument attribute.
     * 
     * @return Returns the effortCertificationDocument.
     */
    public EffortCertificationDocument getEffortCertificationDocument() {
        return effortCertificationDocument;
    }

    /**
     * Sets the effortCertificationDocument attribute value.
     * 
     * @param effortCertificationDocument The effortCertificationDocument to set.
     */
    public void setEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        this.effortCertificationDocument = effortCertificationDocument;
    }
}
