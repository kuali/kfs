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
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.ConfigureContext;
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
    private TemAccountingLineTotalsValidation validation;
    private static final String AIRLINE_EXPENSE_TYPE_CODE = "A";
    private static final String FINANCIAL_OBJECT_CODE = "5070";
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
        validation = new TemAccountingLineTotalsValidation();
        validation.setAccountingDistributionService(SpringContext.getBean(AccountingDistributionService.class));
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        //setup tr
        tr = new TravelReimbursementDocument();
        TravelerDetail traveler = new TravelerDetail();
        traveler.setId(1); // use KHUNTLEY's profile
        tr.setTemProfileId(1); // use KHUNTLEY's profile
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
        actualExpense.setExpenseTypeCode("O");
        actualExpense.setExpenseAmount(new KualiDecimal(EXPENSE_AMOUNT));

        //setup actualExpense
        ExpenseTypeObjectCode travelExpenseTypeCode = new ExpenseTypeObjectCode();
        travelExpenseTypeCode.setExpenseTypeCode(AIRLINE_EXPENSE_TYPE_CODE);
        travelExpenseTypeCode.setFinancialObjectCode(FINANCIAL_OBJECT_CODE);
        ExpenseType expenseType = new ExpenseType();
        expenseType.setCode("A");
        expenseType.setPrepaidExpense(false);
        travelExpenseTypeCode.setExpenseType(expenseType);
        actualExpense.setTravelExpenseTypeCode(travelExpenseTypeCode);

        //setup sourceLines
        sourceLines = new ArrayList();
        TemSourceAccountingLine sal = new TemSourceAccountingLine();
        sal.setAmount(new KualiDecimal(ACCOUNTING_LINE_AMOUNT));
        sal.setFinancialObjectCode(perDiemObjCode.getFinancialObjectCode());
        sal.setCardType(TemConstants.ACTUAL_EXPENSE);
        //sal.setObjectCode(perDiemObjCode);
        sal.setSequenceNumber(1);
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
            assertTrue(validation.validate(event));
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
