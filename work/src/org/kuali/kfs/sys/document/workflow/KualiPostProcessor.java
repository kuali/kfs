/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kuali.workflow.postprocessor;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.core.UserSession;
import org.kuali.core.bo.DocumentStatusChange;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;

/**
 * @author bmcgough
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);
    

    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws RemoteException {
        LOG.debug("entering post processor");
        try {
            if (EdenConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_CANCEL_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_DISAPPROVED_CD.equals(statusChangeEvent.getNewRouteStatus()) || EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                LOG.debug("passing document " + statusChangeEvent.getRouteHeaderId() + " to DocumentService");
                DocumentStatusChange docStatChange = new DocumentStatusChange();
                docStatChange.setFinancialDocumentNumber(statusChangeEvent.getRouteHeaderId().toString());
                docStatChange.setStatusChangeEvent(statusChangeEvent.getNewRouteStatus());
                GlobalVariables.setUserSession(new UserSession(Constants.SCHEDULED_TASK_USER_ID));
                SpringServiceLocator.getDocumentService().handleDocumentRouteStatusChangeEvent(docStatChange);
                // writeOutDocumentStatusChange(statusChangeEvent);org.kuali.workflow.postprocessor.KualiPostProcessor
            }
        } catch (Exception e) {
            LOG.error("Caught Exception handing StatusChangeEvent", e);
            throw new RuntimeException(e.getMessage());
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
        return true;
    }
}
