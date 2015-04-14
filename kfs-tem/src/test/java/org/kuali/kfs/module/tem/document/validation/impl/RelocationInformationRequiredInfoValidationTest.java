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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext(session = khuntley)
public class RelocationInformationRequiredInfoValidationTest extends KualiTestBase {

    private RelocationInformationRequiredInfoValidation validation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new RelocationInformationRequiredInfoValidation();
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }

    /**
     * This method tests validate method
     */
    @Test
    public void testValidation() {
        TravelRelocationDocument document = new TravelRelocationDocument();
        ActualExpense expense = new ActualExpense();
        expense.setExpenseAmount(new KualiDecimal(100));
        document.addActualExpense(expense);
        document.setFromCountryCode(validation.USA_COUNTRY_CODE);

        AttributedDocumentEvent event = new AttributedDocumentEventBase("description", "errorPathPrefix", document);

        assertFalse(validation.validate(event));

        document.setFromStateCode("AZ");

        assertTrue(validation.validate(event));

        document.setToCountryCode(validation.USA_COUNTRY_CODE);
        assertFalse(validation.validate(event));

        document.setToCountryCode("UK");
        assertTrue(validation.validate(event));


    }

}
