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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class PurchasingAccountsPayableDocumentBaseTest extends KualiTestBase {

    PurchasingAccountsPayableDocument purapDoc;
    Integer currentFY;

    protected void setUp() throws Exception {
        super.setUp();
        KNSGlobalVariables.setMessageList(new MessageList());
        purapDoc = new PurchaseOrderDocument();
        currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    protected void tearDown() throws Exception {
        purapDoc = null;
        super.tearDown();
    }
    
    public void testIsPostingYearNext_UseCurrent() {
        purapDoc.setPostingYear(currentFY);
        assertFalse(purapDoc.isPostingYearNext());
    }

    public void testIsPostingYearNext_UseNext() {
        purapDoc.setPostingYear(currentFY + 1);
        assertTrue(purapDoc.isPostingYearNext());
    }

    public void testIsPostingYearNext_UsePast() {
        purapDoc.setPostingYear(currentFY - 1);
        assertFalse(purapDoc.isPostingYearNext());
    }

    public void testGetPostingYearNextOrCurrent_UseCurrent() {
        purapDoc.setPostingYear(currentFY);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), currentFY);
    }

    public void testGetPostingYearNextOrCurrent_UseNext() {
        Integer nextFY = currentFY + 1;
        purapDoc.setPostingYear(nextFY);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), nextFY);
    }

    public void testGetPostingYearNextOrCurrent_UsePast() {
        purapDoc.setPostingYear(currentFY - 1);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), currentFY);
    }

}

