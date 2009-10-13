/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.vnd.fixture;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.BeginEndDates;

public enum VendorContractBeginEndDatesFixture {

    RIGHT_ORDER_RIGHT_ORDER(BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE), WRONG_ORDER_RIGHT_ORDER(BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE), RIGHT_ORDER_WRONG_ORDER(BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE), WRONG_ORDER_WRONG_ORDER(BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE, BeginEndDates.LAST_DATE, BeginEndDates.FIRST_DATE), ;

    private Date date11;
    private Date date12;
    private Date date21;
    private Date date22;

    private VendorContractBeginEndDatesFixture(Date date11, Date date12, Date date21, Date date22) {
        this.date11 = date11;
        this.date12 = date12;
        this.date21 = date21;
        this.date22 = date22;
    }

    /**
     * This method does the setup for the tests which examine the implementation of the requirement that vendor contracts be
     * validated so that their begin date comes before their end date. Uses two contracts.
     * 
     * @param date11 For the first contract, the begin date
     * @param date12 For the first contract, the end date
     * @param date21 For the second contract, the begin date
     * @param date22 For the second contract, the end date
     * @return A VendorRule object, with sufficient information to run the validation method
     */
    public List populateContracts() {
        VendorContract contract1 = new VendorContract();
        VendorContract contract2 = new VendorContract();
        contract1.setVendorContractBeginningDate(date11);
        contract1.setVendorContractEndDate(date12);
        contract2.setVendorContractBeginningDate(date21);
        contract2.setVendorContractEndDate(date22);
        List<VendorContract> contracts = new ArrayList();
        contracts.add(contract1);
        contracts.add(contract2);
        return contracts;
    }
}
