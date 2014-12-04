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

import org.kuali.kfs.sys.suite.TestSuiteBuilder;

/**
 * This class shuts down spring after unit testing
 */
@TestSuiteBuilder.Exclude
public class ContinuousIntegrationShutdown extends KualiTestBase {

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        stopSpringContext();
    }

    public void testNothing() throws Exception {
    }
}
