package org.kuali.module.purap.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.module.purap.bo.ReceivingCorrectionItem;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ReceivingCorrectionDocument extends ReceivingDocumentBase {

    private String receivingLineDocumentNumber;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    
    //Collections
    private List<ReceivingCorrectionItem> items;
    
    private ReceivingLineDocument receivingLineDocument;
    
    /**
     * Default constructor.
     */
    public ReceivingCorrectionDocument() {
        super();
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

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
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
    public Class getItemClass() {
        return ReceivingCorrectionItem.class;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public ReceivingCorrectionItem getItem(int pos) {
        return (ReceivingCorrectionItem) items.get(pos);
    }

}
