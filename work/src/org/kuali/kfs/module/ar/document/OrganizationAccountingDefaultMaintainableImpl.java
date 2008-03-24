/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.maintenance;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.document.CustomerInvoiceDocument;

public class OrganizationAccountingDefaultMaintainableImpl extends KualiMaintainableImpl {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationAccountingDefault.class);
    
    @Override
    public List getSections(Maintainable oldMaintainable) {

        List<Section> sections = super.getSections(oldMaintainable);

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if (!receivableOffsetOption.equals(ArConstants.OrganizationAccountingOptionsConstants.SHOW_EDIT_PAYMENTS_DEFAULTS_TAB)) {

            for (Iterator iter = sections.iterator(); iter.hasNext();) {

                Section section = (Section) iter.next();
                if (section.getSectionTitle().equals(ArConstants.OrganizationAccountingOptionsConstants.NAME_OF_THE_TAB_TO_HIDE)) {
                    try {
                        boolean success = sections.remove(section);
                        break;
                    }
                    catch (Exception e) {
                        LOG.error("Error removing tab",e);
                    }
                }
            }
        }
        return sections;
    }


    /*
     * }catch (InstantiationException e) { LOG.error("Unable to create instance of object class" + e.getMessage()); throw new
     * RuntimeException("Unable to create instance of object class" + e.getMessage()); } catch (IllegalAccessException e) {
     * LOG.error("Unable to create instance of object class" + e.getMessage()); throw new RuntimeException("Unable to create
     * instance of object class" + e.getMessage()); } } } return sections; }
     */

}
