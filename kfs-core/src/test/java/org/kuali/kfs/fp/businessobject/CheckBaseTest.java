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
package org.kuali.kfs.fp.businessobject;


import java.sql.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class...
 */
@ConfigureContext
public class CheckBaseTest extends KualiTestBase {
    private CheckBase crchk = null;
    private static final KualiDecimal AMOUNT = new KualiDecimal("100.27");
    private static final String GUID = "123456789012345678901234567890123456";
    private static final Long VER_NBR = new Long(1);
    private static Date date;
    private static final String CHECK_NUMBER = "123456";
    private static final String DESCRIPTION = "Description 123.";
    private static final String DOC_HDR_ID = "999999";
    private static final Integer SEQ_ID = new Integer(1);
    private static final Integer DEPOSIT_LINE_NUMBER = new Integer(1);
    private static final String GENERAL_LEDGER_INPUT_TYPE = "CR";
    private static final String CASHIERING_STATUS = "C";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        date = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        crchk = new CheckBase();
        crchk.setAmount(AMOUNT);
        crchk.setCheckDate(date);
        crchk.setCheckNumber(CHECK_NUMBER);
        crchk.setDescription(DESCRIPTION);
        crchk.setDocumentNumber(DOC_HDR_ID);
        crchk.setFinancialDocumentDepositLineNumber(DEPOSIT_LINE_NUMBER);
        crchk.setObjectId(GUID);
        crchk.setSequenceId(SEQ_ID);
        crchk.setVersionNumber(VER_NBR);
        crchk.setCashieringStatus(CASHIERING_STATUS);
        crchk.setFinancialDocumentTypeCode(GENERAL_LEDGER_INPUT_TYPE);
    }

    public void testCashReceiptCheckPojo() {
        assertEquals(AMOUNT, crchk.getAmount());
        assertEquals(date, crchk.getCheckDate());
        assertEquals(CHECK_NUMBER, crchk.getCheckNumber());
        assertEquals(DESCRIPTION, crchk.getDescription());
        assertEquals(DOC_HDR_ID, crchk.getDocumentNumber());
        assertEquals(DEPOSIT_LINE_NUMBER, crchk.getFinancialDocumentDepositLineNumber());
        assertEquals(GUID, crchk.getObjectId());
        assertEquals(SEQ_ID, crchk.getSequenceId());
        assertEquals(VER_NBR, crchk.getVersionNumber());
        assertEquals(GENERAL_LEDGER_INPUT_TYPE, crchk.getFinancialDocumentTypeCode());
        assertEquals(CASHIERING_STATUS, crchk.getCashieringStatus());
    }
}
