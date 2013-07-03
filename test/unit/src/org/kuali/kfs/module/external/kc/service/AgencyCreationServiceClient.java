/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.TestCase;

import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.AgencyCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AgencyParametersDTO;
import org.kuali.kfs.integration.cg.service.AgencyCreationService;
import org.kuali.kfs.module.cg.service.impl.AgencyCreationServiceImpl;

/**
 * This class tests the AgencyCreationService
 */
public class AgencyCreationServiceClient extends TestCase {
    private AgencyParametersDTO agencyParameters;
    private AgencyCreationService agencyCreationService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // Initialize service objects.
        agencyCreationService = new AgencyCreationServiceImpl();

        // Initialize objects.
        agencyParameters = new AgencyParametersDTO();
        agencyParameters.setPrincipalId("6162502038"); // khuntley

        agencyParameters.setSponsorCode("99999");
        agencyParameters.setAgencyTypeCode("D");
        agencyParameters.setFullName("full name");
        agencyParameters.setReportingName("Reporting Name");
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * This method tests the remote Service without using KSB so the server should be up before testing
     */
    public void testCreateAgencyServiceSoap() {
        try {
            URL url = new URL("http://localhost:8080/kfs-dev/remoting/agencyCreationServiceSOAP?wsdl");

            QName qName = new QName("KFS", "agencyCreationServiceSOAP");

            Service service = Service.create(url, qName);
            AgencyCreationService agencyService = (AgencyCreationService) service.getPort(AgencyCreationService.class);

            AgencyCreationStatusDTO creationStatus = agencyService.createAgency(agencyParameters);
            System.out.println("document number: " + creationStatus.getDocumentNumber());
            assertTrue(creationStatus.getStatus().equals(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS));

        }
        catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }

}
