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
package org.kuali.module.gl.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.kuali.rice.KNSServiceLocator;

public class CorrectionDocumentUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocumentUtils.class);
    public static final int DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT = 1000;
    
    /**
     * The GLCP document will always be on restricted functionality mode, regardless of input group size
     */
    public static final int RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE = 0;
    
    /**
     * The GLCP document will never be on restricted functionality mode, regardless of input group size
     */
    public static final int RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED = -1;
    
    public static final int DEFAULT_RECORDS_PER_PAGE = 10;
    
    public static int getRecordCountFunctionalityLimit() {
        String limitString = KNSServiceLocator.getKualiConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.GENERAL_LEDGER_CORRECTION_PROCESS,
                KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORD_COUNT_FUNCTIONALITY_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        
        return DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT;
    }
    
    public static int getRecordsPerPage() {
        String limitString = KNSServiceLocator.getKualiConfigurationService().getApplicationParameterValue(KFSConstants.ParameterGroups.GENERAL_LEDGER_CORRECTION_PROCESS,
                KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORDS_PER_PAGE);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return DEFAULT_RECORDS_PER_PAGE;
    }
    
    /**
     * 
     * @param correctionForm
     * @return
     */
    public static boolean isRestrictedFunctionalityMode(int inputGroupSize, int recordCountFunctionalityLimit) {
        return (recordCountFunctionalityLimit != CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED && inputGroupSize >= recordCountFunctionalityLimit) ||
                recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE;
    }
    
    /**
     * When a correction criterion is about to be added to a group, this will check if it is valid, meaning that
     * the field name is not blank
     * 
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionCriteriaForAdding(CorrectionCriteria correctionCriteria) {
        String fieldName = correctionCriteria.getCorrectionFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        return true;
    }
    
    /**
     * When a document is about to be saved, this will check if it is valid, meaning that the field name and value are both blank
     * 
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionCriteriaForSaving(CorrectionCriteria correctionCriteria) {
        return correctionCriteria == null || 
                (StringUtils.isBlank(correctionCriteria.getCorrectionFieldName()) &&  StringUtils.isBlank(correctionCriteria.getCorrectionFieldValue()));
    }
    
    /**
     * When a correction change is about to be added to a group, this will check if it is valid, meaning that
     * the field name is not blank
     * 
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionChangeForAdding(CorrectionChange correctionChange) {
        String fieldName = correctionChange.getCorrectionFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        return true;
    }
    
    /**
     * When a document is about to be saved, this will check if it is valid, meaning that the field name and value are both blank
     * 
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionChangeForSaving(CorrectionChange correctionChange) {
        return correctionChange == null || 
                (StringUtils.isBlank(correctionChange.getCorrectionFieldName()) &&  StringUtils.isBlank(correctionChange.getCorrectionFieldValue()));
    }
}
