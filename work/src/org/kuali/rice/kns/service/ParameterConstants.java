/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kuali.rice.kns.util.KNSConstants;


public class ParameterConstants {
    public static final String NERVOUS_SYSTEM_NAMESPACE = KNSConstants.KNS_NAMESPACE;
    public static final String ALL_COMPONENT = "All";
    public static final String DOCUMENT_COMPONENT = "Document";
    public static final String LOOKUP_COMPONENT = "Lookup";
    public static final String BATCH_COMPONENT = "Batch";

    @Retention(RetentionPolicy.RUNTIME)
    @Target( { ElementType.TYPE })
    public @interface NAMESPACE {
        String namespace();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target( { ElementType.TYPE })
    public @interface COMPONENT {
        String component();
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class NERVOUS_SYSTEM_ALL {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class NERVOUS_SYSTEM_DOCUMENT {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class NERVOUS_SYSTEM_LOOKUP {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class NERVOUS_SYSTEM_BATCH {
    }

}
