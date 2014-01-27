/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao;
import org.kuali.kfs.module.tem.dataaccess.impl.ExpenseTypeObjectCodeDaoOjb;
import org.kuali.kfs.module.tem.service.impl.TravelExpenseServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class TravelExpenseServiceTest extends KualiTestBase {
    protected static final String MONKEY_EXPENSE_TYPE_CODE = "M";
    protected static final String GIRAFFE_EXPENSE_TYPE_CODE = "G";

    protected TravelExpenseService travelExpenseService;

    protected enum MockExpenseTypeObjectCode {
        MONKEY("M","TT", "All", "All", "6000"),
        GIRAFFE_TT("G", "TT", "All", "All", "6001"),
        GIRAFFE_TA("G", "TA", "All", "All", "6002"),
        GIRAFFE_EMP_IN("G", "TA", "EMP", "IN", "6003"),
        GIRAFFE_TRIP_ALL("G", "TA", "EMP", "All", "6004"),
        GIRAFFE_TRAVELER_ALL("G", "TA", "All", "IN", "6005");

        private String expenseTypeCode;
        private String documentTypeName;
        private String travelerType;
        private String tripType;
        private String financialObjectCode;

        MockExpenseTypeObjectCode(String expenseTypeCode, String documentTypeName, String travelerType, String tripType, String financialObjectCode) {
            this.expenseTypeCode = expenseTypeCode;
            this.documentTypeName = documentTypeName;
            this.travelerType = travelerType;
            this.tripType = tripType;
            this.financialObjectCode = financialObjectCode;
        }

        protected ExpenseTypeObjectCode buildExpenseTypeObjectCode() {
            ExpenseTypeObjectCode etoc = new ExpenseTypeObjectCode();
            etoc.setExpenseTypeObjectCodeId(1L);
            etoc.setExpenseTypeCode(expenseTypeCode);
            etoc.setDocumentTypeName(documentTypeName);
            etoc.setTravelerTypeCode(travelerType);
            etoc.setTripTypeCode(tripType);
            etoc.setFinancialObjectCode(financialObjectCode);
            return etoc;
        }
    }

    @Override
    public void setUp() {
        ExpenseTypeObjectCodeDao expenseTypeObjectCodeDao = new ExpenseTypeObjectCodeDaoOjb() {
            @Override
            public List<ExpenseTypeObjectCode> findMatchingExpenseTypeObjectCodes(String expenseTypeCode, Set<String> documentTypes, String tripType, String travelerType) {
                List<ExpenseTypeObjectCode> results = new ArrayList<ExpenseTypeObjectCode>();
                if (MONKEY_EXPENSE_TYPE_CODE.equals(expenseTypeCode)) {
                    results.add(MockExpenseTypeObjectCode.MONKEY.buildExpenseTypeObjectCode());
                } else if (GIRAFFE_EXPENSE_TYPE_CODE.equals(expenseTypeCode)) {
                    if ("IN".equals(tripType) && documentTypes.contains("TA")) {
                        results.add(MockExpenseTypeObjectCode.GIRAFFE_TRAVELER_ALL.buildExpenseTypeObjectCode());
                    }
                    if (documentTypes.contains("TA")) {
                        results.add(MockExpenseTypeObjectCode.GIRAFFE_TA.buildExpenseTypeObjectCode());
                    }
                    results.add(MockExpenseTypeObjectCode.GIRAFFE_TT.buildExpenseTypeObjectCode());
                    if ("IN".equals(tripType) && "EMP".equals(travelerType) && documentTypes.contains("TA")) {
                        results.add(MockExpenseTypeObjectCode.GIRAFFE_EMP_IN.buildExpenseTypeObjectCode());
                    }
                    if ("EMP".equals(travelerType) && documentTypes.contains("TA")) {
                        results.add(MockExpenseTypeObjectCode.GIRAFFE_TRIP_ALL.buildExpenseTypeObjectCode());
                    }
                } else {
                    throw new UnsupportedOperationException("Hey, I'm just a mock interface.  I only know about monkeys and giraffes, not an expense type code like: "+expenseTypeCode);
                }
                return results;
            }

            @Override
            public List<ExpenseTypeObjectCode> findMatchingExpenseTypesObjectCodes(Set<String> documentTypes, String tripType, String travelerType) {
                List<ExpenseTypeObjectCode> results = new ArrayList<ExpenseTypeObjectCode>();
                results.add(MockExpenseTypeObjectCode.MONKEY.buildExpenseTypeObjectCode());
                results.add(MockExpenseTypeObjectCode.GIRAFFE_EMP_IN.buildExpenseTypeObjectCode());
                results.add(MockExpenseTypeObjectCode.GIRAFFE_TT.buildExpenseTypeObjectCode());
                results.add(MockExpenseTypeObjectCode.GIRAFFE_TA.buildExpenseTypeObjectCode());
                results.add(MockExpenseTypeObjectCode.GIRAFFE_TRAVELER_ALL.buildExpenseTypeObjectCode());
                results.add(MockExpenseTypeObjectCode.GIRAFFE_TRIP_ALL.buildExpenseTypeObjectCode());
                return results;
            }
        };

        travelExpenseService = new TravelExpenseServiceImpl() {
            @Override
            public ExpenseTypeObjectCodeDao getExpenseTypeObjectCodeDao() {
                return expenseTypeObjectCodeDao;
            }

        };
    }

    public void testGetExpenseType() {
        final TravelExpenseService travelExpenseService = SpringContext.getBean(TravelExpenseService.class);

        // monkeys are easy.  let's try monkeys first
        final ExpenseTypeObjectCode monkeyETOC1 = travelExpenseService.getExpenseType("M", "TA", "IN", "EMP");
        assertEquals("M-TA-EMP-IN found correct monkey expense type object code", "6000", monkeyETOC1.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC2 = travelExpenseService.getExpenseType("M", "ENT", "IN", "EMP");
        assertEquals("M-ENT-EMP-IN found correct monkey expense type object code", "6000", monkeyETOC2.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC3 = travelExpenseService.getExpenseType("M", "TA", "OUT", "EMP");
        assertEquals("M-TA-EMP-OUT found correct monkey expense type object code", "6000", monkeyETOC3.getFinancialObjectCode());
        final ExpenseTypeObjectCode monkeyETOC4 = travelExpenseService.getExpenseType("M", "TA", "OUT", "NON");
        assertEquals("M-TA-NON-OUT found correct monkey expense type object code", "6000", monkeyETOC4.getFinancialObjectCode());

        // giraffes are a bit harder
        final ExpenseTypeObjectCode giraffeETOC1 = travelExpenseService.getExpenseType("G", "TA", "OUT", "NON");
        assertEquals("G-TA-NON-OUT found correct giraffe expense type object code", "6002", giraffeETOC1.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC2 = travelExpenseService.getExpenseType("G", "ENT", "OUT", "NON");
        assertEquals("G-ENT-NON-OUT found correct giraffe expense type object code", "6001", giraffeETOC2.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC3 = travelExpenseService.getExpenseType("G", "TA", "IN", "EMP");
        assertEquals("G-TA-EMP-IN found correct giraffe expense type object code", "6003", giraffeETOC3.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC4 = travelExpenseService.getExpenseType("G", "TA", "IN", "NON");
        assertEquals("G-TA-NON-IN found correct giraffe expense type object code", "6005", giraffeETOC4.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC5 = travelExpenseService.getExpenseType("G", "TA", "OUT", "EMP");
        assertEquals("G-TA-EMP-OUT found correct giraffe expense type object code", "6004", giraffeETOC5.getFinancialObjectCode());
        final ExpenseTypeObjectCode giraffeETOC6 = travelExpenseService.getExpenseType("G", "ENT", "IN", "EMP");
        assertEquals("G-ENT-EMP-IN found correct giraffe expense type object code", "6001", giraffeETOC6.getFinancialObjectCode());
    }
}
