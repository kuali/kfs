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
package org.kuali.kfs.module.tem;

import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.NAMESPACE;


public class TemParameterConstants extends KfsParameterConstants {

    public static final String TEM_NAMESPACE = TemConstants.PARAM_NAMESPACE;

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class TEM_ALL {
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class TEM_DOCUMENT {
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class TEM_LOOKUP {
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class TEM_BATCH {
    }

    }
