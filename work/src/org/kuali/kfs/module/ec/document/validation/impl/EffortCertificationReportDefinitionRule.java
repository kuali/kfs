/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ec.document.validation.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportPosition;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Contains Business Rules for the Effort Certification Report Maintenance Document.
 */
public class EffortCertificationReportDefinitionRule extends MaintenanceDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportDefinitionRule.class);

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules() start");
        
        if (GlobalVariables.getErrorMap().getErrorCount() > 0) {
            return false;
        }
        
        boolean isValid = true;
        EffortCertificationReportDefinition reportDefintion = (EffortCertificationReportDefinition) document.getNewMaintainableObject().getBusinessObject();
        
        // report begin fiscal year must be less than report end fiscal year
        Integer beginPeriodCode = Integer.parseInt(reportDefintion.getEffortCertificationReportBeginPeriodCode());
        Integer endPeriodCode = Integer.parseInt(reportDefintion.getEffortCertificationReportEndPeriodCode());
        if (reportDefintion.getEffortCertificationReportBeginFiscalYear() > reportDefintion.getEffortCertificationReportEndFiscalYear() || (reportDefintion.getEffortCertificationReportBeginFiscalYear().equals(reportDefintion.getEffortCertificationReportEndFiscalYear()) && Integer.parseInt(reportDefintion.getEffortCertificationReportBeginPeriodCode()) >= Integer.parseInt(reportDefintion.getEffortCertificationReportEndPeriodCode()))) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR, EffortKeyConstants.ERROR_END_FISCAL_YEAR);
            isValid = false;
        }

        // at least one position object group code defined for report
        Collection<EffortCertificationReportPosition> reportPositions = reportDefintion.getEffortCertificationReportPositions();
        if (reportPositions == null || reportPositions.isEmpty()) {
            putFieldError(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITIONS, EffortKeyConstants.ERROR_NOT_HAVE_POSITION_GROUP);
            isValid = false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);

        if(success && line instanceof EffortCertificationReportPosition) {
            EffortCertificationReportPosition reportPosition = (EffortCertificationReportPosition)line;           
            String errorKey = KNSConstants.MAINTENANCE_ADD_PREFIX + collectionName + "." + EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITION_OBJECT_GROUP_CODE;
            
            success &= this.reportPositionObjectGroupExists(errorKey, reportPosition);
        }
        
        return success; 
    }

    // determine whether the given report position object group exists
    private boolean reportPositionObjectGroupExists(String errorKey, EffortCertificationReportPosition reportPosition) {
        String positionObjectGroupCode = reportPosition.getEffortCertificationReportPositionObjectGroupCode();
        
        if(StringUtils.isNotBlank(positionObjectGroupCode) && ObjectUtils.isNull(reportPosition.getPositionObjectGroup())) {
            String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(EffortCertificationReportPosition.class, EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_POSITION_OBJECT_GROUP_CODE);
            putFieldError(errorKey, KFSKeyConstants.ERROR_EXISTENCE, label + "(" + positionObjectGroupCode + ")");
            
            return false;
        }
        
        return true;
    }
}
