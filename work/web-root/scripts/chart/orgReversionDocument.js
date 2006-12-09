/*
 * Copyright 2006 The Kuali Foundation.
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
function updateObjectName( objField ) {
	// we want the base label - and the object field is in the detail section
    var detailElPrefix = findElPrefix( objField.name );
    var elPrefix = findElPrefix( detailElPrefix );
	var fiscalYear = getElementValue( elPrefix + ".universityFiscalYear" );
	var chartCode = getElementValue( elPrefix + ".chartOfAccountsCode" );
	var objectCode = objField.value.toUpperCase().trim();
	var nameFieldName = detailElPrefix + ".organizationReversionObject.financialObjectCodeName";
	if ( fiscalYear != "" && chartCode != "" && objectCode != "" ) {
		loadKualiObjectWithCallback( function( responseText ) {
			setRecipientValue( nameFieldName, responseText );
		}, "object", fiscalYear, chartCode, objectCode, "", "", "", "" );
	} else if ( objectCode == "" ) {
		clearRecipients( nameFieldName );
	} else if ( chartCode == "" ) {
		setRecipientValue(nameFieldName, 'chart code is empty');
	} else if ( fiscalYear == "" ) {
		setRecipientValue(nameFieldName, 'fiscal year is missing');	
	}
}
