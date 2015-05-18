/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.config

import org.junit.Test
import org.junit.Before
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import javax.xml.namespace.QName
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.kuali.rice.kns.service.BusinessObjectDictionaryService
import org.kuali.rice.kns.lookup.KualiLookupableImpl
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl
import org.apache.struts.config.ControllerConfig

import static org.junit.Assert.assertEquals
import org.kuali.rice.kim.api.identity.PersonService
import org.kuali.rice.kim.api.identity.Person

/**
 * Tests KualiControllerConfig
 *
 */
class KualiControllerConfigTest {
    def strutsControllerConfig = { "250M" } as ControllerConfig;
    String parameterMaxFileSize;

    @Before
    void setupFakeEnv() {
        parameterMaxFileSize = null

        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID")
        ConfigContext.init(config)

        GlobalResourceLoader.stop()

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                [ parameterService:
                    [ getParameterValueAsString: { ns, cmp, param -> parameterMaxFileSize } ] as ParameterService
                ][name.getLocalPart()]
            }
        })

    }

    @Test
    void testGetMaxFileSizeFallbackToStrutsDefault() {
        def config = new KualiControllerConfig(strutsControllerConfig)
        config.freeze()
        assertEquals("250M", config.getMaxFileSize())

        parameterMaxFileSize = ""
        assertEquals("250M", config.getMaxFileSize())
    }

    @Test
    void testGetMaxFileSizeUsesParameter() {
        parameterMaxFileSize = "300M"
        def config = new KualiControllerConfig(strutsControllerConfig)
        config.freeze()

        assertEquals("300M", config.getMaxFileSize())

        // ensure parameter change is reflected in struts even though config is frozen
        parameterMaxFileSize = "200M"
        assertEquals("200M", config.getMaxFileSize())
    }
}
