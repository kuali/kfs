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

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchDirectoryHelper;
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

    private BatchDirectoryHelper originEntryBatchDirectoryHelper;
    private BatchDirectoryHelper collectorXmlBatchDirectoryHelper;
    private boolean copiedCollector1;
    private boolean copiedCollector2;
    private boolean copiedCollector3;

    /**
     * Constructs a CollectorStepTest instance
     */
    public CollectorStepTest() {
        super();
    }

    /**
     * Creates originEntry directory if needed and .done file for test input file.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        originEntryBatchDirectoryHelper = new BatchDirectoryHelper("gl","originEntry");
        originEntryBatchDirectoryHelper.createBatchDirectory();
        collectorXmlBatchDirectoryHelper = new BatchDirectoryHelper("gl","collectorXml");
        collectorXmlBatchDirectoryHelper.createBatchDirectory();

        // copy fixture files to directory
        copiedCollector1 = copyFixtureToCollectorXml("gl_collector1.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());
        copiedCollector2 = copyFixtureToCollectorXml("gl_collector2.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());
        copiedCollector3 = copyFixtureToCollectorXml("gl_collector3.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());

        String doneFileName = generateDoneFileName();

        File doneFile = new File(doneFileName);
        if (!doneFile.exists()) {
            doneFile.createNewFile();
        }
    }

    protected boolean copyFixtureToCollectorXml(String fixtureName, String directory) {
        File f = new File(directory + File.separator + fixtureName);
        if (!f.exists()) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = CollectorStepTest.class.getClassLoader().getResourceAsStream("org/kuali/kfs/gl/batch/fixture/" + fixtureName);
                if (input != null) {
                    output = new FileOutputStream(f);
                    IOUtils.copy(input, output);
                    input.close();
                    output.close();
                    return true;
                }
            } catch (IOException ioe) {
                LOG.error("Could not copy file", ioe);
                throw new RuntimeException(ioe);
            } finally {
                input = null;
                output = null;
            }
        }
        return false;
    }

    protected void deleteCollectorFile(String fileName, String directory) {
        File f = new File(directory + File.separator + fileName);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (copiedCollector1) {
            deleteCollectorFile("gl_collector1.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());
        }
        if (copiedCollector2) {
            deleteCollectorFile("gl_collector2.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());
        }
        if (copiedCollector3) {
            deleteCollectorFile("gl_collector3.xml", collectorXmlBatchDirectoryHelper.getBatchFileDirectoryName());
        }

        originEntryBatchDirectoryHelper.removeBatchDirectory();
        collectorXmlBatchDirectoryHelper.removeBatchDirectory();
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
