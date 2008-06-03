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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.spring.Logged;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.service.EffortCertificationCreateService;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.effort.util.EffortCertificationParameterFinder;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This Process creates effort certification documents from the temporary build table created by the batch process and routes effort
 * certification documents to project directors, fiscal officers, and central workgroups. The process includes the following steps:
 * 
 * <li>construct an effort certification document from a temporary effort certification document; </li>
 * <li>route each effort certification document; </li>
 * <li>delete the temporary effort certification document after routing successfully. </li>
 */
@Transactional
public class EffortCertificationCreateServiceImpl implements EffortCertificationCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationCreateServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private EffortCertificationDocumentService effortCertificationDocumentService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationCreateService#create()
     */
    @Logged
    public void create() {
        Integer fiscalYear = EffortCertificationParameterFinder.getCreateReportFiscalYear();
        String reportNumber = EffortCertificationParameterFinder.getCreateReportNumber();

        this.create(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationCreateService#create(java.lang.Integer, java.lang.String)
     */
    @Logged
    public void create(Integer fiscalYear, String reportNumber) {
        Map<String, String> fieldValues = EffortCertificationReportDefinition.buildKeyMap(fiscalYear, reportNumber);

        String errorMessage = this.validateReportDefintion(fieldValues);
        if (StringUtils.isNotEmpty(errorMessage)) {
            LOG.fatal(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Collection<EffortCertificationDocumentBuild> documentsBuild = businessObjectService.findMatching(EffortCertificationDocumentBuild.class, fieldValues);
        for (EffortCertificationDocumentBuild documentBuild : documentsBuild) {          
            boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);
            
            if(isCreated) {
                businessObjectService.delete(documentBuild);
            }
        }
    }

    /**
     * check if a report has been defined and its docuemnts have not been generated. The combination of fiscal year and report
     * number can determine a report definition.
     * 
     * @param fieldValues the map containing fiscalYear and report number
     * @return a message if a report has not been defined or its documents have been gerenated; otherwise, return null
     */
    private String validateReportDefintion(Map<String, String> fieldValues) {
        String fiscalYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);
        String combinedFieldValues = new StringBuilder(fiscalYear).append(EffortConstants.VALUE_SEPARATOR).append(reportNumber).toString();

        // Fiscal Year is required
        if (StringUtils.isEmpty(fiscalYear)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null).getMessage();
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null).getMessage();
        }

        // check if a report has been defined
        EffortCertificationReportDefinition reportDefinition = (EffortCertificationReportDefinition) businessObjectService.findByPrimaryKey(EffortCertificationReportDefinition.class, fieldValues);
        if (reportDefinition == null) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID, combinedFieldValues).getMessage();
        }

        // check if the selected report definition is still active
        if (!reportDefinition.isActive()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, combinedFieldValues).getMessage();
        }
           
        // check if the report period of the selected report definition is open. If not, throws an error message
        if (!KFSConstants.PeriodStatusCodes.OPEN.equals(reportDefinition.getEffortCertificationReportPeriodStatusCode())) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_PERIOD_NOT_OPENED, combinedFieldValues).getMessage();
        }

        // check if any document has been generated for the selected report definition. If so, return with an error message
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            return MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_EXIST, reportNumber, fiscalYear).getMessage();
        }

        // check if any document build has been generated for the selected report definition. If not, return with an error message
        int countOfDocumentsBuild = businessObjectService.countMatching(EffortCertificationDocumentBuild.class, fieldValues);
        if (countOfDocumentsBuild <= 0) {
            return MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_BUILD_NOT_EXIST, reportNumber, fiscalYear).getMessage();
        }

        return null;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the effortCertificationDocumentService attribute value.
     * @param effortCertificationDocumentService The effortCertificationDocumentService to set.
     */
    public void setEffortCertificationDocumentService(EffortCertificationDocumentService effortCertificationDocumentService) {
        this.effortCertificationDocumentService = effortCertificationDocumentService;
    }
}
