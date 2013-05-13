/*
 * Copyright 2010 The Kuali Foundation.
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
