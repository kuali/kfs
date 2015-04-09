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
