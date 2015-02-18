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
package org.kuali.kfs.gl.batch;

import java.io.File;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Tests the CollecterStep. DEPENDENCIES: Collector card xml file transaction1.xml must be in /opt/kuali/dev/staging/collector/ this
 * file can be obtained by running the project's ant dist-local, or copying from
 * build/externalConfigDirectory/static/staging/collector/
 */
@ConfigureContext
public class CollectorStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorStepTest.class);

    /**
     * Constructs a CollectorStepTest instance
     */
    public CollectorStepTest() {
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

        // warren: this is just testing code to list out the contents of the staging directory
        File directory = new File("/opt/kuali/unt/staging/");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                System.err.println("TESTING: " + file.getName());
                if (file.isDirectory()) {
                    File[] files2 = file.listFiles();
                    for (File file2 : files2) {
                        System.err.println("TESTING2: " + file2.getName());
                    }
                }
            }
        }

        String doneFileName = generateDoneFileName();

        File doneFile = new File(doneFileName);
        if (!doneFile.exists()) {
            doneFile.createNewFile();
        }
    }

    /**
     * Deletes the file created in setUp()
     */
    protected void deleteDoneFile() {
        File doneFile = new File(generateDoneFileName());
        if (doneFile.exists()) {
            doneFile.delete();
        }
    }

    /**
     * Determines if the .done file with the expected file name exists
     * 
     * @return true if the done file exists, false otherwise
     */
    protected boolean isDoneFileExists() {
        File doneFile = new File(generateDoneFileName());
        return doneFile.exists();
    }

    /**
     * Generates the standard name of the .done file to check
     * 
     * @return the full path and name of the done file to check
     */
    protected String generateDoneFileName() {
        return SpringContext.getBean(CollectorXmlInputFileType.class).getDirectoryPath() + "/gl_collector1.done";
    }

    /**
     * Tests the whole step completes successfully.
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT29)
    public void testAll() throws Exception {
        try {
            Step step = BatchSpringContext.getStep("collectorStep");
            CollectorStep collectorStep = (CollectorStep) ProxyUtils.getTargetIfProxied(step);
            
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

            boolean goodExit = collectorStep.execute(getClass().getName(), dateTimeService.getCurrentDate());

            assertTrue("collector step did not exit with pass", goodExit);
            assertFalse("done file was not removed", isDoneFileExists());
        }
        finally {
            deleteDoneFile();
        }
    }
}
