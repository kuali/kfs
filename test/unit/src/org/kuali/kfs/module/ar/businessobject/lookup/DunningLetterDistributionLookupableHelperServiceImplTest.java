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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.DunningCampaignFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterDistributionFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterTemplateFixture;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.web.struts.DunningLetterDistributionLookupForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the Referral To Collections lookup.
 */
@ConfigureContext(session = khuntley)
public class DunningLetterDistributionLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionLookupableHelperServiceImplTest.class);

    private DunningLetterDistributionLookupableHelperServiceImpl dunningLetterDistributionLookupableHelperServiceImpl;
    private DunningLetterDistributionLookupForm dunningLetterDistributionLookupForm;
    private Map fieldValues;

    private static final String invoiceDocumentNumber = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dunningLetterDistributionLookupableHelperServiceImpl = new DunningLetterDistributionLookupableHelperServiceImpl();
        dunningLetterDistributionLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        dunningLetterDistributionLookupableHelperServiceImpl.setBusinessObjectClass(DunningLetterDistributionLookupResult.class);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        dunningLetterDistributionLookupForm = new DunningLetterDistributionLookupForm();
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

        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);

        // To create Dunning Campaign and Dunning LEtter Distribtuions and templates.

        DunningCampaign dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        DunningLetterDistribution dunningLetterDistribution = DunningLetterDistributionFixture.AR_DLD1.createDunningLetterDistribution();
        dunningLetterDistribution.setActiveIndicator(true);
        dunningLetterDistribution.setSendDunningLetterIndicator(true);
        DunningLetterTemplate dunningLetterTemplate = DunningLetterTemplateFixture.CG_DLTS1.createDunningLetterTemplate();
        SpringContext.getBean(BusinessObjectService.class).save(dunningLetterTemplate);
        dunningLetterDistribution.setDunningLetterTemplate(dunningLetterTemplate.getLetterTemplateCode());
        dunningCampaign.getDunningLetterDistributions().add(dunningLetterDistribution);
        SpringContext.getBean(BusinessObjectService.class).save(dunningCampaign);

        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setDunningCampaignFromFixture((Award) award);
        cgInvoice.setAward(award);
        cgInvoice.setAge(10);
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());

        cgInvoice.setBillingDate(today);

        documentService.saveDocument(cgInvoice);
        fieldValues = new LinkedHashMap();
        fieldValues.put("invoiceDocumentNumber", cgInvoice.getDocumentNumber());

        Map<String, String> qualification = new HashMap<String, String>(3);
        qualification.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, cgInvoice.getBillByChartOfAccountCode());
        qualification.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, cgInvoice.getBilledByOrganizationCode());

        org.kuali.rice.kim.api.role.RoleService roleService = KimApiServiceLocator.getRoleService();
        Person user = GlobalVariables.getUserSession().getPerson();
        roleService.assignPrincipalToRole(user.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        dunningLetterDistributionLookupableHelperServiceImpl = null;
        dunningLetterDistributionLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of ReferralToCollectionsLookupableHelperServiceImpl.
     */
    public void testPerformLookup() {
        Collection resultTable = new ArrayList<String>();
        dunningLetterDistributionLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(dunningLetterDistributionLookupableHelperServiceImpl.performLookup(dunningLetterDistributionLookupForm, resultTable, true).size() > 0);
    }
}
