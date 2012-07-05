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

import static org.kuali.rice.kns.service.ParameterConstants.DOCUMENT_COMPONENT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterConstants;
import org.kuali.rice.kns.service.ParameterConstants.COMPONENT;
import org.kuali.rice.kns.service.ParameterConstants.NAMESPACE;
import org.kuali.rice.kns.util.KNSConstants;


public class TemParameterConstants extends KfsParameterConstants {

    public static final String TEM_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "TEM";
    
    public static final String AUTHORIZATION_PARAM_DTL_TYPE = "TravelAuthorization";
    public static final String REIMBURSEMENT_PARAM_DTL_TYPE = "TravelReimbursement";
    public static final String RELOCATION_PARAM_DTL_TYPE = "TravelRelocation";
    public static final String ENTERTAINMENT_PARAM_DTL_TYPE = "TravelEntertainment";

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
    @COMPONENT(component = AUTHORIZATION_PARAM_DTL_TYPE)
    public final class TEM_REIMBURSEMENT{
    }
    
    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = AUTHORIZATION_PARAM_DTL_TYPE)
    public final class TEM_RELOCATION {
    }
    
    @NAMESPACE(namespace = TEM_NAMESPACE)
    @COMPONENT(component = AUTHORIZATION_PARAM_DTL_TYPE)
    public final class TEM_ENTERTAINMENT{
    }
    
}
