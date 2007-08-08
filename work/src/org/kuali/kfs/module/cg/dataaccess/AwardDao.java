/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.dao;

import java.util.Collection;

import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.Close;

/**
 * Implementations of this interface provide access to persisted Awards.
 */
public interface AwardDao {

    /**
     * Get a {@link Collection} of {@link Award}s to close. This is used by 
     * the {@link CloseBatchStep}.
     * 
     * @param c
     * @return
     */
    public Collection<Award> getAwardsToClose(Close c);
    
    /**
     * Save an {@link Award}.
     * 
     * @param award
     */
    public void save(Award award);
    
    /**
     * Delete all Awards.
     */
    public void deleteAll();
}
