/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Maintainable class for Collector Hierarchy
 */
public class CollectorHierarchyMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        // get old collector information for collector head
        BusinessObject bo = this.getBusinessObject();
        if (bo instanceof CollectorHierarchy) {

            CollectorHierarchy newObj = (CollectorHierarchy) bo;
            Map<String, String> criterias = new HashMap<String, String>();
            criterias.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR, ((CollectorHierarchy) bo).getPrincipalId());
            BusinessObjectService boService = (BusinessObjectService) SpringContext.getBean(BusinessObjectService.class);

            // remove old one collector informations from database
            CollectorHierarchy oldObj = (CollectorHierarchy) boService.findByPrimaryKey(CollectorHierarchy.class, criterias);
            if (ObjectUtils.isNotNull(oldObj)) {
                List<CollectorInformation> collInfos = oldObj.getCollectorInformations();
                if (ObjectUtils.isNotNull(collInfos) && !collInfos.isEmpty()) {
                    boService.delete(collInfos);
                }
            }

            super.saveBusinessObject();
        }

    }

}
