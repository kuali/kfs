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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum EndowmentTransactionCodeFixture {
    INCOME_TRANSACTION_CODE("TST123",// code
            "Test transaction code",// desc
            "I",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    ), 
    EXPENSE_TRANSACTION_CODE("TST124",// code
            "Test transaction code",// desc
            "E",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    ), 
    EXPENSE_TRANSACTION_CODE_COMMITTED("TST124",// code
            "Test transaction code",// desc
            "E",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    ),    
    ASSET_TRANSACTION_CODE("TST125",// code
            "Test transaction code",// desc
            "A",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    ), 
    ASSET_TRANSACTION_CODE_2("TST123",// code
            "Test transaction code",// desc
            "A",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    ),     
    INCOME_TRANSACTION_CODE_COMMITTED("123TST",// code
            "Test transaction code",// desc
            "I",// endowmentTransactionTypeCode;
            true,// corpusIndicator;
            true// active
    );


    public final String code;
    public final String desc;
    public final String endowmentTransactionTypeCode;
    public final boolean corpusIndicator;
    public final boolean active;

    private EndowmentTransactionCodeFixture(String code, String desc, String endowmentTransactionTypeCode, boolean corpusIndicator, boolean active) {
        this.code = code;
        this.desc = desc;
        this.endowmentTransactionTypeCode = endowmentTransactionTypeCode;
        this.corpusIndicator = corpusIndicator;
        this.active = active;
    }

    /**
     * This method creates a EndowmentTransactionCode record and saves it to the DB table.
     * 
     * @return EndowmentTransactionCode
     */
    public EndowmentTransactionCode createEndowmentTransactionCode() {
        EndowmentTransactionCode endowmentTransactionCode = new EndowmentTransactionCode();

        endowmentTransactionCode.setCode(this.code);
        endowmentTransactionCode.setName(this.desc);
        endowmentTransactionCode.setEndowmentTransactionTypeCode(this.endowmentTransactionTypeCode);
        endowmentTransactionCode.setCorpusIndicator(this.corpusIndicator);
        endowmentTransactionCode.setActive(this.active);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(endowmentTransactionCode);

        return endowmentTransactionCode;
    }
}
