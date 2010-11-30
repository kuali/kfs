/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service;

import java.io.IOException;

import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;
import org.kuali.kfs.module.cg.service.impl.CfdaServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class CfdaServiceTest extends KualiTestBase {

//    public void testPatternExtraction() {
//
//        String[] table = { "<TD ALIGN=\"Center\" NOWRAP valign=\"TOP\"  WIDTH=\"0\"><A HREF=\"CATALOG.PROGRAM_TEXT_RPT.SHOW?p_arg_names=prog_nbr&p_arg_values=10.001\"><FONT STYLE = \"font-family:Arial; color:#00008B; font-size:10pt; \"><B><U>10.001</U></B></FONT></A></TD>\n", "<TD ALIGN=\"Left\" NOWRAP valign=\"TOP\"  WIDTH=\"0\"><FONT STYLE = \"font-family:Arial; font-size:10pt; \">USDA</FONT></TD>\n", "<TD ALIGN=\"Left\" NOWRAP valign=\"TOP\"  WIDTH=\"666\"><FONT STYLE = \"font-family:Arial; font-size:10pt; \">Agricultural Research_Basic and Applied Research</FONT></TD>" };
//
//        String n = ((CfdaServiceImpl)SpringContext.getBean(CfdaService.class)).extractCfdaNumberFrom(table[0]);
//        String a = ((CfdaServiceImpl)SpringContext.getBean(CfdaService.class)).extractCfdaAgencyFrom(table[1]);
//        String t = ((CfdaServiceImpl)SpringContext.getBean(CfdaService.class)).extractCfdaTitleFrom(table[2]);
//
//        assertEquals("Number extraction failed", "10.001", n);
//        assertEquals("Agency extraction failed", "USDA", a);
//        assertEquals("Title extraction failed", "Agricultural Research_Basic and Applied Research", t);
//    }

    public void testUpdate() throws IOException {
        // Commented out since causing unit tests to pause for 15 minutes (and doesn't really test anything anyway)
        //CfdaUpdateResults results = SpringContext.getBean(CfdaService.class).update();
    }

}
