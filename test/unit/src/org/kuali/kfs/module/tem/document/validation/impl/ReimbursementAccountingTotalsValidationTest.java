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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class ReimbursementAccountingTotalsValidationTest extends KualiTestBase {

    private static final String DEFAULT_CHART_CODE = "BL";
    private static final int EXPENSE_AMOUNT = 100;
    private static final int ACCOUNTING_LINE_AMOUNT = 100;
    private TmAccountingLineTotalsValidation validation;
    private static final String AIRLINE_EXPENSE_TYPE_CODE = "A";
    private static final String FINANCIAL_OBJECT_CODE = "6100";
    private BusinessObjectService businessObjectService;
    private ObjectCodeService objectCodeService;
    private static final Logger LOG = Logger.getLogger(ReimbursementAccountingTotalsValidationTest.class);
    private AttributedDocumentEventBase event = null;
    private TravelReimbursementDocument tr = null;
    private List<ActualExpense> oteList = null;
    private ObjectCode perDiemObjCode = null;
    private ActualExpense actualExpense = null;
    private List sourceLines = null;

    @Override
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        validation = new TmAccountingLineTotalsValidation();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        //setup tr
        tr = new TravelReimbursementDocument();
        TravelerDetail traveler = new TravelerDetail();
        TripType tripType = new TripType();

        //Out of state TripType
        Map<String, String> outOfState = new HashMap<String, String>();
        outOfState.put("code", "OUT");
        List<TripType> results = (List<TripType>) businessObjectService.findMatching(TripType.class, outOfState);
        TripType tt = !results.isEmpty() ? results.get(0) : null;
        tr.setTripType(tt);

        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        tr.setTraveler(traveler);

        //setup oteList
        oteList = new ArrayList<ActualExpense>();
        tr.setActualExpenses(oteList);
        tr.setPerDiemExpenses(new ArrayList<PerDiemExpense>());
        event = new AttributedDocumentEventBase("description", "errorPathPrefix", tr);

        perDiemObjCode = objectCodeService.getByPrimaryIdForCurrentYear(DEFAULT_CHART_CODE, FINANCIAL_OBJECT_CODE);

        // Override refreshReferenceObject - setting travelExpenseTypeCode manually
        actualExpense = new ActualExpense() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing;
            }
        };
        actualExpense.setExpenseAmount(new KualiDecimal(EXPENSE_AMOUNT));

        //setup actualExpense
        ExpenseTypeObjectCode travelExpenseTypeCode = new ExpenseTypeObjectCode();
        travelExpenseTypeCode.setExpenseTypeCode(AIRLINE_EXPENSE_TYPE_CODE);
        travelExpenseTypeCode.setFinancialObjectCode(FINANCIAL_OBJECT_CODE);
        actualExpense.setTravelExpenseTypeCode(travelExpenseTypeCode);

        //setup sourceLines
        sourceLines = new ArrayList();
        SourceAccountingLine sal = new SourceAccountingLine();
        sal.setAmount(new KualiDecimal(ACCOUNTING_LINE_AMOUNT));
        sal.setObjectCode(perDiemObjCode);
        sourceLines.add(sal);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }

    /**
     * This method tests {@link ActualExpense#validate(AttributedDocumentEvent)} with no expense.
     */
    @Test
    public void testValidation_emptyTest() {
        assertTrue(validation.validate(event));
    }

    /**
     * This method tests {@link ActualExpense#validate(AttributedDocumentEvent)} with other expense and no accounting line.
     */
    @Test
    public void testValidation_noAccountingLine() {
        // financial object code need to exists in the current fiscal year
        if(perDiemObjCode != null){
            oteList.add(actualExpense);

            // test with other expense added and no accounting line
            assertFalse(validation.validate(event));
        }else{
            LOG.error("PerDiemObjCode is null. Financial object code need to exists in the current fiscal year.");
        }
    }

    /**
     * This method tests {@link ActualExpense#validate(AttributedDocumentEvent)} with other expense and accounting line.
     */
    @Test
    public void testValidation_withAccountingLine() {
        // financial object code need to exists in the current fiscal year
        if(perDiemObjCode != null){
            oteList.add(actualExpense);
            tr.setSourceAccountingLines(sourceLines);

            // test with other expense added and with accounting line
            assertTrue(validation.validate(event));
        }else{
            LOG.error("PerDiemObjCode is null. Financial object code need to exists in the current fiscal year.");
        }
    }

}
