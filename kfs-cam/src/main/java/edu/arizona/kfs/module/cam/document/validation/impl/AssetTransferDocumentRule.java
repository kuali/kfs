package edu.arizona.kfs.module.cam.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.module.cam.CamsKeyConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.CamsPropertyConstants;
import edu.arizona.kfs.module.cam.document.AssetTransferDocument;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class AssetTransferDocumentRule extends org.kuali.kfs.module.cam.document.validation.impl.AssetTransferDocumentRule {

	@Override
	protected boolean processCustomSaveDocumentBusinessRules(Document document) {
		boolean success = true;
		AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;

		if (StringUtils.isBlank(assetTransferDocument.getInventoryUnitOrganizationCode())) {
			putError(CamsPropertyConstants.INVENTORY_UNIT_ORGANIZATION_CODE, CamsKeyConstants.ERROR_INVENTORY_ORG_OWNER_CODE_REQUIRED, assetTransferDocument.getInventoryUnitOrganizationCode());
			success = false;
		}

		if (StringUtils.isBlank(assetTransferDocument.getInventoryUnitChartOfAccountsCode())) {
			putError(CamsPropertyConstants.INVENTORY_UNIT_CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_CODE_REQUIRED, assetTransferDocument.getInventoryUnitChartOfAccountsCode());
			success = false;
		}

		if (StringUtils.isBlank(assetTransferDocument.getInventoryUnitCode())) {
			putError(KFSPropertyConstants.INVENTORY_UNIT_CODE, CamsKeyConstants.ERROR_INVENTORY_UNIT_CODE_REQUIRED, assetTransferDocument.getInventoryUnitCode());
			success = false;
		}

		if (ObjectUtils.isNotNull(assetTransferDocument.getOrganizationOwnerAccount()) && StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerAccount().getOrganizationCode()) && StringUtils.isNotBlank(assetTransferDocument.getInventoryUnitOrganizationCode())) {
			if (!assetTransferDocument.getOrganizationOwnerAccount().getOrganizationCode().equalsIgnoreCase(assetTransferDocument.getInventoryUnitOrganizationCode())) {
				putError(CamsPropertyConstants.INVENTORY_UNIT_ORGANIZATION_CODE, CamsKeyConstants.ERROR_INVENTORY_COA_NOT_MATCH_ORG_OWNER_CODE, assetTransferDocument.getInventoryUnitOrganizationCode());
				success = false;
			}
		}

		if (success == true) {
			success &= super.processCustomSaveDocumentBusinessRules(document);
		}

		return success;
	}

}
