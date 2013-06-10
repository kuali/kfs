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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.businessobject.Award;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.document.CGInvoiceDocumentSetupTest;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.DunningCampaignFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterDistributionFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterTemplateFixture;
import org.kuali.kfs.module.ar.web.struts.DunningLetterDistributionOnDemandLookupForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the Referral To Collections lookup.
 */
@ConfigureContext(session = khuntley)
public class DunningLetterDistributionOnDemandLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionOnDemandLookupableHelperServiceImplTest.class);

    private DunningLetterDistributionOnDemandLookupableHelperServiceImpl dunningLetterDistributionOnDemandLookupableHelperServiceImpl;
    private DunningLetterDistributionOnDemandLookupForm dunningLetterDistributionOnDemandLookupForm;
    private Map fieldValues;
    
    private static final String invoiceDocumentNumber = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dunningLetterDistributionOnDemandLookupableHelperServiceImpl = new DunningLetterDistributionOnDemandLookupableHelperServiceImpl();
        dunningLetterDistributionOnDemandLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        dunningLetterDistributionOnDemandLookupableHelperServiceImpl.setBusinessObjectClass(DunningLetterDistributionOnDemandLookupResult.class);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        dunningLetterDistributionOnDemandLookupForm = new DunningLetterDistributionOnDemandLookupForm();
        //To create a basic invoice with test data
        
        String coaCode = "BL";
        String orgCode = "SRS";
        ContractsAndGrantsCGBAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsCGBAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsCGBAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);
        
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        
        //To create Dunning Campaign and Dunning LEtter Distribtuions and templates.
        DunningCampaign dunningCampaign = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(DunningCampaign.class, DunningCampaignFixture.AR_DUNC1);
        if(ObjectUtils.isNull(dunningCampaign)){
        dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        DunningLetterDistribution dunningLetterDistribution = DunningLetterDistributionFixture.AR_DLD1.createDunningLetterDistribution();
        DunningLetterTemplate dunningLetterTemplate = DunningLetterTemplateFixture.CG_DLTS1.createDunningLetterTemplate();
        dunningLetterDistribution.setDunningLetterTemplate(dunningLetterTemplate.getLetterTemplateCode());
        dunningCampaign.getDunningLetterDistributions().add(dunningLetterDistribution);
        SpringContext.getBean(BusinessObjectService.class).save(dunningCampaign);
        }
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setDunningCampaignFromFixture((Award)award);
        cgInvoice.setAward(award);
        documentService.saveDocument(cgInvoice);
        fieldValues = new LinkedHashMap();
        fieldValues.put("invoiceDocumentNumber", cgInvoice.getDocumentNumber());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        dunningLetterDistributionOnDemandLookupableHelperServiceImpl = null;
        dunningLetterDistributionOnDemandLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of ReferralToCollectionsLookupableHelperServiceImpl.
     */
    public void testPerformLookup() {
        Collection resultTable = new ArrayList<String>();
        dunningLetterDistributionOnDemandLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(dunningLetterDistributionOnDemandLookupableHelperServiceImpl.performLookup(dunningLetterDistributionOnDemandLookupForm, resultTable, true).size() > 0);
    }
}
