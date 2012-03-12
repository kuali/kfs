/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class DeleteCashControlDetailEvent extends CashControlDetailEventBase {

    public DeleteCashControlDetailEvent(String errorPathPrefix, Document document, CashControlDetail cashControlDetail) {
        super("Deleteing cash control detail to document " + getDocumentId(document), errorPathPrefix, document, cashControlDetail);
    }

    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return DeleteCashControlDetailRule.class;
    }

    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteCashControlDetailRule) rule).processDeleteCashControlDetailBusinessRules((TransactionalDocument) getDocument(), getCashControlDetail());
    }
}
