/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.batch.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.List;

import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.businessobject.defaultvalue.NextAgencyStagingDataIdFinder;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ErrorMessage;

@ConfigureContext(session = khuntley)
public class ExpenseImportByTripServiceTest extends KualiTestBase {

    private ExpenseImportByTripService expenseImportByTripService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sas;
    private ParameterService parameterService;
    private DocumentService documentService;

    private static final String TRIP_ID = "12345678";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        expenseImportByTripService = SpringContext.getBean(ExpenseImportByTripService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        documentService = SpringContext.getBean(DocumentService.class);
    }


    /**
     *
     * This method tests {@link ExpenseImportByTripService#validateAccountingInfo(TemProfile, AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false, session = khuntley)
    public void testValidateAccountingInfo() {
        AgencyStagingData agency = createAgencyStagingData();
        TravelAuthorizationDocument travelAuth = createTA();
        // success case
        expenseImportByTripService.validateAccountingInfo(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

        TripAccountingInformation accountingInfo = agency.getTripAccountingInformation().get(0);

        // test with an invalid account
        accountingInfo.setTripAccountNumber("");
        expenseImportByTripService.validateAccountingInfo(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT));

        // test with an invalid sub-account
        accountingInfo.setTripAccountNumber("1031400");
        accountingInfo.setTripSubAccountNumber("ZZ");
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        expenseImportByTripService.validateAccountingInfo(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT));

        // test with an invalid project code
        accountingInfo.setTripSubAccountNumber("");
        accountingInfo.setProjectCode("COOL");
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        expenseImportByTripService.validateAccountingInfo(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT));

    }

    /**
     *
     * This method tests {@link ExpenseImportByTripService#validateTripId(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateTripId() throws Exception {

        // invalid trip id
        AgencyStagingData agency = createAgencyStagingData();
        expenseImportByTripService.validateTripId(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID));
    }


    /**
     *
     * This method tests {@link ExpenseImportByTripService#isDuplicate(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testIsDuplicate() {
        AgencyStagingData dbData = createAgencyStagingData();
        businessObjectService.save(dbData);

        // duplicate entry test
        AgencyStagingData importData = createAgencyStagingData();
        List<ErrorMessage> errorMessages = expenseImportByTripService.validateDuplicateData(importData);
        assertTrue(!errorMessages.isEmpty());

        // not a duplicate
        importData.setTripId("987654321");
        errorMessages = expenseImportByTripService.validateDuplicateData(importData);
        assertTrue(errorMessages.isEmpty());
    }

    /**
     *
     * This method tests {@link ExpenseImportByTripService#areMandatoryFieldsPresent(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testAreMandatoryFieldsPresent() {
        AgencyStagingData agency = createAgencyStagingData();
        // all fields present
        List<ErrorMessage> errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertTrue(errorMessages.isEmpty());

        // missing fields, testing in reverse order of the if block to hit all possible checks
        agency.setAirTicketNumber("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setTripInvoiceNumber("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setTripExpenseAmount("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.getTripAccountingInformation().get(0).setTripAccountNumber("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setTransactionPostingDate(null);
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setAlternateTripId(null);
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setTripId("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());

        agency.setCreditCardOrAgencyCode("");
        errorMessages = expenseImportByTripService.validateMandatoryFieldsPresent(agency);
        assertFalse(errorMessages.isEmpty());
    }

    protected TemProfile createTemProfile() {
        TemProfile profile = new TemProfile();
        Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
        profile.setProfileId(newProfileId);
        profile.getTemProfileAddress().setProfileId(newProfileId);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }

    protected AgencyStagingData createAgencyStagingData() {
        AgencyStagingData agency = new AgencyStagingData();

        NextAgencyStagingDataIdFinder idFinder = new NextAgencyStagingDataIdFinder();

        agency.setId(Integer.valueOf(idFinder.getValue()));
        agency.setImportBy(ExpenseImportTypes.IMPORT_BY_TRIP);
        agency.setTravelerName("Traveler Bob");
        agency.setCreditCardOrAgencyCode("1234");
        agency.setTripId(TRIP_ID);
        agency.setAlternateTripId("12345678");
        agency.setTransactionPostingDate(dateTimeService.getCurrentSqlDate());
        agency.setTripExpenseAmount(new KualiDecimal(123.45));
        agency.setTripInvoiceNumber("invoice12345");
        agency.setAirTicketNumber("12345678");
        //agency.

        TripAccountingInformation account = new TripAccountingInformation();
        account.setTripChartCode("BL");
        account.setTripAccountNumber("1031400");
        agency.addTripAccountingInformation(account);

        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        return agency;
    }

    protected TravelAuthorizationDocument createTA() {
        TravelAuthorizationDocument ta = null;
        try {
            ta = (TravelAuthorizationDocument)documentService.getNewDocument(TravelAuthorizationDocument.class);
            ta.getDocumentHeader().setDocumentDescription("testing");
            ta.setTravelDocumentIdentifier(TRIP_ID);
            ta.setTripTypeCode("IN");
            ta.setApplicationDocumentStatus("In Process");

            TemSourceAccountingLine line = new TemSourceAccountingLine();
            line.setAccountNumber("1031400");
            line.setSubAccountNumber("ADV");
            line.setFinancialObjectCode("6000");
            line.setChartOfAccountsCode("BL");
            line.setCardType(TemConstants.ADVANCE);
            line.setAmount(new KualiDecimal(50));
            ta.addSourceAccountingLine(line);

            TemProfile temProfile = new TemProfile();
            temProfile.setPrincipalName("abeal");
            temProfile.setProfileId(8);
            temProfile.setTravelerTypeCode("EMP");
            ta.setTemProfile(temProfile);

            documentService.saveDocument(ta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ta;
    }
}
