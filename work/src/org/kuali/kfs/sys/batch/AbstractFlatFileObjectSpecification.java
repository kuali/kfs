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

import java.util.List;

/**
 * Abstract class representing a FlatFilePrefixObjectSpecification - something that helps parse a line into an
 * object.
 */
public abstract class AbstractFlatFileObjectSpecification implements FlatFileObjectSpecification {

    protected Class<?> businessObjectClass;
    protected List <FlatFilePropertySpecification> parseProperties;
    protected Class<?> parentBusinessObjectClass;
    protected String parentTargetProperty;

    public AbstractFlatFileObjectSpecification() {
        super();
    }

    public Class<?> getBusinessObjectClass() {
        return businessObjectClass;
    }

    public void setBusinessObjectClass(Class<?> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    public List<FlatFilePropertySpecification> getParseProperties() {
        return parseProperties;
    }

    public void setParseProperties(List<FlatFilePropertySpecification> parseProperties) {
        this.parseProperties = parseProperties;
    }

    public Class<?> getParentBusinessObjectClass() {
    	return parentBusinessObjectClass;
    }

    public void setParentBusinessObjectClass(Class<?> parentBusinessObjectClass) {
    	this.parentBusinessObjectClass = parentBusinessObjectClass;
    }

    public String getParentTargetProperty() {
    	return parentTargetProperty;
    }

    public void setParentTargetProperty(String parentProperty) {
    	this.parentTargetProperty = parentProperty;
    }

}