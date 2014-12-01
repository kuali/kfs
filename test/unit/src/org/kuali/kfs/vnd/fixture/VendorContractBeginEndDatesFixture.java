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
