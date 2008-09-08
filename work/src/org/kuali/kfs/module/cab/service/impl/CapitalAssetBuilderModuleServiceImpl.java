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
package org.kuali.kfs.module.cab.service.impl;

import java.util.List;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

public class CapitalAssetBuilderModuleServiceImpl implements CapitalAssetBuilderModuleService {

    private ParameterService parameterService;
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public boolean validateIndividualCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems) {
        return true;
    }
    
    public boolean validateOneSystemCapitalAssetSystemFromPurchasing(String systemState, CapitalAssetSystem capitalAssetSystem) {
        return true;
    }

    public boolean validateMultipleSystemsCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems) {
        return true;
    }
    
    /**
     * @see org.kuali.kfs.integration.service.CapitalAssetBuilderModuleService#doesDocumentExceedThreshold(org.kuali.rice.kns.util.KualiDecimal)
     */
    public boolean doesDocumentExceedThreshold(KualiDecimal docTotal) {
        String parameterThreshold = parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT);
        if (docTotal.compareTo(new KualiDecimal(parameterThreshold)) > 0) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.integration.service.CapitalAssetBuilderModuleService#validateAccounts(java.util.List, java.lang.String)
     */
    public boolean validateAccounts(List<SourceAccountingLine> accountingLines, String transactionType) {
        // TODO Auto-generated method stub
        return false;
    }

}
