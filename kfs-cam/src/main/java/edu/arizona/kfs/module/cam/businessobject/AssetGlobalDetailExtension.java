package edu.arizona.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

import edu.arizona.kfs.sys.KFSPropertyConstants;

public class AssetGlobalDetailExtension extends PersistableBusinessObjectExtensionBase {

	private static final long serialVersionUID = 1L;
	private Long capitalAssetNumber;
	private String documentNumber;
	private String inventoryUnitCode;
	private String inventoryUnitOrganizationCode;
	private String inventoryUnitChartOfAccountsCode;

	private AssetInventoryUnit assetInvUnitObj;
	private AssetGlobalDetail assetGlobalDetailObj;

	public Long getCapitalAssetNumber() {
		return capitalAssetNumber;
	}

	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getInventoryUnitCode() {
		return inventoryUnitCode;
	}

	public void setInventoryUnitCode(String inventoryUnitCode) {
		this.inventoryUnitCode = inventoryUnitCode;
	}

	public String getInventoryUnitOrganizationCode() {
		return inventoryUnitOrganizationCode;
	}

	public void setInventoryUnitOrganizationCode(String inventoryUnitOrganizationCode) {
		this.inventoryUnitOrganizationCode = inventoryUnitOrganizationCode;
	}

	public String getInventoryUnitChartOfAccountsCode() {
		return inventoryUnitChartOfAccountsCode;
	}

	public void setInventoryUnitChartOfAccountsCode(String inventoryUnitChartOfAccountsCode) {
		this.inventoryUnitChartOfAccountsCode = inventoryUnitChartOfAccountsCode;
	}

	public AssetInventoryUnit getAssetInvUnitObj() {
		return assetInvUnitObj;
	}

	public void setAssetInvUnitObj(AssetInventoryUnit assetInvUnitObj) {
		this.assetInvUnitObj = assetInvUnitObj;
	}

	public AssetGlobalDetail getAssetGlobalDetailObj() {
		return assetGlobalDetailObj;
	}

	public void setAssetGlobalDetailObj(AssetGlobalDetail assetGlobalDetailObj) {
		this.assetGlobalDetailObj = assetGlobalDetailObj;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		
		if (this.capitalAssetNumber != null && this.documentNumber != null) {
			m.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.capitalAssetNumber.toString());
			m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber.toString());
		}

		return m;
	}
}
