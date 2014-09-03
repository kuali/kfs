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
import java.io.FileReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.IcrEncumbranceSuite;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * A class to test functionality of the IcrEncumbranceSortStepTest class.
 */
@ConfigureContext
@AnnotationTestSuite(IcrEncumbranceSuite.class)
public class IcrEncumbranceSortStepTest extends IcrEncumbranceStepTestBase {

    private IcrEncumbranceSortStep icrEncumbranceSortStep;

    /**
     * Setup services used in test.
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        icrEncumbranceSortStep = SpringContext.getBean(IcrEncumbranceSortStep.class);
        icrEncumbranceSortStep.setParameterService(SpringContext.getBean(ParameterService.class));
    }

    /**
     * Test to ensure IcrEncumbranceSortStep is performing file i/o correctly,
     * and that at the very least is not dropping or dupe'ing records.
     */
    @Override
    public void testExecute(){
        TestUtils.setSystemParameter(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, "Y");

        // Create an input file via the related service
        File inputFile = icrEncumbranceService.buildIcrEncumbranceFeed();

        // Perform work to be tested
        icrEncumbranceSortStep.execute("testIcrEncumbranceSortStep", dateTimeService.getCurrentDate());

        // Grab the lines from the input file
        List<String> inputLines = null;
        try {
            inputLines = IOUtils.readLines(new FileReader(inputFile));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Grab the lines of sorted file
        String outputFilePath = super.batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        List<String> outputLines = null;
        try {
            outputLines = IOUtils.readLines(new FileReader(outputFilePath));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Ensure line counts match
        int inputLineCount = inputLines.size();
        int outputLineCount = outputLines.size();
        assertTrue("There should not be a mismatch of line counts between input and output files: input.size(): " + inputLineCount + ", output.size(): " + outputLineCount, inputLineCount == outputLineCount);

    }

}
