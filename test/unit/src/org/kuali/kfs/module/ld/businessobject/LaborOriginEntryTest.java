/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.bo;

import junit.framework.TestCase;

public class LaborOriginEntryTest extends TestCase {

    public LaborOriginEntryTest() {
        super();
    }

    public void testGetLine() throws Exception {

        String line = null;

      /*  line = "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC          CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                       ";
        String convertedLine = "2007BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                       ";
        assertEquals(convertedLine, new LaborOriginEntry(line).getLine());
*/
        line = "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA";
        String convertedLine = "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                              3493.50D2006-12-22                                                 2006-12-222006-12-31168.00   2007060000149952 1  REGS12PAE 11 M001010207                     IU IUBLA";
        assertEquals(convertedLine, new LaborOriginEntry(line).getLine());
        
        line = "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                              3493.50D2006-12-22                                                 2006-12-222006-12-31168.00   2007060000149952 1  REGS12PAE 11 M001010207                     IU IUBLA";
        assertEquals(line, new LaborOriginEntry(line).getLine());
        
       /* line = "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     12345CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                       ";
        assertEquals(line, new LaborOriginEntry(line).getLine());

        line = "2007BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC     00045CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                       ";
        assertEquals(line, new LaborOriginEntry(line).getLine());
*/
    }

}
