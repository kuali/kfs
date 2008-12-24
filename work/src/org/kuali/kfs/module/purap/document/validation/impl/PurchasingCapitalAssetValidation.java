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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class PurchasingCapitalAssetValidation extends GenericValidation {

    CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;    
    PurchasingService purchasingService;
    
    public boolean validate(AttributedDocumentEvent event) {
        GlobalVariables.getErrorMap().clearErrorPath();
        boolean valid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument)event.getDocument();
        
        //We only need to do capital asset validations if the capital asset system type
        //code is not blank.
        if (StringUtils.isNotBlank(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            String systemState = purchasingDocument.getCapitalAssetSystemStateCode();
            String chartCode = purchasingDocument.getChartOfAccountsCode();
            String documentType = (purchasingDocument instanceof RequisitionDocument) ? "REQUISITION" : "PURCHASE_ORDER";
            if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                valid &= capitalAssetBuilderModuleService.validateIndividualCapitalAssetSystemFromPurchasing(systemState, purchasingDocument.getPurchasingCapitalAssetItems(), chartCode, documentType);
            }
            else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
                valid &= capitalAssetBuilderModuleService.validateOneSystemCapitalAssetSystemFromPurchasing(systemState, purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), chartCode, documentType);
            }
            else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.MULTIPLE)) {
                valid &= capitalAssetBuilderModuleService.validateMultipleSystemsCapitalAssetSystemFromPurchasing(systemState, purchasingDocument.getPurchasingCapitalAssetSystems(), purchasingDocument.getPurchasingCapitalAssetItems(), chartCode, documentType);
            }
            // validate complete location addresses
            if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (CapitalAssetSystem system : purchasingDocument.getPurchasingCapitalAssetSystems()) {
                    for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= purchasingService.checkCapitalAssetLocation(location);
                    }
                }
            } else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= purchasingService.checkCapitalAssetLocation(location);
                    }
                }
            
        }
        return valid;
    }

}
