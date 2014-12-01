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

function updateObjectNames( objField ) {
  function arrayContains(ary, ele) {
    var result = false;
    var count = 0;
    while (count < ary.length) {
      if (ary[count] == ele) {
        result = true;
      }
      count += 1;
    }
    return result;
  }

	// we want the base label - and the object field is in the detail section
  var detailElPrefix = findElPrefix( objField.name );
  var elPrefix = findElPrefix( detailElPrefix );
	var fiscalYear = getElementValue( elPrefix + ".universityFiscalYear" );
  // now, get all of the distinct chart of account codes from the organizations tab
  var chartCodes = [];
  var chartCode = getElementValue(elPrefix + ".add.organizationReversionGlobalOrganizations.chartOfAccountsCode");
  if (chartCode) {
    chartCodes.push(chartCode);
  }
  var orgCount = 0;
  var chartElementName = elPrefix + ".organizationReversionGlobalOrganizations["+orgCount+"].chartOfAccountsCode";
  while (kualiElements[chartElementName]) {
    chartCode = getElementValue(chartElementName);
    if (chartCode && !arrayContains(chartCodes, chartCode)) {
      chartCodes.push(chartCode);
    }
    orgCount += 1;
    chartElementName = elPrefix + ".organizationReversionGlobalOrganizations["+orgCount+"].chartOfAccountsCode";
  }
  
	var objectCode = getElementValue( objField.name );
	var nameFieldName = detailElPrefix + ".objectCodeNames";
	if ( fiscalYear != "" && chartCodes.length > 0 && objectCode != "" ) {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == 'string' ) {
				setRecipientValue( nameFieldName, data );
			} else {
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( nameFieldName, wrapError( "object not found" ), true );
			}
		};
		ObjectCodeService.getObjectCodeNamesByCharts( fiscalYear, chartCodes, objectCode, dwrReply );
	} else if ( objectCode == "" ) {
		clearRecipients( nameFieldName );
	} else if ( chartCodes.length == 0 ) {
		setRecipientValue(nameFieldName, 'there are no chart codes currently', true );
	} else if ( fiscalYear == "" ) {
		setRecipientValue(nameFieldName, 'fiscal year is missing', true );	
	}
}
