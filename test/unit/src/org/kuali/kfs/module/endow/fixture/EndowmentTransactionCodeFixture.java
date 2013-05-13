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
