/*
 * Copyright 2007 The Kuali Foundation
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

