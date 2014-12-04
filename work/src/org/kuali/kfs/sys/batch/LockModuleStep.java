/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
