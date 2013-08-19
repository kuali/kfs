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
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.TestCase;

import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.AwardCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AwardParametersDTO;
import org.kuali.kfs.integration.cg.service.AwardCreationService;
import org.kuali.kfs.module.cg.service.impl.AwardCreationServiceImpl;

/**
 * This class tests the AwardCreationService
 */
public class AwardCreationServiceClient extends TestCase {
    private AwardParametersDTO awardParameters;
    private AwardCreationService awardCreationService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // Initialize service objects.
        awardCreationService = new AwardCreationServiceImpl();

        // Initialize objects.
        awardParameters = new AwardParametersDTO();
        awardParameters.setPrincipalId("6162502038"); // khuntley
        awardParameters.setUnit("def");

        awardParameters.setSponsorCode("999999");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.NOVEMBER);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        awardParameters.setProjectStartDate(new java.sql.Date(cal.getTime().getTime()));

        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 1);
        awardParameters.setProjectEndDate(new java.sql.Date(cal.getTime().getTime()));

        awardParameters.setProposalSubmissionDate(new java.sql.Date(cal.getTime().getTime()));


        awardParameters.setAwardId("3999");
        awardParameters.setAwardProjectTitle("My project title edit");
        awardParameters.setAwardPurposeCode("A");
        awardParameters.setAwardStatusCode("A");
        awardParameters.setProposalAwardTypeCode("C");
        awardParameters.setProposalPrimaryProjectDirectorId("4374009622");
        awardParameters.setProposalDirectCostAmount("4.50");
        awardParameters.setProposalIndirectCostAmount("5.50");


        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
