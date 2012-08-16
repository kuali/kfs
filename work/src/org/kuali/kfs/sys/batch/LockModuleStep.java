/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.LockModuleService;

/***
 * The step to lock or unlock module for the batch  
 */
public class LockModuleStep extends AbstractStep{
   
    private boolean lockModule;
    private LockModuleService lockModuleService;
    private String namespaceCode;
    
    public boolean execute(String jobName, Date jobRunDate)
    throws InterruptedException {
            String openStr = "lock";
            if(!lockModule) {
                openStr = "unlock";
            }
            lockModuleService.lockModule(namespaceCode, lockModule);
            return true;
    }
   
    /**
     * 
     * @return true if module to be locked , otherwise false
     */
    public boolean isLockModule() {
        return lockModule;
    }

    /**
     * set to true if module to be locked, otheriwse false
     * @param lockModule
     */
    public void setLockModule(boolean lockModule) {
        this.lockModule = lockModule;
    }

    public LockModuleService getLockModuleService() {
        return lockModuleService;
    }

    public void setLockModuleService(LockModuleService lockModuleService) {
        this.lockModuleService = lockModuleService;
    }

    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }
    
}
