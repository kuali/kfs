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
