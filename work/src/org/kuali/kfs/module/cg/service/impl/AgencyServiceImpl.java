/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of the Agency service.
 */
public class AgencyServiceImpl implements AgencyService {

    protected BusinessObjectService businessObjectService;
    protected NoteService noteService;

    /**
     * @see org.kuali.kfs.module.cg.service.AgencyService#getByPrimaryId(String)
     */
    @Override
    public Agency getByPrimaryId(String agencyNumber) {
        return businessObjectService.findByPrimaryKey(Agency.class, mapPrimaryKeys(agencyNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(String agencyNumber) {
        Map<String, Object> primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber.trim());
        return primaryKeys;
    }

    public List<Note> getAgencyNotes(String agencyNumber) {
        Agency agency = getByPrimaryId(agencyNumber);
        List<Note> notes = new ArrayList<Note>();
       if (ObjectUtils.isNotNull(agency)) {
            notes = noteService.getByRemoteObjectId(agency.getObjectId());
        }
        return notes;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
