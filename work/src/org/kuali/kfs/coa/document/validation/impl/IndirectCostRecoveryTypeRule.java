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
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.service.ChartService;

public class IndirectCostRecoveryTypeRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Account.class);

    private IndirectCostRecoveryType indirectCostRecoveryType;
    private List indirectCostRecoveryExclusionTypeDetails;
    private ChartService chartService;

    
    public IndirectCostRecoveryTypeRule () {
        chartService = SpringContext.getBean(ChartService.class);
    }
    
    public void setupConvenienceObjects() {
        indirectCostRecoveryType = (IndirectCostRecoveryType) super.getNewBo();
        indirectCostRecoveryExclusionTypeDetails = indirectCostRecoveryType.getIndirectCostRecoveryExclusionTypeDetails();
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
        for(int i=0;i<indirectCostRecoveryExclusionTypeDetails.size();i++) {
            String collectionElementPath = "indirectCostRecoveryExclusionTypeDetails[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(collectionElementPath);
            isValid &= itemIsValid((IndirectCostRecoveryExclusionType) indirectCostRecoveryExclusionTypeDetails.get(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(collectionElementPath);
        }
        return isValid;
    }
    
    public boolean itemIsValid(IndirectCostRecoveryExclusionType item) {
        boolean isValid = true;
        if(ObjectUtils.isNotNull(chartService.getUniversityChart().getChartOfAccountsCode())) {
            item.refreshReferenceObject(KFSPropertyConstants.CHART);
            if(item.getChartOfAccountsCode().equals(chartService.getUniversityChart().getChartOfAccountsCode())) {
                item.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
                if(ObjectUtils.isNull(item.getObjectCodeCurrent())) {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, ddService.getAttributeLabel(IndirectCostRecoveryExclusionType.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE));
                    isValid = false;
                }
            } else {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_CHART_NOT_UNIVERSITY_CHART_MULTIVALUE_LOOKUP, item.getChartOfAccountsCode(), ddService.getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                isValid = false;                    
            }
            
        } else {
            LOG.info("The Chart Service found no Charts of University type.");
        }
        return isValid;
    }
    
}
