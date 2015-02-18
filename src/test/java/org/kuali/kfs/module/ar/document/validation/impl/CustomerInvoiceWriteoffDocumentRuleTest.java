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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentTestUtil;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceWriteoffDocumentRuleTest extends KualiTestBase {
    
 public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceWriteoffDocumentRuleTest.class);
    
    private CustomerInvoiceWriteoffDocumentRule rule;
    private CustomerInvoiceWriteoffDocument document;
    private final static String VALID_CHART_OF_ACCOUNTS_CODE_FOR_PARM = "BL";
    private final static String INVALID_CHART_OF_ACCOUNTS_CODE_FOR_PARM = "XX";
    private final static String CHART_WRITEOFF_PARM_VALUE="BL=5105";
    private final static String ORG_ACCT_DEFAULT_CHART = "UA";
    private final static String ORG_ACCT_DEFAULT_ORG = "VPIT";
    private final static String ORG_ACCT_DEFAULT_WRITEOFF_ACCT = "1031400";
    private final static String ORG_ACCT_DEFAULT_WRITEOFF_CHART = "BL";
    private final static String ORG_ACCT_DEFAULT_WRITEOFF_OBJECT_CODE = "1500";
    
    @Override
    protected  void setUp() throws Exception {
        super.setUp();
        rule = new CustomerInvoiceWriteoffDocumentRule();
        document = new CustomerInvoiceWriteoffDocument();
    }
    
    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        super.tearDown();
    }
    
    /**
     * This method...
     */
    public void testDoesChartCodeHaveCorrespondingWriteoffObjectCode_Valid(){
        TestUtils.setSystemParameter(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_OBJECT_CODE_BY_CHART, CHART_WRITEOFF_PARM_VALUE);
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS_CODE_FOR_PARM);
        
        assertTrue(rule.doesChartCodeHaveCorrespondingWriteoffObjectCode(customerInvoiceDetail));
    }
    
    /**
     * This method...
     */
    public void testDoesChartCodeHaveCorrespondingWriteoffObjectCode_Invalid(){
        TestUtils.setSystemParameter(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_OBJECT_CODE_BY_CHART, CHART_WRITEOFF_PARM_VALUE);
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setChartOfAccountsCode(INVALID_CHART_OF_ACCOUNTS_CODE_FOR_PARM);
        
        assertFalse(rule.doesChartCodeHaveCorrespondingWriteoffObjectCode(customerInvoiceDetail));
    }    

    /**
     * This method...
     */
    public void testDoesOrganizationAccountingDefaultHaveWriteoffInformation_Valid() throws WorkflowException {
        OrganizationAccountingDefault organizationAccountingDefault = new OrganizationAccountingDefault();
        organizationAccountingDefault.setChartOfAccountsCode(ORG_ACCT_DEFAULT_CHART);
        organizationAccountingDefault.setOrganizationCode(ORG_ACCT_DEFAULT_ORG);
        organizationAccountingDefault.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        organizationAccountingDefault.setWriteoffAccountNumber(ORG_ACCT_DEFAULT_WRITEOFF_ACCT);
        organizationAccountingDefault.setWriteoffFinancialObjectCode(ORG_ACCT_DEFAULT_WRITEOFF_OBJECT_CODE);
        organizationAccountingDefault.setWriteoffChartOfAccountsCode(ORG_ACCT_DEFAULT_WRITEOFF_CHART);
        SpringContext.getBean(BusinessObjectService.class).save(organizationAccountingDefault);
        
        String customerInvoiceDocumentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = new CustomerInvoiceWriteoffDocument();
        customerInvoiceWriteoffDocument.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDocumentNumber);
        
        assertTrue(rule.doesOrganizationAccountingDefaultHaveWriteoffInformation(customerInvoiceWriteoffDocument));
    }
    
    /**
     * This method...
     */
    public void testDoesOrganizationAccountingDefaultHaveWriteoffInformation_Invalid() throws WorkflowException {
        
        String customerInvoiceDocumentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = new CustomerInvoiceWriteoffDocument();
        customerInvoiceWriteoffDocument.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDocumentNumber);

        //this should assert false because there isn't a org acct default row yet
        assertFalse(rule.doesOrganizationAccountingDefaultHaveWriteoffInformation(customerInvoiceWriteoffDocument));
        
        OrganizationAccountingDefault organizationAccountingDefault = new OrganizationAccountingDefault();
        organizationAccountingDefault.setChartOfAccountsCode(ORG_ACCT_DEFAULT_CHART);
        organizationAccountingDefault.setOrganizationCode(ORG_ACCT_DEFAULT_ORG);
        organizationAccountingDefault.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        SpringContext.getBean(BusinessObjectService.class).save(organizationAccountingDefault);
        
        //this should assert false because the org acct default doesn't have writeoff account, object code, or chart
        assertFalse(rule.doesOrganizationAccountingDefaultHaveWriteoffInformation(customerInvoiceWriteoffDocument));
    }
    
    /**
     * This method...
     */
    public void testDoesCustomerInvoiceDocumentHaveValidBalance_Valid() throws WorkflowException {
        
        String customerInvoiceDocumentNumber = CustomerInvoiceDocumentTestUtil.submitNewCustomerInvoiceDocument(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO,
                new CustomerInvoiceDetailFixture[]
                {CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE},
                null);
        
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = new CustomerInvoiceWriteoffDocument();
        customerInvoiceWriteoffDocument.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDocumentNumber);
        
        assertTrue(rule.doesCustomerInvoiceDocumentHaveValidBalance(customerInvoiceWriteoffDocument));        
    }
    
}

