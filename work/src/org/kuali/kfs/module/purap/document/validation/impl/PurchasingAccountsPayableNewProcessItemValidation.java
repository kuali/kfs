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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableNewProcessItemValidation extends GenericValidation {

    private PurchasingAccountsPayableDocument purapDocument;
    private PurapService purapService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        //Fetch the business rules that are common to the below the line items on all purap documents
        String documentTypeClassName = purapDocument.getClass().getName();
        String[] documentTypeArray = StringUtils.split(documentTypeClassName, ".");
        String documentType = documentTypeArray[documentTypeArray.length - 1];

        boolean requiresAccountValidationOnAllEnteredItems = requiresAccountValidationOnAllEnteredItems(purapDocument);
        
        return success;
    }
    
    /**
     * Determines whether the document will require account validation to be done on all of its items.
     * 
     * @param document The PurchasingAccountsPayable document to be validated.
     * @return boolean true.
     */
    protected boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {

        return true;
    }
 
    
    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }    
    
    public PurchasingAccountsPayableDocument getPurapDocument() {
        return purapDocument;
    }

    public void setPurapDocument(PurchasingAccountsPayableDocument purapDocument) {
        this.purapDocument = purapDocument;
    }    

}
