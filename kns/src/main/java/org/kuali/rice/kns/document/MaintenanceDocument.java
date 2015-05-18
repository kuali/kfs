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
package org.kuali.rice.kns.document;

import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * Common interface for all maintenance documents.
 */
public interface MaintenanceDocument extends org.kuali.rice.krad.maintenance.MaintenanceDocument {

    /**
     * @return Maintainable which holds the new maintenance record
     */
    public Maintainable getNewMaintainableObject();

    /**
     * @return Maintainable which holds the old maintenance record
     */
    public Maintainable getOldMaintainableObject();

    /**
     * Returns a reference to the PersistableBusinessObject that this MaintenanceDocument is maintaining.
     */
    public PersistableBusinessObject getDocumentBusinessObject();
    
    /**
     * @return boolean - indicates whether this is an edit or new maintenace document by the existence of an old maintainable
     */
    public boolean isOldBusinessObjectInDocument();

}
