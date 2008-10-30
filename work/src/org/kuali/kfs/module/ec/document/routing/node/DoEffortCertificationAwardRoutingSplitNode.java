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
package org.kuali.kfs.module.ec.document.routing.node;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.engine.node.SplitNode;
import org.kuali.rice.kew.engine.node.SplitResult;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class DoEffortCertificationAwardRoutingSplitNode implements SplitNode {
    
    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        establishGlobalVariables();
        boolean shouldEffortCertificationRoute = ((EffortCertificationDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(routeContext.getDocument().getRouteHeaderId().toString())).isEffortDistributionChanged();
        List branchNames = new ArrayList();
        if (shouldEffortCertificationRoute) {
            branchNames.add("EffortDistributionIsChangedBranch");
        }
        else {
            branchNames.add("EffortDistributionIsNotChangedBranch");
        }
        return new SplitResult(branchNames);
    }
    protected void establishGlobalVariables() throws WorkflowException {
        if (GlobalVariables.getUserSession() == null) {
            GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
        }
        GlobalVariables.clear();
    }
}
