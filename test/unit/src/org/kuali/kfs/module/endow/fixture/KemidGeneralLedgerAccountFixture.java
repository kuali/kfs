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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum KemidGeneralLedgerAccountFixture {
    KEMID_GL_ACCOUNT("TESTKEMID", // kemid
            "I", // incomePrincipalIndicatorCode
            "BL", // chartCode
            ".....", // accountNumber
            true // active
    ),
    KEMID_GL_ACCOUNT_FOR_ASSET("TESTKEMID", // kemid
            "P", // incomePrincipalIndicatorCode
            "BL", // chartCode
            ".....", // accountNumber
            true // active
    ),
    KEMID_GL_ACCOUNT_FOR_ASSET1("TSTKEMID1", // kemid
            "P", // incomePrincipalIndicatorCode
            "BL", // chartCode
            ".....", // accountNumber
            true // active
    ),
    KEMID_GL_ACCOUNT_FOR_ASSET2("TSTKEMID2", // kemid
            "I", // incomePrincipalIndicatorCode
            "BL", // chartCode
            ".....", // accountNumber
            true // active
    ),
    
    KEMID_GL_ACCOUNT_FOR_GAIN_LOSS_TRANSACTIONS_COMMITTED("TEST_KEMID", // kemid
            "I", // incomePrincipalIndicatorCode
            "BL", // chartCode
            ".....", // accountNumber
            true // active    
    );

    public final String kemid;
    public final String incomePrincipalIndicatorCode;
    public final String chartCode;
    public final String accountNumber;
    public final boolean active;

    private KemidGeneralLedgerAccountFixture(String kemid, String incomePrincipalIndicatorCode, String chartCode, String accountNumber, boolean active) {
        this.kemid = kemid;
        this.incomePrincipalIndicatorCode = incomePrincipalIndicatorCode;
        this.chartCode = chartCode;
        this.accountNumber = accountNumber;
        this.active = active;
    }

    /**
     * This method creates a KemidGeneralLedgerAccount record and saves it to the DB table.
     * 
     * @return KemidGeneralLedgerAccount
     */
    public KemidGeneralLedgerAccount createKemidGeneralLedgerAccount() {
        KemidGeneralLedgerAccount kemidGeneralLedgerAccount = new KemidGeneralLedgerAccount();

        kemidGeneralLedgerAccount.setKemid(this.kemid);
        kemidGeneralLedgerAccount.setIncomePrincipalIndicatorCode(this.incomePrincipalIndicatorCode);
        kemidGeneralLedgerAccount.setChartCode(this.chartCode);
        kemidGeneralLedgerAccount.setAccountNumber(this.accountNumber);
        kemidGeneralLedgerAccount.setActive(this.active);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(kemidGeneralLedgerAccount);

        return kemidGeneralLedgerAccount;
    }

}
