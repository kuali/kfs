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
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.web.struts.TicklersReportLookupForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the Ticklers lookup.
 */
@ConfigureContext(session = khuntley)
public class TicklersReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static final String CHART_OF_ACCOUNTS_CODE = "BL";
    private static final String ORGANIZATION_CODE = "SRS";

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TicklersReportLookupableHelperServiceImplTest.class);

    private Person user;
    private RoleService roleService;
    private TicklersReportLookupableHelperServiceImpl ticklersReportLookupableHelperServiceImpl;
    private TicklersReportLookupForm ticklersReportLookupForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ticklersReportLookupableHelperServiceImpl = new TicklersReportLookupableHelperServiceImpl();
        ticklersReportLookupableHelperServiceImpl.setBusinessObjectClass(TicklersReport.class);
        ticklersReportLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        ticklersReportLookupForm = new TicklersReportLookupForm();

        fieldValues = new LinkedHashMap();

        Map<String, String> qualification = new HashMap<String, String>(3);
        qualification.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, CHART_OF_ACCOUNTS_CODE);
        qualification.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, ORGANIZATION_CODE);

        roleService = KimApiServiceLocator.getRoleService();
        user = GlobalVariables.getUserSession().getPerson();
        roleService.assignPrincipalToRole(user.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        ticklersReportLookupableHelperServiceImpl = null;
        ticklersReportLookupForm = null;
        fieldValues = null;

        Map<String, String> qualification = new HashMap<String, String>(3);
        qualification.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, CHART_OF_ACCOUNTS_CODE);
        qualification.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, ORGANIZATION_CODE);
        Person user = GlobalVariables.getUserSession().getPerson();
        roleService.removePrincipalFromRole(user.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);
        roleService = null;
        user = null;
    }

    /**
     * This method tests the performLookup method of TicklersReportLookupableHelperServiceImpl.
     */
    public void testPerformLookup() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        Collection resultTable = new ArrayList<String>();
        // To create a basic invoice with test data

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        // To add data for OrganizationOptions as fixture.
        OrganizationOptions organizationOptions = new OrganizationOptions();

        organizationOptions.setChartOfAccountsCode(CHART_OF_ACCOUNTS_CODE);
        organizationOptions.setOrganizationCode(ORGANIZATION_CODE);
        organizationOptions.setProcessingChartOfAccountCode(CHART_OF_ACCOUNTS_CODE);
        organizationOptions.setProcessingOrganizationCode(ORGANIZATION_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(organizationOptions);

        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, CHART_OF_ACCOUNTS_CODE, ORGANIZATION_CODE);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentService.saveDocument(cgInvoice);

        // to Add events
        Event event = new Event();
        event.setDocumentNumber(cgInvoice.getDocumentNumber());
        event.setInvoiceNumber(cgInvoice.getDocumentNumber());
        event.setActivityCode("TEST");
        event.setFollowupInd(true);
        event.setEventRouteStatus(KewApiConstants.ROUTE_HEADER_PROCESSED_CD);
        event.setUser(user);
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());
        event.setFollowupDate(today);
        event.setInvoiceDocument(cgInvoice);
        SpringContext.getBean(BusinessObjectService.class).save(event);

        cgInvoice.getEvents().add(event);
        documentService.saveDocument(cgInvoice);

        ticklersReportLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(ticklersReportLookupableHelperServiceImpl.performLookup(ticklersReportLookupForm, resultTable, true).size() > 0);
    }
}
