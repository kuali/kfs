package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetailBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobalDetail extends GlobalBusinessObjectDetailBase {

    private String documentNumber;
    private Long capitalAssetNumber;
    private DocumentHeader documentHeader;
    private Asset asset;
    private AssetRetirementGlobal assetRetirementGlobal;

    private transient String hiddenFieldForError;

    /**
     * Default constructor.
     */
    public AssetRetirementGlobalDetail() {
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
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * 
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    public AssetRetirementGlobal getAssetRetirementGlobal() {
        return assetRetirementGlobal;
    }

    public void setAssetRetirementGlobal(AssetRetirementGlobal assetRetirementGlobal) {
        this.assetRetirementGlobal = assetRetirementGlobal;
    }


    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    
    /**
     * Gets the hiddenFieldForError attribute. 
     * @return Returns the hiddenFieldForError.
     */
    public String getHiddenFieldForError() {
        return hiddenFieldForError;
    }

    /**
     * Sets the hiddenFieldForError attribute value.
     * @param hiddenFieldForError The hiddenFieldForError to set.
     */
    public void setHiddenFieldForError(String hiddenFieldForError) {
        this.hiddenFieldForError = hiddenFieldForError;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }
}
