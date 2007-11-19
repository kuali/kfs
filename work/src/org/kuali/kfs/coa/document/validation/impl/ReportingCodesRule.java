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
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ReportingCodes;

/**
 * 
 * This class implements the business rules specific to the {@link ReportingCodes} Maintenance Document.
 */
public class ReportingCodesRule extends MaintenanceDocumentRuleBase {

    private ReportingCodes oldReportingCode;
    private ReportingCodes newReportingCode;

    private BusinessObjectService businessObjectService;

    /**
     * 
     * Constructs a ReportingCodesRule and pseudo-injects services
     */
    public ReportingCodesRule() {
        super();
        setBusinessObjectService((BusinessObjectService) SpringContext.getBean(BusinessObjectService.class));
    }

    /**
     * This performs rules checks on document route
     * <ul>
     * <li>{@link ProjectCodeRule#checkReportsToReportingCode()}</li>
     * </ul>
     * This rule fails on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        success &= checkReportsToReportingCode();
        return success;
    }

    /**
     * This performs rules checks on document save
     * <ul>
     * <li>{@link ProjectCodeRule#checkReportsToReportingCode()}</li>
     * </ul>
     * This rule does not fail on business rule failures
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        checkReportsToReportingCode();
        return success;
    }

    /**
     * 
     * This method sets the convenience objects like newReportingCode and oldReportingCode, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldReportingCode = (ReportingCodes) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newReportingCode = (ReportingCodes) super.getNewBo();
    }

    /**
     * 
     * This checks to see if the user has entered in two different values for the reporting code and the 
     * reports to reporting code. If they are different then it makes sure that the reports to reporting code actually exists
     * in the system.
     * @return true if the reports to reporting code is filled and exists or true if it isn't filled in (doesn't need to be), false otherwise
     */
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
