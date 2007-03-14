/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class represents a User Session
 * 
 * 
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 4532616762540067557L;

    private static final Logger LOG = Logger.getLogger(UserSession.class);

    private UniversalUser universalUser;
    private UniversalUser backdoorUser;
    private UserVO workflowUser;
    private UserVO backdoorWorkflowUser;
    private int nextObjectKey;
    private Map objectMap;

    private transient Map workflowDocMap = new HashMap();

    /**
     * Take in a netid, and construct the user from that.
     * 
     * @param networkId
     * @throws UserNotFoundException
     * @throws EdenUserNotFoundException
     * @throws ResourceUnavailableException
     */
    public UserSession(String networkId) throws UserNotFoundException, WorkflowException {
        this.universalUser = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(networkId));
        this.workflowUser = KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdVO(networkId));
        this.nextObjectKey = 0;
        this.objectMap = new HashMap();
    }

    /**
     * @return the networkId of the current user in the system, backdoor network id if backdoor is set
     */
    public String getNetworkId() {
        if (backdoorUser != null) {
            return backdoorUser.getPersonUserIdentifier();
        }
        else {
            return universalUser.getPersonUserIdentifier();
        }
    }

    /**
     * This returns who is logged in. If the backdoor is in use, this will return the network id of the person that is standing in
     * as the backdoor user.
     * 
     * @return String
     */
    public String getLoggedInUserNetworkId() {
        return universalUser.getPersonUserIdentifier();
    }

    /**
     * @return the KualiUser which is the current user in the system, backdoor if backdoor is set
     */
    public UniversalUser getUniversalUser() {
        if (backdoorUser != null) {
            return backdoorUser;
        }
        else {
            return universalUser;
        }
    }

    /**
     * @return the workflowUser which is the current user in the system, backdoor if backdoor is set
     */
    public UserVO getWorkflowUser() {
        if (backdoorUser != null) {
            return backdoorWorkflowUser;
        }
        else {
            return workflowUser;
        }
    }

    /**
     * override the current user in the system by setting the backdoor networkId, which is useful when dealing with routing or other
     * reasons why you would need to assume an identity in the system
     * 
     * @param networkId
     * @throws UserNotFoundException
     * @throws ResourceUnavailableException
     * @throws EdenUserNotFoundException
     */
    public void setBackdoorUser(String networkId) throws UserNotFoundException, WorkflowException {
       // only allow backdoor in non-production environments
       if ( !KNSServiceLocator.getKualiConfigurationService().isProductionEnvironment() ) {
        this.backdoorUser = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(networkId));
        this.backdoorWorkflowUser = KNSServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdVO(networkId));
        this.workflowDocMap = new HashMap();
       }
    }

    /**
     * clear the backdoor user
     * 
     */
    public void clearBackdoorUser() {
        this.backdoorUser = null;
        this.backdoorWorkflowUser = null;
        this.workflowDocMap = new HashMap();
    }

    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class
     * 
     * @param object
     * @return
     */
    public String addObject(Object object, String keyPrefix) {
        String objectKey = keyPrefix + nextObjectKey++;
        objectMap.put(objectKey, object);
        return objectKey;
    }

    /**
     * allows adding an arbitrary object to the session with static a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class
     * 
     * @param object
     * 
     */
    public void addObject(String key, Object object) {

        objectMap.put(key, object);

    }


    /**
     * allows adding an arbitrary object to the session and returns a string key that can be used to later access this object from
     * the session using the retrieveObject method in this class
     * 
     * @param object
     * @return
     */
    public String addObject(Object object) {
        String objectKey = nextObjectKey++ + "";
        objectMap.put(objectKey, object);
        return objectKey;
    }

    /**
     * allows for fetching an object that has been put into the userSession based on the key that would have been returned when
     * adding the object
     * 
     * @param objectKey
     * @return
     */
    public Object retrieveObject(String objectKey) {
        return this.objectMap.get(objectKey);
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on the key that would have been
     * assigned
     * 
     * @param objectKey
     */
    public void removeObject(String objectKey) {
        this.objectMap.remove(objectKey);
    }

    /**
     * allows for removal of an object from session that has been put into the userSession based on a key that starts with the given
     * prefix
     * 
     * @param objectKey
     */
    public void removeObjectsByPrefix(String objectKeyPrefix) {
        List removeKeys = new ArrayList();
        for (Iterator iter = objectMap.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (key.startsWith(objectKeyPrefix)) {
                removeKeys.add(key);
            }
        }

        for (Iterator iter = removeKeys.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            this.objectMap.remove(key);
        }
    }

    /**
     * @return boolean indicating if the backdoor is in use
     */
    public boolean isBackdoorInUse() {
        return backdoorUser != null;
    }

    /**
     * retrieve a flexdoc from the userSession based on the document id
     * 
     * @param docId
     * @return
     */
    public KualiWorkflowDocument getWorkflowDocument(String docId) {
        if (workflowDocMap == null) {
            workflowDocMap = new HashMap();
        }
        if (workflowDocMap.containsKey(docId)) {
            return (KualiWorkflowDocument) workflowDocMap.get(docId);
        }
        else {
            return null;
        }
    }

    /**
     * set a flexDoc into the userSession which will be stored under the document id
     * 
     * @param flexDoc
     */
    public void setWorkflowDocument(KualiWorkflowDocument workflowDocument) {
        try {
            if (workflowDocMap == null) {
                workflowDocMap = new HashMap();
            }
            workflowDocMap.put(workflowDocument.getRouteHeaderId().toString(), workflowDocument);
        }
        catch (WorkflowException e) {
            throw new IllegalStateException("could not save the document in the session msg: " + e.getMessage());
        }
    }

    private void refreshUserGroups(UniversalUser universalUser) {
        universalUser.refreshUserGroups();
    }
}