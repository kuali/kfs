/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class is used to represent an invoice agency address detail business object.
 */
public class InvoiceAgencyAddressDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String agencyAddressTypeCode;
    private String agencyAddressName;
    private String preferredAgencyInvoiceTemplateCode;
    private String agencyInvoiceTemplateCode;
    private String invoiceIndicatorCode;
    private String preferredInvoiceIndicatorCode;
    private long noteId;

    private ContractsGrantsInvoiceDocument invoiceDocument;
    private ContractsAndGrantsAgencyAddress agencyAddress;

    /**
     * Default constructor.
     */
    public InvoiceAgencyAddressDetail() {

    }

    /**
     * Gets the invoiceIndicatorCode attribute.
     * 
     * @return Returns the invoiceIndicatorCode.
     */
    public String getInvoiceIndicatorCode() {
        return invoiceIndicatorCode;
    }

    /**
     * Gets the noteId attribute.
     * 
     * @return Returns the noteId.
     */
    public long getNoteId() {
        return noteId;
    }

    /**
     * Sets the noteId attribute value.
     * 
     * @param noteId The noteId to set.
     */
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    /**
     * Sets the invoiceIndicatorCode attribute value.
     * 
     * @param invoiceIndicatorCode The invoiceIndicatorCode to set.
     */
    public void setInvoiceIndicatorCode(String invoiceIndicatorCode) {
        this.invoiceIndicatorCode = invoiceIndicatorCode;
    }

    /**
     * Gets the preferredInvoiceIndicatorCode attribute.
     * 
     * @return Returns the preferredInvoiceIndicatorCode.
     */
    public String getPreferredInvoiceIndicatorCode() {
        return preferredInvoiceIndicatorCode;
    }

    /**
     * Sets the preferredInvoiceIndicatorCode attribute value.
     * 
     * @param preferredInvoiceIndicatorCode The preferredInvoiceIndicatorCode to set.
     */
    public void setPreferredInvoiceIndicatorCode(String preferredInvoiceIndicatorCode) {
        this.preferredInvoiceIndicatorCode = preferredInvoiceIndicatorCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the agencyAddress attribute.
     * 
     * @return Returns the agencyAddress.
     */
    public ContractsAndGrantsAgencyAddress getAgencyAddress() {
        return agencyAddress = (ContractsAndGrantsAgencyAddress) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).retrieveExternalizableBusinessObjectIfNecessary(this, agencyAddress, "agencyAddress");
    }

    /**
     * Sets the agencyAddress attribute value.
     * 
     * @param agencyAddress The agencyAddress to set.
     */
    public void setAgencyAddress(ContractsAndGrantsAgencyAddress agencyAddress) {
        this.agencyAddress = agencyAddress;
    }

    /**
     * Gets the agencyAddressIdentifier attribute.
     * 
     * @return Returns the agencyAddressIdentifier
     */
    public Long getAgencyAddressIdentifier() {
        return agencyAddressIdentifier;
    }

    /**
     * Sets the agencyAddressIdentifier attribute.
     * 
     * @param agencyAddressIdentifier The agencyAddressIdentifier to set.
     */
    public void setAgencyAddressIdentifier(Long agencyAddressIdentifier) {
        this.agencyAddressIdentifier = agencyAddressIdentifier;
    }

    /**
     * Gets the agencyAddressTypeCode attribute.
     * 
     * @return Returns the agencyAddressTypeCode
     */
    public String getAgencyAddressTypeCode() {
        return agencyAddressTypeCode;
    }

    /**
     * Sets the agencyAddressTypeCode attribute.
     * 
     * @param agencyAddressTypeCode The agencyAddressTypeCode to set.
     */
    public void setAgencyAddressTypeCode(String agencyAddressTypeCode) {
        this.agencyAddressTypeCode = agencyAddressTypeCode;
    }

    /**
     * Gets the agencyAddressName attribute.
     * 
     * @return Returns the agencyAddressName
     */
    public String getAgencyAddressName() {
        return agencyAddressName;
    }

    /**
     * Sets the agencyAddressName attribute.
     * 
     * @param agencyAddressName The agencyAddressName to set.
     */
    public void setAgencyAddressName(String agencyAddressName) {
        this.agencyAddressName = agencyAddressName;
    }

    /**
     * Gets the agencyInvoiceTemplateCode attribute.
     * 
     * @return Returns the agencyInvoiceTemplateCode.
     */
    public String getAgencyInvoiceTemplateCode() {

        return agencyInvoiceTemplateCode;
    }

    /**
     * Sets the agencyInvoiceTemplateCode attribute value.
     * 
     * @param agencyInvoiceTemplateCode The agencyInvoiceTemplateCode to set.
     */
    public void setAgencyInvoiceTemplateCode(String agencyInvoiceTemplateCode) {
        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
    }

    /**
     * Gets the preferredAgencyInvoiceTemplateCode attribute.
     * 
     * @return Returns the preferredAgencyInvoiceTemplateCode.
     */
    public String getPreferredAgencyInvoiceTemplateCode() {

        return preferredAgencyInvoiceTemplateCode;
    }

    /**
     * Sets the preferredAgencyInvoiceTemplateCode attribute value.
     * 
     * @param preferredAgencyInvoiceTemplateCode The preferredAgencyInvoiceTemplateCode to set.
     */
    public void setPreferredAgencyInvoiceTemplateCode(String preferredAgencyInvoiceTemplateCode) {
        this.preferredAgencyInvoiceTemplateCode = preferredAgencyInvoiceTemplateCode;
    }


    /**
     * Gets the invoiceDocument attribute.
     * 
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument attribute value.
     * 
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(agencyNumber, this.agencyNumber);
        m.put(agencyAddressTypeCode, this.agencyAddressTypeCode);
        m.put(agencyAddressName, this.agencyAddressName);
        m.put(preferredAgencyInvoiceTemplateCode, this.preferredAgencyInvoiceTemplateCode);
        m.put(agencyInvoiceTemplateCode, this.agencyInvoiceTemplateCode);
        m.put(invoiceIndicatorCode, invoiceIndicatorCode);
        m.put(preferredInvoiceIndicatorCode, this.preferredInvoiceIndicatorCode);
        return m;
    }

}
