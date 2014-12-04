/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.document.Document;

/**
 * Describes a Final Billed Indicator Entry.
 */
public class FinalBilledIndicatorEntry extends PersistableBusinessObjectBase {

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
