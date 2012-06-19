package org.kuali.kfs.sys.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;

public class KualiHeartbeatServlet extends HttpServlet {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiHeartbeatServlet.class);
	/**
	 *
	 */
	private static final long serialVersionUID = 4901222949286730892L;

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
		this.doPost(req, resp);
	}
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        StringBuilder sb = new StringBuilder(200);
        boolean errors = false;
        sb.append("<html><head><title>KFS Heartbeat Monitor</title></head><body>");
		try {
	        Collection<Chart> hbResult = SpringContext.getBean(BusinessObjectService.class).findAll(Chart.class);
		    // force a call to KIM
	        if ( hbResult == null || hbResult.isEmpty() ) {
	            sb.append( "ERROR: NO CHARTS RETRIEVED");
	            errors = true;
	        } else {
	            // we don't care what it returns, only that the call to KIM does not bomb out and returns *somebody*
	            Person p = hbResult.iterator().next().getFinCoaManager();
	            if ( p == null ) {
	                sb.append( "POTENTIAL PROBLEM: CHART MANAGER CALL TO KIM IMPLEMENTATION RETURNED NULL");
	                errors = true;
	            }
	        }
	        // attempt a workflow call
	        DocumentType docDTO = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(KFSConstants.ROOT_DOCUMENT_TYPE);
	        if ( docDTO == null  ) {
	            sb.append( "POTENTIAL PROBLEM: " + KFSConstants.ROOT_DOCUMENT_TYPE + " document type not found.  Missing KFS Document hierarchy." );
	            errors = true;
	        }
	        if ( !errors ) {
	            sb.append( "LUB-DUB,LUB-DUB");
	        }
		} catch ( Exception ex ){
		    sb.append( "Exception running heartbeat monitor: " + ex.getClass() + ": " + ex.getMessage() );
		    StringWriter sw = new StringWriter(1000);
		    PrintWriter pw = new PrintWriter( sw );
		    ex.printStackTrace( pw );
		    sb.append( sw.toString() );
		    LOG.fatal( "Failed to detect heartbeat.  Apply paddles stat!   beeeeeeeeeeeeeeeeeeeeeeeeeeep  It's dead Jim.", ex);
		} finally {
		    sb.append( "</body></html>");
    		try {
    			resp.getWriter().println(sb.toString());
    			resp.addHeader("Content-Type", "text/html");
    		} catch (IOException ex) {
                LOG.error( "Failure writing to heartbeat servlet output stream:", ex );
    		}
		}
	}
}
