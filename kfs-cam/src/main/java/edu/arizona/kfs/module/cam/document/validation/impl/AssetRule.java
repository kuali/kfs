package edu.arizona.kfs.module.cam.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.cam.CamsKeyConstants;
import edu.arizona.kfs.module.cam.CamsPropertyConstants;
import edu.arizona.kfs.module.cam.businessobject.AssetExtension;
import edu.arizona.kfs.module.cam.businessobject.AssetInventoryUnit;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class AssetRule extends org.kuali.kfs.module.cam.document.validation.impl.AssetRule {

	protected boolean isDocumentValidForSave(MaintenanceDocument document) {
		boolean valid = super.isDocumentValidForSave(document);

		LOG.info("processCustomSaveDocumentBusinessRules called");

		// get the new Asset Bo
		setupConvenienceObjects();

		AssetExtension assetExtension = (AssetExtension) newAsset.getExtension();

		assetExtension.refreshReferenceObject(CamsPropertyConstants.AssetGlobalExtension.ASSET_INVENTORY_UNIT_OBJECT);
		AssetInventoryUnit invUnit = assetExtension.getAssetInvUnitObj();

		if (ObjectUtils.isNull(invUnit)) {
			putError(KFSPropertyConstants.INVENTORY_UNIT_CODE, CamsKeyConstants.ERROR_INVENTORY_UNIT_CODE_NOT_EXIST, CamsConstants.AssetGlobal.ASSET_INVENTORY_UNIT_NULL);
			valid = false;
		}
		else {
			BusinessObjectService bos = SpringContext.getBean(BusinessObjectService.class);
			Map<String, String> fieldValues = new HashMap<String, String>();
			fieldValues.put(KFSPropertyConstants.INVENTORY_UNIT_CODE, invUnit.getInventoryUnitCode());
			fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invUnit.getChartOfAccountsCode());
			fieldValues.put(KFSPropertyConstants.ORGANIZATION_CODE, invUnit.getOrganizationCode());
			fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
			Collection<AssetInventoryUnit> col = bos.findMatching(AssetInventoryUnit.class, fieldValues);

			if (col.size() < 1) {
				putError(KFSPropertyConstants.INVENTORY_UNIT_CODE, CamsKeyConstants.ERROR_INVENTORY_UNIT_CODE_NOT_EXIST, invUnit.getInventoryUnitCode());
				valid = false;
			}

			if (!newAsset.getOrganizationOwnerAccount().getChartOfAccountsCode().equalsIgnoreCase(invUnit.getChartOfAccountsCode())) {
				putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_NOT_MATCH_ORG_OWNER_COA, invUnit.getChartOfAccountsCode());
				valid = false;
			}

			if (!newAsset.getOrganizationOwnerAccount().getOrganizationCode().equalsIgnoreCase(invUnit.getOrganizationCode())) {
				putError(KFSPropertyConstants.ORGANIZATION_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_NOT_MATCH_ORG_OWNER_CODE, invUnit.getOrganizationCode());
				valid = false;
			}
		}
		return valid;

	}

	public void setupConvenienceObjects() {
		LOG.debug("setupConvenienceObjects called");
		oldAsset = (Asset) super.getOldBo();
		newAsset = (Asset) super.getNewBo();
		
	}

}
