/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.bo.OrganizationParameter;


public class OrganizationParameterRule extends MaintenanceDocumentRuleBase {
    
    private OrganizationParameter newOrganizationParameter;
    private BusinessObjectService boService;

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // setup newDelegateChange convenience objects, make sure all possible sub-objects are populated
        newOrganizationParameter = (OrganizationParameter) super.getNewBo();
        boService = (BusinessObjectService) super.getBoService();
        super.setupConvenienceObjects();
    }
    
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();        
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }
        
    protected boolean checkChartOfAccountsCode() {
        LOG.info("checkChartOfAccountsCode called");
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean success = true;        
        Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId(newOrganizationParameter.getChartOfAccountsCode());
        if (ObjectUtils.isNull(chart)) {
            success &= false;
            errorMap.putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, PurapKeyConstants.ERROR_INVALID_CHART_OF_ACCOUNTS_CODE);
        }
        return success;
    }
    
    protected boolean checkOrganizationCode() {
        LOG.info("checkOrganizationCode called");
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean success = true;        
        Org org = SpringContext.getBean(OrganizationService.class).getByPrimaryId(newOrganizationParameter.getChartOfAccountsCode(), newOrganizationParameter.getOrganizationCode());
        if (ObjectUtils.isNull(org)) {
            success &= false;
            errorMap.putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + KFSPropertyConstants.ORGANIZATION_CODE, PurapKeyConstants.ERROR_INVALID_ORGANIZATION_CODE);
        }
        return success;
    }

}

