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
package org.kuali.kfs.sys.identity;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext
public class KFSPermissionValidationTest extends KualiTestBase {

    
    @ConfigureContext(session=UserNameFixture.day)
    public void testChartPermission_1() throws Exception {
        DocumentAuthorizer auth = new MaintenanceDocumentAuthorizerBase();
        Person user = GlobalVariables.getUserSession().getPerson();
        String documentTypeName = "PVEN"; 
        assertTrue( 
                GlobalVariables.getUserSession().getPrincipalName() + " should be able to initiate " + documentTypeName, 
                auth.canInitiate( documentTypeName, user)
                );
        Document doc = SpringContext.getBean(DocumentService.class).getNewDocument(documentTypeName);
        assertTrue( 
                "Initiator should be able to open the document",
                auth.canOpen(doc, user)
                );       
        
        documentTypeName = "GOBJ";
        
        assertFalse( 
                GlobalVariables.getUserSession().getPrincipalName() + " should not be able to initiate " + documentTypeName, 
                auth.canInitiate( documentTypeName, GlobalVariables.getUserSession().getPerson())
                );
    }

}
