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
package org.kuali.kfs.module.ld.util;

import java.util.Date;

import org.kuali.kfs.module.ld.batch.service.LaborPosterService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.datetime.DateTimeService;

@ConfigureContext(session = UserNameFixture.kfs)
public class LaborPosterRunner extends KualiTestBase {
    private LaborPosterService laborPosterService;

    protected void setUp() throws Exception {
        super.setUp();
        laborPosterService = SpringContext.getBean(LaborPosterService.class);
    }

    public void testPoster() throws Exception {
        System.out.println("Labor Poster started");
        long start = System.currentTimeMillis();
        System.out.println("Labor Poster is running ...");
        System.out.printf("Starting Time = %d (ms)\n", start);
        Date jobRunDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        BatchSpringContext.getJobDescriptor("laborBatchJob").getSteps().get(2).execute("laborBatchJob", jobRunDate);
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Execution Time = %d (ms)\n", elapsedTime);
        System.out.println("Labor Poster stopped");
    }
}

