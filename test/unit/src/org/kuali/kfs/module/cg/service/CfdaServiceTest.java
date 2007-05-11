package org.kuali.module.cg.service.impl;

import junit.framework.TestCase;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import static org.kuali.kfs.util.SpringServiceLocator.*;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.module.cg.service.CfdaUpdateResults;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: May 8, 2007
 * Time: 2:04:06 PM
 */
@WithTestSpringContext
public class CfdaServiceTest extends KualiTestBase {

    public void testPatternExtraction() {

        String[] table = {
                "<TD ALIGN=\"Center\" NOWRAP valign=\"TOP\"  WIDTH=\"0\"><A HREF=\"CATALOG.PROGRAM_TEXT_RPT.SHOW?p_arg_names=prog_nbr&p_arg_values=10.001\"><FONT STYLE = \"font-family:Arial; color:#00008B; font-size:10pt; \"><B><U>10.001</U></B></FONT></A></TD>\n",
                "<TD ALIGN=\"Left\" NOWRAP valign=\"TOP\"  WIDTH=\"0\"><FONT STYLE = \"font-family:Arial; font-size:10pt; \">USDA</FONT></TD>\n",
                "<TD ALIGN=\"Left\" NOWRAP valign=\"TOP\"  WIDTH=\"666\"><FONT STYLE = \"font-family:Arial; font-size:10pt; \">Agricultural Research_Basic and Applied Research</FONT></TD>"
        };

        String n = CfdaServiceImpl.extractCfdaNumberFrom(table[0]);
        String a = CfdaServiceImpl.extractCfdaAgencyFrom(table[1]);
        String t = CfdaServiceImpl.extractCfdaTitleFrom(table[2]);
        
        assertEquals("Number extraction failed", "10.001", n);
        assertEquals("Agency extraction failed", "USDA", a);
        assertEquals("Title extraction failed", "Agricultural Research_Basic and Applied Research", t);
    }

    public void testUpdate() throws IOException {
        CfdaUpdateResults results = getCfdaService().update();
    }

}
