package edu.arizona.kfs.module.cam.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import edu.arizona.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.web.struts.AssetTransferForm;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;

import edu.arizona.kfs.module.cam.businessobject.AssetExtension;

public class AssetTransferAction extends
		org.kuali.kfs.module.cam.document.web.struts.AssetTransferAction {

	@Override
	public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);

		// refresh asset information
		AssetTransferForm assetTransferForm = (AssetTransferForm) form;
		AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
		handleRequestFromLookup(request, assetTransferForm, assetTransferDocument);
		handleRequestFromWorkflow(assetTransferForm, assetTransferDocument);
		Asset asset = assetTransferDocument.getAsset();
		asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
		asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
		SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(asset);
		SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(asset);
		AssetExtension extension = (AssetExtension) asset.getExtension();

		// populate old asset fields for historic retaining on document
		String command = assetTransferForm.getCommand();
		if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
			assetTransferDocument.setOldOrganizationOwnerChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());
			assetTransferDocument.setOldOrganizationOwnerAccountNumber(asset.getOrganizationOwnerAccountNumber());
			assetTransferDocument.setOldInventoryUnitCode(extension.getInventoryUnitCode());
			assetTransferDocument.setOldInventoryUnitChartOfAccountsCode(extension.getInventoryUnitChartOfAccountsCode());
			assetTransferDocument.setOldInventoryUnitOrganizationCode(extension.getInventoryUnitOrganizationCode());
		}

		this.refresh(mapping, form, request, response);

		return docHandlerForward;
	}

}
