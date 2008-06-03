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
package org.kuali.module.effort.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.bo.EffortCertificationReportEarnPaygroup;
import org.kuali.module.effort.bo.EffortCertificationReportPosition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.service.EffortCertificationReportDefinitionService;
import org.kuali.module.integration.bo.EffortCertificationReport;

/**
 * Provide the implementation of the service methods related to EffortCertificationReportDefinition
 * 
 * @see org.kuali.module.effort.bo.EffortCertificationReportDefinition
 */
public class EffortCertificationReportDefinitionServiceImpl implements EffortCertificationReportDefinitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportDefinitionServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#findReportDefinitionByPrimaryKey(java.util.Map)
     */
    public EffortCertificationReportDefinition findReportDefinitionByPrimaryKey(Map<String, String> fieldValues) {        
        return (EffortCertificationReportDefinition) businessObjectService.findByPrimaryKey(EffortCertificationReportDefinition.class, fieldValues);
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#validateEffortCertificationReportDefinition(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public String validateEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        Integer fiscalYear = effortCertificationReportDefinition.getUniversityFiscalYear();
        String reportNumber = effortCertificationReportDefinition.getEffortCertificationReportNumber();

        // Fiscal Year is required
        if (ObjectUtils.isNull(fiscalYear)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null).getMessage();
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null).getMessage();
        }
        
        String combinedFieldValues = new StringBuilder(fiscalYear.toString()).append(EffortConstants.VALUE_SEPARATOR).append(reportNumber).toString();

        // check if there exists the given report definition
        effortCertificationReportDefinition = (EffortCertificationReportDefinition)businessObjectService.retrieve(effortCertificationReportDefinition);
        if (effortCertificationReportDefinition == null) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_NOT_EXIST, combinedFieldValues).getMessage();
        }

        // check if the given report definition is still active
        if (!effortCertificationReportDefinition.isActive()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, combinedFieldValues).getMessage();
        }

        return null;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#findPositionObjectGroupCodes(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public List<String> findPositionObjectGroupCodes(EffortCertificationReportDefinition reportDefinition) {
        Map<String, String> fieldValues = reportDefinition.buildKeyMapForCurrentReportDefinition();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE.toString());

        Collection<EffortCertificationReportPosition> reportPosition = businessObjectService.findMatching(EffortCertificationReportPosition.class, fieldValues);

        List<String> positionGroupCodes = new ArrayList<String>();
        for (EffortCertificationReportPosition position : reportPosition) {
            positionGroupCodes.add(position.getEffortCertificationReportPositionObjectGroupCode());
        }

        return positionGroupCodes;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#findReportEarnCodePayGroups(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public Map<String, Set<String>> findReportEarnCodePayGroups(EffortCertificationReportDefinition reportDefinition) {
        Collection<EffortCertificationReportEarnPaygroup> reportEarnPay = this.findReportEarnPay(reportDefinition);
        Map<String, Set<String>> earnCodePayGroups = new HashMap<String, Set<String>>();

        for (EffortCertificationReportEarnPaygroup earnPay : reportEarnPay) {
            String payGroup = earnPay.getPayGroup();
            String earnCode = earnPay.getEarnCode();

            if (earnCodePayGroups.containsKey(payGroup)) {
                Set<String> earnCodeSet = earnCodePayGroups.get(payGroup);
                earnCodeSet.add(earnCode);
            }
            else {
                Set<String> earnCodeSet = new HashSet<String>();
                earnCodeSet.add(earnCode);
                earnCodePayGroups.put(payGroup, earnCodeSet);
            }
        }

        return earnCodePayGroups;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#findReportEarnPay(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public Collection<EffortCertificationReportEarnPaygroup> findReportEarnPay(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_TYPE_CODE, reportDefinition.getEffortCertificationReportTypeCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE.toString());

        return businessObjectService.findMatching(EffortCertificationReportEarnPaygroup.class, fieldValues);
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#existsPendingEffortCertification(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean hasPendingEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        
        int numOfPendingDocuments = businessObjectService.countMatching(EffortCertificationDocumentBuild.class, fieldValues);
        if(numOfPendingDocuments > 0) {
            return true;
        }
        
        List<String> pendingStatusCodes = Arrays.asList(KFSConstants.DocumentStatusCodes.ENROUTE);
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, pendingStatusCodes);    
        
        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#hasApprovedEffortCertification(java.lang.String, org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean hasApprovedEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();        
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);    

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#hasBeenUsedForEffortCertificationGeneration(org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }
    
    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportDefinitionService#hasBeenUsedForEffortCertificationGeneration(java.lang.String, org.kuali.module.effort.bo.EffortCertificationReportDefinition)
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(String emplid, EffortCertificationReport reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}