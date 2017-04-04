package edu.arizona.kfs.module.cam.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetExtension extends PersistableBusinessObjectExtensionBase {
	private static final long serialVersionUID = 1L;

	private Long capitalAssetNumber;
	private String inventoryUnitCode;
	private String inventoryUnitOrganizationCode;
	private String inventoryUnitChartOfAccountsCode;
	
	private Asset assetObj;
	private AssetInventoryUnit assetInvUnitObj;
	private List<AssetAccountResponsibility> assetAccountResponsibilities;
	
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		if (this.capitalAssetNumber != null) {
			m.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.capitalAssetNumber.toString());
		}
		
		return m;
	}
	
	public String getOrganizationOwnerAccountOrganizationCode() {
		if (ObjectUtils.isNotNull(assetObj) && ObjectUtils.isNotNull(assetObj.getOrganizationOwnerAccount()) && StringUtils.isNotBlank(assetObj.getOrganizationOwnerAccount().getOrganizationCode())) {
			return assetObj.getOrganizationOwnerAccount().getOrganizationCode();
		}

		return null;
	}
	
	public String getOrganizationOwnerAccountChartOfAccountsCode() {
		if (ObjectUtils.isNotNull(assetObj) && ObjectUtils.isNotNull(assetObj.getOrganizationOwnerAccount()) && StringUtils.isNotBlank(assetObj.getOrganizationOwnerAccount().getChartOfAccountsCode())) {
			return assetObj.getOrganizationOwnerAccount().getChartOfAccountsCode();
		}

		return null;
	}

	public Long getCapitalAssetNumber() {
		return capitalAssetNumber;
	}

	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
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

	public Asset getAssetObj() {
		return assetObj;
	}

	public void setAssetObj(Asset assetObj) {
		this.assetObj = assetObj;
	}

	public AssetInventoryUnit getAssetInvUnitObj() {
		return assetInvUnitObj;
	}

	public void setAssetInvUnitObj(AssetInventoryUnit assetInvUnitObj) {
		this.assetInvUnitObj = assetInvUnitObj;
	}

	public List<AssetAccountResponsibility> getAssetAccountResponsibilities() {
		return assetAccountResponsibilities;
	}

	public void setAssetAccountResponsibilities(List<AssetAccountResponsibility> assetAccountResponsibilities) {
		this.assetAccountResponsibilities = assetAccountResponsibilities;
	}
	
	@Override
	public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
		List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
		managedLists.add(new ArrayList<PersistableBusinessObject>(getAssetAccountResponsibilities()));
		return managedLists;
	}
}
