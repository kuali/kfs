package edu.arizona.kfs.module.cam.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.cam.CamsKeyConstants;
import edu.arizona.kfs.module.cam.CamsPropertyConstants;
import edu.arizona.kfs.module.cam.businessobject.AssetGlobalDetailExtension;
import edu.arizona.kfs.module.cam.businessobject.AssetInventoryUnit;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class AssetGlobalRule extends org.kuali.kfs.module.cam.document.validation.impl.AssetGlobalRule {

	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		boolean success = true;
		success &= super.processCustomSaveDocumentBusinessRules(document);

		AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

		List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
		int sharedIndex = 0;
		int uniqueIndex = 0;

		if (!assetSharedDetails.isEmpty() && !assetSharedDetails.get(0).getAssetGlobalUniqueDetails().isEmpty()) {
			for (AssetGlobalDetail locationDetail : assetSharedDetails) {
				List<AssetGlobalDetail> assetGlobalUniqueDetails = locationDetail.getAssetGlobalUniqueDetails();
				for (AssetGlobalDetail unique : assetGlobalUniqueDetails) {
					AssetGlobalDetailExtension assetGlobalDetailExtension = (AssetGlobalDetailExtension) unique.getExtension();
					assetGlobalDetailExtension.refreshReferenceObject(CamsPropertyConstants.AssetGlobalExtension.ASSET_INVENTORY_UNIT_OBJECT);
					AssetInventoryUnit invUnit = assetGlobalDetailExtension.getAssetInvUnitObj();

					String errorPath = MAINTAINABLE_ERROR_PREFIX + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + uniqueIndex + "]";
					GlobalVariables.getMessageMap().addToErrorPath(errorPath);

					if (ObjectUtils.isNull(invUnit)) {
						GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalExtension.EXTENSION_INVENTORY_UNIT_CODE, CamsKeyConstants.ERROR_INVENTORY_UNIT_CODE_NOT_EXIST, CamsConstants.AssetGlobal.ASSET_INVENTORY_UNIT_NULL);
						success = false;
					}
					else {
						BusinessObjectService bos = SpringContext.getBean(BusinessObjectService.class);
						Map<String, String> fieldValues = new HashMap<String, String>();
						fieldValues.put(KFSPropertyConstants.INVENTORY_UNIT_CODE, invUnit.getInventoryUnitCode());
						fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invUnit.getChartOfAccountsCode());
						fieldValues.put(KFSPropertyConstants.ORGANIZATION_CODE, invUnit.getOrganizationCode());
						fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
						Collection<AssetInventoryUnit> col = bos.findMatching(AssetInventoryUnit.class, fieldValues);
						
						// Check if inventory unit code exists
						if (col.size() < 1) {
							GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalExtension.EXTENSION_INVENTORY_UNIT_CODE, CamsKeyConstants.ERROR_INVENTORY_UNIT_CODE_NOT_EXIST, invUnit.getInventoryUnitCode());
							success = false;
						}

						// check to see if the COA Code on the assetGlobal account matches the one entered on the screen.
						if (!assetGlobal.getOrganizationOwnerAccount().getChartOfAccountsCode().equalsIgnoreCase(invUnit.getChartOfAccountsCode())) {
							GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalExtension.EXTENSION_INVENTORY_UNIT_CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_NOT_MATCH_ORG_OWNER_COA, invUnit.getChartOfAccountsCode());
							success = false;
						}

						// check to see if the Org Code on the assetGlobal account matches the one entered on the screen.
						if (!assetGlobal.getOrganizationOwnerAccount().getOrganizationCode().equalsIgnoreCase(invUnit.getOrganizationCode())) {
							GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetGlobalExtension.EXTENSION_INVENTORY_UNIT_ORGANIZATION_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_NOT_MATCH_ORG_OWNER_CODE, invUnit.getOrganizationCode());
							success = false;
						}
					}
					GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
					uniqueIndex++;
				}
				sharedIndex++;
			}
		}
		return success;
	}

}
