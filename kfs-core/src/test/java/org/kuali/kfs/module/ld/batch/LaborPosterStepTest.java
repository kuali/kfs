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
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext
public class LaborPosterStepTest extends KualiTestBase {
    private LaborPosterStep laborPosterStep;

    @Override
    public void setUp() throws Exception {
        super.setUp();

//        laborPosterStep = SpringContext.getBean(LaborPosterStep.class);
    }

    public void testExecute() throws Exception {
        // This is time-consuming process, which is only good to run locally

        // System.out.println(laborPosterStep.getName());
        // laborPosterStep.execute();
    }
}
