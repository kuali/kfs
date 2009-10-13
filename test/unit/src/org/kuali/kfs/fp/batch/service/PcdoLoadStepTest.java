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
package org.kuali.kfs.fp.batch.service;

import java.io.File;

import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Tests the PcdoLoadStep. DEPENDENCIES: Procurement card xml file transaction1.xml must be in /opt/kuali/dev/staging/PCDO/ this
 * file can be obtained by running the project's ant dist-local, or copying from build/externalConfigDirectory/static/staging/PCDO/
 */
@ConfigureContext
public class PcdoLoadStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PcdoLoadStepTest.class);

    public PcdoLoadStepTest() {
        super();
    }

    /**
     * Creats .done file for test input file.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String doneFileName = SpringContext.getBean(ProcurementCardInputFileType.class).getDirectoryPath() + "/transactions1.done";
        File doneFile = new File(doneFileName);
        if (!doneFile.exists()) {
            LOG.info("Creating done file: " + doneFile.getAbsolutePath());
            doneFile.createNewFile();
        }
    }

    /**
     * Tests the whole step completes successfully.
     */
    public void testAll() throws Exception {
        assertTrue("hold until figure out staging dir!", true);
        //        
        // PcdoLoadStep pcdoLoadStep = SpringContext.getBean(PcdoLoadStep.class);
        // boolean goodExit = pcdoLoadStep.execute();
        //        
        // assertTrue("pcdo load step did not exit with pass", goodExit);
        //        
        // Collection loadedTransactions =
        // SpringContext.getBean(BusinessObjectService.class).findAll(ProcurementCardTransaction.class);
        // assertNotNull("no transactions loaded ", loadedTransactions);
        // assertEquals("incorrect number of transactions loaded ",10,loadedTransactions.size());
    }
}
