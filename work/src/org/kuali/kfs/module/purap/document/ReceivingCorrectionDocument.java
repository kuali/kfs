package org.kuali.module.purap.document;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.module.purap.bo.ReceivingCorrectionItem;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionDocument extends PurchasingAccountsPayableDocumentBase {

    private String documentNumber;
    private String receivingLineDocumentNumber;

    private ReceivingLineDocument receivingLineDocument;
    private DocumentHeader documentHeader;

    
    /**
     * Default constructor.
     */
    public ReceivingCorrectionDocument() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() { 
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the receivingLineDocumentNumber attribute.
     * 
     * @return Returns the receivingLineDocumentNumber
     * 
     */
    public String getReceivingLineDocumentNumber() { 
        return receivingLineDocumentNumber;
    }

    /**
     * Sets the receivingLineDocumentNumber attribute.
     * 
     * @param receivingLineDocumentNumber The receivingLineDocumentNumber to set.
     * 
     */
    public void setReceivingLineDocumentNumber(String receivingLineDocumentNumber) {
        this.receivingLineDocumentNumber = receivingLineDocumentNumber;
    }

    /**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the receivingLineDocument attribute. 
     * @return Returns the receivingLineDocument.
     */
    public ReceivingLineDocument getReceivingLineDocument() {
        return receivingLineDocument;
    }

    /**
     * Sets the receivingLineDocument attribute value.
     * @param receivingLineDocument The receivingLineDocument to set.
     * @deprecated
     */
    public void setReceivingLineDocument(ReceivingLineDocument receivingLineDocument) {
        this.receivingLineDocument = receivingLineDocument;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return ReceivingCorrectionItem.class;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible() {
        return null;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    public String getPurApSourceDocumentLabelIfPossible() {
        return null;
    }

}
