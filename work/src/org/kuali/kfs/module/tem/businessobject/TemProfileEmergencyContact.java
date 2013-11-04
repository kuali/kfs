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
package org.kuali.kfs.module.tem.businessobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Emergency Contact
 *
 */
@Entity
@Table(name="TEM_PROFILE_EM_CONT_T")
public class TemProfileEmergencyContact extends EmergencyContact {
    private TmProfile profile;
    private Integer profileId;

    @ManyToOne
    @JoinColumn(name="tem_profile_id")
    public TmProfile getProfile() {
        return profile ;
    }

    public void setProfile(TmProfile profile) {
        this.profile = profile;
    }

    @Column(name="tem_profile_id",nullable=false)
    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
}
