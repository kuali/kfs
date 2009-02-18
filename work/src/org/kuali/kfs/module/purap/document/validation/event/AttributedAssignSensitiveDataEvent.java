/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.event;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.impl.AssignSensitiveDataRule;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

public final class AttributedAssignSensitiveDataEvent extends AttributedDocumentEventBase {
    
    private PurchaseOrderDocument accountingDocumentForValidation;
    private List<SensitiveData> sensitiveDatas;
    
    /**
     * Constructs an AssignSensitiveDataEvent with the given errorPathPrefix, document, and sensitive data list.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param sensitiveDatas the sensitive data list to be checked for assignment
     */
    public AttributedAssignSensitiveDataEvent(String errorPathPrefix, Document document, List<SensitiveData> sensitiveDatas) {
        super("Assign sensitive data to purchase order " + getDocumentId(document), errorPathPrefix, document);
        this.sensitiveDatas = sensitiveDatas;
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public PurchaseOrderDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(PurchaseOrderDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public List<SensitiveData> getSensitiveDatas() {
        return sensitiveDatas;
    }

    public void setSensitiveDatas(List<SensitiveData> sensitiveDatas) {
        this.sensitiveDatas = sensitiveDatas;
    }
    
}