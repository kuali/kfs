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

import java.util.List;

import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurApAccountingLine;

public enum PaymentRequestItemFixture {
    
    ITEM_TYPE_WITH_GOOD_ACCOUNTS(ItemTypeCodes.ITEM_TYPE_ITEM_CODE, true, true),
    ;
    
    String itemType;
    List<PurApAccountingLine> accountingLines;
    SourceAccountingLine sal = new SourceAccountingLine();   
    
    private PaymentRequestItemFixture(String itemType, boolean acct1, boolean acct2) {
        this.itemType = itemType;
        if (acct1) {   
            PurApAccountingLine paal = (PurApAccountingLine)sal;
            accountingLines.add(paal);
        }
        if (acct2) {   
            PurApAccountingLine paal = (PurApAccountingLine)sal;
            accountingLines.add(paal);
        }
    }

}
