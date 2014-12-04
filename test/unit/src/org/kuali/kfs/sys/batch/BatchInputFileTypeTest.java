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

