/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.businessobject.Award;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsAgingOpenInvoicesReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.web.struts.ContractsGrantsAgingOpenInvoicesReportForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the ContractsGrantsAgingOpenInvoicesReport lookup.
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImplTest.class);

    private ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl agingOpenInvoicesReportLookupableHelperServiceImpl;
    private ContractsGrantsAgingOpenInvoicesReportForm agingOpenInvoicesReportForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // setting up document
        String chartCode = "BL";
        String orgCode = "SRS";
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

        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, chartCode, orgCode);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        cgInvoice.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);

        cgInvoice.getAccountsReceivableDocumentHeader().setDocumentHeader(cgInvoice.getDocumentHeader());

        cgInvoice.setBillingDate(new java.sql.Date(new Date().getTime()));
        cgInvoice.setAward(award);
        cgInvoice.setOpenInvoiceIndicator(true);

        cgInvoice.setCustomerName(customerName);
        documentService.saveDocument(cgInvoice);

        agingOpenInvoicesReportLookupableHelperServiceImpl = new ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl();
        agingOpenInvoicesReportLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        agingOpenInvoicesReportLookupableHelperServiceImpl.setBusinessObjectClass(ContractsGrantsAgingOpenInvoicesReport.class);
        fieldValues = new LinkedHashMap();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        fieldValues.put("backLocation", null);
        fieldValues.put("reportRunDate", dateFormat.format(new Date()));
        fieldValues.put(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_ORGANIZATION_CODE, orgCode);
        fieldValues.put(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_CHART_CODE, chartCode);
        fieldValues.put("docFormKey", null);
        fieldValues.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        fieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, customerName);
        fieldValues.put("businessObjectClassName", ContractsGrantsAgingOpenInvoicesReport.class.getName());

        Map<String,String[]> parameters = new HashMap<String, String[]>();
        parameters.put(KFSPropertyConstants.CUSTOMER_NUMBER, new String[] { customerNumber });
        parameters.put(KFSPropertyConstants.CUSTOMER_NAME, new String[] { customerName });
        agingOpenInvoicesReportLookupableHelperServiceImpl.setParameters(parameters);

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;
        assertNotNull("search results not null", displayList = agingOpenInvoicesReportLookupableHelperServiceImpl.getSearchResults(fieldValues));
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)}
     * .
     */
    public void testPerformLookupLookupFormCollectionBoolean() {
        agingOpenInvoicesReportForm = new ContractsGrantsAgingOpenInvoicesReportForm();
        agingOpenInvoicesReportForm.setFieldsForLookup(fieldValues);

        Collection resultTable = new ArrayList<String>();
        assertNotNull("lookup list not null", agingOpenInvoicesReportLookupableHelperServiceImpl.performLookup(agingOpenInvoicesReportForm, resultTable, true));
    }

}
