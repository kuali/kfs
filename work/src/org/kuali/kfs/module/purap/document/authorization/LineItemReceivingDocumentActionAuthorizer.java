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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Determines permissions for a user to view the
 * buttons on Line Item Receiving Document.
 * 
 */
public class LineItemReceivingDocumentActionAuthorizer {

    private LineItemReceivingDocument receivingLine;
    
    /**
     * Constructs a LineItemReceivingDocumentActionAuthorizer.
     * 
     * @param po A LineItemReceivingDocument
     */
    public LineItemReceivingDocumentActionAuthorizer(LineItemReceivingDocument rl, Map editingMode) {
        this.receivingLine = rl;
    }

    /**
     * Determines if a correction receiving document can be created for the line item receiving document
     * 
     * @return
     */
    public boolean canCreateCorrection() {
        Person user = GlobalVariables.getUserSession().getPerson();
        String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CorrectionReceivingDocument.class);
        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(documentTypeName);
        boolean isUserAuthorized = documentAuthorizer.canInitiate(documentTypeName, user);
        return SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(receivingLine) && isUserAuthorized;
    }
}
