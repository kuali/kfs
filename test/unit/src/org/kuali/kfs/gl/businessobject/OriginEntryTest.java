/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.bo;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class OriginEntryTest extends KualiTestBase {

    public OriginEntryTest() {
        super();
    }

    public void testGetLine() throws Exception {

        String line = null;

        line = "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC          CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                       ";
        String convertedLine = "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                       ";
        assertEquals(convertedLine, new OriginEntryFull(line).getLine());

        line = "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

        line = "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     12345CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

        line = "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00045CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                       ";
        assertEquals(line, new OriginEntryFull(line).getLine());

    }

}
