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
package org.kuali.kfs.module.endow.fixture;

import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum GLLinkFixture {
    GL_LINK_BL_CHART("TST123", // endowmentTransactionCode
            "BL", // chartCode
            "5000", // object
            true // active
    ),
    GL_LINK_BL_CHART_COMMITTED("123TST", // endowmentTransactionCode
            "BL", // chartCode
            "5000", // object
            true // active
    ),    
    GL_LINK_UA_CHART("TST123", // endowmentTransactionCode
            "UA", // chartCode
            "5000", // object
            true // active
    ), GL_LINK_IN_CHART("TST123", // endowmentTransactionCode
            "IN", // chartCode
            "5000", // object
            true // active
    ), GL_LINK_BA_CHART("TST123", // endowmentTransactionCode
            "BA", // chartCode
            "5000", // object
            true // active
    );

    public final String endowmentTransactionCode;
    public final String chartCode;
    public final String object;
    public final boolean active;

    private GLLinkFixture(String endowmentTransactionCode, String chartCode, String object, boolean active) {
        this.endowmentTransactionCode = endowmentTransactionCode;
        this.chartCode = chartCode;
        this.object = object;
        this.active = active;

    }

    /**
     * This method creates a GLLink record and saves it to the DB table.
     * 
     * @return GLLink
     */
    public GLLink createGLLink() {
        GLLink glLink = new GLLink();

        glLink.setEndowmentTransactionCode(this.endowmentTransactionCode);
        glLink.setChartCode(this.chartCode);
        glLink.setObject(this.object);
        glLink.setActive(this.active);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(glLink);

        return glLink;
    }

}
