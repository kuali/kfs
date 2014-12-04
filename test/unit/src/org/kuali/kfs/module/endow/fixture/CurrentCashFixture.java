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

