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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * A basic test of origin entry, making sure that line parsing works as expected
 */
@ConfigureContext
public class OriginEntryTest extends KualiTestBase {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryTest.class);

    /**
     * Constructs a OriginEntryTest instance
     */
    public OriginEntryTest() {
        super();
    }

    /**
     * Tests that converting String-formatted origin entries to origin entries to String-formatted entries again
     * has the expected format 
     *
     * @throws Exception thrown if the parsing of the entry went really really bad?  It's doubtful this exception will ever be thrown for this test
     */
    public void testGetLine() throws Exception {

        String line = null;

        String testingYear = TestUtils.getFiscalYearForTesting().toString();
        line = testingYear+"BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC          CONCERTO OFFICE PRODUCTS                                48.53C2006-01-05          ----------                                       ";
        String convertedLine = testingYear+"BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C2006-01-05          ----------                                       ";
        final String lineFromOE = new OriginEntryFull(line).getLine();
        assertEquals(convertedLine, lineFromOE);

        line = testingYear+"BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

        line = testingYear+"BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     12345CONCERTO OFFICE PRODUCTS                +00000000000000048.53D2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

        line = testingYear+"BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00045CONCERTO OFFICE PRODUCTS                +00000000000000048.53D2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

    }

}
