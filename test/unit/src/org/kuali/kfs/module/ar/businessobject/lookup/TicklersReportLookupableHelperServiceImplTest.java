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
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.web.struts.TicklersReportLookupForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the Ticklers lookup.
 */
@ConfigureContext(session = wklykins)
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
        ticklersReportLookupableHelperServiceImpl.setCollectionActivityDocumentService(SpringContext.getBean(CollectionActivityDocumentService.class));
        ticklersReportLookupableHelperServiceImpl.setContractsGrantsReportHelperService(SpringContext.getBean(ContractsGrantsReportHelperService.class));
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

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, CHART_OF_ACCOUNTS_CODE, ORGANIZATION_CODE, errorMessages);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        // to Add events
        CollectionEvent event = new CollectionEvent();
        event.setDocumentNumber(cgInvoice.getDocumentNumber());
        event.setInvoiceNumber(cgInvoice.getDocumentNumber());
        event.setActivityCode("TEST");
        event.setFollowup(true);
        event.setUser(user);
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());
        event.setFollowupDate(today);
        event.setInvoiceDocument(cgInvoice);
        SpringContext.getBean(BusinessObjectService.class).save(event);

        cgInvoice.getCollectionEvents().add(event);
        documentService.saveDocument(cgInvoice);

        ticklersReportLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(ticklersReportLookupableHelperServiceImpl.performLookup(ticklersReportLookupForm, resultTable, true).size() > 0);
    }
}
