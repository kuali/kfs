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
package org.kuali.rice.kns.util;

import java.util.Map;

/**
 * Inquiry screens and maintenance documents may render a collection of BOs on a screen.  These
 * BOs may implement {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable}, which means that the BO has an active
 * flag of true or false.  Some screens may give the user the ability to not render (i.e. hide) inactive
 * collection elements.  This class has several utilities to control that behavior. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class InactiveRecordsHidingUtils {
	
	private InactiveRecordsHidingUtils() {
		throw new UnsupportedOperationException("do not call");
	}
	
    /**
     * Returns whether a collection has been set to show inactive records.  Note that if a collection has not been set to show inactive inactive records, then
     * this method will return false.
     * 
     * @param inactiveRecordDisplay a Map used to keep state between invocations of this method and {@link #setShowInactiveRecords(Map, String, boolean)}
     * @param collectionName the name of the collection 
     * @return
     */
    public static boolean getShowInactiveRecords(Map<String, Boolean> inactiveRecordDisplay, String collectionName) {
	// by default, show the actives
        boolean showInactive = true;
        
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }
        // remove periods from the collection name due to parsing limitation in Apache beanutils 
        collectionName = collectionName.replace( '.', '_' );
        
        if (inactiveRecordDisplay.containsKey(collectionName)) {
            Object inactiveSetting = inactiveRecordDisplay.get(collectionName);
            
            // warren: i copied this code from somewhere else, and have no idea why they're testing to see whether it's a
            // Boolean, but I'm guessing that it has to do w/ the PojoFormBase not setting things correctly
            if (inactiveSetting instanceof Boolean) {
                showInactive = ((Boolean) inactiveSetting).booleanValue();
            }
            else {
                showInactive = Boolean.parseBoolean(((String[]) inactiveSetting)[0]);
            }
        }
        
        return showInactive;
    }
    
    /**
     * Sets whether a method should show inactive records
     * 
     * @param inactiveRecordDisplay a Map used to keep state between invocations of this method and {@link #getShowInactiveRecords(Map, String)}
     * @param collectionName the name of the collection
     * @param showInactive whether to show inactive records
     */
    public static void setShowInactiveRecords(Map<String, Boolean> inactiveRecordDisplay, String collectionName, boolean showInactive) {
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }
        
        // remove periods from the collection name due to parsing limitation in Apache beanutils 
        collectionName = collectionName.replace( '.', '_' );

        inactiveRecordDisplay.put(collectionName, new Boolean(showInactive));
    }
    
    public static String formatCollectionName(String collectionName) {
	return collectionName.replace( '.', '_' );
    }
}
