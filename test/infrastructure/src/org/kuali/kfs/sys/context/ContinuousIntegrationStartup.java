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
package org.kuali.kfs.sys.context;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.suite.TestSuiteBuilder;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class preps for the tests to run
 */
@TestSuiteBuilder.Exclude
@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class ContinuousIntegrationStartup extends KualiTestBase {

    @Override
    protected void tearDown() throws Exception {
        super.setUp();
        Job.runStep(SpringContext.getBean(ParameterService.class), "genesisBatchJob", 1, BatchSpringContext.getStep("genesisBatchStep"), SpringContext.getBean(DateTimeService.class).getCurrentDate());
    }

    public void testNothing() throws Exception {
    }
}

