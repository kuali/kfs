/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetComponent;
import org.kuali.kfs.module.cam.businessobject.AssetRepairHistory;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Presentation Controller for Asset Maintenance Documents
 */
public class AssetPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);

        MaintenanceDocument document = (MaintenanceDocument) businessObject;

        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document)) {
            fields.addAll(Arrays.asList(CamsConstants.Asset.EDIT_DETAIL_INFORMATION_FIELDS));
            fields.addAll(Arrays.asList(CamsConstants.Asset.EDIT_ORGANIZATION_INFORMATION_FIELDS));
        }

        // Hide payment sequence numbers
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        hideInactiveAssetComponent(fields, asset);
        hideInactiveAssetRepairHistory(fields, asset);
        hideInactiveAssetWarranty(fields, asset);

        int size = asset.getAssetPayments().size();
        for (int i = 0; i < size; i++) {
            fields.add(CamsPropertyConstants.Asset.ASSET_PAYMENTS + "[" + i + "]." + CamsPropertyConstants.AssetPayment.PAYMENT_SEQ_NUMBER);
            fields.add(CamsPropertyConstants.Asset.ASSET_PAYMENTS + "[" + i + "]." + CamsPropertyConstants.AssetPayment.CAPITAL_ASSET_NUMBER);
        }

        if (!CamsConstants.AssetRetirementReasonCode.MERGED.equals(asset.getRetirementReasonCode())) {
            // hide merge target capital asset number
            fields.add(CamsPropertyConstants.Asset.RETIREMENT_INFO_MERGED_TARGET);
        }

        return fields;
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);

        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document)) {
            fields.add(CamsPropertyConstants.Asset.ASSET_INVENTORY_STATUS);
            fields.add(CamsPropertyConstants.Asset.VENDOR_NAME);
            fields.add(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        }
        else {
            // acquisition type code is read-only during edit
            fields.add(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
            // fabrication fields are read-only
            fields.addAll(Arrays.asList(CamsConstants.Asset.FABRICATION_INFORMATION_FIELDS));
        }

        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
        // if tag was created in a prior fiscal year, set tag number, asset type code and description as view only
        if (SpringContext.getBean(AssetService.class).isAssetTaggedInPriorFiscalYear(asset)) {
            AssetAuthorizer documentAuthorizer = (AssetAuthorizer) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(document);
            boolean isAuthorized = documentAuthorizer.isAuthorized(document, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.EDIT_WHEN_TAGGED_PRIOR_FISCAL_YEAR, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            if (!isAuthorized) {
                fields.addAll(SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, CamsConstants.Parameters.EDITABLE_FIELDS_WHEN_TAGGED_PRIOR_FISCAL_YEAR));
            }
        }

        return fields;
    }

    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenSectionIds(businessObject);

        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        Asset asset = (Asset) document.getNewMaintainableObject().getBusinessObject();

        if (asset.getAssetPayments().size() > CamsConstants.Asset.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY) {
            // Hide the payment section if there are more then CamsConstants.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY
            fields.add(CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION);
        }

        if (SpringContext.getBean(AssetService.class).isAssetFabrication(document)) {
            // fabrication request asset creation. Hide sections that are only applicable to asset edit. For fields
            // that are to be hidden for asset edit, see AssetAuthorizer.addMaintenanceDocumentRestrictions
            fields.add(CamsConstants.Asset.SECTION_ID_LAND_INFORMATION);
            fields.add(CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION);
            fields.add(CamsConstants.Asset.SECTION_ID_PAYMENT_LOOKUP);
            fields.add(CamsConstants.Asset.SECTION_ID_DOCUMENT_LOOKUP);
            fields.add(CamsConstants.Asset.SECTION_ID_DEPRECIATION_INFORMATION);
            fields.add(CamsConstants.Asset.SECTION_ID_HISTORY);
            fields.add(CamsConstants.Asset.SECTION_ID_RETIREMENT_INFORMATION);
            fields.add(CamsConstants.Asset.SECTION_ID_EQUIPMENT_LOAN_INFORMATION);
            fields.add(CamsConstants.Asset.SECTION_ID_WARRENTY);
            fields.add(CamsConstants.Asset.SECTION_ID_REPAIR_HISTORY);
            fields.add(CamsConstants.Asset.SECTION_ID_COMPONENTS);
            fields.add(CamsConstants.Asset.SECTION_ID_MERGE_HISTORY);
        }
        else {
            // Asset edit
            if (asset.getEstimatedFabricationCompletionDate() == null) {
                // hide fabrication only if asset is not fabricated through CAMS
                fields.add(CamsConstants.Asset.SECTION_ID_FABRICATION_INFORMATION);
            }

            if (!SpringContext.getBean(AssetService.class).isAssetRetired(asset)) {
                // if asset is not retired, hide retirement information
                fields.add(CamsConstants.Asset.SECTION_ID_RETIREMENT_INFORMATION);
            }

            if (asset.getExpectedReturnDate() == null || asset.getLoanReturnDate() != null) {
                // If asset is not loaned, hide the section
                fields.add(CamsConstants.Asset.SECTION_ID_LOAN_INFORMATION);
            }
            if (asset.getSeparateHistory() == null) {
                // If asset is not separated, hide the section
                fields.add(CamsConstants.Asset.SECTION_ID_HISTORY);
            }
        }

        return fields;
    }


    @Override
    public boolean canEdit(Document document) {
        AssetService assetService = SpringContext.getBean(AssetService.class);

        // for fabrication document, disallow edit when document routing to the 'Management' FYI users.
        if (assetService.isAssetFabrication((MaintenanceDocument) document)) {
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (workflowDocument.isEnroute()) {
                Set<String> nodeNames = assetService.getCurrentRouteLevels(workflowDocument);

                if (nodeNames.contains(CamsConstants.RouteLevelNames.MANAGEMENT) && !workflowDocument.isApprovalRequested()) {
                    return false;
                }
            }
        }
        // for retired asset, disable edit.
        MaintenanceDocument maitDocument = (MaintenanceDocument) document;
        Asset asset = (Asset) maitDocument.getOldMaintainableObject().getBusinessObject();
        return !getAssetService().isAssetRetired(asset) & super.canEdit(document);
    }


    @Override
    public boolean canBlanketApprove(Document document) {
        return true;
    }

    @Override
    public boolean canRoute(Document document) {
        MaintenanceDocument maitDocument = (MaintenanceDocument) document;
        Asset asset = (Asset) maitDocument.getOldMaintainableObject().getBusinessObject();
        return !getAssetService().isAssetRetired(asset) & super.canRoute(document);
    }

    @Override
    public boolean canSave(Document document) {
        MaintenanceDocument maitDocument = (MaintenanceDocument) document;
        Asset asset = (Asset) maitDocument.getOldMaintainableObject().getBusinessObject();
        return !getAssetService().isAssetRetired(asset) & super.canSave(document);
    }

    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> actions = super.getDocumentActions(document);

        if (document instanceof LedgerPostingDocument) {
            // check accounting period is enabled for doc type in system parameter
            String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
            ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
            ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
            if (evaluator.evaluationSucceeds()) {
                actions.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
        }

        return actions;
    }

    private void hideInactiveAssetWarranty(Set<String> fields , Asset asset){
        if(ObjectUtils.isNotNull(asset.getAssetWarranty())) {
            if(!asset.getAssetWarranty().isActive()) {
                fields.add(CamsPropertyConstants.Asset.ASSET_WARRANTY);
            }
        }
    }

    private  void hideInactiveAssetComponent(Set<String> fields , Asset asset){
      List<AssetComponent> components = asset.getAssetComponents();
      int i=0;
      for(AssetComponent component : components ) {
          if(!component.isActive()) {
              fields.add(CamsPropertyConstants.Asset.ASSET_COMPONENTS + "[" + i + "]");
          }
          i++;
      }
    }


    private  void hideInactiveAssetRepairHistory(Set<String> fields , Asset asset) {
        int i=0;
        for(AssetRepairHistory assetRepairHistory : asset.getAssetRepairHistory()) {
            if(!assetRepairHistory.isActive()) {
                fields.add(CamsPropertyConstants.Asset.ASSET_REPAIR_HISTORY+ "[" + i + "]");
            }
           i++;
        }
    }
}
