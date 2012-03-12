/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.FundingSource;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;

/* 
 * 
*/
public class FundingSourceRule extends MaintenanceDocumentRuleBase {

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.checkForSystemParametersExistence();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    protected boolean checkForSystemParametersExistence() {
        LOG.info("checkForSystemParametersExistence called");
        boolean success = true;
        Collection<String> parameterValues = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(RequisitionDocument.class, PurapParameterConstants.DEFAULT_FUNDING_SOURCE) );
        FundingSource newFundingSource = (FundingSource)getNewBo();
        FundingSource oldFundingSource= (FundingSource)getOldBo();
        //If the new funding source code matches with the funding source in the DEFAULT_FUNDING_SOURCE 
        //system parameters then we can't inactivate this funding source, so return false.
        if (parameterValues.contains(newFundingSource.getFundingSourceCode()) && ! newFundingSource.isActive() && oldFundingSource.isActive()) {
            success = false;
            String documentLabel = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(newFundingSource.getClass());
            putGlobalError(KFSKeyConstants.ERROR_CANNOT_INACTIVATE_USED_IN_SYSTEM_PARAMETERS, documentLabel);
        }
        return success;
    }
}
