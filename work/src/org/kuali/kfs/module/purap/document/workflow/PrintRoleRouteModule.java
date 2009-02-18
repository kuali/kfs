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
package org.kuali.kfs.module.purap.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.role.RoleRouteModule;
import org.kuali.rice.kns.exception.KualiException;

public class PrintRoleRouteModule extends RoleRouteModule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PrintRoleRouteModule.class);

    @Override
    public List<ActionRequestValue> findActionRequests(RouteContext context) {
       List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
        try {
            actionRequests = super.findActionRequests(context);
            for (ActionRequestValue actionRequest : actionRequests) {
                // recurse through actionRequest children, setting the same label on each
                labelActionRequest(actionRequest);
            }
        }
        catch (Exception e) {
            LOG.error("Problem retrieving workflow action requests.", e);
            throw new KualiException(e);
        }
        return actionRequests;
    }

    private void labelActionRequest(ActionRequestValue actionRequest) {
        actionRequest.setRequestLabel("PRINT");
        for (ActionRequestValue child : actionRequest.getChildrenRequests())
        {
          labelActionRequest(child);
        }
    }

}
