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
package org.kuali.rice.kns.web.struts.form.pojo

import org.apache.struts.config.ControllerConfig
import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.kuali.rice.kim.api.identity.PersonService
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm
import org.kuali.rice.krad.util.KRADConstants

import javax.xml.namespace.QName

import static org.junit.Assert.assertEquals
import org.kuali.rice.kim.impl.identity.TestPerson
import org.kuali.rice.kns.util.WebUtils

/**
 * Tests PojoFormBase
 */
class PojoFormBaseTest {
    def strutsControllerConfig = { "250M" } as ControllerConfig;
    String maxUploadSize;
    String maxAttachmentSize;

    @Before
    void setupFakeEnv() {
        maxUploadSize = maxAttachmentSize = null

        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID")
        ConfigContext.init(config)

        GlobalResourceLoader.stop()

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                [ parameterService:
                    [ getParameterValueAsString: { ns, cmp, param ->
                        [ (KRADConstants.MAX_UPLOAD_SIZE_PARM_NM): maxUploadSize,
                          (KRADConstants.ATTACHMENT_MAX_FILE_SIZE_PARM_NM): maxAttachmentSize ][param]
                    } ] as ParameterService,
                    // KualiMaintenanceForm -> Note -> AdHocRoutePerson -> PersonService getPersonImplementationClass lookup :(
                    // stub impl class
                    personService: { TestPerson.class } as PersonService
                ][name.getLocalPart()]
            }
        })
    }

    /**
     * Tests that PojoFormBase uses the max upload size parameter
     * to determine max file upload size
     */
    @Test
    void testGetMaxFileSizesGlobalDefault() {
        // when no parameters are defined whatsoever, uses PojoFormBase hardcoded default
        assertEquals(["250M"], new PojoFormBase().getMaxUploadSizes())

        maxUploadSize = "300M"
        assertEquals(["300M"], new PojoFormBase().getMaxUploadSizes())
    }

    /**
     * Tests how KualiMaintenanceForm derives max upload sizes
     */
    @Test
    void testGetMaxFileSizesKualiDocumentDefault() {
        // nothing defined, falls back to hardcoded default
        assertEquals(["250M"], new KualiMaintenanceForm().getMaxUploadSizes())

        // only global default is set
        maxUploadSize = "300M"
        assertEquals(["300M"], new KualiMaintenanceForm().getMaxUploadSizes())

        // if the max attachment size param is set, then the sizes list is not empty
        // and therefore global default is not used
        maxAttachmentSize = "200M"
        assertEquals(["200M"], new KualiMaintenanceForm().getMaxUploadSizes())
    }
}
