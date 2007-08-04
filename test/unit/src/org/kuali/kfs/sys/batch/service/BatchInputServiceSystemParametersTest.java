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
import org.kuali.kfs.KFSConstants.ParameterGroups;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.TestUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants.TestConstants.Data2;
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

        configurationService = SpringServiceLocator.getKualiConfigurationService();
        batchInputFileService = SpringServiceLocator.getBatchInputFileService();
        pcdoBatchInputFileType = SpringServiceLocator.getProcurementCardInputFileType();
        collectorBatchInputFileType = SpringServiceLocator.getCollectorInputFileType();

        validWorkgroupUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        invalidWorkgroupUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID1);
    }
    
    /**
     * Verifies system parameters needed by the batch upload process exist in the db.
     */
    public final void testSystemParametersExist() throws Exception {
        String[] activeFileTypes = configurationService.getApplicationParameterValues(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME + " is not setup or contains no file types", activeFileTypes != null && activeFileTypes.length > 0);

        String pcdoUploadWorkgroup = configurationService.getApplicationParameterValue(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, pcdoBatchInputFileType.getWorkgroupParameterName());
        assertTrue("system parameter " + pcdoBatchInputFileType.getWorkgroupParameterName() + " does not exist or has empty value.", StringUtils.isNotBlank(pcdoUploadWorkgroup));

        String collectorUploadWorkgroup = configurationService.getApplicationParameterValue(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, collectorBatchInputFileType.getWorkgroupParameterName());
        assertTrue("system parameter " + collectorBatchInputFileType.getWorkgroupParameterName() + " does not exist or has empty value.", StringUtils.isNotBlank(collectorUploadWorkgroup));
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
     * Sets an invalid workgroup on system parameters and verifies user is not authorized.
     */
    public final void testIsUserAuthorizedForBatchType_invalidWorkgroupParameter() throws Exception {
        UniversalUser nonWorkgroupUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        UniversalUser workgroupUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);

        setWorkgroupSystemParameter(pcdoBatchInputFileType.getWorkgroupParameterName(), "foo");
        assertFalse("user is authorized for pcdo batch type but workgroup parameter is invalid", batchInputFileService.isUserAuthorizedForBatchType(pcdoBatchInputFileType, workgroupUser));

        setWorkgroupSystemParameter(collectorBatchInputFileType.getWorkgroupParameterName(), "foo");
        assertFalse("user is authorized for collector batch type but workgroup parameter is invalid", batchInputFileService.isUserAuthorizedForBatchType(collectorBatchInputFileType, workgroupUser));
    }

    /**
     * Sets an valid workgroup on system parameters and verifies user is authorized.
     */
    public final void testIsUserAuthorizedForBatchType_validWorkgroupParameter() throws Exception {
        UniversalUser workgroupUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        String validWorkgroup = Data2.KUALI_FMSOPS;

        setWorkgroupSystemParameter(pcdoBatchInputFileType.getWorkgroupParameterName(), validWorkgroup);
        assertTrue("user is not authorized for pcdo batch type but workgroup parameter is valid", batchInputFileService.isUserAuthorizedForBatchType(pcdoBatchInputFileType, workgroupUser));

        setWorkgroupSystemParameter(collectorBatchInputFileType.getWorkgroupParameterName(), validWorkgroup);
        assertTrue("user is not authorized for collector batch type but workgroup parameter is valid", batchInputFileService.isUserAuthorizedForBatchType(pcdoBatchInputFileType, workgroupUser));
    }

    /**
     * Changes the text for the batch input active system parameter, stores and clears cache.
     */
    private final void setActiveSystemParameter(String parameterText, boolean multiValue) throws Exception {
        TestUtils.setSystemParameter(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME, parameterText, false, multiValue);
    }

    /**
     * Changes the text for the given workgroup system parameter, stores and clears cache.
     */
    private final void setWorkgroupSystemParameter(String workgroupName, String parameterText) throws Exception {
        TestUtils.setSystemParameter(ParameterGroups.BATCH_UPLOAD_SECURITY_GROUP_NAME, workgroupName, parameterText, false, false);
    }
}
