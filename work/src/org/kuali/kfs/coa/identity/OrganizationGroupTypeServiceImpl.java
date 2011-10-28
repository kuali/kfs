/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.service.support.impl.KimGroupTypeServiceBase;

public class OrganizationGroupTypeServiceImpl extends KimGroupTypeServiceBase {
    protected static final String DOCUMENT_TYPE_NAME = "ORG";

// RICE_20_INSERT    List<String> workflowRoutingAttributes = new ArrayList<String>(2);
    {
        workflowRoutingAttributes.add( KfsKimAttributes.CHART_OF_ACCOUNTS_CODE );
        workflowRoutingAttributes.add( KfsKimAttributes.ORGANIZATION_CODE );
    }    
    
// RICE_20_INSERT    @Override
// RICE_20_INSERT    public List<String> getWorkflowRoutingAttributes(String routeLevel) {
// RICE_20_INSERT        return Collections.unmodifiableList(workflowRoutingAttributes);
// RICE_20_INSERT    }
    
    @Override
    public String getWorkflowDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }
}
