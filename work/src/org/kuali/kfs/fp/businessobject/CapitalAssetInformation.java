package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

public class CapitalAssetInformation extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private Long capitalAssetNumber;
    private Integer capitalAssetQuantity;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetDescription;
    private String capitalAssetManufacturerModelNumber;
    

    private CapitalAssetManagementAsset capitalAssetManagementAsset;
    private CapitalAssetManagementAssetType capitalAssetManagementAssetType;
    private List<CapitalAssetInformationDetail> capitalAssetInformationDetails;

    private VendorDetail vendorDetail;
    // non-persistent field
    private Integer nextItemLineNumber;

    /**
     * Constructs a CapitalAssetInformation.java.
     */
    public CapitalAssetInformation() {
        super();
        capitalAssetInformationDetails = new TypedArrayList(CapitalAssetInformationDetail.class);
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentNumber", getDocumentNumber());

        return m;
    }

    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }

    public String getCapitalAssetManufacturerModelNumber() {
        return capitalAssetManufacturerModelNumber;
    }

    public void setCapitalAssetManufacturerModelNumber(String capitalAssetManufacturerModelNumber) {
        this.capitalAssetManufacturerModelNumber = capitalAssetManufacturerModelNumber;
    }

    public String getCapitalAssetManufacturerName() {
        return capitalAssetManufacturerName;
    }

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public Integer getCapitalAssetQuantity() {
        // Return capitalAssetQuantity first if it already set. Otherwise, return the size of details. If the order is reversed, the
        // user input of quantity may be overridden.
        if (this.capitalAssetQuantity != null) {
            return this.capitalAssetQuantity;
        }

        if (ObjectUtils.isNotNull(capitalAssetInformationDetails) && !capitalAssetInformationDetails.isEmpty()) {
            return capitalAssetInformationDetails.size();
        }
        return null;
    }

    public void setCapitalAssetQuantity(Integer capitalAssetQuantity) {
        this.capitalAssetQuantity = capitalAssetQuantity;
    }


    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailedAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailedAssignedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the capitalAssetManagementAsset attribute.
     * 
     * @return Returns the capitalAssetManagementAsset.
     */
    public CapitalAssetManagementAsset getCapitalAssetManagementAsset() {
        capitalAssetManagementAsset = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAsset.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAsset, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET);
        return capitalAssetManagementAsset;
    }

    /**
     * Sets the capitalAssetManagementAsset attribute value.
     * 
     * @param capitalAssetManagementAsset The capitalAssetManagementAsset to set.
     */
    public void setCapitalAssetManagementAsset(CapitalAssetManagementAsset capitalAssetManagementAsset) {
        this.capitalAssetManagementAsset = capitalAssetManagementAsset;
    }

    /**
     * Gets the capitalAssetManagementAssetType attribute.
     * 
     * @return Returns the capitalAssetManagementAssetType.
     */
    public CapitalAssetManagementAssetType getCapitalAssetManagementAssetType() {
        capitalAssetManagementAssetType = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAssetType.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAssetType, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET_TYPE);
        return capitalAssetManagementAssetType;
    }

    /**
     * Sets the capitalAssetManagementAssetType attribute value.
     * 
     * @param capitalAssetManagementAssetType The capitalAssetManagementAssetType to set.
     */
    @Deprecated
    public void setCapitalAssetManagementAssetType(CapitalAssetManagementAssetType capitalAssetManagementAssetType) {
        this.capitalAssetManagementAssetType = capitalAssetManagementAssetType;
    }

    /**
     * Gets the vendorDetail attribute.
     * 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * 
     * @param vendorDetail The vendorDetail to set.
     */
    @Deprecated
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, this.getVendorHeaderGeneratedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, this.getVendorDetailAssignedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_NAME, this.getVendorName());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, this.getCapitalAssetTypeCode());

        return simpleValues;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        if (ObjectUtils.isNotNull(vendorDetail)) {
            vendorName = vendorDetail.getVendorName();
        }
        else if (StringUtils.isNotBlank(vendorName) && vendorName.indexOf(" > ") > 0){
                vendorName = vendorName.substring(vendorName.indexOf(" > ") + 2, vendorName.length());
        }

        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the capitalAssetInformationDetails attribute.
     * 
     * @return Returns the capitalAssetInformationDetails.
     */
    public List<CapitalAssetInformationDetail> getCapitalAssetInformationDetails() {
        return capitalAssetInformationDetails;
    }

    /**
     * Sets the capitalAssetInformationDetails attribute value.
     * 
     * @param capitalAssetInformationDetails The capitalAssetInformationDetails to set.
     */
    public void setCapitalAssetInformationDetails(List<CapitalAssetInformationDetail> capitalAssetInformationDetails) {
        this.capitalAssetInformationDetails = capitalAssetInformationDetails;
    }

    /**
     * Gets the nextItemLineNumber attribute. 
     * @return Returns the nextItemLineNumber.
     */
    public Integer getNextItemLineNumber() {
        return nextItemLineNumber;
    }

    /**
     * Sets the nextItemLineNumber attribute value.
     * @param nextItemLineNumber The nextItemLineNumber to set.
     */
    public void setNextItemLineNumber(Integer nextItemLineNumber) {
        this.nextItemLineNumber = nextItemLineNumber;
    }
    
    
}
