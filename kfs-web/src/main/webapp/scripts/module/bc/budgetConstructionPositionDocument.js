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
function updateBCPositionFTE( bcPositionField ) {
	// we want the base label 
    var elPrefix = findElPrefix( bcPositionField.name );
	var positionStandardHoursDefault = getElementValue( elPrefix + ".positionStandardHoursDefault" );
	var iuNormalWorkMonths = getElementValue( elPrefix + ".iuNormalWorkMonths" );
	var iuPayMonths = getElementValue( elPrefix + ".iuPayMonths" );	
	var positionFTEFieldName = elPrefix + ".positionFullTimeEquivalency";
	
	if ( positionStandardHoursDefault != "" && iuNormalWorkMonths != "" && iuPayMonths != "" ) {
		var dwrReply = {
			callback:function( responseText ) {
				setRecipientValue( positionFTEFieldName, responseText );
			},
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
			}
		};
		BudgetConstructionPosition.getCalculatedBCPositionFTE( positionStandardHoursDefault, iuNormalWorkMonths, iuPayMonths, dwrReply );
	} else {
		clearRecipients( positionFTEFieldName );
	}
}
