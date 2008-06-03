/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.workflow.module.effort.node;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.rice.kns.util.KNSConstants;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;
import edu.iu.uis.eden.exception.WorkflowException;

public class DoEffortCertificationRecreateRoutingSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        establishGlobalVariables();
        boolean shouldEffortCertificationRoute = ((EffortCertificationDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(routeContext.getDocument().getRouteHeaderId().toString())).getEffortCertificationDocumentCode();
        List branchNames = new ArrayList();
        if (shouldEffortCertificationRoute) {
            branchNames.add("RecreateWorkgroupBranch");
        }
        else {
            branchNames.add("NoRecreateWorkgroupBranch");
        }
        return new SplitResult(branchNames);
    }

    protected void establishGlobalVariables() throws WorkflowException, UserNotFoundException {
        if (GlobalVariables.getUserSession() == null) {
            GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
        }
        GlobalVariables.clear();
    }
}
