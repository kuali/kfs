/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/coa/businessobject/ICRTypeCodeTest.java,v $
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
package org.kuali.module.chart.bo.codes;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.RelatesTo;
import static org.kuali.test.suite.RelatesTo.JiraIssue.KULRNE42;

/**
 * Tests of the ICRTypeCode BO.
 */
@WithTestSpringContext
public class ICRTypeCodeTest extends KualiTestBase {

    /**
     * The isActive method should always return true, at least until a phase 2 task adds active indicators to all BOs.
     */
    public void testIsActive() {
        ICRTypeCode bo = (ICRTypeCode) (SpringServiceLocator.getBusinessObjectService().findAll(ICRTypeCode.class).toArray()[0]);
        assertEquals(true, bo.isActive());
    }
}
