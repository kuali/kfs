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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsInvoiceDocumentServiceImpl;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardInvoiceAccountFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.CustomerAddressFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAddressDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceDetailAccountObjectCodeFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceSuspensionCategoryFixture;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.kfs.module.ar.service.impl.ContractsGrantsInvoiceCreateDocumentServiceImpl;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.fixture.LetterOfCreditFundFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the ContractsGrantsInvoiceDocumentService
 */
@ConfigureContext(session = wklykins)
public class ContractsGrantsInvoiceDocumentServiceTest extends KualiTestBase {

    ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    ContractsGrantsInvoiceDocumentServiceImpl contractsGrantsInvoiceDocumentServiceImpl = new ContractsGrantsInvoiceDocumentServiceImpl();
    ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentServiceImpl = new ContractsGrantsInvoiceCreateDocumentServiceImpl();

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        contractsGrantsInvoiceDocumentServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setObjectCodeService(SpringContext.getBean(ObjectCodeService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        contractsGrantsInvoiceDocumentServiceImpl.setOptionsService(SpringContext.getBean(OptionsService.class));

        contractsGrantsInvoiceCreateDocumentServiceImpl.setAwardAccountObjectCodeTotalBilledDao(SpringContext.getBean(AwardAccountObjectCodeTotalBilledDao.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentServiceImpl);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setCostCategoryService(SpringContext.getBean(CostCategoryService.class));
        contractsGrantsInvoiceCreateDocumentServiceImpl.setOptionsService(SpringContext.getBean(OptionsService.class));
        super.setUp();
    }

    /**
     * Tests the prorateBill() method of service.
     */
    public void testProrateBill() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);

        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_2 = ContractsGrantsInvoiceDetailFixture.INV_DTL2.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        KualiDecimal value1 = new KualiDecimal(5);
        KualiDecimal value2 = new KualiDecimal(0);
        contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).setExpenditures(value1);
        contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(1).setExpenditures(value2);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // setup various invoice detail collections on invoice document
        contractsGrantsInvoiceCreateDocumentServiceImpl.generateValuesForCategories(contractsGrantsInvoiceDocument.getDocumentNumber(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes(), new HashMap<String, KualiDecimal>(), new ArrayList<AwardAccountObjectCodeTotalBilled>());

        contractsGrantsInvoiceDocumentService.prorateBill(contractsGrantsInvoiceDocument);

        assertEquals(value1, contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).getExpenditures());
        assertEquals(value2, contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(1).getExpenditures());

        // change the award total, it should now prorate
        contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().setAwardTotal(new KualiDecimal(4));

        contractsGrantsInvoiceDocumentService.prorateBill(contractsGrantsInvoiceDocument);

