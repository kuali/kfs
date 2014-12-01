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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddressType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerAddressType extends PersistableBusinessObjectBase implements MutableInactivatable, AccountsReceivableCustomerAddressType {

	private String customerAddressTypeCode;
	private String customerAddressTypeDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerAddressType() {

	}

	/**
	 * Gets the customerAddressTypeCode attribute.
	 *
	 * @return Returns the customerAddressTypeCode
	 *
	 */
	@Override
    public String getCustomerAddressTypeCode() {
		return customerAddressTypeCode;
	}

	/**
	 * Sets the customerAddressTypeCode attribute.
	 *
	 * @param customerAddressTypeCode The customerAddressTypeCode to set.
	 *
	 */
	@Override
    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
		this.customerAddressTypeCode = customerAddressTypeCode;
	}


	/**
	 * Gets the customerAddressTypeDescription attribute.
	 *
	 * @return Returns the customerAddressTypeDescription
	 *
	 */
	@Override
    public String getCustomerAddressTypeDescription() {
		return customerAddressTypeDescription;
	}

	/**
	 * Sets the customerAddressTypeDescription attribute.
	 *
	 * @param customerAddressTypeDescription The customerAddressTypeDescription to set.
	 *
	 */
	@Override
    public void setCustomerAddressTypeDescription(String customerAddressTypeDescription) {
		this.customerAddressTypeDescription = customerAddressTypeDescription;
	}


	/**
	 * Gets the active attribute.
	 *
	 * @return Returns the active
	 *
	 */
	@Override
    public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active attribute.
	 *
	 * @param active The active to set.
	 *
	 */
	@Override
    public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("customerAddressTypeCode", this.customerAddressTypeCode);
	    return m;
    }
}
