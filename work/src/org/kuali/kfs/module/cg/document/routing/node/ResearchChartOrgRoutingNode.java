/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.workflow.module.kra.node;


import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.node.RouteNode;

public class ResearchChartOrgRoutingNode {

    private static final String DYNAMIC_RSP_PROCESS_NAME = "DynamicRSP";

    protected RouteNode getChartOrgNode(String chart, String org, DocumentType documentType) {
        // we're at RSP if chart=org
        if (chart.equals(org)) {
            return documentType.getNamedProcess(DYNAMIC_RSP_PROCESS_NAME).getInitialRouteNode();
        }
        return documentType.getNamedProcess("ChartOrg").getInitialRouteNode();
        // return super.getChartOrgNode(chart, org, documentType);
    }
}
