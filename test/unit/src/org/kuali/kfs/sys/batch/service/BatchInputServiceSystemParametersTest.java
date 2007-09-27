/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.service;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.module.financial.batch.pcard.ProcurementCardInputFileType;
import org.kuali.module.gl.batch.collector.CollectorInputFileType;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants.TestConstants.Data4;

/**
 * Tests system parameters are setup and methods on the batch input types are correctly using them.
 */
@ConfigureContext
public class BatchInputServiceSystemParametersTest extends KualiTestBase {
    private KualiConfigurationService configurationService;
    private BatchInputFileService batchInputFileService;
    
    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;
    
    private UniversalUser validWorkgroupUser;
    private UniversalUser invalidWorkgroupUser;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        configurationService = SpringContext.getBean(KualiConfigurationService.class);
        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorInputFileType.class);

        validWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        invalidWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID1);
    }
    
    /**
     * Verifies system parameters needed by the batch upload process exist in the db.
     */
    public final void testSystemParametersExist() throws Exception {
        String[] activeFileTypes = configurationService.getParameterValues(KFSConstants.CORE_NAMESPACE, KFSConstants.Components.BATCH, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME + " is not setup or contains no file types", activeFileTypes != null && activeFileTypes.length > 0 && StringUtils.isNotBlank( activeFileTypes[0] ) );

        String pcdoUploadWorkgroup = configurationService.getParameterValue(pcdoBatchInputFileType.getWorkgroupParameterNamespace(), pcdoBatchInputFileType.getWorkgroupParameterComponent(), pcdoBatchInputFileType.getWorkgroupParameterName());
        assertTrue("system parameter " + pcdoBatchInputFileType.getWorkgroupParameterNamespace()+"/"+pcdoBatchInputFileType.getWorkgroupParameterComponent()+"/"+pcdoBatchInputFileType.getWorkgroupParameterName() + " does not exist or has empty value.", StringUtils.isNotBlank(pcdoUploadWorkgroup));

        String collectorUploadWorkgroup = configurationService.getParameterValue(collectorBatchInputFileType.getWorkgroupParameterNamespace(), collectorBatchInputFileType.getWorkgroupParameterComponent(), collectorBatchInputFileType.getWorkgroupParameterName());
        assertTrue("system parameter " + collectorBatchInputFileType.getWorkgroupParameterNamespace()+"/"+collectorBatchInputFileType.getWorkgroupParameterComponent()+"/"+collectorBatchInputFileType.getWorkgroupParameterName() + " does not exist or has empty value.", StringUtils.isNotBlank(collectorUploadWorkgroup));
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
        TestUtils.setSystemParameter(KFSConstants.CORE_NAMESPACE, KFSConstants.Components.BATCH, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME, parameterText, false, multiValue);
    }

 }
