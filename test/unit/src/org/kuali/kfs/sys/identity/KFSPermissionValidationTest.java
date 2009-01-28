/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

@ConfigureContext
public class KFSPermissionValidationTest extends KualiTestBase {

    
    @ConfigureContext(session=UserNameFixture.day)
    public void testChartPermission_1() throws Exception {
        DocumentAuthorizer auth = new MaintenanceDocumentAuthorizerBase();
        Person user = GlobalVariables.getUserSession().getPerson();
        String documentTypeName = "VendorMaintenanceDocument"; 
        assertTrue( 
                GlobalVariables.getUserSession().getPrincipalName() + " should be able to initiate " + documentTypeName, 
                auth.canInitiate( documentTypeName, user)
                );
        Document doc = KNSServiceLocator.getDocumentService().getNewDocument(documentTypeName);
        assertTrue( 
                "Initiator should be able to open the document",
                auth.canOpen(doc, user)
                );       
        
        documentTypeName = "ObjectCodeGlobalMaintenanceDocument";
        
        assertFalse( 
                GlobalVariables.getUserSession().getPrincipalName() + " should not be able to initiate " + documentTypeName, 
                auth.canInitiate( documentTypeName, GlobalVariables.getUserSession().getPerson())
                );
    }

}
