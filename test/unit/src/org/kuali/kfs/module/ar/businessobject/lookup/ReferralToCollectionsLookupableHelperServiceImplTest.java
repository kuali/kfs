/*
 * Copyright 2012 The Kuali Foundation.
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

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsInvoiceDocumentServiceImpl;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;

/**
 * This class tests the Referral To Collections lookup.
 */
@ConfigureContext(session = wklykins)
public class ReferralToCollectionsLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferralToCollectionsLookupableHelperServiceImplTest.class);

    private ReferralToCollectionsLookupableHelperServiceImpl referralToCollectionsLookupableHelperServiceImpl;
    private ReferralToCollectionsReportLookupableHelperServiceImpl referralToCollectionsReportLookupableHelperServiceImpl;
    private ContractsGrantsInvoiceDocumentServiceImpl contractsGrantsInvoiceDocumentServiceImpl;
    private MultipleValueLookupForm referralToCollectionsLookupForm;
    private Map fieldValues;

    private static final String AGENCY_NUMBER = "11505";
    String customerNumber = "ABB2";
    String customerName = "WOODS CORPORATION";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contractsGrantsInvoiceDocumentServiceImpl = new ContractsGrantsInvoiceDocumentServiceImpl();
        contractsGrantsInvoiceDocumentServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setContractsGrantsInvoiceDocumentDao(SpringContext.getBean(ContractsGrantsInvoiceDocumentDao.class));

        referralToCollectionsLookupableHelperServiceImpl = new ReferralToCollectionsLookupableHelperServiceImpl();
        referralToCollectionsLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentServiceImpl);
        referralToCollectionsLookupableHelperServiceImpl.setContractsGrantsReportHelperService(SpringContext.getBean(ContractsGrantsReportHelperService.class));
        referralToCollectionsLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        referralToCollectionsLookupableHelperServiceImpl.setBusinessObjectClass(ReferralToCollectionsLookupResult.class);
        referralToCollectionsLookupableHelperServiceImpl.setAccountService(SpringContext.getBean(AccountService.class));
        referralToCollectionsLookupableHelperServiceImpl.setReferralToCollectionsService(SpringContext.getBean(ReferralToCollectionsService.class));

        referralToCollectionsReportLookupableHelperServiceImpl = new ReferralToCollectionsReportLookupableHelperServiceImpl();
        referralToCollectionsReportLookupableHelperServiceImpl.setPersonService(SpringContext.getBean(PersonService.class));

        referralToCollectionsLookupForm = new MultipleValueLookupForm();
        fieldValues = new LinkedHashMap();
        fieldValues.put("agencyNumber", AGENCY_NUMBER);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        referralToCollectionsLookupableHelperServiceImpl = null;
        referralToCollectionsLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of ReferralToCollectionsLookupableHelperServiceImpl.
     */
    public void testPerformLookup() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        Collection resultTable = new ArrayList<String>();
        referralToCollectionsLookupForm.setFieldsForLookup(fieldValues);

        // To create a basic invoice with test data
        String coaCode = "BL";
        String orgCode = "SRS";
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        // To add data for OrganizationOptions as fixture
        OrganizationOptions organizationOptions = new OrganizationOptions();

        organizationOptions.setChartOfAccountsCode(coaCode);
        organizationOptions.setOrganizationCode(orgCode);
        organizationOptions.setProcessingChartOfAccountCode(coaCode);
        organizationOptions.setProcessingOrganizationCode(orgCode);
        SpringContext.getBean(BusinessObjectService.class).save(organizationOptions);

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        assertTrue(referralToCollectionsLookupableHelperServiceImpl.performLookup(referralToCollectionsLookupForm, resultTable, true).size() > 0);
    }

    /**
     * Tests the GetInvoiceDocumentsForReferralToCollectionsLookup() method of service class.
     */
    public void testGetInvoiceDocumentsForReferralToCollectionsLookup() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        Map<String, String> fieldValues = new LinkedHashMap<String, String>();
        fieldValues.put("agencyNumber", "11505");

        // To create a basic invoice with test data
        String coaCode = "BL";
        String orgCode = "SRS";
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        // To add data for OrganizationOptions as fixture.
        OrganizationOptions organizationOptions = new OrganizationOptions();

        organizationOptions.setChartOfAccountsCode(coaCode);
        organizationOptions.setOrganizationCode(orgCode);
        organizationOptions.setProcessingChartOfAccountCode(coaCode);
        organizationOptions.setProcessingOrganizationCode(orgCode);
        SpringContext.getBean(BusinessObjectService.class).save(organizationOptions);

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        assertTrue(referralToCollectionsLookupableHelperServiceImpl.getInvoiceDocumentsForReferralToCollectionsLookup(fieldValues).size() > 0);
    }

    public void testFilterRecordsForCollectorCanViewDoc() throws WorkflowException {
        String collector = "3421900658"; // no name qualification

        Collection<ReferralToCollectionsDocument> sourceCollectionDocuments = new ArrayList<>();
        ReferralToCollectionsDocument document = new ReferralToCollectionsDocument();
        document.setCustomerName("MILESTONE KUALI TESTING");
        sourceCollectionDocuments.add(document);

        referralToCollectionsReportLookupableHelperServiceImpl.filterRecordsForCollector(collector, sourceCollectionDocuments);
        assertTrue("collection should still contain doc", sourceCollectionDocuments.size() == 1);

    }

    public void testFilterRecordsForCollectorCannotViewDoc() throws WorkflowException {
        String collector = "5740007891"; // L name qualification

        Collection<ReferralToCollectionsDocument> sourceCollectionDocuments = new ArrayList<>();
        ReferralToCollectionsDocument document = new ReferralToCollectionsDocument();
        document.setCustomerName("MILESTONE KUALI TESTING");
        sourceCollectionDocuments.add(document);

        referralToCollectionsReportLookupableHelperServiceImpl.filterRecordsForCollector(collector, sourceCollectionDocuments);
        assertTrue("collection should NOT still contain doc", sourceCollectionDocuments.size() == 0);

    }
}
