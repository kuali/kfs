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
package org.kuali.kfs.fp.document.service;

import java.util.List;

import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the DisbursementVoucherTax service.
 */
@ConfigureContext(session=UserNameFixture.khuntley)
public class DisbursementVoucherTaxServiceTest extends KualiTestBase {
    private DisbursementVoucherDocument dvDocument;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dvDocument = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getNewDocument(DisbursementVoucherDocument.class);
        dvDocument.setDvPayeeDetail(new DisbursementVoucherPayeeDetail());
        dvDocument.setDvNonResidentAlienTax(new DisbursementVoucherNonResidentAlienTax());
        dvDocument.getDvPayeeDetail().setDisbVchrAlienPaymentCode(true);

        AccountingLine line = new SourceAccountingLine();
        line.setChartOfAccountsCode("UA");
        line.setAccountNumber("1912610");
        line.setFinancialObjectCode("5000");
        line.setAmount(new KualiDecimal(100));
        line.setSequenceNumber(new Integer(1));

        dvDocument.getSourceAccountingLines().add(line);
        dvDocument.setNextSourceLineNumber(new Integer(2));
    }

    /**
     * Test validation of nra information before generation of lines
     *
     * @throws Exception
     */
    public void testValidateNRA() throws Exception {
        // check tax is not created for attributes that it should not be generated for
        dvDocument.setDisbVchrCheckTotalAmount(new KualiDecimal(100));
        dvDocument.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(KualiDecimal.ZERO);
        dvDocument.getDvNonResidentAlienTax().setStateIncomeTaxPercent(KualiDecimal.ZERO);

        // should not be generated for non-reportable
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("N");
        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        List newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 1);
        GlobalVariables.getMessageMap().clearErrorMessages();

        // should not be generated for foreign source
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(true);
        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 1);
        GlobalVariables.getMessageMap().clearErrorMessages();

        // should not be generated for treaty exempt
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(true);
        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 1);
        GlobalVariables.getMessageMap().clearErrorMessages();

        // should not be generated for doc reference is given
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setReferenceFinancialDocumentNumber("foo");
        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 1);
        GlobalVariables.getMessageMap().clearErrorMessages();

        // should not be generated if check amount is 0
        dvDocument.setDisbVchrCheckTotalAmount(KualiDecimal.ZERO);
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setReferenceFinancialDocumentNumber("");
        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 1);
        GlobalVariables.getMessageMap().clearErrorMessages();

    }

    /**
     * Test generation and clearing of nra tax lines.
     */
    public void testGenerateNRATaxLines() {
        KualiDecimal orginalCheckAmount = new KualiDecimal(100);
        dvDocument.setDisbVchrCheckTotalAmount(new KualiDecimal(100));
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setReferenceFinancialDocumentNumber("");
        dvDocument.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(new KualiDecimal(14));
        dvDocument.getDvNonResidentAlienTax().setStateIncomeTaxPercent(new KualiDecimal(3.4));
        dvDocument.getDvNonResidentAlienTax().setPostalCountryCode("USA");

        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        List newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.size() == 2);
        assertTrue(newTaxNumbers.get(0).equals(new Integer(2)));
        assertTrue(newTaxNumbers.get(1).equals(new Integer(3)));
        assertTrue(dvDocument.getNextSourceLineNumber().equals(new Integer(4)));
        assertTrue(dvDocument.getSourceAccountingLines().size() == 3);

        // test clearning
        SpringContext.getBean(DisbursementVoucherTaxService.class).clearNRATaxLines(dvDocument);
        assertEquals(1, dvDocument.getSourceAccountingLines().size());
        assertEquals("Check total credited correctly", new KualiDecimal(100), dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match check total", new KualiDecimal(100), dvDocument.getSourceTotal());


        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertTrue(newTaxNumbers.size() == 2);
        assertTrue(newTaxNumbers.get(0).equals(new Integer(4)));
        assertTrue(newTaxNumbers.get(1).equals(new Integer(5)));
        assertTrue(dvDocument.getNextSourceLineNumber().equals(new Integer(6)));
        assertEquals(3, dvDocument.getSourceAccountingLines().size());

        // validate debit of check total amount and accounting lines
        assertEquals("Check total not debited correctly", new KualiDecimal(82.6), dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match check total", new KualiDecimal(82.6), dvDocument.getSourceTotal());
        SpringContext.getBean(DisbursementVoucherTaxService.class).clearNRATaxLines(dvDocument);
    }

    /**
     * Tests correct calculation of gross up amounts
     */
    public void testGrossUp() {
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setReferenceFinancialDocumentNumber(null);
        dvDocument.getDvNonResidentAlienTax().setPostalCountryCode("USA");
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxGrossUpCode(true);

        runGrossUpTest(new KualiDecimal(100), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(50.55), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(10), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(12.82), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(12.83), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(12.84), new KualiDecimal(14), new KualiDecimal(3.4));
        runGrossUpTest(new KualiDecimal(8456234.23), new KualiDecimal(14), new KualiDecimal(3.4));
    }


    private void runGrossUpTest(KualiDecimal checkAmount, KualiDecimal federalTax, KualiDecimal stateTax) {
        dvDocument.setDisbVchrCheckTotalAmount(checkAmount);
        dvDocument.getSourceAccountingLine(0).setAmount(checkAmount);
        dvDocument.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(federalTax);
        dvDocument.getDvNonResidentAlienTax().setStateIncomeTaxPercent(stateTax);

        SpringContext.getBean(DisbursementVoucherTaxService.class).processNonResidentAlienTax(dvDocument);
        List newTaxNumbers = SpringContext.getBean(DisbursementVoucherTaxService.class).getNRATaxLineNumbers(dvDocument.getDvNonResidentAlienTax().getFinancialDocumentAccountingLineText());
        assertEquals( "Number of new tax lines is incorrect.", 3, newTaxNumbers.size() );
        assertEquals("Check total does not match original amount", checkAmount, dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match original amount", checkAmount, dvDocument.getSourceTotal());
        SpringContext.getBean(DisbursementVoucherTaxService.class).clearNRATaxLines(dvDocument);
    }
}
