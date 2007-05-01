/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ReportingCodes;

public class ReportingCodesRule extends MaintenanceDocumentRuleBase {

    private ReportingCodes oldReportingCode;
    private ReportingCodes newReportingCode;

    private BusinessObjectService businessObjectService;

    public ReportingCodesRule() {
        super();
        setBusinessObjectService((BusinessObjectService) SpringServiceLocator.getBusinessObjectService());
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        success &= checkReportsToReportingCode();
        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        checkReportsToReportingCode();
        return success;
    }

    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldReportingCode = (ReportingCodes) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newReportingCode = (ReportingCodes) super.getNewBo();
    }

    private boolean checkReportsToReportingCode() {
        boolean success = true;
        boolean oneMissing = false;
        boolean bothMissing = false;
        boolean doExistenceTest = false;

        // if one of the codes is blank but the other isnt (ie, they are different), then
        // do the existence test
        if (StringUtils.isBlank(newReportingCode.getFinancialReportingCode()) && StringUtils.isBlank(newReportingCode.getFinancialReportsToReportingCode())) {
            bothMissing = true;
        }
        else if (StringUtils.isBlank(newReportingCode.getFinancialReportingCode()) || StringUtils.isBlank(newReportingCode.getFinancialReportsToReportingCode())) {
            oneMissing = true;
        }
        if (oneMissing && !bothMissing) {
            doExistenceTest = true;
        }

        // if both codes are there, but they are different, then do the existence test
        if (StringUtils.isNotBlank(newReportingCode.getFinancialReportingCode())) {
            if (!newReportingCode.getFinancialReportingCode().equalsIgnoreCase(newReportingCode.getFinancialReportsToReportingCode())) {
                doExistenceTest = true;
            }
        }

        // if these two aren't equal then we need to make sure that the object exists
        if (doExistenceTest) {

            // attempt to retrieve the specified object from the db
            PersistableBusinessObject referenceBo;
            referenceBo = businessObjectService.getReferenceIfExists((PersistableBusinessObject) newReportingCode, "reportingCodes");
            if (!ObjectUtils.isNotNull(referenceBo)) {
                putFieldError("financialReportsToReportingCode", KFSKeyConstants.ERROR_EXISTENCE, "Reports To Reporting Code");
                success &= false;
            }
        }
        return success;
    }

    private void setBusinessObjectService(BusinessObjectService boService) {
        businessObjectService = boService;
    }

}
