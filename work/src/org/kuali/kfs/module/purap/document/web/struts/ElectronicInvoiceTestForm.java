/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.web.struts;

import org.apache.struts.upload.FormFile;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * Struts Action Form for Electroinc Invoice Reject document.
 */
public class ElectronicInvoiceTestForm extends KualiDocumentFormBase {

    private FormFile xmlFile;
    private String returnXml;

    public FormFile getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(FormFile xmlFile) {
        this.xmlFile = xmlFile;
    }
    
    public String getReturnXml() {
        return returnXml;
    }

    public void setReturnXml(String returnXml) {
        this.returnXml = returnXml;
    }

    /**
     * Constructs a PaymentRequestForm instance and sets up the appropriately casted document.
     */
    public ElectronicInvoiceTestForm() {
        super();
    }
    
}
