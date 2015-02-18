/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ReportingCode;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This class implements the business rules specific to the {@link ReportingCodes} Maintenance Document.
 */
public class ReportingCodesRule extends MaintenanceDocumentRuleBase {

    protected ReportingCode oldReportingCode;
    protected ReportingCode newReportingCode;

    protected BusinessObjectService businessObjectService;

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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
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
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldReportingCode = (ReportingCode) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newReportingCode = (ReportingCode) super.getNewBo();
    }

    /**
     * 
     * This checks to see if the user has entered in two different values for the reporting code and the 
     * reports to reporting code. If they are different then it makes sure that the reports to reporting code actually exists
     * in the system.
     * @return true if the reports to reporting code is filled and exists or true if it isn't filled in (doesn't need to be), false otherwise
     */
    protected boolean checkReportsToReportingCode() {
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
            referenceBo = (PersistableBusinessObject)businessObjectService.getReferenceIfExists((PersistableBusinessObject) newReportingCode, "reportingCodes");
            if (!ObjectUtils.isNotNull(referenceBo)) {
                putFieldError("financialReportsToReportingCode", KFSKeyConstants.ERROR_EXISTENCE, "Reports To Reporting Code");
                success &= false;
            }
        }
        return success;
    }

    protected void setBusinessObjectService(BusinessObjectService boService) {
        businessObjectService = boService;
    }

}
