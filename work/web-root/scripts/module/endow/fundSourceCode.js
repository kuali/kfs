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

 function loadFundSourceDesc(fundSourceCodeFieldName)
 { 
	var elPrefix = findElPrefix( fundSourceCodeFieldName.name );
	var fundSourceCodeDescFieldName = elPrefix + ".fundSource.name";
	setFundSourceDesc(fundSourceCodeFieldName, fundSourceCodeDescFieldName);
 }
 
 function setFundSourceDesc( fundSourceCodeFieldName, fundSourceCodeDescFieldName ){
	 
	var fundSourceCode = dwr.util.getValue( fundSourceCodeFieldName );

	if (fundSourceCode =='') {
		clearRecipients(fundSourceCodeDescFieldName, "");
	} else {
		fundSourceCode = fundSourceCode.toUpperCase();
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'object' ) {
				setRecipientValue( fundSourceCodeDescFieldName, data.name );
			} else {
				setRecipientValue( fundSourceCodeDescFieldName, wrapError( "Fund source code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( fundSourceCodeDescFieldName, wrapError( "Fund source code not found" ), true );
			}
		};
		FundSourceService.getByPrimaryKey(fundSourceCode, dwrReply );
	}
}
