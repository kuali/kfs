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
package org.kuali.kfs.module.cam.document.validation.event;

import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.impl.BarcodeInventoryErrorDocumentRule;

public final class ValidateBarcodeInventoryEvent extends KualiDocumentEventBase {
    
    public ValidateBarcodeInventoryEvent(String errorPathPrefix, Document document) {
        super("", errorPathPrefix, document);
    }

    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return BarcodeInventoryErrorDocumentRule.class;
    }

    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail = ((BarcodeInventoryErrorDocument)getDocument()).getBarcodeInventoryErrorDetail(); 
        return ((BarcodeInventoryErrorDocumentRule) rule).validateBarcodeInventoryErrorDetail(barcodeInventoryErrorDetail);
    }
    
}
