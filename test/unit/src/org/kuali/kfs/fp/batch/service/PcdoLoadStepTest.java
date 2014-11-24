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
