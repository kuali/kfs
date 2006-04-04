/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ReportingCodes;

public class ReportingCodesRule extends MaintenanceDocumentRuleBase {

    private ReportingCodes oldReportingCode;
    private ReportingCodes newReportingCode;

    private BusinessObjectService businessObjectService;
    
    public ReportingCodesRule() {
        super();
        setBusinessObjectService((BusinessObjectService)SpringServiceLocator.getBusinessObjectService());
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
        
        //  setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldReportingCode = (ReportingCodes) super.getOldBo();

        //  setup newAccount convenience objects, make sure all possible sub-objects are populated
        newReportingCode = (ReportingCodes) super.getNewBo();
    }
    
    private boolean checkReportsToReportingCode() {
        boolean success = true;
        // if these two aren't equal then we need to make sure that the object exists
        if (!newReportingCode.getFinancialReportingCode().equalsIgnoreCase(newReportingCode.getFinancialReportsToReportingCode())) {
            // attempt to retrieve the specified object from the db
            BusinessObject referenceBo = businessObjectService.getReferenceIfExists((BusinessObject)newReportingCode, "reportingCodes");
            if (!ObjectUtils.isNotNull(referenceBo)) {
                putFieldError("financialReportsToReportingCode", 
                        KeyConstants.ERROR_EXISTENCE, 
                        "Reports To Reporting Code");
                success &= false;
                
            }
        }
        return success;
    }
    
    private void setBusinessObjectService(BusinessObjectService boService) {
        businessObjectService = boService;
    }

}
