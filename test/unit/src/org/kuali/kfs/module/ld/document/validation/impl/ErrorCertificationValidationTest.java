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

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ErrorCertification;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * The unit tests for methods in the ErrorCertificationValidation class using a Salary Expense Transfer document.
 *
 * @see org.kuali.kfs.module.ld.document.validation.impl.ErrorCertificationValidation
 */
@ConfigureContext
public class ErrorCertificationValidationTest extends KualiTestBase {
    private SalaryExpenseTransferDocument stDocument;
    private ErrorCertificationValidation errorCertificationTabValidation;
    private MyAttributedDocumentEvent event;
    private ErrorCertification errorCertification;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stDocument = new SalaryExpenseTransferDocument();
        event = new MyAttributedDocumentEvent(stDocument);
        errorCertificationTabValidation = new ErrorCertificationValidation();
        errorCertification = new ErrorCertification();
        errorCertification.setDocumentNumber("1");
        stDocument.setErrorCertification(errorCertification);
    }

    public void testPartiallyFullErrorCertification() {
        setUpErrorCertification(false);

        errorCertificationTabValidation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertTrue("Error Certification Tab isn't required, but should be.", hasError);
    }

    public void testFullErrorCertification() {
        setUpErrorCertification(true);

        errorCertificationTabValidation.validate(event);

        boolean hasError = GlobalVariables.getMessageMap().doesPropertyHaveError(LaborKeyConstants.ErrorCertification.ERROR_ERROR_CERT_KEY);
        assertFalse("Unexpected requirement of Error Certification Tab.", hasError);
    }

    protected void setUpErrorCertification(boolean isCompleted) {
        if (isCompleted) {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("test description");
            errorCertification.setExpenditureProjectBenefit("test benefit");
        }
        else {
            errorCertification.setErrorCorrectionReason("test reason");
            errorCertification.setErrorDescription("test desc");
            errorCertification.setExpenditureDescription("");
            errorCertification.setExpenditureProjectBenefit("");
        }
    }

    static class MyAttributedDocumentEvent extends AttributedDocumentEventBase {
        public MyAttributedDocumentEvent(SalaryExpenseTransferDocument d) {
            super("", "", d);
        }
    }
}
