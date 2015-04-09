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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.gl.batch.CollectorXmlInputFileType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.KualiTestConstants.TestConstants.Data4;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Tests system parameters are setup and methods on the batch input types are correctly using them.
 */
@ConfigureContext
public class BatchInputServiceSystemParametersTest extends KualiTestBase {
    private ParameterService parameterService;
    private BatchInputFileService batchInputFileService;

    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;

    private Person validWorkgroupUser;
    private Person invalidWorkgroupUser;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parameterService = SpringContext.getBean(ParameterService.class);
        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorXmlInputFileType.class);

        validWorkgroupUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID2);
        invalidWorkgroupUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID1);
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to empty and verify both pcdo & collector are inactive
     */
    public final void testIsBatchInputTypeActive_emptySystemParameter() throws Exception {
        setActiveSystemParameter("", true);
        assertFalse("pcdo isActive method not false when active param is empty", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method not false when active param is empty", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to an invalid group and verify both pcdo & collector are
     * inactive
     */
    public final void testIsBatchInputTypeActive_invalidSystemParameter() throws Exception {
        setActiveSystemParameter("foo", true);
        assertFalse("pcdo isActive method not false when active param is foo", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method not false when active param is foo", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to contain both indentifiers and verify both pcdo & collector
     * are active
     */
    public final void testIsBatchInputTypeActive_systemParameterContainsBoth() throws Exception {
        setActiveSystemParameter(collectorBatchInputFileType.getFileTypeIdentifer() + ";" + pcdoBatchInputFileType.getFileTypeIdentifer(), true);
        assertTrue("pcdo isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertTrue("collector isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to contain one indentifier and verify the correct one is
     * active and other type is inactive.
     */
    public final void testIsBatchInputTypeActive_systemParameterContainsOne() throws Exception {
        setActiveSystemParameter(collectorBatchInputFileType.getFileTypeIdentifer(), true);
        assertFalse("pcdo isActive method is true when active param does not contain identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertTrue("collector isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));

        setActiveSystemParameter(pcdoBatchInputFileType.getFileTypeIdentifer(), true);
        assertTrue("pcdo isActive method is false when active param contains identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method is true when active param does not contain identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }


    /**
     * Changes the text for the batch input active system parameter, stores and clears cache.
     */
    private final void setActiveSystemParameter(String parameterText, boolean multiValue) throws Exception {
        TestUtils.setSystemParameter(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME, parameterText);
    }

}

