/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class TravelDocumentActualExpenseLineValidationTest extends KualiTestBase {

    private static final int EXPENSE_AMOUNT = 100;
    private TravelDocumentActualExpenseLineValidation validation;
    private DateTimeService dateTimeService;
    private DocumentService docService;
    private DictionaryValidationService dictionaryValidationService;

    private static final String AIRFARE_EXPENSE_TYPE_CODE = "A";
    private static final String MILEAGE_EXPENSE_TYPE_CODE = "M";
    private static final String HOSTEDMEAL_BREAKFAST_EXPENSE_TYPE_CODE = "HB";
    private static final String LODGING_EXPENSE_TYPE_CODE = "L";
    private static final String LODGING_ALLOWANCE_EXPENSE_TYPE_CODE = "LA";
    private static final String RENTALCAR_EXPENSE_TYPE_CODE = "R";


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new TravelDocumentActualExpenseLineValidation();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        docService = SpringContext.getBean(DocumentService.class);
        dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);
        validation.setDictionaryValidationService(dictionaryValidationService);
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
        TravelReimbursementDocument tr = null;
        try {
            tr = (TravelReimbursementDocument)docService.getNewDocument(TravelReimbursementDocument.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();
//        tr.setActualExpenses(actualExpenses);
        //ActualExpense ote = new ActualExpense();

//        @SuppressWarnings("rawtypes")
//        AddActualExpenseLineEvent event = new AddActualExpenseLineEvent("errorPathPrefix", tr, ote);
//
//        //test using empty actualExpense
//        assertFalse(validation.validate(event));

        ActualExpense ote2 = new ActualExpense();

        ote2.setExpenseAmount(new KualiDecimal(EXPENSE_AMOUNT));
        ote2.setExpenseDate(dateTimeService.getCurrentSqlDate());
        ote2.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE_CODE);
       //tr.addActualExpense(ote2);

        AddActualExpenseLineEvent event2 = new AddActualExpenseLineEvent("errorPathPrefix", tr, ote2);
        validation.setActualExpenseForValidation(ote2);
        assertTrue(validation.validate(event2));
    }

    @Test
    public void testValidateGeneralRules(){

        TravelReimbursementDocument document = new TravelReimbursementDocument();
        List<ActualExpense> actualExpenses = new ArrayList<ActualExpense>();
        ActualExpense airFareEntry = new ActualExpense();
        ActualExpense mileageEntry = new ActualExpense();
        ExpenseTypeObjectCode aTravelExpenseTypeCode = new ExpenseTypeObjectCode();

        document.setActualExpenses(actualExpenses);

        aTravelExpenseTypeCode.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE_CODE);

      //Testing expense amount required validation since expense type is not mileage
        airFareEntry.setTravelExpenseTypeCode(aTravelExpenseTypeCode);
        airFareEntry.setExpenseDate(dateTimeService.getCurrentSqlDate());
        airFareEntry.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE_CODE);
        assertTrue(validation.validateGeneralRules(airFareEntry, document));

      //Testing expense amount required validation since expense type is not mileage
        airFareEntry.setExpenseAmount(new KualiDecimal(-100));
        assertFalse(validation.validateGeneralRules(airFareEntry, document));

      //Testing Currency rate required validation
        airFareEntry.setExpenseAmount(new KualiDecimal(100));
        airFareEntry.setCurrencyRate(BigDecimal.ZERO);
        assertTrue(validation.validateGeneralRules(airFareEntry, document));

        //Testing duplicate entry
        ActualExpense duplicateAirfareEntry = new ActualExpense();
        duplicateAirfareEntry.setTravelExpenseTypeCode(aTravelExpenseTypeCode);
        duplicateAirfareEntry.setExpenseTypeCode(AIRFARE_EXPENSE_TYPE_CODE);
        duplicateAirfareEntry.setExpenseAmount(new KualiDecimal(100.00));
        duplicateAirfareEntry.setExpenseDate(dateTimeService.getCurrentSqlDate());
        actualExpenses.add(airFareEntry);
        assertFalse(validation.validateGeneralRules(duplicateAirfareEntry, document));

        //Testing expense amount not required validation when expense type is mileage
        ExpenseTypeObjectCode mTravelExpenseTypeCode = new ExpenseTypeObjectCode();
        mTravelExpenseTypeCode.setExpenseTypeCode(MILEAGE_EXPENSE_TYPE_CODE);
        mileageEntry.setExpenseAmount(KualiDecimal.ZERO);
        mileageEntry.setTravelExpenseTypeCode(mTravelExpenseTypeCode);
        assertTrue(validation.validateGeneralRules(mileageEntry, document));
    }
    @Test
    public void testValidateAirfareRules(){

    }
    @Test
    public void testValidateRentalCarRules(){

    }
    @Test
    public void testValidateMileageRules(){

    }
    @Test
    public void testValidateLodgingRules(){

    }
    @Test
    public void testValidateLodgingAllowanceRules(){

    }
    @Test
    public void testValidateMealsRuels(){

    }
    @Test
    public void testMaximumAmountRules(){

    }
    @Test
    public void testValidatePerDiemRules(){

    }
}
