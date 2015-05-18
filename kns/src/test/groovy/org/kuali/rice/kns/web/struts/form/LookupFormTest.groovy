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
package org.kuali.rice.kns.web.struts.form

import org.junit.Test
import org.apache.struts.mock.MockHttpServletRequest
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService
import org.kuali.rice.krad.service.DataObjectAuthorizationService

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertFalse
import org.junit.Before
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import javax.xml.namespace.QName
import org.kuali.rice.core.api.resourceloader.ResourceLoader
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import org.kuali.rice.coreservice.framework.parameter.ParameterService
import org.kuali.rice.krad.util.KRADConstants
import org.kuali.rice.krad.bo.BusinessObject
import org.kuali.rice.krad.bo.BusinessObjectBase
import org.kuali.rice.kns.service.BusinessObjectDictionaryService
import org.kuali.rice.kns.lookup.KualiLookupableImpl
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl
import org.kuali.rice.kns.lookup.Lookupable
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.google.common.base.Supplier
import org.kuali.rice.core.framework.config.property.SimpleConfig
import com.google.common.collect.HashMultimap

/**
 * tests lookup form
 */
class LookupFormTest {
    class TestBO extends BusinessObjectBase {
        public void refresh() {}
    }

    @Before
    void setupFakeEnv() {
        def config = new SimpleConfig()
        config.putProperty(CoreConstants.Config.APPLICATION_ID, "APPID");
        ConfigContext.init(config);

        GlobalResourceLoader.stop();

        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("Foo", "Bar")) {
            def getService(QName name) {
                [ parameterService:
                    [ getParameterValueAsString: { s0,s1,s2 -> null },
                      getParameterValueAsBoolean: { s0,s1,s2,s3 -> false }
                    ] as ParameterService,
                  dataObjectAuthorizationService:
                    [ attributeValueNeedsToBeEncryptedOnFormsAndLinks: {s0,s1 -> false } ] as DataObjectAuthorizationService,
                  businessObjectDictionaryService:
                    [ getLookupableID: { s0 -> null } ] as BusinessObjectDictionaryService,
                  kualiLookupable: {
                      def l = new KualiLookupableImpl() {
                          void setBusinessObjectClass(Class boClass) {}
                          List getRows() { [] as List }
                          String getExtraButtonSource() { null }
                          String getExtraButtonParams() { null }
                      }
                      l.setLookupableHelperService(new KualiLookupableHelperServiceImpl())
                      return l
                  }()
                ][name.getLocalPart()]
            }
        });
    }

    @Test(expected=RuntimeException)
    void testFormRequiresBusinessObject() {
        new LookupForm().populate(new MockHttpServletRequest())
    }

    @Test void testFormViewFlags() {
        def form = new LookupForm();
        def req = new MockHttpServletRequest();

        assertTrue(form.headerBarEnabled)
        assertTrue(form.lookupCriteriaEnabled)
        req.addParameter(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TestBO.class.getName())
        req.addParameter(LookupForm.HEADER_BAR_ENABLED_PARAM, "false")
        req.addParameter(LookupForm.SEARCH_CRITERIA_ENABLED_PARAM, "false")

        form.populate(req)

        assertFalse(form.headerBarEnabled)
        assertFalse(form.lookupCriteriaEnabled)
    }
}
