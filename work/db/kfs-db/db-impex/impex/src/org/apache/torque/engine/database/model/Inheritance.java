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
package org.apache.torque.engine.database.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.xml.sax.Attributes;

/**
 * A Class for information regarding possible objects representing a table
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id: Inheritance.java,v 1.1 2007-10-21 07:57:27 abyrne Exp $
 */
public class Inheritance
{
    private String key;
    private String className;
    private String ancestor;
    private Column parent;

    /**
     * Imports foreign key from an XML specification
     *
     * @param attrib the xml attributes
     */
    public void loadFromXML (Attributes attrib)
    {
        setKey(attrib.getValue("key"));
        setClassName(attrib.getValue("class"));
        setAncestor(attrib.getValue("extends"));
    }

    /**
     * Get the value of key.
     * @return value of key.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Set the value of key.
     * @param v  Value to assign to key.
     */
    public void setKey(String  v)
    {
        this.key = v;
    }


    /**
     * Get the value of parent.
     * @return value of parent.
     */
    public Column getColumn()
    {
        return parent;
    }

    /**
     * Set the value of parent.
     * @param v  Value to assign to parent.
     */
    public void setColumn(Column  v)
    {
        this.parent = v;
    }

    /**
     * Get the value of className.
     * @return value of className.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Set the value of className.
     * @param v  Value to assign to className.
     */
    public void setClassName(String  v)
    {
        this.className = v;
    }

    /**
     * Get the value of ancestor.
     * @return value of ancestor.
     */
    public String getAncestor()
    {
        return ancestor;
    }

    /**
     * Set the value of ancestor.
     * @param v  Value to assign to ancestor.
     */
    public void setAncestor(String  v)
    {
        this.ancestor = v;
    }

    /**
     * String representation of the foreign key. This is an xml representation.
     *
     * @return string representation in xml
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(" <inheritance key=\"")
              .append(key)
              .append("\" class=\"")
              .append(className)
              .append('\"');


        if (ancestor != null)
        {
            result.append(" extends=\"")
                  .append(ancestor)
                  .append('\"');
        }

        result.append("/>");

        return result.toString();
    }
}
