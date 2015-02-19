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
