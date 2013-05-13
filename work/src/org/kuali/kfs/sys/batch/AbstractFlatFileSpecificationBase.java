/*
 * Copyright 2011 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;


/**
 * Abstract class which is the parent of all FlatFileSpecification implementations
 */
public abstract class AbstractFlatFileSpecificationBase implements FlatFileSpecification, InitializingBean {
    protected Class<?> defaultBusinessObjectClass;
    protected List<FlatFileObjectSpecification> objectSpecifications;
    private List<Class<?>> childrenList = new ArrayList<Class<?>>();
    
    /**
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#getObjectSpecification(Class)
     */
    public FlatFileObjectSpecification getObjectSpecification(Class<?> businessObjectClass) {
        for (FlatFileObjectSpecification objectSpecification : objectSpecifications) {
            if (objectSpecification.getBusinessObjectClass().equals(businessObjectClass)) {
                return objectSpecification;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.FlatFileSpecification#getObjectSpecifications()
     */
    public List<FlatFileObjectSpecification> getObjectSpecifications() {
        return objectSpecifications;
    }
    

    /**
     * Sets the list of FlatFileObjectSpecifications which instruct the FlatFileSpecification how to parse
     * @param objectSpecifications
     */
    public void setObjectSpecifications(List<FlatFileObjectSpecification> objectSpecifications) {
        this.objectSpecifications = objectSpecifications;
    }

    public void setDefaultBusinessObjectClass(Class<?> businessObjectClass) {
        this.defaultBusinessObjectClass = businessObjectClass;
    }
    
    public void afterPropertiesSet() throws Exception{
        Class<?> child;
        for(FlatFileObjectSpecification specification: objectSpecifications) {
            if(specification.getParentBusinessObjectClass() != null) {
                child = specification.getBusinessObjectClass();
                if(childrenList.contains(child)) {
                    //A child can't appear twice within the same configuration, without having two parents
                    throw new Exception("The child " + child + " can't have more than one parent");
                } else {
                    childrenList.add(child);
                }
            }
        }
    }

}