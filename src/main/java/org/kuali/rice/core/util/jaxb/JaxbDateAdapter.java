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
package org.kuali.rice.core.util.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Marshall/unmarshall java.util.Date
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class JaxbDateAdapter extends XmlAdapter<String, Date> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Date date) throws Exception {
		return (null != date ? Long.toString(date.getTime()) : null);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Date unmarshal(String dateStr) throws Exception {
		return (null != dateStr ? new Date(Long.parseLong(dateStr)) : null);
	}

}
