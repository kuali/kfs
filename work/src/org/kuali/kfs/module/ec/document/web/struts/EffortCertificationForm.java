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
 * This class is the action form for Internal Billing.
 */
public class EffortCertificationForm extends KualiTransactionalDocumentFormBase {
    private EffortCertificationDetail newDetailLine;
    
    public EffortCertificationForm() {
        super();
        setDocument(new EffortCertificationDocument());
    }

    /**
     * 
     * @return
     */
    public EffortCertificationDetail getNewDetailLine() {
        return newDetailLine;
    }
    
    /**
     * @param newDetailLine
     */
    public void setNewDetailLine(EffortCertificationDetail newDetailLine) {
        this.newDetailLine = newDetailLine;
    }
  
}
