package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableDocument extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer purapDocumentIdentifier;
    private Integer purchaseOrderIdentifier;
    private String documentTypeCode;
    private boolean active;

    // References
    private DocumentType documentType;
    private FinancialSystemDocumentHeader documentHeader;
    private List<PurchasingAccountsPayableItemAsset> purchasingAccountsPayableItemAssets;

    public PurchasingAccountsPayableDocument() {
        this.purchasingAccountsPayableItemAssets = new TypedArrayList(PurchasingAccountsPayableItemAsset.class);
    }

    // non-persistent
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public List<PurchasingAccountsPayableItemAsset> getPurchasingAccountsPayableItemAssets() {
        return purchasingAccountsPayableItemAssets;
    }

    public void setPurchasingAccountsPayableItemAssets(List<PurchasingAccountsPayableItemAsset> purchasingAccountsPayableItemAssets) {
        this.purchasingAccountsPayableItemAssets = purchasingAccountsPayableItemAssets;
    }

    public String getPurApContactEmailAddress() {
        return purApContactEmailAddress;
    }

    public void setPurApContactEmailAddress(String purApContactEmailAddress) {
        this.purApContactEmailAddress = purApContactEmailAddress;
    }

    public String getPurApContactPhoneNumber() {
        return purApContactPhoneNumber;
    }

    public void setPurApContactPhoneNumber(String purApContactPhoneNumber) {
        this.purApContactPhoneNumber = purApContactPhoneNumber;
    }

    /**
     * Need to override this method, so we can save item assets, the framework can delete the allocated item assets.
     * 
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = new ArrayList<List>();

        managedLists.add(getPurchasingAccountsPayableItemAssets());
        return managedLists;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}
