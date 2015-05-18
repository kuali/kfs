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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.document.Document;

import java.util.List;
import java.util.ListIterator;

//Adhoc Document Complete functionality utility
public class RouteToCompletionUtil {

    /***
     * Checks if there is atleast one Ad-Hoc Completion request for the document and based on that returns a boolean value.
     */
    public static boolean checkIfAtleastOneAdHocCompleteRequestExist(Document document) {
        boolean foundAtleastOneCompleteReq = false;
        // iterating the adhoc recpients list to check if there is atleast on complete request for the document.
        foundAtleastOneCompleteReq = loopAndCheckValue(document.getAdHocRouteWorkgroups()) || loopAndCheckValue(document.getAdHocRoutePersons());
        return foundAtleastOneCompleteReq;
    }

    /***
     * Loops and checks if the required value is present in the loop used for checking if there is atleast one adhoc completion
     * request present for a person or work group
     */
    public static boolean loopAndCheckValue(List adhoc) {
        if (adhoc == null) {
            return false;
        }
        ListIterator<AdHocRouteRecipient> groupIter = adhoc.listIterator();
        String valueToCheck = null;
        AdHocRouteRecipient recipient = null;
        boolean foundAtleastOneCompleteReq = false;
        while (groupIter.hasNext()) {
            recipient = groupIter.next();
            valueToCheck = recipient.getActionRequested();
            if (StringUtils.isNotEmpty(valueToCheck)) {
                if (KewApiConstants.ACTION_REQUEST_COMPLETE_REQ.equals(valueToCheck)) {
                    foundAtleastOneCompleteReq = true;
                    break;
                }
            }
        }
        return foundAtleastOneCompleteReq;
    }
}
