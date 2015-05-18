/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.comparator;

import org.displaytag.model.Cell;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CellComparatorHelperTest {

    @Test public void testExtractFromHref() {

        String href = "<a style=\"color: red;\" href=\"inquiry.do?businessObjectClassName=org.kuali.rice.krad.bo.Options&amp;universityFiscalYear=2004&amp;methodToCall=start\" target=\"blank\">needle</a>";

        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");

    }

    @Test public void testExtractFromHrefAndRemoveNbsp() {

        String href = "<a href=\"haystack\">needle&nbsp;</a>";

        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");

    }

    @Test public void testRemoveNbsp() {

        String bad = "needle&nbsp;";

        Cell cell = new Cell(bad);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");

    }

    @Test public void testLeaveSimpleValueAlone() {

        String good = "needle";
        Cell cell = new Cell(good);
        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");

    }

    @Test public void testMessyHref() {

        String href = "<a onClick=\"foo();\" href=\"haystack\" class=\"my favorite class\" >needle</a>&nbsp;";

        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");

    }

}
