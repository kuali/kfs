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

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang.StringUtils;

/**
 * An XML adapter that simply performs a null-safe trim on the value to be marshalled or unmarshalled.
 * 
 * <p>Only use this adapter when it is necessary for the remaining whitespace-related characters to
 * remain as-is.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StringTrimmingAdapter extends XmlAdapter<String,String> {

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public String unmarshal(String v) throws Exception {
        return StringUtils.trim(v);
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(String v) throws Exception {
        return StringUtils.trim(v);
    }

}
