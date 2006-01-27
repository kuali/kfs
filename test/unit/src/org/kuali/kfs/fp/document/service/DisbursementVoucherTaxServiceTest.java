/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherNonResidentAlienTax;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the DisbursementVoucherTax service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherTaxServiceTest extends KualiTestBaseWithSpring {
    private DisbursementVoucherTaxService disbursementVoucherTaxService;
    private DisbursementVoucherDocument dvDocument;

    protected void setUp() throws Exception {
        super.setUp();
        this.disbursementVoucherTaxService = SpringServiceLocator.getDisbursementVoucherTaxService();
        dvDocument = new DisbursementVoucherDocument();
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
     * @throws Exception
     */
    public void testValidateNRA() throws Exception {
        // check tax is not created for attributes that it should not be generated for
        dvDocument.setDisbVchrCheckTotalAmount(new KualiDecimal(100));
        dvDocument.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(new KualiDecimal(0));
        dvDocument.getDvNonResidentAlienTax().setStateIncomeTaxPercent(new KualiDecimal(0));

        // should not be generated for non-reportable
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("N");
        List newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getErrorMap().size() == 1);
        GlobalVariables.getErrorMap().clear();

        // should not be generated for foreign source
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(true);
        newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getErrorMap().size() == 1);
        GlobalVariables.getErrorMap().clear();

        // should not be generated for treatly exempt
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(true);
        newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getErrorMap().size() == 1);
        GlobalVariables.getErrorMap().clear();

        // should not be generated for doc reference is given
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setFinancialDocumentReferenceNbr("foo");
        newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getErrorMap().size() == 1);
        GlobalVariables.getErrorMap().clear();

        // should not be generated if check amount is 0
        dvDocument.setDisbVchrCheckTotalAmount(new KualiDecimal(0));
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setFinancialDocumentReferenceNbr("");
        newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.isEmpty());
        assertTrue(GlobalVariables.getErrorMap().size() == 1);
        GlobalVariables.getErrorMap().clear();

    }

    /**
     * Test generation and clearing of nra tax lines.
     *  
     */
    public void testGenerateNRATaxLines() {
        KualiDecimal orginalCheckAmount = new KualiDecimal(100);
        dvDocument.setDisbVchrCheckTotalAmount(new KualiDecimal(100));
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setFinancialDocumentReferenceNbr("");
        dvDocument.getDvNonResidentAlienTax().setFederalIncomeTaxPercent(new KualiDecimal(14));
        dvDocument.getDvNonResidentAlienTax().setStateIncomeTaxPercent(new KualiDecimal(3.4));
        dvDocument.getDvNonResidentAlienTax().setPostalCountryCode("USA");

        List newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.size() == 2);
        assertTrue(newTaxNumbers.get(0).equals(new Integer(2)));
        assertTrue(newTaxNumbers.get(1).equals(new Integer(3)));
        assertTrue(dvDocument.getNextSourceLineNumber().equals(new Integer(4)));
        assertTrue(dvDocument.getSourceAccountingLines().size() == 3);

        // test clearning
        disbursementVoucherTaxService.clearNRATaxLines(dvDocument, newTaxNumbers);
        assertEquals(1,dvDocument.getSourceAccountingLines().size());
        assertEquals("Check total credited correctly",new KualiDecimal(100),dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match check total", new KualiDecimal(100), dvDocument.getSourceTotal());     
        
        
        newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.size() == 2);
        assertTrue(newTaxNumbers.get(0).equals(new Integer(4)));
        assertTrue(newTaxNumbers.get(1).equals(new Integer(5)));
        assertTrue(dvDocument.getNextSourceLineNumber().equals(new Integer(6)));
        assertEquals(3,dvDocument.getSourceAccountingLines().size());

        // validate debit of check total amount and accounting lines
        assertEquals("Check total not debited correctly",new KualiDecimal(82.6),dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match check total", new KualiDecimal(82.6), dvDocument.getSourceTotal());
        disbursementVoucherTaxService.clearNRATaxLines(dvDocument, newTaxNumbers);
    }
    
    /**
     * Tests correct calculation of gross up amounts
     */
    public void testGrossUp() {
        dvDocument.getDvNonResidentAlienTax().setIncomeClassCode("F");
        dvDocument.getDvNonResidentAlienTax().setForeignSourceIncomeCode(false);
        dvDocument.getDvNonResidentAlienTax().setIncomeTaxTreatyExemptCode(false);
        dvDocument.getDvNonResidentAlienTax().setFinancialDocumentReferenceNbr("");
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
        
        List newTaxNumbers = disbursementVoucherTaxService.processNonResidentAlienTax(dvDocument);
        assertTrue(newTaxNumbers.size() == 3);
        assertEquals("Check total does not match original amount",checkAmount,dvDocument.getDisbVchrCheckTotalAmount());
        assertEquals("Source total does not match original amount", checkAmount, dvDocument.getSourceTotal()); 
        disbursementVoucherTaxService.clearNRATaxLines(dvDocument, newTaxNumbers);
    }
}