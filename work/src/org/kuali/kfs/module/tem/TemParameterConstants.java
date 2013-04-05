/*
 * Copyright 2012 The Kuali Foundation
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
