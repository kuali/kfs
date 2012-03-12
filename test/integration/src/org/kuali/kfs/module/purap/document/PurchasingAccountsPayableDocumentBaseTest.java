/*
 * Copyright 2007-2008 The Kuali Foundation
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

