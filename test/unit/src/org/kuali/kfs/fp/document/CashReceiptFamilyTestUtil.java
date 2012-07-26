/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext
public class CashReceiptFamilyTestUtil {

    public static SourceAccountingLine buildSourceAccountingLine(String documentNumber, Integer postingYear, Integer sequenceNumber) {
        SourceAccountingLine line = new SourceAccountingLine();
        line.setChartOfAccountsCode("BA");
        line.setAccountNumber("6044900");
        line.setFinancialObjectCode("5000");
        line.setAmount(new KualiDecimal("1.00"));
        line.setPostingYear(postingYear);
        line.setDocumentNumber(documentNumber);
        line.setSequenceNumber(sequenceNumber);
        line.refresh();

        return line;
    }
}
