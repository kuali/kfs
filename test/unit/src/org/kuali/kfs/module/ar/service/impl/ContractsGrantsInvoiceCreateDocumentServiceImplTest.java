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
package org.kuali.kfs.module.ar.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardOrganizationFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.module.ar.fixture.OrganizationAccountingDefaultFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceImplTest extends KualiTestBase {

    private ContractsGrantsInvoiceCreateDocumentServiceImpl contractsGrantsInvoiceCreateDocumentServiceImpl;
    protected ConfigurationService configurationService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected AccountingPeriodService accountingPeriodService;
    protected AccountService accountService;
    protected ContractsGrantsBillingAwardVerificationService contractsGrantsBillingAwardVerificationService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        configurationService = SpringContext.getBean(ConfigurationService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);
        financialSystemDocumentService = SpringContext.getBean(FinancialSystemDocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        accountService = SpringContext.getBean(AccountService.class);
        contractsGrantsBillingAwardVerificationService = SpringContext.getBean(ContractsGrantsBillingAwardVerificationService.class);

        contractsGrantsInvoiceCreateDocumentServiceImpl = new ContractsGrantsInvoiceCreateDocumentServiceImpl();
        contractsGrantsInvoiceCreateDocumentServiceImpl.setConfigurationService(configurationService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setContractsGrantsInvoiceDocumentService(contractsGrantsInvoiceDocumentService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setVerifyBillingFrequencyService(verifyBillingFrequencyService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setFinancialSystemDocumentService(financialSystemDocumentService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setBusinessObjectService(businessObjectService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setAccountService(accountService);
        contractsGrantsInvoiceCreateDocumentServiceImpl.setContractsGrantsBillingAwardVerificationService(contractsGrantsBillingAwardVerificationService);
    }


    public void testPerformAwardValidationStartDateMissing() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        ((Award)award).setAwardBeginningDate(null);
        awards.add(award);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(award).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_START_DATE_MISSING_ERROR)));
    }

    public void testPerformAwardValidationInvalidBillingFrequency() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();
        awards.add(award);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(award).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_BILLING_FREQUENCY_MISSING_ERROR)));
    }

    public void testPerformAwardValidationInvalidBillingPeriod() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_INVALID_DATES.createAward();
        ((Award)award).setAwardBeginningDate(new Date(DateTimeUtils.currentTimeMillis()));
        awards.add(award);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(award).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INVALID_BILLING_PERIOD)));
    }

    public void testPerformAwardValidationValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should be empty.", invalidGroup.size() == 0);
        assertTrue("qualifiedAwards should  contain one award.", qualifiedAwards.size() == 1);
        assertTrue("qualifiedAwards should contain our initial award.", qualifiedAwards.get(0).equals(awards.get(0)));
    }

    public void testPerformAwardValidationAwardInvoicingSuspended() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setExcludedFromInvoicing(true);
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING)));
    }

    public void testPerformAwardValidationInactiveAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setActive(false);
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR)));
    }

    public void testPerformAwardValidationSuspendedAndInactiveAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setActive(false);
        ((Award)awards.get(0)).setExcludedFromInvoicing(true);
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING)));
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(1).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR)));
    }

    public void testPerformAwardValidationTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards.get(0)).setActive(false);
        ((Award)awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 2);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our first award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR)));
        assertTrue("invalidGroup should contain our second award with the error message we're expecting.", invalidGroup.get(awards.get(1)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING)));
    }

    public void testPerformAwardValidationOneValidOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setExcludedFromInvoicing(true);
        awards.addAll(awards2);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertEquals("invalidGroup should contain one award.", 1, invalidGroup.size());
        assertEquals("qualifiedAwards should contain one award.", 1, qualifiedAwards.size());
        assertTrue("qualifiedAwards should contain our initial award.", qualifiedAwards.get(0).equals(awards.get(0)));
        assertTrue("invalidGroup should contain our second award with the error message we're expecting.", invalidGroup.get(awards.get(1)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING)));
    }

    public void testPerformAwardValidationMissingAwardInvoicingOption() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setInvoicingOptionCode(null);
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR)));
    }

    public void testPerformAwardValidationInvalidPredeterminedBillingFrequency() {
      List<ContractsAndGrantsBillingAward> awards = setupAwards();
      Award award = ((Award)awards.get(0));
      award.setBillingFrequencyCode(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
      AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
      awardAccount_2.refreshReferenceObject("account");
      award.getAwardAccounts().add(awardAccount_2);

      Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
      List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

      contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

      assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
      assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
      assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR)));
    }

    public void testPerformAwardValidationInvalidMilestoneBillingFrequency() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setBillingFrequencyCode(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE);
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.refreshReferenceObject("account");
        award.getAwardAccounts().add(awardAccount_2);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR)));
      }

    public void testPerformAwardValidationNoActiveAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).getAwardAccounts().clear();
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_NO_ACTIVE_ACCOUNTS_ASSIGNED_ERROR)));
    }

    public void testPerformAwardValidationExpiredAccount() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = ((Award)awards.get(0)).getActiveAwardAccounts();
        for (ContractsAndGrantsBillingAwardAccount awardAccount: awardAccounts) {
            ((AwardAccount)awardAccount).getAccount().setAccountExpirationDate(new Date(DateTime.now().minusDays(1).toDate().getTime()));
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR));

        for (ContractsAndGrantsBillingAwardAccount awardAccount: awardAccounts) {
            errorMessage.append(" (expired account: " + ((AwardAccount)awardAccount).getAccount().getAccountNumber() + " expiration date " + ((AwardAccount)awardAccount).getAccount().getAccountExpirationDate() + ") ");
        }

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(errorMessage.toString()));
    }

    public void testPerformAwardValidationExpiredAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.refreshReferenceObject("account");
        awardAccounts.add(awardAccount_2);

        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.getAccount().setAccountExpirationDate(new Date(DateTime.now().minusDays(1).toDate().getTime()));
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR));

        for (ContractsAndGrantsBillingAwardAccount awardAccount: awardAccounts) {
            errorMessage.append(" (expired account: " + ((AwardAccount)awardAccount).getAccount().getAccountNumber() + " expiration date " + ((AwardAccount)awardAccount).getAccount().getAccountExpirationDate() + ") ");
        }

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(errorMessage.toString()));
    }

    public void testPerformAwardValidationAwardFinalInvoiceAlreadyBilled() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();

        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.setFinalBilledIndicator(true);
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR)));
    }

    public void testPerformAwardValidationAwardFinalInvoiceAlreadyBilledTwoAccounts() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.refreshReferenceObject("account");
        awardAccounts.add(awardAccount_2);

        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.setFinalBilledIndicator(true);
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR)));
    }

    public void testPerformAwardValidationAwardFinalInvoicePartiallyBilled() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();
        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.setFinalBilledIndicator(true);
        }

        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.refreshReferenceObject("account");
        awardAccounts.add(awardAccount_2);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR)));
    }

    public void testPerformAwardValidationNoValidMilestonesToInvoice() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setBillingFrequencyCode(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES)));
    }

    public void testPerformAwardValidationNoValidBillsToInvoice() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setBillingFrequencyCode(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_BILLS)));
    }

    public void testPerformAwardValidationAgencyHasNoMatchingCustomer() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).getAgency().setCustomerNumber(null);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD)));
    }

    @ConfigureContext(session = wklykins)
    public void testPerformAwardValidationNoValidAwardAccounts() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS)));
    }

    @ConfigureContext(session = wklykins)
    public void testPerformAwardValidAwardAccountsValidationMilestone() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setBillingFrequencyCode(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES)));
    }

    @ConfigureContext(session = wklykins)
    public void testPerformAwardValidAwardAccountsValidationPredeterminedBilling() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setBillingFrequencyCode(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_BILLS)));
    }

    public void testPerformAwardValidationNoContractControlAccountsByCCA() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setInvoicingOptionCode(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();
        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.getAccount().setContractControlAccount(null);
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertEquals("invalidGroup should contain our initial award with the error message we're expecting.", configurationService.getPropertyValueAsString(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT).replace("{0}", awards.get(0).getInvoicingOptionDescription()), invalidGroup.get(awards.get(0)).get(0));
    }

    public void testPerformAwardValidationNoContractControlAccountsByAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setInvoicingOptionCode(ArConstants.INV_AWARD);

        List<AwardAccount> awardAccounts = ((Award)awards.get(0)).getAwardAccounts();
        for (AwardAccount awardAccount: awardAccounts) {
            awardAccount.getAccount().setContractControlAccount(null);
        }

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertEquals("invalidGroup should contain our initial award with the error message we're expecting.", configurationService.getPropertyValueAsString(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT).replace("{0}", awards.get(0).getInvoicingOptionDescription()), invalidGroup.get(awards.get(0)).get(0));
    }

    public void testPerformAwardValidationMultipleContractControlAccountsByAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setInvoicingOptionCode(ArConstants.INV_AWARD);

        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_3.createAwardAccount();
        awardAccount_2.refreshReferenceObject("account");
        ((Award)awards.get(0)).getAwardAccounts().add(awardAccount_2);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.AwardConstants.ERROR_MULTIPLE_CTRL_ACCT).replace("{0}", awards.get(0).getInvoicingOptionDescription())));
    }

    public void testPerformAwardValidationNoSysInfo() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.getAwardOrganizations().clear();
        AwardOrganization awardOrganization = ARAwardOrganizationFixture.AWD_ORG2.createAwardOrganization();
        award.getAwardOrganizations().add(awardOrganization);

        OrganizationAccountingDefault orgAcctDefault = OrganizationAccountingDefaultFixture.BASE_OAD.createOrganizationAccountingDefault();
        businessObjectService.save(orgAcctDefault);

        OrganizationOptions organizationOptions = new OrganizationOptions();
        organizationOptions.setChartOfAccountsCode(orgAcctDefault.getChartOfAccountsCode());
        organizationOptions.setOrganizationCode(orgAcctDefault.getOrganizationCode());
        organizationOptions.setProcessingChartOfAccountCode(orgAcctDefault.getChartOfAccountsCode());
        organizationOptions.setProcessingOrganizationCode(orgAcctDefault.getOrganizationCode());
        businessObjectService.save(organizationOptions);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP)));
    }

    public void testPerformAwardValidationNoOrgAccountingDefault() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        Award award = ((Award)awards.get(0));
        award.getAwardOrganizations().clear();
        AwardOrganization awardOrganization = ARAwardOrganizationFixture.AWD_ORG3.createAwardOrganization();
        award.getAwardOrganizations().add(awardOrganization);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP)));
    }

    public void testPerformAwardValidationNoSysInfoOrOrgAccountingDefault() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        Award award = ((Award)awards.get(0));
        award.getAwardOrganizations().clear();
        AwardOrganization awardOrganization = ARAwardOrganizationFixture.AWD_ORG4.createAwardOrganization();
        award.getAwardOrganizations().add(awardOrganization);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP)));
    }

    @ConfigureContext(session = wklykins)
    public void testPerformAwardValidationInvoicesInProgressMilestone() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setBillingFrequencyCode(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupMilestones(cgInvoice);
        documentService.saveDocument(cgInvoice);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS)));
    }

    @ConfigureContext(session = wklykins)
    public void testPerformAwardValidationInvoicesInProgressPredeterminedBilling() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setBillingFrequencyCode(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentServiceImpl.performAwardValidation(awards, invalidGroup, qualifiedAwards);

        assertTrue("invalidGroup should contain one award.", invalidGroup.size() == 1);
        assertTrue("qualifiedAwards should be empty.", qualifiedAwards.size() == 0);
        assertTrue("invalidGroup should contain our initial award with the error message we're expecting.", invalidGroup.get(awards.get(0)).get(0).equals(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS)));
    }

    protected List<ContractsAndGrantsBillingAward> setupAwards() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        awards.add(award);

        return awards;
    }

    protected void setupMilestones(ContractsGrantsInvoiceDocument document) {
        List<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_1 = InvoiceMilestoneFixture.INV_MLSTN_2.createInvoiceMilestone();
        invMilestone_1.setDocumentNumber(document.getDocumentNumber());

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(document.getInvoiceGeneralDetail().getAward(), currPeriod);
        Date invoiceDate = pair[1];
        Date completionDate = new Date(new DateTime(invoiceDate.getTime()).minusDays(1).toDate().getTime());

        invMilestone_1.setMilestoneActualCompletionDate(completionDate);

        businessObjectService.save(invMilestone_1);
        invoiceMilestones.add(invMilestone_1);
        document.setInvoiceMilestones(invoiceMilestones);

        Milestone milestone = new Milestone();
        milestone.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        milestone.setMilestoneNumber(invMilestone_1.getMilestoneNumber());
        milestone.setMilestoneIdentifier(invMilestone_1.getMilestoneIdentifier());
        milestone.setMilestoneDescription(invMilestone_1.getMilestoneDescription());
        milestone.setMilestoneAmount(invMilestone_1.getMilestoneAmount());
        milestone.setMilestoneActualCompletionDate(invMilestone_1.getMilestoneActualCompletionDate());
        milestone.setMilestoneExpectedCompletionDate(completionDate);
        milestone.setBilled(false);
        milestone.setAward(document.getInvoiceGeneralDetail().getAward());
        milestone.setActive(true);

        MilestoneSchedule milestoneSchedule = new MilestoneSchedule();
        milestoneSchedule.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        List<Milestone> milestones = new ArrayList<Milestone>();
        milestones.add(milestone);
        milestoneSchedule.setMilestones(milestones);
        businessObjectService.save(milestoneSchedule);
        businessObjectService.save(milestone);
    }

    protected void setupBills(ContractsGrantsInvoiceDocument document) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_2.createInvoiceBill();
        invBill_1.setDocumentNumber(document.getDocumentNumber());

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(document.getInvoiceGeneralDetail().getAward(), currPeriod);
        Date invoiceDate = pair[1];
        Date billDate = new Date(new DateTime(invoiceDate.getTime()).minusDays(1).toDate().getTime());

        invBill_1.setBillDate(billDate);

        businessObjectService.save(invBill_1);
        invoiceBills.add(invBill_1);
        document.setInvoiceBills(invoiceBills);

        Bill bill = new Bill();
        bill.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        bill.setBillNumber(invBill_1.getBillNumber());
        bill.setBillDescription(invBill_1.getBillDescription());
        bill.setBillIdentifier(invBill_1.getBillIdentifier());
        bill.setBillDate(invBill_1.getBillDate());
        bill.setEstimatedAmount(invBill_1.getEstimatedAmount());
        bill.setBilled(false);
        bill.setAward(document.getInvoiceGeneralDetail().getAward());
        bill.setActive(true);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(document.getInvoiceGeneralDetail().getProposalNumber());
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }

}
