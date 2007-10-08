/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.fixtures;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.test.fixtures.AccountingLineFixture;

public enum RequisitionAccountingLineFixture {
    BASIC_REQ_ACCOUNT_1(
    );
    
    
    private RequisitionAccountingLineFixture(
    ) {
    }
    
    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;

        //TODO: what should this debit code really be
        line = (PurApAccountingLine) puralFixture.createPurApAccountingLine(RequisitionAccount.class, alFixture);
        
        return line;
    }
}
