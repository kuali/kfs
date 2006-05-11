/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or
 * copying this Original Work, you agree that you have read, understand, and
 * will comply with the terms and conditions of the Educational Community
 * License. You may obtain a copy of the License at:
 * http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS",
 * WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.kuali.workflow.postprocessor;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.UserSession;
import org.kuali.core.bo.DocumentStatusChange;
import org.kuali.core.bo.RouteNodeChange;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;


/**
 * This class is the postProcessor for the Kuali application, and it is responsible for plumbing events up to documents
 * using the built into the document methods for handling route status and other routing changes that take place asyncronously
 * and potentially on a different server.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);
    
/**
 * Handle route status change events that are bubbled to us from workflow using the document service to
 * 
 */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws RemoteException {
        LOG.debug("entering post processor");
        try {
            if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(statusChangeEvent.getNewRouteStatus())
                    || EdenConstants.ROUTE_HEADER_SAVED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                LOG.debug("passing document " + statusChangeEvent.getRouteHeaderId() + " to DocumentService");
                DocumentStatusChange docStatChange = new DocumentStatusChange();
                docStatChange.setFinancialDocumentNumber(statusChangeEvent.getRouteHeaderId().toString());
                docStatChange.setStatusChangeEvent(statusChangeEvent.getNewRouteStatus());
                GlobalVariables.setUserSession(new UserSession(Constants.SCHEDULED_TASK_USER_ID));
                SpringServiceLocator.getDocumentService().handleDocumentRouteStatusChangeEvent(docStatChange);
            }
        } catch (Exception e) {
            LOG.error("Caught Exception handling StatusChangeEvent", e);
            throw new RuntimeException("Caught Exception handling StatusChangeEvent in KualiPostProcessor: " + e.getMessage(), e);
        }
        return true;
    }
    
    public boolean doActionTaken(ActionTakenEventVO event) throws RemoteException {
        return true;
    }
    public boolean doDeleteRouteHeader(DeleteEventVO event) throws RemoteException {
        return true;
    }
    public boolean doRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) throws RemoteException {
    	// on route level change we'll serialize the XML for the document
        LOG.debug("Invoking RouteLevelChange post processor");
        try {
                LOG.debug("passing document " + levelChangeEvent.getRouteHeaderId() + " to DocumentService");
                RouteNodeChange routeNodeChange = new RouteNodeChange();
                routeNodeChange.setFinancialDocumentNumber(levelChangeEvent.getRouteHeaderId().toString());
                routeNodeChange.setOldNodeName(levelChangeEvent.getOldNodeName());
                routeNodeChange.setNewNodeName(levelChangeEvent.getNewNodeName());
                GlobalVariables.setUserSession(new UserSession(Constants.SCHEDULED_TASK_USER_ID));
                SpringServiceLocator.getDocumentService().handleDocumentRouteNodeChangeEvent(routeNodeChange);
        } catch (Exception e) {
            LOG.error("Caught Exception handling StatusChangeEvent", e);
            throw new RuntimeException("Caught Exception handling StatusChangeEvent in KualiPostProcessor: " + e.getMessage(), e);
        }
        return true;
    }
}
