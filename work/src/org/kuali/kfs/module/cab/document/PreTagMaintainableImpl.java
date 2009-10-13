/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document;

import java.util.Map;

import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class PreTagMaintainableImpl extends FinancialSystemMaintainable {
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        // clear the pre-tag details when coyping pre-tag information
        Pretag oldPreTag = (Pretag) document.getOldMaintainableObject().getBusinessObject();
        Pretag newPreTag = (Pretag) document.getNewMaintainableObject().getBusinessObject();
        oldPreTag.getPretagDetails().clear();
        newPreTag.getPretagDetails().clear();
    }
}
