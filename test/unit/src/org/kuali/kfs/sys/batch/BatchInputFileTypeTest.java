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
package org.kuali.kfs.batch;

import java.io.File;
import java.util.ArrayList;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants.TestConstants.Data4;

/**
 * Tests the collector and pcdo instances of BatchInputFileType.
 * 
 * @see org.kuali.kfs.batch.BatchInputFileType
 */
@ConfigureContext
public class BatchInputFileTypeTest extends KualiTestBase {
    private static final String TEST_BATCH_XML_DIRECTORY = "org/kuali/kfs/batch/xml/";
    
    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        pcdoBatchInputFileType = SpringServiceLocator.getProcurementCardInputFileType();
        collectorBatchInputFileType = SpringServiceLocator.getCollectorInputFileType();
    }

    /**
     * Verifies the directories specified by the batch input types do exist on the local file system.
     */
    public final void testDirectoryName() throws Exception {
        File pcdoDirectory = new File(pcdoBatchInputFileType.getDirectoryPath());
        assertTrue("pcdo directory " + pcdoBatchInputFileType.getDirectoryPath() + " does not exist on local file system", pcdoDirectory.exists());

        File collectorDirectory = new File(collectorBatchInputFileType.getDirectoryPath());
        assertTrue("collector directory " + collectorBatchInputFileType.getDirectoryPath() + " does not exist on local file system", collectorDirectory.exists());
    }
    
    /**
     * Assures any workgroup user is granted permission for a pcdo file.
     */
    public final void testCheckAuthorization_pcdo() throws Exception {
        Object parsedContents = new ArrayList();
        UniversalUser createUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        UniversalUser nonCreateUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID1);
        
        String saveFileName = pcdoBatchInputFileType.getFileName(createUser, parsedContents, "testFile.xml");
        File batchFile = new File(saveFileName);
        
        assertTrue("user who created batch file does not have file authorization", pcdoBatchInputFileType.checkAuthorization(createUser, batchFile));
        assertTrue("other user does not have file authorization", pcdoBatchInputFileType.checkAuthorization(nonCreateUser, batchFile));
    }
    
    /**
     * Assures that only the user who created the collector file is granted permission to manage.
     */
    public final void testCheckAuthorization_collector() throws Exception {
        Object parsedContents = new CollectorBatch();
        UniversalUser createUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        UniversalUser nonCreateUser = SpringServiceLocator.getUniversalUserService().getUniversalUserByAuthenticationUserId(Data4.USER_ID1);

        String saveFileName = collectorBatchInputFileType.getFileName(createUser, parsedContents, "testFile.xml");
        File batchFile = new File(saveFileName);
        
        assertTrue("user who created batch file does not have file authorization", collectorBatchInputFileType.checkAuthorization(createUser, batchFile));
        assertFalse("user who did not create batch file has authorization", collectorBatchInputFileType.checkAuthorization(nonCreateUser, batchFile));
    }
    
}
