/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PrintInvoiceOptions extends PersistableBusinessObjectBase implements MutableInactivatable {

	private String printInvoiceIndicator;
	private String printInvoiceDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PrintInvoiceOptions() {

	}

	/**
	 * Gets the printInvoiceIndicator attribute.
	 * 
	 * @return Returns the printInvoiceIndicator
	 * 
	 */
	public String getPrintInvoiceIndicator() { 
		return printInvoiceIndicator;
	}

	/**
	 * Sets the printInvoiceIndicator attribute.
	 * 
	 * @param printInvoiceIndicator The printInvoiceIndicator to set.
	 * 
	 */
	public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
		this.printInvoiceIndicator = printInvoiceIndicator;
	}


	/**
	 * Gets the printInvoiceDescription attribute.
	 * 
	 * @return Returns the printInvoiceDescription
	 * 
	 */
	public String getPrintInvoiceDescription() { 
		return printInvoiceDescription;
	}

	/**
	 * Sets the printInvoiceDescription attribute.
	 * 
	 * @param printInvoiceDescription The printInvoiceDescription to set.
	 * 
	 */
	public void setPrintInvoiceDescription(String printInvoiceDescription) {
		this.printInvoiceDescription = printInvoiceDescription;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("printInvoiceIndicator", this.printInvoiceIndicator);
	    return m;
    }
}
