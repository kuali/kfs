/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;

/**
 * Presentation Controller for Asset Retirement Global Maintenance Documents
 */
public class AssetRetirementPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    protected static final Map<String, String[]> NON_VIEWABLE_SECTION_MAP = new HashMap<String, String[]>();
    static {
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.GIFT, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.SOLD, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.AUCTION, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.THEFT, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT });
    }

    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);

        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        if (!SpringContext.getBean(AssetRetirementService.class).isAssetRetiredByMerged(assetRetirementGlobal)) {
            fields.add(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER);
            fields.add(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC);
        }

        conditionallyHideAssetCollectionEditing(document);

        // Hide all the fields in the add section within the new asset retired details collection, except capitalAssetNumber.
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ORGANIZATION_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.INVENTORY_STATUS_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.CONDITION_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.CAPITAL_ASSET_DESCRIPTION);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.VENDOR_NAME);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.SERIAL_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.GOVERNMENT_TAG_NUMBER);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.LAST_INVENTORY_DATE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.CREATE_DATE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.TOTAL_COST_AMOUNT);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ACCUMULATED_DEPRECIATION);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.BOOK_VALUE);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.FEDERAL_CONTRIBUTION);
        fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS + "." + CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET + "." + CamsPropertyConstants.Asset.ORGANIZATION_TEXT);

        return fields;
    }

    /**
     * Hide add asset collection when document starts routing.
     * 
     * @param document
     */
    protected void conditionallyHideAssetCollectionEditing(MaintenanceDocument document) {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();
        MaintainableCollectionDefinition maintCollDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection(CamsConstants.DocumentTypeName.ASSET_RETIREMENT_GLOBAL, "assetRetirementGlobalDetails");
        if (SpringContext.getBean(AssetService.class).isDocumentEnrouting(document)) {
            // Once document starts routing, disallow add/delete asset button and multiple lookup.
            maintCollDef.setIncludeAddLine(false);
            maintCollDef.setIncludeMultipleLookupLine(false);
            for (AssetRetirementGlobalDetail assetDetail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
                assetDetail.setNewCollectionRecord(false);
            }
        }
        else {
            maintCollDef.setIncludeAddLine(true);
            maintCollDef.setIncludeMultipleLookupLine(true);
        }
    }

    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenSectionIds(businessObject);

        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        // check account period selection is enabled
        // PERFORMANCE: cache this setting - move call to service
        // check accounting period is enabled for doc type in system parameter
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        // PERFORMANCE: cache this setting - move call to service
        ParameterEvaluator evaluator = getParameterEvaluatorService().getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (!evaluator.evaluationSucceeds()) {
            fields.add(KFSConstants.ACCOUNTING_PERIOD_TAB_ID);
        }

        // If retirement reason code is not defined in NON_VIEWABLE_SECTION_MAP, hide all retirement detail sections.
        String[] nonViewableSections = NON_VIEWABLE_SECTION_MAP.get(assetRetirementGlobal.getRetirementReasonCode());

        if (nonViewableSections == null) {
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD);
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT);
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT);
        }
        else {
            fields.addAll(Arrays.asList(nonViewableSections));
        }

        if (!SpringContext.getBean(AssetRetirementService.class).isAssetRetiredByMerged(assetRetirementGlobal)) {
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_TARGET_ASSET_RETIREMENT_INFO);
        }

        return fields;
    }

    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = (WorkflowDocument) document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
            Set<String> nodeNames = SpringContext.getBean(AssetService.class).getCurrentRouteLevels(workflowDocument);

            if (nodeNames.contains(CamsConstants.RouteLevelNames.EXTERNAL_TRANSFER) || nodeNames.contains(CamsConstants.RouteLevelNames.PURCHASING)) {
                return false;
            }
        }
        return super.canEdit(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);
        
        if (document instanceof LedgerPostingDocument) {
            // check accounting period is enabled for doc type in system parameter
            String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
            ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
            ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
            if (evaluator.evaluationSucceeds()) {
                documentActions.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
        }
        
        return documentActions;
    }

}
