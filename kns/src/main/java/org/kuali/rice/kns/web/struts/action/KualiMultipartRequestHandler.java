/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.web.struts.action;

import org.apache.log4j.Logger;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.CommonsMultipartRequestHandler;

import java.util.List;

/**
 * Subclass of the MultipartRequestHandler used by Struts.  This one allows the maximum upload size to be set
 * by the application rather than by an init parameter. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiMultipartRequestHandler extends CommonsMultipartRequestHandler {
    private static final Logger LOG = Logger.getLogger(KualiMultipartRequestHandler.class);

    private String sizeMax;
    
    /**
     * Returns the maximum allowable size, in bytes, of an uploaded file. The
     * value is obtained from the current module's controller configuration.
     *
     * @param mc The current module's configuration.
     *
     * @return The maximum allowable file size, in bytes.
     */
    public long getSizeMax(ModuleConfig mc) {
        return convertSizeToBytes( sizeMax, super.getSizeMax(mc) );
    }

    public String getSizeMaxString() {
        return sizeMax;
    }    

    public void setSizeMax( String sizeString ) {
    	this.sizeMax = sizeString;
    }
    
//    public long convertSizeToBytes(String sizeString, long defaultSize) {
//	return super.convertSizeToBytes(sizeString, defaultSize);
//    }
    
    /**
     * Sets the max size string to the item in the list that represents the largest size.
     */
    public void setMaxUploadSizeToMaxOfList( List<String> sizes ) {
	long maxSize = 0L;
	for ( String size : sizes ) {
	    long currSize = convertSizeToBytes(size, 0L);
	    if ( currSize == 0L ) {
		LOG.warn( "Unable to parse max size (" + size + ").  Ignoring." );
	    }
	    if ( currSize > maxSize ) {
		maxSize = currSize;
		sizeMax = size;
	    }
	}
    }
    
    public long calculateMaxUploadSizeToMaxOfList( List<String> sizes ) {
    	long maxSize = 0L;
    	for ( String size : sizes ) {
    	    long currSize = convertSizeToBytes(size, 0L);
    	    if ( currSize == 0L ) {
    		LOG.warn( "Unable to parse max size (" + size + ").  Ignoring." );
    	    }
    	    if ( currSize > maxSize ) {
    		maxSize = currSize;    		
    	    }
    	}
    	return maxSize;
    }
    
}
