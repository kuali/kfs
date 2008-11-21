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
package org.kuali.kfs.sys.document.routing;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.SimpleNode;
import org.kuali.rice.kew.engine.node.SimpleResult;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;

/**
 * This class...
 */
public class SimpleBooleanSplitNode implements SimpleNode {

    /**
     * @see org.kuali.rice.kew.engine.node.SimpleNode#process(org.kuali.rice.kew.engine.RouteContext, org.kuali.rice.kew.engine.RouteHelper)
     */
    public SimpleResult process(RouteContext context, RouteHelper helper) throws Exception {
        SimpleResult result = null;
        
        String documentID = context.getDocument().getRouteHeaderId().toString();
        Document document = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentID);
        String nodeName = context.getNodeInstance().getRouteNode().getRouteNodeName();
        if (document instanceof FinancialSystemTransactionalDocumentBase) {
           boolean ret = ((FinancialSystemTransactionalDocumentBase)document).answerSplitNodeQuestion(nodeName);
           result = new SimpleResult(ret);
        } else if (document instanceof FinancialSystemMaintenanceDocument) {
            boolean ret = ((FinancialSystemMaintenanceDocument) document).answerSplitNodeQuestion(nodeName);
            result = new SimpleResult(ret);
        } else {
            throw new IllegalArgumentException("Document with id " + documentID + " is not an instace of " + FinancialSystemMaintenanceDocument.class + " or " + FinancialSystemTransactionalDocument.class);
        }
        
        return result;
    }

}
