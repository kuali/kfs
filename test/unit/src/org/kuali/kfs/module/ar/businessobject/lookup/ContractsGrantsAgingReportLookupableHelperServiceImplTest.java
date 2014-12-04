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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsAgingReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.CustomerAgingReportService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.web.struts.ContractsGrantsAgingReportForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ErrorMessage;

/**
 * This class tests the ContractsGrantsAgingReport lookup
 */
@ConfigureContext(session = wklykins)
public class ContractsGrantsAgingReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportLookupableHelperServiceImplTest.class);

    private ContractsGrantsAgingReportLookupableHelperServiceImpl contractsGrantsAgingReportLookupableHelperServiceImpl;
    private ContractsGrantsAgingReportForm contractsGrantsAgingReportForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // setting up document
        String chartCode = "BL";
        String orgCode = "UGCS";
        String customerNumber = "ABB2";
        String customerName = "WOODS CORPORATION";

        // To create a basic invoice with test data
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, chartCode, orgCode, errorMessages);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        cgInvoice.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
        cgInvoice.getAccountsReceivableDocumentHeader().setDocumentHeader(cgInvoice.getDocumentHeader());

        cgInvoice.setBillingDate(new java.sql.Date(new Date().getTime()));
        cgInvoice.getInvoiceGeneralDetail().setAward(award);
        cgInvoice.setOpenInvoiceIndicator(true);
        cgInvoice.setCustomerName(customerName);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }

        documentService.saveDocument(cgInvoice);

        contractsGrantsAgingReportLookupableHelperServiceImpl = new ContractsGrantsAgingReportLookupableHelperServiceImpl();
        contractsGrantsAgingReportLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.setBusinessObjectClass(ContractsAndGrantsAgingReport.class);
        contractsGrantsAgingReportLookupableHelperServiceImpl.setDateTimeService(SpringContext.getBean(DateTimeService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.setContractsGrantsAgingReportService(SpringContext.getBean(ContractsGrantsAgingReportService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.setCustomerAgingReportService(SpringContext.getBean(CustomerAgingReportService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.setCustomerCreditMemoDocumentService(SpringContext.getBean(CustomerCreditMemoDocumentService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.setModuleService(SpringContext.getBean(KualiModuleService.class));
        contractsGrantsAgingReportLookupableHelperServiceImpl.afterPropertiesSet();
        fieldValues = new LinkedHashMap();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        fieldValues.put("backLocation", null);
        fieldValues.put("reportRunDate", dateFormat.format(new Date()));
        fieldValues.put("docFormKey", null);
        fieldValues.put("businessObjectClassName", ContractsAndGrantsAgingReport.class.getName());

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingReportLookupableHelperServiceImplTest#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;
        // run search
        KualiDecimal test0to30total = new KualiDecimal("0.00");
        KualiDecimal test31to60total = new KualiDecimal("0.00");
        KualiDecimal test61to90total = new KualiDecimal("0.00");
        KualiDecimal test91toSYSPRtotal = new KualiDecimal("0.00");
        KualiDecimal testSYSPRplus1orMoretotal = new KualiDecimal("0.00");
        assertNotNull("search results not null", displayList = contractsGrantsAgingReportLookupableHelperServiceImpl.getSearchResults(fieldValues));


        // add all 0to30 totals
        for (Object aDisplayList : displayList) {
            ContractsAndGrantsAgingReport detail = (ContractsAndGrantsAgingReport) aDisplayList;
            test0to30total = test0to30total.add(detail.getUnpaidBalance0to30());
            test31to60total = test31to60total.add(detail.getUnpaidBalance31to60());
            test61to90total = test61to90total.add(detail.getUnpaidBalance61to90());
            test91toSYSPRtotal = test91toSYSPRtotal.add(detail.getUnpaidBalance91toSYSPR());
            testSYSPRplus1orMoretotal = testSYSPRplus1orMoretotal.add(detail.getUnpaidBalanceSYSPRplus1orMore());
        }
        assertEquals(contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal0to30().toString(), test0to30total.toString());
        assertEquals(contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal31to60().toString(), test31to60total.toString());
        assertEquals(contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal61to90().toString(), test61to90total.toString());
        LOG.info("\n\n\n\n***************************************************************************************\n" + "\n\t\t testtotal0to30 = " + contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal0to30().toString() + "\t\t\t\t\tactualtotal0to30 = " + test0to30total.toString() + "\n\t\t testtotal31to60 = " + contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal31to60().toString() + "\t\t\t\t\t\tactualtotal31to60 = " + test31to60total.toString() + "\n\t\t testtotal61to90 = " + contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal61to90().toString() + "\t\t\t\t\t\tactualtotal61to90 = " + test61to90total.toString() + "\n\t\t testtotal91toSYSPR = " + contractsGrantsAgingReportLookupableHelperServiceImpl.getTotal91toSYSPR().toString() + "\t\t\t\t\tactualtotal91toSYSPR = " + test91toSYSPRtotal.toString() + "\n\t\t testtotalSYSPRplus1orMore = " + contractsGrantsAgingReportLookupableHelperServiceImpl.getTotalSYSPRplus1orMore().toString()
                + "\t\t\tactualtotalSYSPRplus1orMore = " + testSYSPRplus1orMoretotal.toString() + "\n\n***************************************************************************************\n\n");


    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingReportLookupableHelperServiceImplTest#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)}
     * .
     */
    public void testPerformLookupLookupFormCollectionBoolean() {

        contractsGrantsAgingReportForm = new ContractsGrantsAgingReportForm();
        contractsGrantsAgingReportForm.setFieldsForLookup(fieldValues);

        Collection resultTable = new ArrayList<String>();
        assertNotNull("lookup list not null", contractsGrantsAgingReportLookupableHelperServiceImpl.performLookup(contractsGrantsAgingReportForm, resultTable, false));
    }

}
