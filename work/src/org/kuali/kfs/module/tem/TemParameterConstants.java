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

    public static final String AUTHORIZATION_PARAM_DTL_TYPE = "TravelAuthorization";
    public static final String REIMBURSEMENT_PARAM_DTL_TYPE = "TravelReimbursement";
    public static final String RELOCATION_PARAM_DTL_TYPE = "TravelRelocation";
    public static final String ENTERTAINMENT_PARAM_DTL_TYPE = "TravelEntertainment";
    public static final String PROFILE_PARAM_DTL_TYPE = "TemProfile";
    public static final String AGENCY_MATCH_PARAM_DTL_TYPE = "AgencyMatchProcess";

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

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = AUTHORIZATION_PARAM_DTL_TYPE)
    public final class TEM_AUTHORIZATION {
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = REIMBURSEMENT_PARAM_DTL_TYPE)
    public final class TEM_REIMBURSEMENT{
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = RELOCATION_PARAM_DTL_TYPE)
    public final class TEM_RELOCATION {
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = ENTERTAINMENT_PARAM_DTL_TYPE)
    public final class TEM_ENTERTAINMENT{
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = PROFILE_PARAM_DTL_TYPE)
    public final class TEM_PROFILE{
    }

    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = AGENCY_MATCH_PARAM_DTL_TYPE)
    public final class TEM_AGENCY_MATCH{
    }
}
