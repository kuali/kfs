/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class IndirectCostRecoveryTypeRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Account.class);

    protected IndirectCostRecoveryType indirectCostRecoveryType;
    protected List indirectCostRecoveryExclusionTypeDetails;
    protected ChartService chartService;

    
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
        GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
        isValid &= checkCollectionItems();
        GlobalVariables.getMessageMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        return isValid;
    }
    
    public boolean checkCollectionItems() {
        boolean isValid = true;
        for(int i=0;i<indirectCostRecoveryExclusionTypeDetails.size();i++) {
            String collectionElementPath = "indirectCostRecoveryExclusionTypeDetails[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(collectionElementPath);
            isValid &= itemIsValid((IndirectCostRecoveryExclusionType) indirectCostRecoveryExclusionTypeDetails.get(i));
            GlobalVariables.getMessageMap().removeFromErrorPath(collectionElementPath);
        }
        return isValid;
    }
    
    public boolean itemIsValid(IndirectCostRecoveryExclusionType item) {
        boolean isValid = true;
        if(ObjectUtils.isNotNull(chartService.getUniversityChart().getChartOfAccountsCode())) {
            if(StringUtils.isBlank(item.getChartOfAccountsCode())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_CHART_CODE, item.getChartOfAccountsCode());
                isValid = false;
            } else if (item.isActive()) {
                item.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
                if(ObjectUtils.isNull(item.getObjectCodeCurrent())) {
                    if(item.isNewCollectionRecord()) {
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE_DELETE, item.getChartOfAccountsCode(), item.getFinancialObjectCode()); 
                    } else {
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE, item.getChartOfAccountsCode(), item.getFinancialObjectCode()); 
                    }
                    isValid = false;
                }                
            }
            
        } else {
            LOG.info("The Chart Service found no Charts of University type.");
        }
        return isValid;
    }
    
}
