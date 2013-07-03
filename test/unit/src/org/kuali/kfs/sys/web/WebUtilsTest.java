/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.web;

import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * Tests to cover functionality performed in org.kuali.kfs.sys.web.WebUtilities
 */
public class WebUtilsTest extends KualiTestBase {
    /**
     * Test that WebUtils.renamePropertyForMaintenanceFramework methods
     */
    public void test_renamePropertyForMatinenanceFramework() {
        String newMaintenanceToReplace = "document.newMaintainableObject.businessObject.props";
        String oldMaintenanceToReplace = "document.oldMaintainableObject.businessObject.props";
        String regularToRemainTheSame = "document.businessObject.monkeys";
        String trickyNewMaintenanceToNotReplace = "fakeProperty.document.newMaintainableObject.businessObject.fakeProperty";
        
        assertEquals("document.newMaintainableObject.businessObject replace doesn't work","document.newMaintainableObject.props",WebUtilities.renamePropertyForMaintenanceFramework(newMaintenanceToReplace));
        assertEquals("document.oldMaintainableObject.businessObject replace doesn't work","document.oldMaintainableObject.props",WebUtilities.renamePropertyForMaintenanceFramework(oldMaintenanceToReplace));
        assertEquals("Replace was a bit too eager", regularToRemainTheSame, WebUtilities.renamePropertyForMaintenanceFramework(regularToRemainTheSame));
        assertEquals("Replace was easily tricked", trickyNewMaintenanceToNotReplace, WebUtilities.renamePropertyForMaintenanceFramework(trickyNewMaintenanceToNotReplace));
        assertNull("Replace chokes on nulls", WebUtilities.renamePropertyForMaintenanceFramework(null));
        
    }
}
