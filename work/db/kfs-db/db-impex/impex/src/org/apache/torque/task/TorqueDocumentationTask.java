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
package org.apache.torque.task;

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

import org.apache.velocity.context.Context;

/**
 * An ant task for generating output by using Velocity
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: TorqueDocumentationTask.java,v 1.1 2007-10-21 07:57:26 abyrne Exp $
 */
public class TorqueDocumentationTask extends TorqueDataModelTask
{
    /** output format for the generated docs */
    private String outputFormat;

    /**
     * Get the current output format.
     *
     * @return the current output format
     */
    public String getOutputFormat()
    {
        return outputFormat;
    }

    /**
     * Set the current output format.
     *
     * @param v output format
     */
    public void setOutputFormat(String v)
    {
        outputFormat = v;
    }

    /**
     * Place our target package value into the context for use in the templates.
     *
     * @return the context
     * @throws Exception a generic exception
     */
    public Context initControlContext() throws Exception
    {
        super.initControlContext();
        context.put("outputFormat", outputFormat);
        context.put("escape", new org.apache.velocity.anakia.Escape());
        return context;
    }
}
