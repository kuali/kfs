/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cams.maintenance;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);

    /**
     * This creates the particular locking representation for this global location document.
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = new ArrayList();
        // TODO:generate locks for each asset retirement detail
        return maintenanceLocks;
    }

}
