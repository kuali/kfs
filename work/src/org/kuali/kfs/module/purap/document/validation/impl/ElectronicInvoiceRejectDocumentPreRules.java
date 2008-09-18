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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PreRulesContinuationBase;

/**
 * Business pre rule(s) applicable to Payment Request documents.
 */
public class ElectronicInvoiceRejectDocumentPreRules extends PreRulesContinuationBase {

    /**
     * Default Constructor
     */
    public ElectronicInvoiceRejectDocumentPreRules() {
        super();
    }

    @Override
    public boolean doRules(Document arg0) {
        // TODO Auto-generated method stub
        return true;
    }

}