        assertEquals(new KualiDecimal(4.00), contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).getExpenditures());
        assertEquals(new KualiDecimal(0), contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(1).getExpenditures());
    }

    /**
     * Tests the updateInvoiceDetailTotalDirectCost() method of service.
     */
    public void testUpdateInvoiceDetailTotalDirectCost() {
        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_2 = ContractsGrantsInvoiceDetailFixture.INV_DTL3.createInvoiceDetail();
        KualiDecimal value1 = new KualiDecimal(2.23);
        KualiDecimal value2 = new KualiDecimal(5.43);
        invoiceDetail_1.setExpenditures(value1);
        invoiceDetail_2.setExpenditures(value2);
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);

        KualiDecimal expectedResult = new KualiDecimal(7.66);
        assertTrue(expectedResult.compareTo(contractsGrantsInvoiceDocumentServiceImpl.getInvoiceDetailExpenditureSum(invoiceDetails)) == 0);
    }

    /**
     * Tests the recalculateAccountDetails() method of service.
     */
    public void testRecalculateAccountDetails() {
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();

        invoiceAccountDetail_1.setExpenditureAmount(new KualiDecimal(4.50));
        invoiceAccountDetail_2.setExpenditureAmount(new KualiDecimal(5.50));

        List<InvoiceAccountDetail> invoiceAccountDetails = new ArrayList<InvoiceAccountDetail>();
        invoiceAccountDetails.add(invoiceAccountDetail_1);
        invoiceAccountDetails.add(invoiceAccountDetail_2);

        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        KualiDecimal value1 = (new KualiDecimal(3.01));
        KualiDecimal value2 = (new KualiDecimal(2.02));

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);

        contractsGrantsInvoiceDocumentServiceImpl.recalculateAccountDetails(invoiceAccountDetails, invoiceDetailAccountObjectCodes);

        assert (invoiceAccountDetails.get(0).getExpenditureAmount().compareTo(value1) == 0);
        assert (invoiceAccountDetails.get(1).getExpenditureAmount().compareTo(value2) == 0);
    }

    /**
     * Tests the recalculateNewTotalBilled() method of service.
     */
    public void testRecalculateNewTotalBilled() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);

        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        KualiDecimal value1 = new KualiDecimal(5.10);
        KualiDecimal value2 = new KualiDecimal(6.50);

        // set InvoiceDetails
        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_2 = ContractsGrantsInvoiceDetailFixture.INV_DTL3.createInvoiceDetail();

        invoiceDetail_1.setExpenditures(value1);
        invoiceDetail_2.setExpenditures(value2);

        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        // set InvoiceAccountDetails
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // setup various invoice detail collections on invoice document
        contractsGrantsInvoiceCreateDocumentServiceImpl.generateValuesForCategories(contractsGrantsInvoiceDocument.getDocumentNumber(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes(), new HashMap<String, KualiDecimal>(), new ArrayList<AwardAccountObjectCodeTotalBilled>());

        // set InvoiceDetailAccountObjectCodes
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);

        KualiDecimal originalTotalBilledValue = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled();

        contractsGrantsInvoiceDocumentServiceImpl.recalculateNewTotalBilled(contractsGrantsInvoiceDocument);

        // assert newTotalBilled hasn't changed
        assert (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().compareTo((originalTotalBilledValue)) == 0);

        // making values in account detail different. This simulates that the user have changed
        // the current expenditure amount and will cause recalucation.
        value1 = new KualiDecimal(10.22);
        value2 = new KualiDecimal(8.44);

        invoiceDetails.get(0).setExpenditures(value1);
        invoiceDetails.get(1).setExpenditures(value1);

        contractsGrantsInvoiceDocumentServiceImpl.recalculateNewTotalBilled(contractsGrantsInvoiceDocument);

        // assert new value
        assert (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().compareTo(new KualiDecimal(18.66)) == 0);
    }

    /**
     * Tests updateSuspensionCategoriesOnDocument() method.
     */
    public void testUpdateSuspensionCategoriesOnDocument() {

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD2.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceSuspensionCategory invoiceSuspensionCategory_1 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR1.createInvoiceSuspensionCategory();
        InvoiceSuspensionCategory invoiceSuspensionCategory_2 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR2.createInvoiceSuspensionCategory();
        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = new ArrayList<>();
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_1);
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_2);
        contractsGrantsInvoiceDocument.setInvoiceSuspensionCategories(invoiceSuspensionCategories);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(5, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());

        // update them again, should be the same results
        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(5, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    /**
     * Tests updateSuspensionCategoriesOnDocument() method on Correction document.
     */
    public void testUpdateSuspensionCategoriesOnCorrectionDocument() {

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        contractsGrantsInvoiceDocument.getFinancialSystemDocumentHeader().setFinancialDocumentInErrorNumber("12345");

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD2.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());

        // update them again, should be the same results
        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    /**
     * Tests updateSuspensionCategoriesOnDocument() method.
     */
    public void testUpdateSuspensionCategoriesOnDocumentValid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        InvoiceSuspensionCategory invoiceSuspensionCategory_1 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR3.createInvoiceSuspensionCategory();
        InvoiceSuspensionCategory invoiceSuspensionCategory_2 = InvoiceSuspensionCategoryFixture.INV_SUSPEN_CTGR4.createInvoiceSuspensionCategory();
        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = new ArrayList<>();
        invoiceSuspensionCategory_1.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
        invoiceSuspensionCategory_2.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_1);
        invoiceSuspensionCategories.add(invoiceSuspensionCategory_2);
        contractsGrantsInvoiceDocument.setInvoiceSuspensionCategories(invoiceSuspensionCategories);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory1Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL7.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("1", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory2Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD6.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("2", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory3Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();
        ((Award)award).setAdditionalFormsRequiredIndicator(true);
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("3", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory4InvalidNoAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("4", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory4InvalidUSAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = CustomerAddressFixture.CUSTOMER_ADDRESS_INVALID_US_PRIMARY1.createCustomerAddress();
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("4", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory4ValidUSAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = CustomerAddressFixture.CUSTOMER_ADDRESS_VALID_US_PRIMARY1.createCustomerAddress();
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory4InvalidInternationalAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = CustomerAddressFixture.CUSTOMER_ADDRESS_INVALID_FOREIGN_PRIMARY1.createCustomerAddress();
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("4", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory4ValidInternationalAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = CustomerAddressFixture.CUSTOMER_ADDRESS_VALID_FOREIGN_PRIMARY1.createCustomerAddress();
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory5InvalidNoAltAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail_1 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        InvoiceAddressDetail invoiceAddressDetail_2 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL2.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail_1.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail_1);
        invoiceAddressDetails.add(invoiceAddressDetail_2);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("5", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory5InvalidUSAltAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail_1 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        InvoiceAddressDetail invoiceAddressDetail_2 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL2.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail_1.setCustomerAddress(customerAddress);
        CustomerAddress customerAddress2 = CustomerAddressFixture.CUSTOMER_ADDRESS_INVALID_US_ALTERNATE1.createCustomerAddress();
        invoiceAddressDetail_2.setCustomerAddress(customerAddress2);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail_1);
        invoiceAddressDetails.add(invoiceAddressDetail_2);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("5", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory5ValidUSAltAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail_1 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        InvoiceAddressDetail invoiceAddressDetail_2 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL2.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail_1.setCustomerAddress(customerAddress);
        CustomerAddress customerAddress2 = CustomerAddressFixture.CUSTOMER_ADDRESS_VALID_US_ALTERNATE1.createCustomerAddress();
        invoiceAddressDetail_2.setCustomerAddress(customerAddress2);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail_1);
        invoiceAddressDetails.add(invoiceAddressDetail_2);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory5InvalidForeignAltAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail_1 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        InvoiceAddressDetail invoiceAddressDetail_2 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL2.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail_1.setCustomerAddress(customerAddress);
        CustomerAddress customerAddress2 = CustomerAddressFixture.CUSTOMER_ADDRESS_INVALID_FOREIGN_ALTERNATE1.createCustomerAddress();
        invoiceAddressDetail_2.setCustomerAddress(customerAddress2);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail_1);
        invoiceAddressDetails.add(invoiceAddressDetail_2);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("5", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory5ValidForeignAltAddress() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail_1 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        InvoiceAddressDetail invoiceAddressDetail_2 = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL2.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail_1.setCustomerAddress(customerAddress);
        CustomerAddress customerAddress2 = CustomerAddressFixture.CUSTOMER_ADDRESS_VALID_FOREIGN_ALTERNATE1.createCustomerAddress();
        invoiceAddressDetail_2.setCustomerAddress(customerAddress2);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail_1);
        invoiceAddressDetails.add(invoiceAddressDetail_2);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(0, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory6Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD8.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("6", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory7Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL1.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL2.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("7", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory8Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD10.createAward();
        ((Award)award).setLetterOfCreditFund(LetterOfCreditFundFixture.CG_LOCF.createLetterOfCreditFund());
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL7.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("8", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory9Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_3.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("9", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory10Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();
        ((Award)award).setExcludedFromInvoicing(true);

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("10", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory11Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD7.createAward();

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("11", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory12Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();

        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_4.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");

        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);

        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD3.createInvoiceDetailAccountObjectCode();

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        contractsGrantsInvoiceDocument.setInvoiceDetailAccountObjectCodes(invoiceDetailAccountObjectCodes);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("12", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    public void testUpdateSuspensionCategoriesOnDocumentCategory13Invalid() {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        assertNotNull(contractsGrantsInvoiceDocument);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD4.createAward();
        ((Award)award).setStopWorkIndicator(true);
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL5.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        invoiceGeneralDetail.setAward(award);
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(invoiceGeneralDetail);

        InvoiceAddressDetail invoiceAddressDetail = InvoiceAddressDetailFixture.INV_ADDRESS_DETAIL1.createInvoiceAddressDetail();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerAddress = customerAddressService.getPrimaryAddress(agency.getCustomerNumber());
        invoiceAddressDetail.setCustomerAddress(customerAddress);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        invoiceAddressDetails.add(invoiceAddressDetail);
        contractsGrantsInvoiceDocument.setInvoiceAddressDetails(invoiceAddressDetails);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL7.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        assertEquals(1, contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size());
        assertEquals("13", contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().iterator().next().getSuspensionCategoryCode());
    }

    /**
     * This method is intended to test if the source accounting lines are created properly for every type of GLPE and invoicing
     * option in the award.
     */
    public void testCreateSourceAccountingLines() {

        String coaCode = "BL";
        String orgCode = "SRS";


        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        contractsGrantsInvoiceDocument.setBillByChartOfAccountCode(coaCode);
        contractsGrantsInvoiceDocument.setBilledByOrganizationCode(orgCode);

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(coaCode);
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgCode);

        contractsGrantsInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);


        InvoiceGeneralDetail inv_Gnrl_Dtl_1 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        InvoiceGeneralDetail inv_Gnrl_Dtl_Mlstn = InvoiceGeneralDetailFixture.INV_GNRL_DTL1.createInvoiceGeneralDetail();
        InvoiceGeneralDetail inv_Gnrl_Dtl_Bill = InvoiceGeneralDetailFixture.INV_GNRL_DTL4.createInvoiceGeneralDetail();
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        ContractsGrantsInvoiceDetail invoiceDetail_1 = ContractsGrantsInvoiceDetailFixture.INV_DTL1.createInvoiceDetail();
        ContractsGrantsInvoiceDetail invoiceDetail_2 = ContractsGrantsInvoiceDetailFixture.INV_DTL3.createInvoiceDetail();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<ContractsGrantsInvoiceDetail>();
        invoiceDetails.add(invoiceDetail_1);
        invoiceDetails.add(invoiceDetail_2);
        contractsGrantsInvoiceDocument.setInvoiceDetails(invoiceDetails);

        KualiDecimal value1 = new KualiDecimal(5);

        KualiDecimal value2 = new KualiDecimal(0);
        contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).setExpenditures(value1);
        contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(1).setExpenditures(value2);

        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL4.createInvoiceAccountDetail();
        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);


        List<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_1 = InvoiceMilestoneFixture.INV_MLSTN_1.createInvoiceMilestone();
        invoiceMilestones.add(invMilestone_1);
        contractsGrantsInvoiceDocument.setInvoiceMilestones(invoiceMilestones);


        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_1.createInvoiceBill();
        invoiceBills.add(invBill_1);
        contractsGrantsInvoiceDocument.setInvoiceBills(invoiceBills);

        // To set Organization Accounting default
        Map<String, Object> criteria = new HashMap<String, Object>();

        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        OrganizationAccountingDefault organizationAccountingDefault = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

        if (ObjectUtils.isNull(organizationAccountingDefault)) {
            organizationAccountingDefault = new OrganizationAccountingDefault();
            organizationAccountingDefault.setChartOfAccountsCode("BL");
            organizationAccountingDefault.setOrganizationCode("SRS");
            organizationAccountingDefault.setUniversityFiscalYear(currentYear);
        }
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode("5000");
        organizationAccountingDefault.setDefaultPaymentFinancialObjectCode("8118");
        SpringContext.getBean(BusinessObjectService.class).save(organizationAccountingDefault);


        // To test for all combinations of GLPE and Award Invoicing options
        // GLPE is 1.
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        // To check if the source accounting lines are created as expected.
        CustomerInvoiceDetail customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE_2.createCustomerInvoiceDetail();

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);
        inv_Gnrl_Dtl_Mlstn.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);
        inv_Gnrl_Dtl_Bill.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_CCA.createAward());
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // To check if the source accounting lines are created as expected.
        customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE_2.createCustomerInvoiceDetail();

        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);
        inv_Gnrl_Dtl_Mlstn.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);
        inv_Gnrl_Dtl_Bill.setAward(ARAwardFixture.CG_AWARD_INV_AWARD.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);

        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward());
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        inv_Gnrl_Dtl_1.setAward(ARAwardFixture.CG_AWARD_INV_CCA.createAward());
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // Use the same customer Invoice detail as Subfund
        customerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE_2.createCustomerInvoiceDetail();


        ContractsGrantsAwardInvoiceAccountInformation awdInvAcct = ARAwardInvoiceAccountFixture.AWD_INV_ACCT_1.createAwardInvoiceAccount();


        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);

        // 1. Invoicing by Award
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        ContractsAndGrantsBillingAward awardInvAward = ARAwardFixture.CG_AWARD_INV_AWARD.createAward();
        awardInvAward.getActiveAwardInvoiceAccounts().clear();
        awardInvAward.getActiveAwardInvoiceAccounts().add(awdInvAcct);
        inv_Gnrl_Dtl_1.setAward(awardInvAward);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1a. Award with Milestones - use the same values from Monthly
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Mlstn);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 1b. Award with Bills - use the same values from Monthly
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_Bill);

        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);


        // 2. Invoicing by Account
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        awardInvAward = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        awardInvAward.getActiveAwardInvoiceAccounts().clear();
        awardInvAward.getActiveAwardInvoiceAccounts().add(awdInvAcct);
        inv_Gnrl_Dtl_1.setAward(awardInvAward);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

        // 3. Invoicing by Contract Control Account. For this case the account number might be different so setting a different
        // invoice account detail here.
        contractsGrantsInvoiceDocument.setInvoiceGeneralDetail(inv_Gnrl_Dtl_1);
        awardInvAward = ARAwardFixture.CG_AWARD_INV_CCA.createAward();
        awardInvAward.getActiveAwardInvoiceAccounts().clear();
        awardInvAward.getActiveAwardInvoiceAccounts().add(awdInvAcct);
        inv_Gnrl_Dtl_1.setAward(awardInvAward);
        accountDetails.clear();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument.setAccountDetails(accountDetails);
        compareSourceAccountingLines(contractsGrantsInvoiceDocument, customerInvoiceDetail);

    }

    /**
     * This method compares the source accounting lines.
     *
     * @param contractsGrantsInvoiceDocument The invoice document object.
     * @param customerInvoiceDetail The customer invoice detail object.
     */
    public void compareSourceAccountingLines(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, CustomerInvoiceDetail customerInvoiceDetail) {

        // To clear source accounting lines every time to check all combinations.
        contractsGrantsInvoiceDocument.getSourceAccountingLines().clear();

        try {
            contractsGrantsInvoiceDocumentService.createSourceAccountingLines(contractsGrantsInvoiceDocument);
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("Call to createSourceAccountingLinesAndGLPEs failed with WorkflowException.");
        }

        CustomerInvoiceDetail invoiceDetail = (CustomerInvoiceDetail) contractsGrantsInvoiceDocument.getSourceAccountingLine(0);

        assertEquals(invoiceDetail.getAccountNumber(), customerInvoiceDetail.getAccountNumber());
        assertEquals(invoiceDetail.getChartOfAccountsCode(), customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals(invoiceDetail.getFinancialObjectCode(), customerInvoiceDetail.getFinancialObjectCode());
        assertEquals(invoiceDetail.getAmount(), customerInvoiceDetail.getAmount());
    }

}
