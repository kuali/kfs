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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public enum RouteNodeFixture {

    REC1 {
        @Override
        public RouteNode newRecord() {
            RouteNode obj = new RouteNode();
            obj.setRouteNodeId(21L);
            obj.setDocumentTypeId(31920L);
            obj.setRouteNodeName("Adhoc Routing");
            obj.setNodeType("org.kuali.rice.kew.engine.node.InitialNode");
            obj.setRouteMethodName("WorkflowDocumentTemplate");
            obj.setFinalApprovalInd(false);
            obj.setMandatoryRouteInd(false);
            obj.setExceptionWorkgroupId("2752");
            obj.setActivationType("S");
            return obj;
        };
    },
    REC2 {
        @Override
        public RouteNode newRecord() {
            RouteNode obj = new RouteNode();
            obj.setRouteNodeId(22L);
            obj.setDocumentTypeId(31920L);
            obj.setRouteNodeName("Adhoc Routing");
            obj.setNodeType("org.kuali.rice.kew.engine.node.InitialNode");
            obj.setRouteMethodName("WorkflowDocumentTemplate");
            obj.setFinalApprovalInd(false);
            obj.setMandatoryRouteInd(false);
            obj.setExceptionWorkgroupId("2752");
            obj.setActivationType("S");
            return obj;
        };
    },
    REC3 {
        @Override
        public RouteNode newRecord() {
            RouteNode obj = new RouteNode();
            obj.setRouteNodeId(23L);
            obj.setDocumentTypeId(31920L);
            obj.setRouteNodeName("Adhoc Routing");
            obj.setNodeType("org.kuali.rice.kew.engine.node.InitialNode");
            obj.setRouteMethodName("WorkflowDocumentTemplate");
            obj.setFinalApprovalInd(false);
            obj.setMandatoryRouteInd(false);
            obj.setExceptionWorkgroupId("2752");
            obj.setActivationType("S");
            return obj;
        };
    };
    public abstract RouteNode newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());
    }

    private static List<RouteNode> getAll() {
        List<RouteNode> recs = new ArrayList<RouteNode>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        return recs;
    }
}
