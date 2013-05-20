/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.document.Document;

/**
 * Describes a Final Invoice Reversal Entry.
 */
public class FinalInvoiceReversalEntry extends PersistableBusinessObjectBase {

    private Long id;
    private String invoiceDocumentNumber;
    private String documentId;
    private Document invoiceDocument; 

    /**
     * Gets the id attribute.
     *
     * @return Returns the id. 
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     *
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber 
     */
    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setInvoiceDocumentNumber(String documentNumber) {
        this.invoiceDocumentNumber = documentNumber;
    }

    /**
     * Gets the documentId attribute.
     *
     * @return Returns the documentId 
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the documentId attribute value.
     *
     * @param documentId The documentId to set.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Gets the invoiceDocument attribute.
     *
     * @return Returns the invoiceDocument 
     */
    public Document getInvoiceDocument() {
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument attribute value.
     *
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(Document invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }
    
    public Document getDocument() {
        return invoiceDocument;
    }

    public void setDocument(Document document) {
        this.invoiceDocument = document;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> toStringMap = new LinkedHashMap<String ,Object>();
        toStringMap.put("id", id);
        toStringMap.put("invoiceDocumentNumber", invoiceDocumentNumber);
        toStringMap.put("documentId", documentId);
        toStringMap.put("document", invoiceDocument);
        return toStringMap;
    }
}
