package edu.arizona.kfs.sys.web.servlet;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.sys.context.SpringContext;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.service.DocuwareService;

public class DocuwareCaller extends HttpServlet {
    private DocuwareService docuwareService = SpringContext.getBean(DocuwareService.class);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String docuwareidvalue = request.getParameter(KFSConstants.DOCUWARE_IDVALUE);
        String docuwareTableValue = request.getParameter(KFSConstants.DOCUWARE_TABLE);
        
        if (StringUtils.isNotBlank(docuwareTableValue) && StringUtils.isNotBlank(docuwareidvalue)) {
            String url= docuwareService.getDocuwareUrl(docuwareTableValue, docuwareidvalue);
        
            if (StringUtils.isNotBlank(url)) {
                response.sendRedirect(url);
            }
        }
    }
}
