/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@ConfigureContext(session = khuntley)
public class WebServicesImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WebServicesImplTest.class);
    static final String TEST_BASE_PACKAGE = "org.kuali.kfs.module.external";
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public boolean isValidfetchXML(URL location) throws SAXException, IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(location.openStream());
            doc.getDocumentElement().normalize();
            LOG.debug("Root Element " + doc.getDocumentElement().getNodeName());
            return true;
        }
        catch (ParserConfigurationException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return false;
    }

    public void testWebServices() throws Exception {
        

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(KfsService.class));
        Set<BeanDefinition> components = provider.findCandidateComponents(TEST_BASE_PACKAGE);
        for (BeanDefinition component : components) {
            String className = component.getBeanClassName();
            Class<KfsService> kfsServiceClass = (Class<KfsService>) Class.forName(className);
            try {
                KfsService kfsServiceInst = kfsServiceClass.newInstance();
                URL myWsdl = kfsServiceInst.getWsdl();
                assertTrue(isValidfetchXML(myWsdl));
            }
            catch (Exception ex) {
                fail(ex.getMessage());
            }
        }
    }
}