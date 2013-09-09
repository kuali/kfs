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
package org.kuali.kfs.module.tem.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.dataaccess.ExpenseTypeObjectCodeDao;

/**
 * A mock version of the expense type object code dao which will return a limited set of (fake) expense type object code records
 */
public class MockExpenseTypeObjectCodeDao implements ExpenseTypeObjectCodeDao {
    protected static final String MONKEY_EXPENSE_TYPE_CODE = "M";
    protected static final String GIRAFFE_EXPENSE_TYPE_CODE = "G";

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

}
