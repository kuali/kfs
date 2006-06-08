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
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * This class is the postProcessor for the Kuali application, and it is responsible for plumbing events up to documents using the
 * built into the document methods for handling route status and other routing changes that take place asyncronously and potentially
 * on a different server.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);

    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws RemoteException {
        try {
            LOG.debug(new StringBuffer("started handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
            GlobalVariables.setUserSession(new UserSession(Constants.SCHEDULED_TASK_USER_ID));
            Document document = SpringServiceLocator.getDocumentService().getByDocumentHeaderId(statusChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                if (!EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                    throw new RuntimeException("unable to load document " + statusChangeEvent.getRouteHeaderId());
                }
            }
            else {
                document.handleRouteStatusChange(statusChangeEvent.getNewRouteStatus());
            }
            LOG.debug(new StringBuffer("finished handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            LOG.error("caught exception while handling route status change", e);
            throw new RuntimeException("post processor caught exception while handling route status change: " + e.getMessage(), e);
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
        // on route level change we'll serialize the XML for the document. we are doing this here cause it's a heavy hitter, and we
        // want to avoid the user waiting for this during sync processing
        try {
            LOG.debug(new StringBuffer("started handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
            GlobalVariables.setUserSession(new UserSession(Constants.SCHEDULED_TASK_USER_ID));
            Document document = SpringServiceLocator.getDocumentService().getByDocumentHeaderId(levelChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                throw new RuntimeException("unable to load document " + levelChangeEvent.getRouteHeaderId());
            }
            document.populateDocumentForRouting();
            document.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            LOG.debug(new StringBuffer("finished handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            LOG.error("caught exception while handling route level change", e);
            throw new RuntimeException("post processor caught exception while handling route level change: " + e.getMessage(), e);
        }
        return true;
    }
}