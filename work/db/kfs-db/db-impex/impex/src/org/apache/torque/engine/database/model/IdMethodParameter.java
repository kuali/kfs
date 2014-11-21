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
 * Information related to an ID method.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id: IdMethodParameter.java,v 1.1 2007-10-21 07:57:27 abyrne Exp $
 */
public class IdMethodParameter
{
    private String name;
    private String value;
    private Table parentTable;

    /**
     * Imports foreign key from an XML specification
     */
    public void loadFromXML (Attributes attrib)
    {
        name = attrib.getValue("name");
        value = attrib.getValue("value");
    }

    /**
     * Get the parameter name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the parameter name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the parameter value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Set the parameter value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * Set the parent Table of the id method
     */
    public void setTable(Table parent)
    {
        parentTable = parent;
    }

    /**
     * Get the parent Table of the id method
     */
    public Table getTable()
    {
        return parentTable;
    }

    /**
     * Returns the Name of the table the id method is in
     */
    public String getTableName()
    {
        return parentTable.getName();
    }

    /**
     * XML representation of the foreign key.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(" <id-method-parameter");
        if (getName() != null)
        {
            result.append(" name=\"")
                  .append(getName())
                  .append("\"");
        }
        result.append(" value=\"")
              .append(getValue())
              .append("\">\n");
        return result.toString();
    }
}
