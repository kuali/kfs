/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.HashMap;
import java.util.Iterator;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

public class BulkReceivingDocumentPreRules extends PromptBeforeValidationBase{

    public boolean doPrompts(Document document) {
        
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;
        
        HashMap<String, String> duplicateMessages = SpringContext.getBean(BulkReceivingService.class).bulkReceivingDuplicateMessages(bulkReceivingDocument);
        
        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Iterator iterator = duplicateMessages.values().iterator();
            StringBuffer msg = new StringBuffer();
            while(iterator.hasNext()) {
                msg.append(iterator.next());
            }
            boolean proceed = super.askOrAnalyzeYesNoQuestion(PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION, msg.toString());
            if (!proceed) {
                abortRulesCheck();
            }
        }
        
        return true;
    }
    

}
