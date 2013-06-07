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
package org.kuali.kfs.module.ar.fixture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public enum CollectorHierarchyFixture {
    
    COLLECTORHIERARCHY1(UserNameFixture.khuntley.toString(), true);
    
    public String principalName;
    public boolean activeIndicator;
    
    private CollectorHierarchyFixture(String principalName, boolean activeIndicator){
        this.principalName = principalName;
        this.activeIndicator = activeIndicator;
    }
    
    public CollectorHierarchy createCollectorHead(){
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        Map<String, String> map = new HashMap<String, String>();
        map.put(ArPropertyConstants.CollectorHierarchyFields.COLLECTOR, UserNameFixture.khuntley.toString());
        CollectorHierarchy collector = (CollectorHierarchy) businessObjectService.findByPrimaryKey(CollectorHierarchy.class, map);
        
        if(ObjectUtils.isNotNull(collector)) {
            //Set collector information
            collector = new CollectorHierarchy();
            List<CollectorInformation> collectorInfo = new ArrayList<CollectorInformation>();
            collectorInfo.add(CollectorInformationFixture.COLLECTORINFORMATION1.assignCollectorToHead());
            collector.setPrincipalName(principalName);
            collector.setPrincipalId(UserNameFixture.khuntley.getPerson().getPrincipalId());
            collector.setActive(activeIndicator);
            businessObjectService.save(collector);
        }
        

        return collector;
    }
}
