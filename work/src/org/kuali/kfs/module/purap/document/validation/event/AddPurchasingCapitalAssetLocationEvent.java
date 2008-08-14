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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetLocation;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingCapitalAssetLocationRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * Add Purchasing Capital Asset Location Event. 
 * This is triggered when a user presses the add button for a given document's location line.
 */
public final class AddPurchasingCapitalAssetLocationEvent extends PurchasingCapitalAssetLocationEventBase {
    /**
     * Constructs an AddLocationEvent with the given errorPathPrefix, document, and location.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public AddPurchasingCapitalAssetLocationEvent(String errorPathPrefix, Document document, PurchasingCapitalAssetLocation location) {
        super("adding location to document " + getDocumentId(document), errorPathPrefix, document, location);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddPurchasingCapitalAssetLocationRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddPurchasingCapitalAssetLocationRule) rule).processAddCapitalAssetLocationBusinessRules((PurchasingDocument)getDocument(), getCapitalAssetLocation());
    }
}
