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
