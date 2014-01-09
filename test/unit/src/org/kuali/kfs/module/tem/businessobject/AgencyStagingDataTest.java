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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.defaultvalue.NextAgencyStagingDataIdFinder;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

public class AgencyStagingDataTest extends KualiTestBase {

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    private NextAgencyStagingDataIdFinder idFinder;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        idFinder = new NextAgencyStagingDataIdFinder();
    }

    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception {

        Date date = dateTimeService.getCurrentSqlDate();
        Timestamp time = dateTimeService.getCurrentTimestamp();
        KualiDecimal amount = new KualiDecimal(1234.56);
        KualiDecimal maxAmount = new KualiDecimal(999999.99);
        // String of 100 chars
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec id egestas lorem. Integer tempor sed.";

        // the spec has 3 accounting information objects
        ArrayList<TripAccountingInformation> tripAccountingInformation = new ArrayList<TripAccountingInformation>();
        for (int i=0; i<3; i++) {
            TripAccountingInformation accountInfo = new TripAccountingInformation();
            accountInfo.setTripAccountNumber("1234567");
            accountInfo.setTripSubAccountNumber("12345");
            accountInfo.setObjectCode("1234");
            accountInfo.setSubObjectCode("123");
            accountInfo.setProjectCode("1234567890");
            accountInfo.setOrganizationReference("12345678");
            tripAccountingInformation.add(accountInfo);
        }

        AgencyStagingData agency = new AgencyStagingData();

        agency.setId(Integer.valueOf(idFinder.getValue()));

        agency.setErrorCode("errorcode");
        agency.setAgencyDataId(123456789);
        agency.setCreditCardOrAgencyCode("abcd");
        agency.setAgency("agency");
        agency.setOtherCompanyName("other company name");
        agency.setAgencyFileName("FL Name");
        agency.setMerchantName("merchant name");
        agency.setBillingCycleDate(date);
        agency.setTripId("abcdef123456");
        agency.setTripInvoiceNumber("invoice1234");
        agency.setTripTravelerTypeId("1");
        agency.setOtherAmount(amount);
        agency.setTravelerName("Traveler Name");
        agency.setTravelerId("1");
        agency.setTravelerNetworkId("123456789");
        agency.setTripExpenseAmount(maxAmount);
        agency.setAlternateTripId(null);
        agency.setTripArrangerName("Arranger Name");
        agency.setTripAccountingInformation(tripAccountingInformation);
        agency.setGroupObjectCode("Group Obj Code");
        agency.setDistributionCode("Code");
        agency.setTripDepartureDate(date);
        agency.setTripReturnDate(date);
        agency.setFareSaverCode("C");
        agency.setAirBookDate(date);
        agency.setAirCarrierCode("SWA");
        agency.setAirTicketNumber("air 1234");
        agency.setPnrNumber("PNR-1234567890");
        agency.setAirTicketClass("class 1");
        agency.setAirTransactionAmount(amount);
        agency.setAirBaseFareAmount(maxAmount);
        agency.setAirTaxAmount(amount);
        agency.setAirLowFareAmount(maxAmount);
        agency.setAirReasonCode("R");
        agency.setAirSegmentId(longText);
        agency.setAirDestinationCode("LIH");
        agency.setAirServiceFeeNumber("12345678901234567890");
        agency.setAirServiceFeeAmount(amount);
        agency.setTransactionUniqueId("unique#98765");
        agency.setLodgingItineraryNumber("L123456");
        agency.setLodgingPrepayDate(date);
        agency.setLodgingAmount(maxAmount);
        agency.setLodgingPrepayDaysNumber("365");
        agency.setLodgingPropertyName("St. Regis Princeville Resort");
        agency.setTripLodgingArrivalDate(date);
        agency.setLodgingDepartureDate(date);
        agency.setLodgingBookingDate(date);
        agency.setLodgingPropertyCityName("Princeville");
        agency.setLodgingPropertyStateCode("HI");
        agency.setLodgingCountryName("US");
        agency.setRentalCarItineraryNumber("rentalCar1234");
        agency.setRentalCarAmount(amount);
        agency.setRentalCarNumberOfDays("365");
        agency.setRentalCarCompanyName("Hertz");
        agency.setRentalCarOpenDate(date);
        agency.setRentalCarCloseDate(date);
        agency.setRentalCarFuelAmount(maxAmount);
        agency.setRentalCarAdditionalAmount(amount);
        agency.setRentalCarTaxAmount(maxAmount);
        agency.setRentalCarSurchargeAmount(amount);
        agency.setRentalCarGovernmentSurchargeAmount(maxAmount);
        agency.setRentalCarBillAmount(amount);
        agency.setRentalCarDetailText(longText);
        agency.setRegistrationCompanyName("Conference Co.");
        agency.setRegistrationAmount(maxAmount);
        agency.setTransactionPostingDate(date);
        agency.setObjectVerNumber("1");
        agency.setCreationTimestamp(time);
        agency.setProcessingTimestamp(time);
        agency.setMoveToHistoryIndicator(true);

        businessObjectService.save(agency);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", agency.getId());

        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, values);
        try {
            AgencyStagingData data = agencyDataList.get(0);
            assertTrue(agency.getErrorCode().equals(data.getErrorCode()));
            assertTrue(agency.getAgencyDataId().equals(data.getAgencyDataId()));
            assertTrue(agency.getCreditCardOrAgencyCode().equals(data.getCreditCardOrAgencyCode()));
            assertTrue(agency.getAgency().equals(data.getAgency()));
            assertTrue(agency.getOtherCompanyName().equals(data.getOtherCompanyName()));
            assertTrue(agency.getAgencyFileName().equals(data.getAgencyFileName()));
            assertTrue(agency.getMerchantName().equals(data.getMerchantName()));
            assertTrue(agency.getBillingCycleDate().equals(data.getBillingCycleDate()));
            assertTrue(agency.getTripId().equals(data.getTripId()));
            assertTrue(agency.getTripInvoiceNumber().equals(data.getTripInvoiceNumber()));
            assertTrue(agency.getTripTravelerTypeId().equals(data.getTripTravelerTypeId()));
            assertTrue(agency.getOtherAmount().equals(data.getOtherAmount()));
            assertTrue(agency.getTravelerName().equals(data.getTravelerName()));
            assertTrue(agency.getTravelerId().equals(data.getTravelerId()));
            assertTrue(agency.getTravelerNetworkId().equals(data.getTravelerNetworkId()));
            assertTrue(agency.getTripExpenseAmount().equals(data.getTripExpenseAmount()));
            assertTrue(agency.getAlternateTripId().equals(data.getAlternateTripId()));
            assertTrue(agency.getTripArrangerName().equals(data.getTripArrangerName()));
            ArrayList<TripAccountingInformation> accountingInfo = data.getTripAccountingInformation();
            assertTrue(agency.getTripAccountingInformation().equals(accountingInfo));
            for(TripAccountingInformation account : accountingInfo) {
                assertTrue(agency.getId().equals(account.getAgencyStagingDataId()));
            }
            assertTrue(agency.getGroupObjectCode().equals(data.getGroupObjectCode()));
            assertTrue(agency.getDistributionCode().equals(data.getDistributionCode()));
            assertTrue(agency.getTripDepartureDate().equals(data.getTripDepartureDate()));
            assertTrue(agency.getTripReturnDate().equals(data.getTripReturnDate()));
            assertTrue(agency.getFareSaverCode().equals(data.getFareSaverCode()));
            assertTrue(agency.getAirBookDate().equals(data.getAirBookDate()));
            assertTrue(agency.getAirCarrierCode().equals(data.getAirCarrierCode()));
            assertTrue(agency.getAirTicketNumber().equals(data.getAirTicketNumber()));
            assertTrue(agency.getPnrNumber().equals(data.getPnrNumber()));
            assertTrue(agency.getAirTicketClass().equals(data.getAirTicketClass()));
            assertTrue(agency.getAirTransactionAmount().equals(data.getAirTransactionAmount()));
            assertTrue(agency.getAirBaseFareAmount().equals(data.getAirBaseFareAmount()));
            assertTrue(agency.getAirTaxAmount().equals(data.getAirTaxAmount()));
            assertTrue(agency.getAirLowFareAmount().equals(data.getAirLowFareAmount()));
            assertTrue(agency.getAirReasonCode().equals(data.getAirReasonCode()));
            assertTrue(agency.getAirSegmentId().equals(data.getAirSegmentId()));
            assertTrue(agency.getAirDestinationCode().equals(data.getAirDestinationCode()));
            assertTrue(agency.getAirServiceFeeNumber().equals(data.getAirServiceFeeNumber()));
            assertTrue(agency.getAirServiceFeeAmount().equals(data.getAirServiceFeeAmount()));
            assertTrue(agency.getTransactionUniqueId().equals(data.getTransactionUniqueId()));
            assertTrue(agency.getLodgingItineraryNumber().equals(data.getLodgingItineraryNumber()));
            assertTrue(agency.getLodgingPrepayDate().equals(data.getLodgingPrepayDate()));
            assertTrue(agency.getLodgingAmount().equals(data.getLodgingAmount()));
            assertTrue(agency.getLodgingPrepayDaysNumber().equals(data.getLodgingPrepayDaysNumber()));
            assertTrue(agency.getLodgingPropertyName().equals(data.getLodgingPropertyName()));
            assertTrue(agency.getTripLodgingArrivalDate().equals(data.getTripLodgingArrivalDate()));
            assertTrue(agency.getLodgingDepartureDate().equals(data.getLodgingDepartureDate()));
            assertTrue(agency.getLodgingBookingDate().equals(data.getLodgingBookingDate()));
            assertTrue(agency.getLodgingPropertyCityName().equals(data.getLodgingPropertyCityName()));
            assertTrue(agency.getLodgingPropertyStateCode().equals(data.getLodgingPropertyStateCode()));
            assertTrue(agency.getLodgingCountryName().equals(data.getLodgingCountryName()));
            assertTrue(agency.getRentalCarItineraryNumber().equals(data.getRentalCarItineraryNumber()));
            assertTrue(agency.getRentalCarAmount().equals(data.getRentalCarAmount()));
            assertTrue(agency.getRentalCarNumberOfDays().equals(data.getRentalCarNumberOfDays()));
            assertTrue(agency.getRentalCarCompanyName().equals(data.getRentalCarCompanyName()));
            assertTrue(agency.getRentalCarOpenDate().equals(data.getRentalCarOpenDate()));
            assertTrue(agency.getRentalCarCloseDate().equals(data.getRentalCarCloseDate()));
            assertTrue(agency.getRentalCarFuelAmount().equals(data.getRentalCarFuelAmount()));
            assertTrue(agency.getRentalCarAdditionalAmount().equals(data.getRentalCarAdditionalAmount()));
            assertTrue(agency.getRentalCarTaxAmount().equals(data.getRentalCarTaxAmount()));
            assertTrue(agency.getRentalCarSurchargeAmount().equals(data.getRentalCarSurchargeAmount()));
            assertTrue(agency.getRentalCarGovernmentSurchargeAmount().equals(data.getRentalCarGovernmentSurchargeAmount()));
            assertTrue(agency.getRentalCarBillAmount().equals(data.getRentalCarBillAmount()));
            assertTrue(agency.getRentalCarDetailText().equals(data.getRentalCarDetailText()));
            assertTrue(agency.getRegistrationCompanyName().equals(data.getRegistrationCompanyName()));
            assertTrue(agency.getRegistrationAmount().equals(data.getRegistrationAmount()));
            assertTrue(agency.getTransactionPostingDate().equals(data.getTransactionPostingDate()));
            assertTrue(agency.getObjectVerNumber().equals(data.getObjectVerNumber()));
            assertTrue(agency.getCreationTimestamp().equals(data.getCreationTimestamp()));
            assertTrue(agency.getProcessingTimestamp().equals(data.getProcessingTimestamp()));
            assertTrue(agency.getMoveToHistoryIndicator()&&data.getMoveToHistoryIndicator());
        }
        catch(Exception e){
            assert(false);
        }
    }
}
