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
package org.kuali.module.chart.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionType;
import org.kuali.module.chart.bo.codes.ICRTypeCode;

public class ICRTypeCodeRule extends MaintenanceDocumentRuleBase {

    private ICRTypeCode iCRTypeCode;
    private List indirectCostRecoveryExclusionTypeCollection;
    private DataDictionaryService dataDictionaryService;
    
    public ICRTypeCodeRule () {
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    }
    
    public void setupConvenienceObjects() {
        iCRTypeCode = (ICRTypeCode) super.getNewBo();
        indirectCostRecoveryExclusionTypeCollection = iCRTypeCode.getIndirectCostRecoveryExclusionTypeCollection();
    }
    
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= itemIsValid((IndirectCostRecoveryExclusionType) line);
        return isValid;
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
        isValid &= checkCollectionItems();
        GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        return isValid;
    }
    
    public boolean checkCollectionItems() {
        boolean isValid = true;
        for(int i=0;i<indirectCostRecoveryExclusionTypeCollection.size();i++) {
            GlobalVariables.getErrorMap().addToErrorPath("indirectCostRecoveryExclusionTypeCollection[" + i + "]");
            isValid &= itemIsValid((IndirectCostRecoveryExclusionType) indirectCostRecoveryExclusionTypeCollection.get(i));
            GlobalVariables.getErrorMap().removeFromErrorPath("indirectCostRecoveryExclusionTypeCollection[" + i + "]");
        }
        return isValid;
    }
    
    public boolean itemIsValid(IndirectCostRecoveryExclusionType item) {
        boolean isValid = true;
        if(StringUtils.isNotBlank(item.getChartOfAccountsCode()) && StringUtils.isNotBlank(item.getFinancialObjectCode())) {
            item.refreshReferenceObject(KFSPropertyConstants.CHART);
            if(ObjectUtils.isNull(item.getChart())) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, RiceKeyConstants.ERROR_EXISTENCE, dataDictionaryService.getAttributeLabel(IndirectCostRecoveryExclusionType.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                isValid = false;
            } else { 
                item.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
                if(ObjectUtils.isNull(item.getObjectCodeCurrent())) {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, dataDictionaryService.getAttributeLabel(IndirectCostRecoveryExclusionType.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
                    isValid = false;
                }
            }
            
        }
        
        return isValid;
    }
    
}
