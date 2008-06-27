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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.service.ChartService;

public class IndirectCostRecoveryTypeRule extends MaintenanceDocumentRuleBase {

    private IndirectCostRecoveryType indirectCostRecoveryType;
    private List IndirectCostRecoveryExclusionTypeDetails;
    private DataDictionaryService dataDictionaryService;
    private ChartService chartService;
    
    public IndirectCostRecoveryTypeRule () {
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        chartService = SpringContext.getBean(ChartService.class);
    }
    
    public void setupConvenienceObjects() {
        indirectCostRecoveryType = (IndirectCostRecoveryType) super.getNewBo();
        IndirectCostRecoveryExclusionTypeDetails = indirectCostRecoveryType.getIndirectCostRecoveryExclusionTypeDetails();
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
    
    public boolean checkCollectionItems() { //TODO: check the included error path
        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath("add.IndirectCostRecoveryExclusionTypeDetails");
        for(int i=0;i<IndirectCostRecoveryExclusionTypeDetails.size();i++) {
            isValid &= itemIsValid((IndirectCostRecoveryExclusionType) IndirectCostRecoveryExclusionTypeDetails.get(i));
        }
        GlobalVariables.getErrorMap().removeFromErrorPath("add.IndirectCostRecoveryExclusionTypeDetails");
        return isValid;
    }
    
    public boolean itemIsValid(IndirectCostRecoveryExclusionType item) {
        boolean isValid = true;
        if(StringUtils.isNotBlank(item.getChartOfAccountsCode()) && StringUtils.isNotBlank(item.getFinancialObjectCode())) {
            if(ObjectUtils.isNotNull(chartService.getUniversityChart().getChartOfAccountsCode())) {
                
                item.refreshReferenceObject(KFSPropertyConstants.CHART);
                if(item.getChartOfAccountsCode().equals(chartService.getUniversityChart().getChartOfAccountsCode())) {
                    item.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
                    if(ObjectUtils.isNull(item.getObjectCodeCurrent())) {
                        GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, dataDictionaryService.getAttributeLabel(IndirectCostRecoveryExclusionType.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
                        isValid = false;
                    }
                } else {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_CHART_NOT_UNIVERSITY_CHART_MULTIVALUE_LOOKUP, item.getChartOfAccountsCode(), dataDictionaryService.getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                    isValid = false;                    
                }
                
            } else {
                // it seems like this should be more of a system error, or something related to functional superusers?
            }
        } else {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(Chart.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
            isValid = false;
        }
        return isValid;
    }
    
}
