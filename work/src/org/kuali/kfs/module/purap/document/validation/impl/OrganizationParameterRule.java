/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.OrganizationParameter;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business rule(s) applicable to Organization Parameter maintenance document.
 */
public class OrganizationParameterRule extends MaintenanceDocumentRuleBase {

    protected OrganizationParameter newOrganizationParameter;
    protected BusinessObjectService boService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // setup newDelegateChange convenience objects, make sure all possible sub-objects are populated
        newOrganizationParameter = (OrganizationParameter) super.getNewBo();
        boService = (BusinessObjectService) super.getBoService();
        super.setupConvenienceObjects();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = true;
        success &= this.checkChartOfAccountsCode();
        success &= this.checkOrganizationCode();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * Validate chart of accounts code
     * 
     * @return Boolean indicating if validation succeeded
     */
    protected boolean checkChartOfAccountsCode() {
        LOG.info("checkChartOfAccountsCode called");
        MessageMap errorMap = GlobalVariables.getMessageMap();
        boolean success = true;
        Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId(newOrganizationParameter.getChartOfAccountsCode());
        if (ObjectUtils.isNull(chart)) {
            success &= false;
            errorMap.putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, PurapKeyConstants.ERROR_INVALID_CHART_OF_ACCOUNTS_CODE);
        }
        return success;
    }

    /**
     * Validate organization code
     * 
     * @return Boolean indicating if validation succeeded
     */
    protected boolean checkOrganizationCode() {
        LOG.info("checkOrganizationCode called");
        MessageMap errorMap = GlobalVariables.getMessageMap();
        boolean success = true;
        Organization org = SpringContext.getBean(OrganizationService.class).getByPrimaryId(newOrganizationParameter.getChartOfAccountsCode(), newOrganizationParameter.getOrganizationCode());
        if (ObjectUtils.isNull(org)) {
            success &= false;
            errorMap.putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + KFSPropertyConstants.ORGANIZATION_CODE, PurapKeyConstants.ERROR_INVALID_ORGANIZATION_CODE);
        }
        return success;
    }

}
