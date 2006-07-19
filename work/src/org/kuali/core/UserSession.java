/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *  
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
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;

import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class represents a User Session
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class UserSession implements Serializable {

    private static final long serialVersionUID = 4532616762540067557L;

    private static final Logger LOG = Logger.getLogger(UserSession.class);

    private KualiUser kualiUser;
    private KualiUser backdoorUser;
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
        this.kualiUser = SpringServiceLocator.getKualiUserService().getUser(new AuthenticationUserId(networkId));
        this.workflowUser = SpringServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdVO(networkId));
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
            return kualiUser.getPersonUserIdentifier();
        }
    }

    /**
     * This returns who is logged in. If the backdoor is in use, this will return the network id of the person that is standing in
     * as the backdoor user.
     * 
     * @return String
     */
    public String getLoggedInUserNetworkId() {
        return kualiUser.getPersonUserIdentifier();
    }

    /**
     * @return the KualiUser which is the current user in the system, backdoor if backdoor is set
     */
    public KualiUser getKualiUser() {
        if (backdoorUser != null) {
            return backdoorUser;
        }
        else {
            return kualiUser;
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
        // TODO - determine how we will get the environment configuration - KULCFG-17
        this.backdoorUser = SpringServiceLocator.getKualiUserService().getUser(new AuthenticationUserId(networkId));
        this.backdoorWorkflowUser = SpringServiceLocator.getWorkflowInfoService().getWorkflowUser(new NetworkIdVO(networkId));
        this.workflowDocMap = new HashMap();
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

    private void refreshUserGroups(KualiUser user) {
        KualiGroupService kualiGroupService = SpringServiceLocator.getKualiGroupService();
        user.setGroups(kualiGroupService.getUsersGroups(kualiUser));
    }
}