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
