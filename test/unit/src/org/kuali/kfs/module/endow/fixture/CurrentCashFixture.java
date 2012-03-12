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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CurrentCashFixture {
    // Current Cash Fixture Record
    CURRENT_CASH_RECORD("TESTKEMID", //kemid
            new KualiDecimal(1250.80), // currentIncomeCash
            new KualiDecimal(1000.21) // currentPrincipalCash
    ),
    PRINCIPAL_SALE_ASSET_RECORD("TSTKEMID1", //kemid
            new KualiDecimal(1001), // currentIncomeCash
            new KualiDecimal(1002) // currentPrincipalCash
    ),
    PRINCIPAL_PURCHASE_ASSET_RECORD("TSTKEMID2", //kemid
            new KualiDecimal(1003), // currentIncomeCash
            new KualiDecimal(1004) // currentPrincipalCash
    );
    
    public final String kemid;
    public final KualiDecimal currentIncomeCash;
    public final KualiDecimal currentPrincipalCash;

    private CurrentCashFixture(String kemid, KualiDecimal currentIncomeCash,KualiDecimal currentPrincipalCash) {
        this.kemid = kemid;
        this.currentIncomeCash = currentIncomeCash;
        this.currentPrincipalCash = currentPrincipalCash;
    }

    /**
     * This method creates a Security record and saves it to table
     * @return current cash record
     */
    public KemidCurrentCash createKemidCurrentCashRecord() {
        KemidCurrentCash kemidCurrentCash = new KemidCurrentCash();

        kemidCurrentCash.setKemid(this.kemid);
        kemidCurrentCash.setCurrentIncomeCash(this.currentIncomeCash);
        kemidCurrentCash.setCurrentPrincipalCash(this.currentPrincipalCash);

        saveCurrentCashRecord(kemidCurrentCash);
        
        return kemidCurrentCash;
    }
    
    /**
     * This method creates a Security record and saves it to table
     * @return current cash record
     */
    public KemidCurrentCash createKemidCurrentCashRecord(String kemid, KualiDecimal currentIncomeCash, KualiDecimal currentPrincipalCash) {
        KemidCurrentCash kemidCurrentCash = new KemidCurrentCash();

        kemidCurrentCash.setKemid(kemid);
        kemidCurrentCash.setCurrentIncomeCash(currentIncomeCash);
        kemidCurrentCash.setCurrentPrincipalCash(currentPrincipalCash);

        saveCurrentCashRecord(kemidCurrentCash);
        
        return kemidCurrentCash;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveCurrentCashRecord(KemidCurrentCash kemidCurrentCash) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(kemidCurrentCash);
    }
}

