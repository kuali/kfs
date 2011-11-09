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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorXmlInputFileType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KualiTestConstants.TestConstants.Data4;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Tests the collector and pcdo instances of BatchInputFileType.
 * 
 * @see org.kuali.kfs.sys.batch.BatchInputFileType
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

        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorXmlInputFileType.class);
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
        Person createUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID2);
        Person nonCreateUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID1);

        String saveFileName = pcdoBatchInputFileType.getFileName(createUser.getPrincipalName(), parsedContents, "testFile.xml");
        File batchFile = new File(saveFileName);

        assertTrue("user who created batch file does not have file authorization", createUser.getPrincipalName().equals(pcdoBatchInputFileType.getAuthorPrincipalName(batchFile)));
        assertFalse("other user does not have file authorization", nonCreateUser.getPrincipalName().equals(pcdoBatchInputFileType.getAuthorPrincipalName(batchFile)));
    }

    /**
     * Assures that only the user who created the collector file is granted permission to manage.
     */
    public final void testCheckAuthorization_collector() throws Exception {
        List<CollectorBatch> parsedContents = new ArrayList<CollectorBatch>();
        parsedContents.add(new CollectorBatch());
        Person createUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID2);
        Person nonCreateUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(Data4.USER_ID1);

        String saveFileName = collectorBatchInputFileType.getFileName(createUser.getPrincipalName(), parsedContents, "testFile.xml");
        File batchFile = new File(saveFileName);

        assertTrue("user who created batch file does not have file authorization", createUser.getPrincipalName().equals(collectorBatchInputFileType.getAuthorPrincipalName(batchFile)));
        assertFalse("user who did not create batch file has authorization", nonCreateUser.getPrincipalName().equals(collectorBatchInputFileType.getAuthorPrincipalName(batchFile)));
    }

}

