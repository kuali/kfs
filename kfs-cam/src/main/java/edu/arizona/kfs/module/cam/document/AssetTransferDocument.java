package edu.arizona.kfs.module.cam.document;

import edu.arizona.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import edu.arizona.kfs.module.cam.businessobject.AssetInventoryUnit;

public class AssetTransferDocument extends org.kuali.kfs.module.cam.document.AssetTransferDocument {

	private static final long serialVersionUID = 1L;
	private String inventoryUnitCode;
	private String inventoryUnitOrganizationCode;
	private String inventoryUnitChartOfAccountsCode;
	private String oldInventoryUnitCode;
	private String oldInventoryUnitOrganizationCode;
	private String oldInventoryUnitChartOfAccountsCode;

	private AssetInventoryUnit assetInvUnitObj;

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

	public String getOldInventoryUnitCode() {
		return oldInventoryUnitCode;
	}

	public void setOldInventoryUnitCode(String oldInventoryUnitCode) {
		this.oldInventoryUnitCode = oldInventoryUnitCode;
	}

	public String getOldInventoryUnitOrganizationCode() {
		return oldInventoryUnitOrganizationCode;
	}

	public void setOldInventoryUnitOrganizationCode(String oldInventoryUnitOrganizationCode) {
		this.oldInventoryUnitOrganizationCode = oldInventoryUnitOrganizationCode;
	}

	public String getOldInventoryUnitChartOfAccountsCode() {
		return oldInventoryUnitChartOfAccountsCode;
	}

	public void setOldInventoryUnitChartOfAccountsCode(String oldInventoryUnitChartOfAccountsCode) {
		this.oldInventoryUnitChartOfAccountsCode = oldInventoryUnitChartOfAccountsCode;
	}

	public AssetInventoryUnit getAssetInvUnitObj() {
		return assetInvUnitObj;
	}

	public void setAssetInvUnitObj(AssetInventoryUnit assetInvUnitObj) {
		this.assetInvUnitObj = assetInvUnitObj;
	}

	@Override
	public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
		super.doRouteStatusChange(statusChangeEvent);

		WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
		if (workflowDocument.isProcessed()) {
			SpringContext.getBean(AssetTransferService.class).saveApprovedChanges(this);
		}

		// Remove asset lock when doc status change. We don't include isFinal since document always go to 'processed' first.
		if (workflowDocument.isCanceled() || workflowDocument.isDisapproved() || workflowDocument.isProcessed()) {
			getCapitalAssetManagementModuleService().deleteAssetLocks(this.getDocumentNumber(), null);
		}
	}

}
