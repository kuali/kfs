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
