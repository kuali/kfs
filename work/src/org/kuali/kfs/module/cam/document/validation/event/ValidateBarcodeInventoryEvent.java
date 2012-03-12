/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.validation.event;

import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.impl.BarcodeInventoryErrorDocumentRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public final class ValidateBarcodeInventoryEvent extends KualiDocumentEventBase {
    boolean updateStatus;
    public ValidateBarcodeInventoryEvent(String errorPathPrefix, Document document, boolean updateStatus) {
        super("", errorPathPrefix, document);
        this.updateStatus = updateStatus;
    }

    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return BarcodeInventoryErrorDocumentRule.class;
    }

    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BarcodeInventoryErrorDocumentRule) rule).validateBarcodeInventoryErrorDetail((BarcodeInventoryErrorDocument)getDocument(),this.updateStatus);
    }
    
}
