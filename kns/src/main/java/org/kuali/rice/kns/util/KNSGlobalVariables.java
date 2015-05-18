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

import org.kuali.rice.core.framework.util.ApplicationThreadLocal;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public final class KNSGlobalVariables {

    private KNSGlobalVariables() {
        throw new UnsupportedOperationException("do not call");
    }

    private static ThreadLocal<KualiForm> kualiForms = new ApplicationThreadLocal<KualiForm>();

    private static ThreadLocal<MessageList> messageLists = new ApplicationThreadLocal<MessageList>() {
        @Override
        protected MessageList initialValue() {
            return new MessageList();
        }
    };

    private static ThreadLocal<HashMap<String, AuditCluster>> auditErrorMaps = new ApplicationThreadLocal<HashMap<String, AuditCluster>>() {
    	@Override
    	protected HashMap<String, AuditCluster> initialValue() {
    		return new HashMap<String, AuditCluster>();
    	}
    };

    /**
     * @return ArrayList containing messages.
     */
    @Deprecated
    public static MessageList getMessageList() {
        return messageLists.get();
    }

    /**
     * Sets a new message list
     *
     * @param messageList
     */
    @Deprecated
    public static void setMessageList(MessageList messageList) {
        messageLists.set(messageList);
    }

    /**
     * @return KualiForm that has been assigned to this thread of execution.
     */
    @Deprecated
    public static KualiForm getKualiForm() {
        return kualiForms.get();
    }

    /**
     * @return ArrayList containing audit error messages.
     */
    @Deprecated
    public static Map<String, AuditCluster> getAuditErrorMap() {
        return auditErrorMaps.get();
    }

    /**
     * Sets a new (clean) AuditErrorList
     *
     * @param errorMap
     */
    @Deprecated
    public static void setAuditErrorMap(HashMap<String, AuditCluster> errorMap) {
        auditErrorMaps.set(errorMap);
    }

    /**
     * sets the kualiForm object into the global variable for this thread
     *
     * @param kualiForm
     */
    @Deprecated
    public static void setKualiForm(KualiForm kualiForm) {
        kualiForms.set(kualiForm);
    }

    @Deprecated
    public static void clear() {
        GlobalVariables.clear();
        messageLists.set(new MessageList());
        auditErrorMaps.set(new HashMap<String, AuditCluster>());
        kualiForms.set(null);
    }
}
