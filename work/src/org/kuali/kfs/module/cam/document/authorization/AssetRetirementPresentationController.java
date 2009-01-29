/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Presentation Controller for Asset Retirement Global Maintenance Documents
 */
public class AssetRetirementPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    private static final Map<String, String[]> NON_VIEWABLE_SECTION_MAP = new HashMap<String, String[]>();
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
        
        return fields;
    }

    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenSectionIds(businessObject);
        
        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();
        
        // If retirement reason code is not defined in NON_VIEWABLE_SECTION_MAP, hide all retirement detail sections.
        String[] nonViewableSections = NON_VIEWABLE_SECTION_MAP.get(assetRetirementGlobal.getRetirementReasonCode());

        if (nonViewableSections == null) {
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD);
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT);
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT);
        } else {
            fields.addAll(Arrays.asList(nonViewableSections));
        }
        
        if (!SpringContext.getBean(AssetRetirementService.class).isAssetRetiredByMerged(assetRetirementGlobal)) {
            fields.add(CamsConstants.AssetRetirementGlobal.SECTION_TARGET_ASSET_RETIREMENT_INFO);
        }
        
        return fields;
    }
    
    @Override
    protected boolean canEdit(Document document) {
        KualiWorkflowDocument workflowDocument = (KualiWorkflowDocument)document.getDocumentHeader().getWorkflowDocument();
        
        if (workflowDocument.stateIsEnroute()) {
            List<String> nodeNames = SpringContext.getBean(AssetService.class).getCurrentRouteLevels(workflowDocument) ;
            
            if (nodeNames.contains(CamsConstants.RouteLevelNames.EXTERNAL_TRANSFER) || nodeNames.contains(CamsConstants.RouteLevelNames.PURCHASING)) {
                return false;
            }
        }
        return super.canEdit(document);
    }
    
}
