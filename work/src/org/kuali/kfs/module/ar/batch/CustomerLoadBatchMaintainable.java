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

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.rice.kns.bo.PersistableBusinessObject;

public class CustomerLoadBatchMaintainable implements BatchMaintainable {

    private boolean boExists = false;
    private Customer customer;
    
    public CustomerLoadBatchMaintainable() {}
    
    public CustomerLoadBatchMaintainable(PersistableBusinessObject object) {
        setBusinessObject(object);
    }
    
    public PersistableBusinessObject getBusinessObject() {
        return customer;
    }

    public boolean isNew() {
        return !boExists;
    }

    public void setNew(boolean isNew) {
        this.boExists = !isNew;
    }
    
    public boolean isUpdate() {
        return boExists;
    }
    
    public void setUpdate(boolean isUpdate) {
        this.boExists = isUpdate;
    }

    public void setBusinessObject(PersistableBusinessObject object) {
        if (!(object instanceof Customer)) {
            throw new IllegalArgumentException("Parameter passed in should be instanceof " + 
                    "Customer, instead was [" + object.getClass().toString() + "]");
        }
        this.customer = (Customer) object;
    }

}
