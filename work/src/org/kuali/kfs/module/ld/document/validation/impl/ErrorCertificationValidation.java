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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ErrorCertification;
import org.kuali.kfs.module.ld.document.ErrorCertifiable;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for error certification tab to check if it's incomplete
 */
public class ErrorCertificationValidation extends GenericValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ErrorCertificationValidation.class);

    /**
     * Validates the Error Certification tab.
     *
     * @param event
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        int numBlankFields = errorCertificationBlankFieldsCheck((ErrorCertifiable) event.getDocument());
        if (LOG.isDebugEnabled()) {
            LOG.debug("numBlankFields: " + numBlankFields);
        }

        // if Error Certification tab is partially filled out
        if ((numBlankFields > 0) && (numBlankFields < LaborConstants.ErrorCertification.NUM_ERROR_CERT_FIELDS)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY, LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_FIELDS_REQ);
            return false;
        }

        return true;
    }

    /**
     * This method goes through all the Error Certification fields looking for blank fields.
     *
     * @param document
     * @return blankFieldCount the number of blank fields in the tab
     */
    public int errorCertificationBlankFieldsCheck(ErrorCertifiable document) {
        int blankFieldCount = 0;

        ErrorCertification ecTab = document.getErrorCertification();

        if (StringUtils.isBlank(ecTab.getExpenditureDescription())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getExpenditureProjectBenefit())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getErrorDescription())) {
            blankFieldCount++;
        }

        if (StringUtils.isBlank(ecTab.getErrorCorrectionReason())) {
            blankFieldCount++;
        }

        return blankFieldCount;
    }
}
