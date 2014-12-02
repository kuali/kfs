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
package org.kuali.kfs.module.ar.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Random;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

@ConfigureContext(session = khuntley)
public class CustomerDocumentServiceImplTest extends KualiTestBase {
    protected CustomerDocumentServiceImpl customerDocumentServiceImpl;
    protected DataDictionaryService dataDictionaryService;
    protected char[] characters = null;
    protected Random rand;

    @Override
    public void setUp() {
        customerDocumentServiceImpl = new CustomerDocumentServiceImpl();
        customerDocumentServiceImpl.setCustomerService(SpringContext.getBean(CustomerService.class));
        customerDocumentServiceImpl.setDataDictionaryService(SpringContext.getBean(DataDictionaryService.class));
        customerDocumentServiceImpl.setDocumentService(SpringContext.getBean(DocumentService.class));
        customerDocumentServiceImpl.setMaintenanceDocumentDictionaryService(SpringContext.getBean(MaintenanceDocumentDictionaryService.class));
        customerDocumentServiceImpl.setKualiModuleService(SpringContext.getBean(KualiModuleService.class));
        customerDocumentServiceImpl.setWorkflowDocumentService(SpringContext.getBean(WorkflowDocumentService.class));

        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        characters = new char[52];
        int count = 0;
        for (char i = 'A'; i <= 'Z'; i++) {
            characters[count] = i;
            count += 1;
        }
        for (char i = 'a'; i <= 'z'; i++) {
            characters[count] = i;
            count += 1;
        }

        rand = new Random();
    }

    public void testTruncateField() {
        final int customerAddressNameLength = dataDictionaryService.getAttributeMaxLength(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_NAME);
        final String customerAddressNameShort = randomString(customerAddressNameLength-3);
        final String customerAddressNameLong = randomString(customerAddressNameLength+7);
        final String customerAddressNameBabyBear = randomString(customerAddressNameLength);

        final String customerAddressNameShortTruncated = customerDocumentServiceImpl.truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_NAME, customerAddressNameShort);
        final String customerAddressNameLongTruncated = customerDocumentServiceImpl.truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_NAME, customerAddressNameLong);
        final String customerAddressNameBabyBearTruncated = customerDocumentServiceImpl.truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_NAME, customerAddressNameBabyBear);

        assertEquals("Customer Address Name Short should have the same length as truncated version", customerAddressNameShort.length(), customerAddressNameShortTruncated.length());
        assertEquals("Customer Address Name Short should be the same String as the truncated version", customerAddressNameShort, customerAddressNameShortTruncated);
        assertEquals("Customer Address Name Long should have the max length of the customer address name field", customerAddressNameLength, customerAddressNameLongTruncated.length());
        assertTrue("Customer Address Name Long should start with the truncated version", customerAddressNameLong.startsWith(customerAddressNameLongTruncated));
        assertEquals("Customer Address Name Baby Bear should have the same length as the truncated version", customerAddressNameBabyBear.length(), customerAddressNameBabyBearTruncated.length());
        assertEquals("Customer Address Name Baby Bear should have the same length as the customer address name field", customerAddressNameLength, customerAddressNameBabyBearTruncated.length());
        assertEquals("Customer Address Name Baby Bear should be the exact same string as the truncated version", customerAddressNameBabyBear, customerAddressNameBabyBearTruncated);
    }

    protected String randomString(int size) {
        StringBuilder s = new StringBuilder();
        int count = 0;
        while (count < size) {
            int idx = rand.nextInt(characters.length);
            s.append(characters[idx]);
            count += 1;
        }
        return s.toString();
    }
}