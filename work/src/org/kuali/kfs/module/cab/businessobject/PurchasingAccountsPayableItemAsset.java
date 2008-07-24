package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.module.cam.businessobject.AssetLocationGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableItemAsset extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer lineItemId; 
    private Integer capitalAssetBuilderLineNumber;
    private String lineItemDescription;
    private Long capitalAssetBuilderQuantity;
    private String capitalAssetDescription;
    private String capitalAssetTypeCode;
    private String vendorName;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private String capitalAssetManagementDocumentNumber;

    private AssetType capitalAssetType;
    private List<PurchasingAccountsPayableAssetDetail> PurchasingAccountsPayableAssetDetail;
    
    
    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the lineItemId attribute. 
     * @return Returns the lineItemId.
     */
    public Integer getLineItemId() {
        return lineItemId;
    }

    /**
     * Sets the lineItemId attribute value.
     * @param lineItemId The lineItemId to set.
     */
    public void setLineItemId(Integer lineItemId) {
        this.lineItemId = lineItemId;
    }

    /**
     * Gets the purchasingAccountsPayableAssetDetail attribute. 
     * @return Returns the purchasingAccountsPayableAssetDetail.
     */
    public List<PurchasingAccountsPayableAssetDetail> getPurchasingAccountsPayableAssetDetail() {
        return PurchasingAccountsPayableAssetDetail;
    }

    /**
     * Sets the purchasingAccountsPayableAssetDetail attribute value.
     * @param purchasingAccountsPayableAssetDetail The purchasingAccountsPayableAssetDetail to set.
     */
    public void setPurchasingAccountsPayableAssetDetail(List<PurchasingAccountsPayableAssetDetail> purchasingAccountsPayableAssetDetail) {
        PurchasingAccountsPayableAssetDetail = purchasingAccountsPayableAssetDetail;
    }

    /**
     * Gets the capitalAssetBuilderLineNumber attribute.
     * 
     * @return Returns the capitalAssetBuilderLineNumber
     * 
     */
    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    /**
     * Sets the capitalAssetBuilderLineNumber attribute.
     * 
     * @param capitalAssetBuilderLineNumber The capitalAssetBuilderLineNumber to set.
     * 
     */
    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
    }
    
    /**
     * Gets the capitalAssetBuilderQuantity attribute.
     * 
     * @return Returns the capitalAssetBuilderQuantity
     * 
     */
    public Long getCapitalAssetBuilderQuantity() {
        return capitalAssetBuilderQuantity;
    }

    /**
     * Sets the capitalAssetBuilderQuantity attribute.
     * 
     * @param capitalAssetBuilderQuantity The capitalAssetBuilderQuantity to set.
     * 
     */
    public void setCapitalAssetBuilderQuantity(Long capitalAssetBuilderQuantity) {
        this.capitalAssetBuilderQuantity = capitalAssetBuilderQuantity;
    }
    
    /**
     * Gets the capitalAssetDescription attribute.
     * 
     * @return Returns the capitalAssetDescription
     */
    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     * 
     * @param capitalAssetDescription The capitalAssetDescription to set.
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }
    
    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }
    
    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    
    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     * 
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     * 
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }
    
    /**
     * Gets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @return Returns the capitalAssetManagementDocumentNumber
     */
    public String getCapitalAssetManagementDocumentNumber() {
        return capitalAssetManagementDocumentNumber;
    }

    /**
     * Sets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @param capitalAssetManagementDocumentNumber The capitalAssetManagementDocumentNumber to set.
     */
    public void setCapitalAssetManagementDocumentNumber(String capitalAssetManagementDocumentNumber) {
        this.capitalAssetManagementDocumentNumber = capitalAssetManagementDocumentNumber;
    }
    
    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }
    
    /**
     * Gets the purApLineItemDescription attribute. 
     * @return Returns the purApLineItemDescription.
     */
    public String getPurApLineItemDescription() {
        return lineItemDescription;
    }

    /**
     * Sets the purApLineItemDescription attribute value.
     * @param purApLineItemDescription The purApLineItemDescription to set.
     */
    public void setPurApLineItemDescription(String purApLineItemDescription) {
        this.lineItemDescription = purApLineItemDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        m.put("lineItemId", this.lineItemId);
        m.put("capitalAssetBuilderLineNumber", this.capitalAssetBuilderLineNumber);
        return m;
    }
}
