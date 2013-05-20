/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.coa.document.ProjectCodeImportDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * GenericValidation to check for duplicate Project Codes entries in the file and system
 */
public class ProjectCodeImportEntryDuplicateValidation extends GenericValidation {
    private static final Logger LOG = Logger.getLogger(ProjectCodeImportEntryDuplicateValidation.class);
    private BusinessObjectService businessObjectService;
    private MassImportLineBase importedLineForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        ProjectCodeImportDocument document = (ProjectCodeImportDocument) event.getDocument();
        HashMap<String, Object> projectCodeMap = new HashMap<String, Object>();
        String mapKey = null;
        Map<String, String> primaryKeys = new HashMap<String, String>();

        ProjectCodeImportDetail validatingLine = (ProjectCodeImportDetail) importedLineForValidation;
        String errorPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE_IMPORT_DETAILS + "[" + validatingLine.getSequenceNumber() == null ? String.valueOf(0) : String.valueOf(validatingLine.getSequenceNumber().intValue() - 1) + "].";
        String validatingKey = getProjectCodeKeyString(validatingLine);

        valid &= checkDuplicatesWithinFile(document, projectCodeMap, validatingLine, errorPrefix, validatingKey);

        valid &= checkDuplicatesInSystem(primaryKeys, validatingLine, errorPrefix);

        return valid;
    }

    private boolean checkDuplicatesInSystem(Map<String, String> primaryKeys, ProjectCodeImportDetail validatingLine, String errorPrefix) {
        boolean valid = true;
        // check duplicate in BO table
        primaryKeys.put(KFSPropertyConstants.CODE, validatingLine.getProjectCode());
        ProjectCode existingProjectCode = businessObjectService.findByPrimaryKey(ProjectCode.class, primaryKeys);

        if (ObjectUtils.isNotNull(existingProjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINTABLE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.ProjectCodeImportConstants.PROJECT_CODE });
            valid = false;
        }
        return valid;
    }

    private boolean checkDuplicatesWithinFile(ProjectCodeImportDocument document, HashMap<String, Object> projectCodeMap, ProjectCodeImportDetail validatingLine, String errorPrefix, String validatingKey) {
        boolean valid = true;
        String mapKey;
        for (ProjectCodeImportDetail importedLine : document.getProjectCodeImportDetails()) {
            mapKey = getProjectCodeKeyString(importedLine);
            if (!projectCodeMap.containsKey(mapKey)) {
                projectCodeMap.put(mapKey, importedLine.getSequenceNumber());
            }
            // check duplicate within the same file
            if (projectCodeMap.containsKey(validatingKey) && !projectCodeMap.get(validatingKey).equals(validatingLine.getSequenceNumber())) {
                GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINFILE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.ProjectCodeImportConstants.PROJECT_CODE });
                valid = false;
                break;
            }
        }
        return valid;
    }

    /**
     * Get the primary key string connected by hyphen for given business object
     *
     * @param validatingLine
     * @return
     */
    protected String getProjectCodeKeyString(ProjectCodeImportDetail validatingLine) {
        return validatingLine.getProjectCode();
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
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
     * Gets the importedLineForValidation attribute.
     *
     * @return Returns the importedLineForValidation.
     */
    public MassImportLineBase getImportedLineForValidation() {
        return importedLineForValidation;
    }

    /**
     * Sets the importedLineForValidation attribute value.
     *
     * @param importedLineForValidation The importedLineForValidation to set.
     */
    public void setImportedLineForValidation(MassImportLineBase importedLineForValidation) {
        this.importedLineForValidation = importedLineForValidation;
    }

}
