/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.workflow.module.vendor.node;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.context.SpringContext;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.engine.node.SplitNode;
import edu.iu.uis.eden.engine.node.SplitResult;

/**
 * Checks for conditions on a Vendor Maintenance document that allow auto-approval by the initiator. If these conditions are not
 * met, standard routing of the Vendor is performed. The conditions for auto-approval are: 1)
 */
public class VendorMaintenanceDocumentApprovalNoApprovalSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        boolean shouldVendorRoute = SpringContext.getBean(VendorService.class).shouldVendorRouteForApproval(routeContext.getDocument().getRouteHeaderId().toString());
        List branchNames = new ArrayList();
        if (shouldVendorRoute) {
            branchNames.add("ApprovalBranch");
        }
        else {
            branchNames.add("NoApprovalBranch");
        }
        return new SplitResult(branchNames);
    }
}
