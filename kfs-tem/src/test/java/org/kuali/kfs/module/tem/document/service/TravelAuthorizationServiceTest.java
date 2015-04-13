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
package org.kuali.kfs.module.tem.document.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;

@ConfigureContext
public class TravelAuthorizationServiceTest extends KualiTestBase {

    private TravelAuthorizationService taService;
    private static final int EXPENSE_AMOUNT = 100;
    private static final int MILEAGE = 2;
    private static final int MILEAGE_RATE = 10;

    private ParameterService parameterService;
    private TravelAuthorizationDocument ta = null;
    private TravelerDetail traveler = null;
    private FinancialSystemDocumentHeader documentHeader = null;

    private static final Logger LOG = Logger.getLogger(TravelAuthorizationServiceTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // setup services
        taService = SpringContext.getBean(TravelAuthorizationService.class);
        parameterService = SpringContext.getBean(ParameterService.class);

        ta = new TravelAuthorizationDocument();

        // setup traveler
        traveler = new TravelerDetail();
        traveler.setFirstName("firstName");
        traveler.setLastName("lastName");
        traveler.setStreetAddressLine1("streetAddressLine1");
        traveler.setStreetAddressLine2("streetAddressLine2");
        traveler.setCityName("city");
        traveler.setStateCode("AZ");
        traveler.setZipCode("85257");
        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);

    }

    @Override
    @After
    public void tearDown() throws Exception {
        taService = null;
        super.tearDown();
    }

    /**
     * This method tests {@link TravelAuthorizationService#createCustomerInvoice(TravelAuthorizationDocument)}
     */
    @Test
    public void testCreateCustomerInvoice() {
        boolean success = false;

        try {
            taService.createCustomerInvoice(ta);
            success = true;
        }
        catch (Exception e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertTrue(success);
    }

    /**
     * This method tests {@link TravelAuthorizationService#getTravelAuthorizationBy(String)}
     */
    @Test
    public void testGetTravelAuthorizationBy() {
        // test getTravelAuthorizationBy for null value
        ta = taService.getTravelAuthorizationBy(null);
        assertNull(ta);

        // test getTravelAuthorizationBy for non existent documentNumber
        try {
            ta = taService.getTravelAuthorizationBy("-1");
        }
        catch (UnknownDocumentIdException e) {
            assertNull(ta);
        }
    }

    /**
     * This method tests {@link TravelAuthorizationService#find(Integer)}
     */
    @Test
    public void testFind() {
        // test find for null value
        List<TravelAuthorizationDocument> result = (List<TravelAuthorizationDocument>) taService.find(null);
        assertNotNull(result);

        // test find for non existent travelDocumentIdentifier
        result = (List<TravelAuthorizationDocument>) taService.find("-1");
        assertNotNull(result);
        assertTrue(result.size() == 0);
    }

    /**
     * This method tests {@link TravelAuthorizationService#findAmendment(Integer)}
     */
    @Test
    public void testFindAmendment() {
        // test findAmendment for null value
        List<TravelAuthorizationAmendmentDocument> result = (List<TravelAuthorizationAmendmentDocument>) taService.findAmendment(null);
        assertNotNull(result);

        // test findAmendment for non existent travelDocumentIdentifier
        result = (List<TravelAuthorizationAmendmentDocument>) taService.findAmendment(-1);
        assertNotNull(result);
        assertTrue(result.size() == 0);
    }
}
