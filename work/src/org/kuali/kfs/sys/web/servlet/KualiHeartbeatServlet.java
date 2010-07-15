package org.kuali.kfs.sys.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

public class KualiHeartbeatServlet extends HttpServlet {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiHeartbeatServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 4901222949286730892L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp){
		this.doPost(req, resp);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp){
        StringBuilder sb = new StringBuilder(200);
        sb.append("<html><head><title>heartbeat</title></head><body>");
		try {
	        ArrayList<Chart> hbResult = (ArrayList<Chart>) SpringContext.getBean(BusinessObjectService.class).findAll(Chart.class);
		    // force a call to KIM
	        if ( hbResult.isEmpty() ) {
	            sb.append( "NO CHARTS RETRIEVED");
	        } else {
	            // we don't care what it returns, only that the call to KIM does not bomb out
	            hbResult.get(0).getFinCoaManager();
	            sb.append( "LUB-DUB,LUB-DUB");
	        }
		} catch ( Exception ex ){
		    sb.append( "Exception running heartbeat monitor: " + ex.getClass() + ": " + ex.getMessage() );
		    StringWriter sw = new StringWriter(1000);
		    PrintWriter pw = new PrintWriter( sw );
		    ex.printStackTrace( pw );
		    sb.append( sw.toString() );
		    LOG.fatal( "Failed to detect heartbeat.  Apply paddles now!", ex);
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
