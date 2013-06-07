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

import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CollectorInformationFixture {
    
    COLLECTORINFORMATION1(UserNameFixture.khuntley.toString(), UserNameFixture.khuntley.toString(), true);
    
    public String principalName;
    public boolean activeIndicator;
    public String collectorHead;
    
    private CollectorInformationFixture(String principalName, String collectorHead, boolean activeIndicator){
        this.principalName = principalName;
        this.activeIndicator = activeIndicator;
        this.collectorHead = collectorHead;
    }
    
    public CollectorInformation assignCollectorToHead(){
        
        CollectorInformation collectorInfo = new CollectorInformation();
        collectorInfo.setPrincipalName(principalName);
        collectorInfo.setPrincipalId(UserNameFixture.khuntley.getPerson().getPrincipalId());
        collectorInfo.setActive(activeIndicator);
        collectorInfo.setHeadPrincipalId(UserNameFixture.khuntley.getPerson().getPrincipalId());
        
        return collectorInfo;
    }
}
