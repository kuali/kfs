/*
 * Copyright 2007-2009 The Kuali Foundation
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
