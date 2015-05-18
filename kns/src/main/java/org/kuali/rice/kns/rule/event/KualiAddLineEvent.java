/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.rule.event;

import org.kuali.rice.kns.rule.AddCollectionLineRule;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

@Deprecated
public class KualiAddLineEvent extends KualiDocumentEventBase {

    private PersistableBusinessObject bo;
    private String collectionName;

    public KualiAddLineEvent( Document document, String collectionName, PersistableBusinessObject addLine ) {
        super("adding bo to document collection " + KualiDocumentEventBase.getDocumentId(document), "", document);
        
        this.bo = addLine;//(BusinessObject)ObjectUtils.deepCopy( addLine );
        this.collectionName = collectionName;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCollectionLineRule)rule).processAddCollectionLineBusinessRules( (MaintenanceDocument)getDocument(), collectionName, bo );
    }

    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return AddCollectionLineRule.class;
    }
}
