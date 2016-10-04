package edu.arizona.kfs.sys.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.service.DocuwareService;


// New URL to View DocuWare Images in KFS
public class DocuwareServiceImpl implements DocuwareService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocuwareServiceImpl.class);
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
    private ParameterService parameterService;
    
    public static int DEFAULT_VALID_UNTIL_TIMESPAN = 30;

    public static final String DOCUWARE_SERVICE = "DocuwareService";
    public static final String DOCUWARE_URL = "DOCUWARE_URL";
    public static final String DOCUWARE_API_KEY = "DOCUWARE_API_KEY";
    public static final String DOCUWARE_KFS_TABLE_GUIDS = "DOCUWARE_KFS_TABLE_GUIDS";
    public static final String DOCUWARE_VALID_UNTIL_NUM_MINUTES = "DOCUWARE_VALID_UNTIL_NUM_MINUTES";

    @Override
    public String getDocuwareUrl(String table, String documentId) {
        String retval = null;
        String docuwareUrl = parameterService.getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, DOCUWARE_SERVICE, DOCUWARE_URL);

        if (LOG.isDebugEnabled()) {
            LOG.debug("table=" + table + ", documentId=" + documentId + ", url=" + docuwareUrl);
        }
        
        if (docuwareUrl != null) {
            String apiKey = parameterService.getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, DOCUWARE_SERVICE, DOCUWARE_API_KEY);
            String validUntilMinutes = parameterService.getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, DOCUWARE_SERVICE, DOCUWARE_VALID_UNTIL_NUM_MINUTES);
            Collection <String>tableGuids = parameterService.getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.KFS, DOCUWARE_SERVICE, DOCUWARE_KFS_TABLE_GUIDS);
            
            if ((tableGuids != null)  && !tableGuids.isEmpty()  && StringUtils.isNotBlank(apiKey)) {
                Map<String, String> guidMap = new HashMap<String, String>();
                
                for (String s : tableGuids) {
                    StringTokenizer st = new StringTokenizer(s, "=");
                    
                    if (st.countTokens() == 2) {
                        guidMap.put(st.nextToken().trim(), st.nextToken().trim());
                    }
                }
                

                if (LOG.isDebugEnabled()) {
                    for (String key : guidMap.keySet()) {
                        LOG.debug(key + "=" + guidMap.get(key));
                    }
                }

                String tableGuid = guidMap.get(table);
                
                
                if (StringUtils.isNotBlank(tableGuid)) {
                    StringBuilder buf = new StringBuilder(256);
                    buf.append(docuwareUrl);
                    buf.append("?");
                    buf.append("lct=");
                    buf.append(apiKey);

                    buf.append("&");
                    buf.append("vu=");
                    buf.append(getValidUntilTimestampString(validUntilMinutes));

                    buf.append("&");
                    buf.append("p=V");

                    buf.append("&");
                    buf.append("fc=");
                    buf.append(tableGuid);

                    buf.append("&");
                    buf.append("q=");
                    buf.append(buildDocumentQuery(documentId));

                    retval = buf.toString();
                }
            }
        }
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("retval: " + retval);
        }
        
        return retval;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public String buildDocumentQuery(String documentId) {
        StringBuilder retval = new StringBuilder(128);
        
        retval.append("[DOCNBR]=");
        // fixes DocuWare URL integration problem
        if (StringUtils.isNotBlank(documentId)) {
            boolean needQuotes = StringUtils.isNumeric(documentId.trim());
            if (needQuotes) {
                retval.append("\"");
            }

            retval.append(documentId.trim());
            
            if (needQuotes) {
                retval.append("\"");
            }
        }
        
        return Base64.encodeBytes(retval.toString().getBytes());
    }
    
    private String getValidUntilTimestampString(String validUntilMinutes) {
        Calendar c = Calendar.getInstance();
        
        
        int minutes =  DEFAULT_VALID_UNTIL_TIMESPAN;
        
        try {
            minutes = Integer.parseInt(validUntilMinutes);
        }
        
        catch (NumberFormatException ex) {};
        
        c.add(Calendar.MINUTE, minutes);
        
        return DF.format(c.getTime()).replace(' ', 'T');
    }
}
