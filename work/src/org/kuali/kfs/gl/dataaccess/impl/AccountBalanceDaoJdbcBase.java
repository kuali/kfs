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
package org.kuali.module.gl.dao.jdbc;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.financial.service.UniversityDateService;

public class AccountBalanceDaoJdbcBase extends PlatformAwareDaoBaseJdbc {
	protected OptionsService optionsService;
	protected UniversityDateService universityDateService;

	public OptionsService getOptionsService() {
		return optionsService;
	}

	public void setOptionsService(OptionsService optionsService) {
		this.optionsService = optionsService;
	}

	public UniversityDateService getUniversityDateService() {
		return universityDateService;
	}

	public void setUniversityDateService(UniversityDateService universityDateService) {
		this.universityDateService = universityDateService;
	}

	
	protected String inString(int count) {
		StringBuffer sb = new StringBuffer( "(" );
		for ( int i = 0; i < count; i++ ) {
			sb.append( '?' );
			if ( i < count - 1 ) {
				sb.append( ',' );
			}
		}
		sb.append( ')' );
		return sb.toString();
	}
	
	protected void purgeCostShareEntries( String tableName, String idColumn ) {
		getSimpleJdbcTemplate().update( 
				"DELETE FROM " + tableName + " i WHERE " + idColumn + " = ? " +
				" AND EXISTS (SELECT 1 FROM ca_a21_sub_acct_t a " +
				" WHERE a.fin_coa_cd = i.fin_coa_cd AND a.account_nbr = i.account_nbr AND a.sub_acct_nbr = i.sub_acct_nbr " +
				" AND a.sub_acct_typ_cd = 'CS')", 
				Thread.currentThread().getId() );		
	}
	
	protected boolean hasEntriesInPendingTable() {
		return getSimpleJdbcTemplate().queryForInt( "select count(*) as COUNT from gl_pending_entry_mt WHERE person_unvl_id = ?", Thread.currentThread().getId()) != 0;
	}
	
	protected void fixPendingEntryDisplay( Integer universityFiscalYear ) {
		getSimpleJdbcTemplate().update("update GL_PENDING_ENTRY_MT set univ_fiscal_yr = ? where PERSON_UNVL_ID = ?", universityFiscalYear, Thread.currentThread().getId() );
		getSimpleJdbcTemplate().update("update gl_pending_entry_mt set SUB_ACCT_NBR = '-----' where (SUB_ACCT_NBR is null or SUB_ACCT_NBR = '     ')");		
	}
	
	protected void clearTempTable( String tableName, String idColumn ) {
        getSimpleJdbcTemplate().update( "DELETE from " + tableName + " WHERE " + idColumn + " = ?", Thread.currentThread().getId() );        		
	}
}
