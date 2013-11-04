/*
 * Copyright 2010 The Kuali Foundation.
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
