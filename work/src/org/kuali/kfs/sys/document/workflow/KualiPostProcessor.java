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
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.Constants;
import org.kuali.core.UserSession;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;


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
            LOG.info(new StringBuffer("started handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
            GlobalVariables.setUserSession(new UserSession(KualiUser.SYSTEM_USER));
            Document document = SpringServiceLocator.getDocumentService().getByDocumentHeaderId(statusChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                if (!EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                    throw new RuntimeException("unable to load document " + statusChangeEvent.getRouteHeaderId());
                }
            }
            else {
                // PLEASE READ BEFORE YOU MODIFY:
                // we dont want to update the document on a Save, as this will cause an
                // OptimisticLockException in many cases, because the DB versionNumber will be
                // incremented one higher than the document in the browser, so when the user then
                // hits Submit or Save again, the versionNumbers are out of synch, and the
                // OptimisticLockException is thrown. This is not the optimal solution, and will
                // be a problem anytime where the user can continue to edit the document after a
                // workflow state change, without reloading the form.
                if (!document.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
                    document.handleRouteStatusChange();
                    if (document.getDocumentHeader().getWorkflowDocument().stateIsCanceled() || document.getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || document.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                        document.getDocumentHeader().setDocumentFinalDate(new java.sql.Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
                    }
                    SpringServiceLocator.getDocumentService().updateDocument(document);
                }
            }
            LOG.info(new StringBuffer("finished handling route status change from ").append(statusChangeEvent.getOldRouteStatus()).append(" to ").append(statusChangeEvent.getNewRouteStatus()).append(" for document ").append(statusChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            logAndRethrow("route status", e);
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
        // on route level change we'll serialize the XML for the document. we
        // are doing this here cause it's a heavy hitter, and we
        // want to avoid the user waiting for this during sync processing
        try {
            LOG.debug(new StringBuffer("started handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
            GlobalVariables.setUserSession(new UserSession(KualiUser.SYSTEM_USER));
            Document document = SpringServiceLocator.getDocumentService().getByDocumentHeaderId(levelChangeEvent.getRouteHeaderId().toString());
            if (document == null) {
                throw new RuntimeException("unable to load document " + levelChangeEvent.getRouteHeaderId());
            }
            document.populateDocumentForRouting();
            document.getDocumentHeader().getWorkflowDocument().saveRoutingData();
            LOG.debug(new StringBuffer("finished handling route level change from ").append(levelChangeEvent.getOldRouteLevel()).append(" to ").append(levelChangeEvent.getNewRouteLevel()).append(" for document ").append(levelChangeEvent.getRouteHeaderId()));
        }
        catch (Exception e) {
            logAndRethrow("route level", e);
        }
        return true;
    }

    private void logAndRethrow(String changeType, Exception e) throws RuntimeException {
        LOG.error("caught exception while handling " + changeType + " change", e);
        logOptimisticDetails(5, e);

        throw new RuntimeException("post processor caught exception while handling " + changeType + " change: " + e.getMessage(), e);
    }

    /**
     * Logs further details of OptimisticLockExceptions, using the given depth value to limit recursion Just In Case
     * 
     * @param depth
     * @param t
     */
    private void logOptimisticDetails(int depth, Throwable t) {
        if ((depth > 0) && (t != null)) {
            if (t instanceof OptimisticLockException) {
                OptimisticLockException o = (OptimisticLockException) t;

                LOG.error("source of OptimisticLockException = " + o.getSourceObject().getClass().getName() + " ::= " + o.getSourceObject());
            }
            else {
                Throwable cause = t.getCause();
                if (cause != t) {
                    logOptimisticDetails(--depth, cause);
                }
            }
        }
    }
}