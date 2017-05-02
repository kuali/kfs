package edu.arizona.kfs.module.cam;

public class CamsPropertyConstants extends org.kuali.kfs.module.cam.CamsPropertyConstants {
	
	public static final String INVENTORY_UNIT_ORGANIZATION_CODE= "inventoryUnitOrganizationCode";
	public static final String INVENTORY_UNIT_CHART_OF_ACCOUNTS_CODE = "inventoryUnitChartOfAccountsCode";

	public static class AssetGlobalExtension {

		public static final String ASSET_INVENTORY_UNIT_OBJECT = "assetInvUnitObj";
		public static final String EXTENSION_INVENTORY_UNIT_CODE = "extension.inventoryUnitCode";
		public static final String EXTENSION_INVENTORY_UNIT_CHART_OF_ACCOUNTS_CODE = "extension.inventoryUnitChartOfAccountsCode";
		public static final String EXTENSION_INVENTORY_UNIT_ORGANIZATION_CODE = "extension.inventoryUnitOrganizationCode";

	}

	public static class AssetAccountResponsibility {
		public static final String AGENCY_NUMBER = "agencyNumber";
		public static final String ASSET_EXTENSION_ACCOUNT_RESPONSIBILITIES = "extension.assetAccountResponsibilities";
		public static final String MAINT_SECTION_ID = "awardHistory";
	}
}
