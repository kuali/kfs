/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.IcrEncumbranceSuite;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class tests the renaming step of generated ICR Encumbrance files.
 *
 * Additionally, all parts of the ICR Encumbrance functionality are being
 * flexed here, since we have to perform all previous steps to get the
 * files on disk to perform this test.
 */
@ConfigureContext
@AnnotationTestSuite(IcrEncumbranceSuite.class)
public class IcrEncumbranceFileRenameStepTest extends IcrEncumbranceStepTestBase {

    private FileRenameStep fileRenameStep;
    private IcrEncumbranceSortStep icrEncumbranceSortStep;
    private PosterIcrEncumbranceEntriesStep posterIcrEncumbranceEntriesStep;


    /**
     * Setup local services.
     *
     * @see org.kuali.kfs.gl.batch.IcrEncumbranceStepTestBase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.fileRenameStep = SpringContext.getBean(FileRenameStep.class);
        this.icrEncumbranceSortStep = SpringContext.getBean(IcrEncumbranceSortStep.class);
        this.posterIcrEncumbranceEntriesStep = SpringContext.getBean(PosterIcrEncumbranceEntriesStep.class);
        this.posterIcrEncumbranceEntriesStep.setParameterService(SpringContext.getBean(ParameterService.class));

        // Override spring-gl-test.xml, since all of the other IcrEncumbranceSuite tests use spring-gl.xml
        fileRenameStep = (FileRenameStep)ProxyUtils.getTargetIfProxied(fileRenameStep);
        fileRenameStep.setBatchFileDirectoryName(batchFileDirectoryName);
    }

    /*
     * This method returns a String of the form:
     * "baseName.timeStamp.data"
     *
     * The rename step simply moves the processed file to a new name with a timestamp/
     */
    private String getNewFileName(String fileName, String runDateString){
        String regex = "\\" + GeneralLedgerConstants.BatchFileSystem.EXTENSION + "$"; // Match '.data$'
        String newFileName = fileName.replaceAll(regex, ""); // remove extension
        newFileName = newFileName + "." + runDateString + GeneralLedgerConstants.BatchFileSystem.EXTENSION; // put it all together
        return newFileName;
    }


    /**
     * This method builds a feed file via a service, sorts the file,
     * posts the records to the general ledger, and then tests to see
     * that files genertated by these steps are properly renamed to
     * contain a timestamp.
     *
     * @see org.kuali.kfs.gl.batch.IcrEncumbranceStepTestBase#testExecute()
     */
    @Override
    public void testExecute() {
        TestUtils.setSystemParameter(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, "Y");

        // Generate feed file
        icrEncumbranceService.buildIcrEncumbranceFeed();

        // Generate sorted feed file
        icrEncumbranceSortStep.execute("testIcrEncumbranceSortStep", dateTimeService.getCurrentDate());

        // Post the file to the general ledger
        posterIcrEncumbranceEntriesStep.execute("testPosterIcrEncumbranceEntriesStep", dateTimeService.getCurrentDate());

        // Assert generated files are present
        for(String fileName : fileNames){
            File file = new File(fileName);
            assertTrue("A generated file does not exist: " + file.getAbsolutePath(), file.exists());
        }

        // Perform file re-naming
        Date jobRunDate = dateTimeService.getCurrentDate();
        fileRenameStep.execute("testIcrEncumbranceFileRenameStep", jobRunDate);

        // Assert generated files do not exist, since the step is supposed to rename w/ a timestamp appended
        for(String fileName : fileNames){
            File file = new File(fileName);
            assertTrue("A generated file was not renamed: " + file.getAbsolutePath(), !file.exists());
        }

        // Assert newly named files are present
        String runDateString = dateTimeService.toDateTimeStringForFilename(jobRunDate);
        for(String fileName : fileNames){

            String newFileName = getNewFileName(fileName, runDateString);
            File file = new File(newFileName);

            assertTrue("File should exist but does not: " + file.getAbsolutePath(), file.exists());

            // super.tearDown() doesn't catch newly named files, so clean-up here
            file.delete();
        }
    }


}
