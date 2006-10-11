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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Status extends BusinessObjectBase {

	private String statusCode;
	private String statusDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
    protected String ojbConcreteClass; // attribute needed for OJB polymorphism - do not alter!

	/**
	 * Default constructor.
	 */
	public Status() {

	}

    /**
     * Gets the statusCode attribute.
     * 
     * @return - Returns the statusCode
     * 
     */
	public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute.
     * 
     * @param - statusCode The statusCode to set.
     * 
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the statusDescription attribute.
     * 
     * @return - Returns the statusDescription
     * 
     */
    public String getStatusDescription() {
        return statusDescription;
    }

    /**
     * Sets the statusDescription attribute.
     * 
     * @param - statusDescription The statusDescription to set.
     * 
     */
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return - Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param - dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}

    /**
     * Gets the ojbConcreteClass attribute. 
     * 
     * @return Returns the ojbConcreteClass.
     */
    public String getOjbConcreteClass() {
        return ojbConcreteClass;
    }

    /**
     * Sets the ojbConcreteClass attribute value.
     * 
     * @param ojbConcreteClass The ojbConcreteClass to set.
     */
    public void setOjbConcreteClass(String ojbConcreteClass) {
        this.ojbConcreteClass = ojbConcreteClass;
    }

    /**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("statusCode", this.statusCode);
	    return m;
    }
}
