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

import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.Close;

/**
 * Implementations of this interface provide access to persisted Proposal 
 * instances.
 */
public interface ProposalDao {
    
    /**
     * Gets a {@link Collection} of {@link Proposal} instances which have not
     * yet been closed.
     * 
     * @param c the {@link Close} instance which is used to determine which
     * Proposals should be returned.
     * @return a {@link Collection} of appropriate {@link Proposals}.
     */
    public Collection<Proposal> getProposalsToClose(Close c);
    
    /**
     * Persist a {@link Proposal}.
     * 
     * @param proposal the instance to persist.
     */
    public void save(Proposal proposal);

}
