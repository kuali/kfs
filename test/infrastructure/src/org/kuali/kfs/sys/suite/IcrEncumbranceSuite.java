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
package org.kuali.kfs.sys.suite;

import junit.framework.Test;

import org.kuali.kfs.sys.ConfigureContext;

/**
 * A suite which includes tests for the ICR Encumbrance service and batch steps.
 *
 * The sequence of steps for the ICR Encumbrance posting job is:
 * 1.) icrEncumbranceFeedStep -> IcrEncumbranceFeedStepTest.java
 * 2.) icrEncumbranceSortStep -> IcrEncumbranceSortStepTest.java
 * 3.) posterIcrEncumbranceEntriesStep -> PosterIcrEncumbranceEntriesStepTest.java
 * 4.) fileRenameStep -> IcrEncumbranceFileRenameStepTest.java
 *
 *  Running the suite also (implicitly) tests the IcrEncumbranceService and
 *  IcrEncumbranceDao classes, since none of the steps will succeed if either
 *  of these classes fail.
 *
 */
@ConfigureContext
@AnnotationTestSuite(IcrEncumbranceSuite.class)
public class IcrEncumbranceSuite extends AnnotationTestSuite.Superclass {

    /**
     * This class returns a suite of tests as determined by the framework.
     *
     * @return a suite of tests for ICR Encumbrance functionality.
     * @throws Exception any exception not caught during context initialization or test-case execution.
     */
    public static Test suite() throws Exception {
        return new IcrEncumbranceSuite().getSuite();
    }

}
