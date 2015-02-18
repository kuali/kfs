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
package org.kuali.kfs.module.ar.core;

import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.confirmCustomerInvoiceValid;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.createFinalizedInvoiceOneLine;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.createFinalizedInvoiceOneLineDiscounted;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.createFinalizedInvoiceTwoLines;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.createFinalizedInvoiceTwoLinesDiscounted;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.createFinalizedInvoiceTwoLinesOneIsDiscounted;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.fifteenHundred;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.fiveHundred;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.oneThousand;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.sevenFifty;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.twelveFifty;
import static org.kuali.kfs.module.ar.core.ArCoreTestUtils.twoFifty;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class FullDocumentLifecycleTest extends KualiTestBase {

    private BusinessObjectService businessObjectService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService paymentApplicationDocumentService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        universityDateService = SpringContext.getBean(UniversityDateService.class);
    }

    public void testInvoiceOpenAmounts_ThroughWriteoffs() throws Exception {

        List<String> invoiceDocNumbers = new ArrayList<String>();
        
        CustomerInvoiceDocument document = createFinalizedInvoiceOneLine();
        invoiceDocNumbers.add(document.getDocumentNumber());
        List<InvoiceDetailExpecteds> expecteds = new ArrayList<InvoiceDetailExpecteds>();
        expecteds.add(new InvoiceDetailExpecteds(oneThousand(), oneThousand(), oneThousand(), false, 0));
        confirmCustomerInvoiceValid(document, oneThousand(), oneThousand(), 0, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLines();
        invoiceDocNumbers.add(document.getDocumentNumber());
        expecteds = new ArrayList<InvoiceDetailExpecteds>();
        expecteds.add(0, new InvoiceDetailExpecteds(oneThousand(), oneThousand(), oneThousand(), false, 0));
        expecteds.add(1, new InvoiceDetailExpecteds(fiveHundred(), fiveHundred(), fiveHundred(), false, 0));
        confirmCustomerInvoiceValid(document, fifteenHundred(), fifteenHundred(), 0, false, true, expecteds);
        
        document = createFinalizedInvoiceOneLineDiscounted();
        invoiceDocNumbers.add(document.getDocumentNumber());
        expecteds = new ArrayList<InvoiceDetailExpecteds>();
        expecteds.add(new InvoiceDetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        confirmCustomerInvoiceValid(document, sevenFifty(), sevenFifty(), 1, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLinesOneIsDiscounted();
        invoiceDocNumbers.add(document.getDocumentNumber());
        expecteds = new ArrayList<InvoiceDetailExpecteds>();
        expecteds.add(new InvoiceDetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        expecteds.add(new InvoiceDetailExpecteds(fiveHundred(), fiveHundred(), fiveHundred(), false, 0));
        confirmCustomerInvoiceValid(document, twelveFifty(), twelveFifty(), 1, false, true, expecteds);
        
        document = createFinalizedInvoiceTwoLinesDiscounted();
        invoiceDocNumbers.add(document.getDocumentNumber());
        expecteds = new ArrayList<InvoiceDetailExpecteds>();
        expecteds.add(new InvoiceDetailExpecteds(oneThousand(), sevenFifty(), sevenFifty(), true, 1, Arrays.asList(twoFifty())));
        expecteds.add(new InvoiceDetailExpecteds(fiveHundred(), twoFifty(), twoFifty(), true, 1, Arrays.asList(twoFifty())));
        confirmCustomerInvoiceValid(document, oneThousand(), oneThousand(), 2, false, true, expecteds);
        
        //TODO need to run writeoffs now, and validate that the openAmounts, totalAmounts, 
        //     and PaidApplieds are all correct.
    }
    
    public void testInvoiceOpenAmounts_CreditMemosToZeroBalance() throws Exception {
        
    }

    public void testInvoiceOpenAmounts_CreditMemosThenWriteoffs() throws Exception {
        
    }

    public void testInvoiceOpenAmounts_CashControlThenPayapps() throws Exception {
        
    }

    public void testInvoiceOpenAmounts_UnappliedPayapps() throws Exception {
        
    }

}
