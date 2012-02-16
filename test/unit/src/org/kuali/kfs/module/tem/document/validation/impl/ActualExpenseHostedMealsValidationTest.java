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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class ActualExpenseHostedMealsValidationTest extends KualiTestBase {
    
    private static final String HOSTED_BREAKFAST = "HB";
    private ActualExpenseHostedMealsValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new ActualExpenseHostedMealsValidation();             
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
        TravelReimbursementDocument tr = new TravelReimbursementDocument();
        ActualExpense ote = new ActualExpense();
        
        @SuppressWarnings("rawtypes")
        AddActualExpenseLineEvent event = new AddActualExpenseLineEvent("errorPathPrefix", tr, ote);
        
        //test using empty actualExpense
        assertTrue(validation.validate(event));
        
        Date today = new Date(new java.util.Date().getTime());
        List<PerDiemExpense> perDiemExpenses = new ArrayList<PerDiemExpense>();
        PerDiemExpense perDiem = new PerDiemExpense();
        perDiem.setMileageDate(new Timestamp(today.getTime()));
        
        TemTravelExpenseTypeCode travelExpenseTypeCode = new TemTravelExpenseTypeCode();
        travelExpenseTypeCode.setCode(HOSTED_BREAKFAST);
        ote.setTravelExpenseTypeCode(travelExpenseTypeCode);
        ote.setExpenseDate(today);
        
        perDiem.setBreakfast(true);
        perDiemExpenses.add(perDiem);
        tr.setPerDiemExpenses(perDiemExpenses);
        
        //test with both perDiem and actualExpense have breakfast.
        assertFalse(validation.validate(event));        
    }
}
