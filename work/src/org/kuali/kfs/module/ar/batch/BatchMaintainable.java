/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch;

import org.kuali.rice.kns.bo.PersistableBusinessObject;

/**
 * 
 * This interface contains methods related to creating a Maintainable 
 * from a batch upload process.
 * 
 */
public interface BatchMaintainable {

    /**
     * 
     * Determines whether the BusinessObject contained is a New BO of its type.
     * @return True if it will generate a New BO entry, False if it will not.
     */
    public boolean isNew();
    
    public void setNew(boolean isNew);
    
    /**
     * 
     * Determines whether the BusinessObject contained already exists in the system, and will 
     * result in an Update of its type.
     * @return True if it will generate an Update, False if not.
     */
    public boolean isUpdate();
    
    public void setUpdate(boolean isUpdate);
    
    /**
     * 
     * Sets the BO this maintainable is concerned with.
     * @param object The object that will be maintained.
     */
    public void setBusinessObject(PersistableBusinessObject object);
    
    /**
     * 
     * Returns the BO this maintainble is concerned with.
     * @return The object that will be maintained.
     */
    public PersistableBusinessObject getBusinessObject();
    
}
