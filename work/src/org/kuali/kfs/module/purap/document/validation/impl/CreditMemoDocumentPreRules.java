/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * Performs prompts and other pre business rule checks for the Credit Memo Docuemnt.
 */
public class CreditMemoDocumentPreRules extends AccountsPayableDocumentPreRulesBase {

    public CreditMemoDocumentPreRules() {
        super();
    }

    @Override
    public boolean doRules(Document document) {
        return super.doRules(document);
    }
    
    public String getDocumentName(){
        return "Credit Memo";
    }
}
