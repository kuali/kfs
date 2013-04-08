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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext
public class ExpenseImportByTravelerServiceTest extends KualiTestBase {

    private ExpenseImportByTravelerService expenseImportByTravelerService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sas;
    private ParameterService parameterService;

    private final static String EMPLOYEE_ID = "123456789";
    private final static String CUSTOMER_NUM = "ABC1234";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        expenseImportByTravelerService = SpringContext.getBean(ExpenseImportByTravelerService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
    }


    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#validateAccountingInfo(TEMProfile, AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateAccountingInfo() {
        AgencyStagingData agency = createAgencyStagingData();
        TEMProfile profile = createTemProfile();
        // parameter is defaulted to 6000, but there are no valid combos that
        // will work with 6000. Set it to 5000 for testing purposes.
        Parameter param = parameterService.getParameter(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_AIR_OBJECT_CODE);
        Parameter.Builder builder = Parameter.Builder.create(param);
        builder.setValue("5000");
        parameterService.updateParameter(builder.build());

        // success case
        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));
        assertTrue(agency.getTripAccountingInformation().size()==1);
        TripAccountingInformation accountingInfo = agency.getTripAccountingInformation().get(0);
        assertTrue(StringUtils.equals(accountingInfo.getTripAccountNumber(), profile.getDefaultAccount()));
        assertTrue(StringUtils.equals(accountingInfo.getTripSubAccountNumber(), profile.getDefaultSubAccount()));
        assertTrue(StringUtils.equals(accountingInfo.getProjectCode(), profile.getDefaultProjectCode()));

        // test with an invalid account
        profile.setDefaultAccount("1234567");
        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT));

        // test with an invalid sub-account
        profile = createTemProfile();
        profile.setDefaultSubAccount("9");
        agency.setTripAccountingInformation(new ArrayList<TripAccountingInformation>());
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT));

        // test with an invalid project code
        profile = createTemProfile();
        profile.setDefaultProjectCode("x");
        agency.setTripAccountingInformation(new ArrayList<TripAccountingInformation>());
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT));

        // test with an invalid object code
        profile = createTemProfile();
        builder.setValue("1");
        parameterService.updateParameter(builder.build());
        agency.setTripAccountingInformation(new ArrayList<TripAccountingInformation>());
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT));

        // not possible to test at the moment
//        profile = createTemProfile();
//        agency = expenseImportByTravelerService.validateAccountingInfo(profile, agency);
//        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT));
    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#validateTraveler(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateTraveler() {
        TEMProfile employee = createTemProfile();
        employee.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        employee.setEmployeeId(EMPLOYEE_ID);
        businessObjectService.save(employee);

        TEMProfile customer = createTemProfile();
        customer.setTravelerTypeCode(TemConstants.NONEMP_TRAVELER_TYP_CD);
        customer.setCustomerNumber(CUSTOMER_NUM);
        businessObjectService.save(customer);

        AgencyStagingData agency = createAgencyStagingData();
        agency.setTravelerId("987654321");
        TEMProfile invalidProfile = expenseImportByTravelerService.validateTraveler(agency);
        assertTrue(ObjectUtils.isNull(invalidProfile));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_TRAVELER));

        agency.setTravelerId(EMPLOYEE_ID);
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        TEMProfile empProfile = expenseImportByTravelerService.validateTraveler(agency);
        assertTrue(empProfile.getEmployeeId().equals(agency.getTravelerId()));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

        agency = createAgencyStagingData();
        agency.setTravelerId(CUSTOMER_NUM);
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        TEMProfile custProfile = expenseImportByTravelerService.validateTraveler(agency);
        assertTrue(custProfile.getCustomerNumber().equals(agency.getTravelerId()));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#isDuplicate(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testIsDuplicate() {
        AgencyStagingData dbData = createAgencyStagingData();
        businessObjectService.save(dbData);

        // duplicate entry test
        AgencyStagingData importData = createAgencyStagingData();
        assertTrue(expenseImportByTravelerService.isDuplicate(importData));

        // not a duplicate
        importData.setTravelerId(CUSTOMER_NUM);
        assertFalse(expenseImportByTravelerService.isDuplicate(importData));
    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#areMandatoryFieldsPresent(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testAreMandatoryFieldsPresent() {
        AgencyStagingData agency = createAgencyStagingData();
        // all fields present
        assertTrue(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));

        // missing fields, testing in reverse order of the if block to hit all possible checks
        agency.setTripInvoiceNumber(null);
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setTripExpenseAmount("");
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setTransactionPostingDate(null);
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setAgency(null);
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setAirTicketNumber("");
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setTravelerId(null);
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
        agency.setCreditCardOrAgencyCode(null);
        assertFalse(expenseImportByTravelerService.areMandatoryFieldsPresent(agency));
    }

    protected TEMProfile createTemProfile() {
        TEMProfile profile = new TEMProfile();
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

        // mandatory fields
        agency.setImportBy(ExpenseImportTypes.IMPORT_BY_TRAVELLER);
        agency.setCreditCardOrAgencyCode("1234");
        agency.setTravelerName("Traveler Bob");
        agency.setTravelerId(EMPLOYEE_ID);
        agency.setAirTicketNumber("12345678");
        agency.setAgency("agency name");
        agency.setTransactionPostingDate(dateTimeService.getCurrentSqlDate());
        agency.setTripExpenseAmount(new KualiDecimal(123.45));
        agency.setTripInvoiceNumber("invoice12345");

        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        return agency;
    }
}
