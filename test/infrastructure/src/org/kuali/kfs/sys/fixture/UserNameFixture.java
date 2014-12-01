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
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

public enum UserNameFixture {

    NO_SESSION, // This is not a user name. It is a Sentinal value telling KualiTestBase not to create a session. (It's needed
                // because null is not a valid default for the ConfigureContext annotation's session element.)
    kfs, // This is the KualiUser.SYSTEM_USER, which certain automated document type authorizers require.
    khuntley, // KualiTestBaseWithSession used this one by default. (testUsername in configuration.properties, no longer used but
                // cannot be removed because that file cannot be committed).
    ghatten, stroud, dfogle, rjweiss, rorenfro, sterner, ferland, hschrein, hsoucy, lraab, jhavens, kcopley, mhkozlow, ineff, vputman, cswinson, mylarge, rruffner, season, dqperron, aatwood, parke, appleton, twatson, butt, jkitchen , bomiddle, aickes, day, jgerhart,
    // Regional Budget Manager
    ocmcnall,
    // University Administration Budget Manager
    wbrazil,
    // Non-KFS User
    bcoffee,
    // Account secondary delegate
    rmunroe,
    // Org Hierarchy Reviewer
    cknotts,
    // Accounting Hierarchy Reviewer
    jrichard;

    static {
        // Assert.assertEquals(KualiUser.SYSTEM_USER, kuluser.toString());
    }

    public Person getPerson() {
        return SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName(toString());
    }
}

