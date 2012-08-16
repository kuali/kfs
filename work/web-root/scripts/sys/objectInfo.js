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

var chartCodeSuffix = ".chartOfAccountsCode";
var chartNameSuffix = ".chart.finChartOfAccountDescription";
var accountNumberSuffix = ".accountNumber";
var accountNameSuffix = ".account.accountName";
var subAccountNumberSuffix = ".subAccountNumber";
var subAccountNameSuffix = ".subAccount.subAccountName";
var objectCodeSuffix = ".financialObjectCode";
var objectCodeNameSuffix = ".objectCode.financialObjectCodeName";
var subObjectCodeSuffix = ".financialSubObjectCode";
var subObjectCodeNameSuffix = ".subObjectCode.financialSubObjectCodeName";
var universityFiscalYearSuffix =".universityFiscalYear";

function loadChartInfo(coaCodeFieldName, coaNameFieldName ) {
    var coaCode = dwr.util.getValue( coaCodeFieldName );

	if (coaCode=="") {
		clearRecipients(coaNameFieldName, ""); 
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( coaNameFieldName, data.finChartOfAccountDescription );
			} else {
				setRecipientValue( coaNameFieldName, wrapError( "chart not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( coaNameFieldName, wrapError( "chart not found" ), true );
			}
		};
		ChartService.getByPrimaryId( coaCode, dwrReply );
	}
}

function setReportsToChartCode() {
	// TODO: detect if in lookup or document mode
	// make AJAX call to get reports-to chart
	var coaCode = dwr.util.getValue( "document.newMaintainableObject" + chartCodeSuffix );

	if (coaCode!="") {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				var reportsToChartDiv = document.getElementById("document.newMaintainableObject.reportsToChartOfAccountsCode.div");
				reportsToChartDiv.innerHTML = data.reportsToChartOfAccountsCode;
			} else {
				window.status = "chart not found."; 
			} },
			errorHandler:function( errorMessage ) { 
				window.status = "Unable to get reports-to chart."; 
			}
		};
		ChartService.getByPrimaryId( coaCode, dwrReply );
	}			
}

function loadAccountInfo( accountCodeFieldName, accountNameFieldName ) {
    var elPrefix = findElPrefix( accountCodeFieldName );
    var accountCode = dwr.util.getValue( accountCodeFieldName );
	var coaCodeFieldName = elPrefix + chartCodeSuffix;
	var coaNameFieldName = elPrefix + chartNameSuffix;

    if (valueChanged( accountCodeFieldName )) {
        setRecipientValue( elPrefix + subAccountNumberSuffix, "" );
        setRecipientValue( elPrefix + subAccountNameSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeSuffix, "" );
        setRecipientValue( elPrefix + subObjectCodeNameSuffix, "" );
    }
    
    var dwrResult = {
    	callback:function(param) {
    	if ( typeof param == "boolean" && param == true) {	
    		var coaCode = dwr.util.getValue( coaCodeFieldName );
    		//alert("Account Can Cross Chart: coaCode = " + coaCode + ", accountCode = " + accountCode);
    		if (accountCode == "") {
    			clearRecipients(accountNameFieldName);
    		} else if (coaCode == "") {
    			setRecipientValue(accountNameFieldName, wrapError( "chart code is empty" ), true );
    		} else {
    			accountCode = accountCode.toUpperCase();
    			var dwrReply = {
    				callback:function(data) {
    				if ( data != null && typeof data == "object" ) {
    					setRecipientValue( accountNameFieldName, data.accountName );
    				} else {
    					setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );			
    				} },
    				errorHandler:function( errorMessage ) { 
    					setRecipientValue( accountNameFieldName, wrapError( "error looking up account"), true );
    				}
    			};
    			AccountService.getByPrimaryIdWithCaching( coaCode, accountCode, dwrReply );
    		}	
    	} else {
    		//alert("Account Cant Cross Chart: coaCodeFieldName = " + coaCodeFieldName);
    		if (accountCode == "") {
    			clearRecipients(accountNameFieldName);
    			clearRecipients(coaCodeFieldName);    		
    			clearRecipients(coaNameFieldName);    		
    		} else {
    			accountCode = accountCode.toUpperCase();
    			var dwrReply = {
    				callback:function(data) {
    				if ( data != null && typeof data == "object" ) {    				
    					setRecipientValue( accountNameFieldName, data.accountName );
    					setRecipientValue( coaCodeFieldName, data.chartOfAccountsCode );
    					//alert("coaCode = " + dwr.util.getValue(coaCodeFieldName+".div"));
    					setRecipientValue( coaNameFieldName, data.chartOfAccounts.finChartOfAccountDescription );
    				} else {
    					setRecipientValue( accountNameFieldName, wrapError( "account not found" ), true );			
    					clearRecipients(coaCodeFieldName);    		
    					clearRecipients(coaNameFieldName);    		
    				} },
    				errorHandler:function( errorMessage ) { 
    					setRecipientValue( accountNameFieldName, wrapError( "error looking up account"), true);
    					clearRecipients(coaCodeFieldName);    		
    					clearRecipients(coaNameFieldName);    		
    				}
    			};
    			AccountService.getUniqueAccountForAccountNumber( accountCode, dwrReply );	    
    		}
    	} },
    	errorHandler:function( errorMessage ) { 
    		setRecipientValue( accountNameFieldName, wrapError( "error looking up AccountCanCrossChart parameter"), true );
    	}
    };
    AccountService.accountsCanCrossCharts(dwrResult);
}

function loadSubAccountInfo( subAccountCodeFieldName, subAccountNameFieldName ) {
    var elPrefix = findElPrefix( subAccountCodeFieldName );	
	var coaCode = getElementValue(elPrefix + chartCodeSuffix);
    var accountCode = getElementValue( elPrefix + accountNumberSuffix );
    var subAccountCode = getElementValue( subAccountCodeFieldName );
    //alert("loadSubAccountInfo:\ncoaCode = " + coaCode + "\naccountCode = " + accountCode + "\nsubAccountCode = " + subAccountCode);
    
	if (subAccountCode=="") {
		clearRecipients(subAccountNameFieldName);
	} else if (coaCode=="") {
		setRecipientValue(subAccountNameFieldName, wrapError( "chart code is empty" ), true );
	} else if (accountCode=="") {
		setRecipientValue(subAccountNameFieldName, wrapError( "account number is empty" ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( subAccountNameFieldName, data.subAccountName );
			} else {
				setRecipientValue( subAccountNameFieldName, wrapError( "sub-account not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subAccountNameFieldName, wrapError( "sub-account not found" ), true );
			}
		};
		SubAccountService.getByPrimaryId( coaCode, accountCode, subAccountCode, dwrReply );
	}
}

function loadObjectInfo(fiscalYear, objectTypeNameRecipient, objectTypeCodeRecipient, objectCodeFieldName, objectNameFieldName) {
	var elPrefix = findElPrefix( objectCodeFieldName );   
    var coaCode = getElementValue( elPrefix + chartCodeSuffix );
    var objectCode = getElementValue( objectCodeFieldName );    
    //alert("loadObjectInfo:\nfiscalYear = " + fiscalYear + "\ncoaCode = " + coaCode + "\nobjectCode = " + objectCode);    

    if (valueChanged( objectCodeFieldName )) {
        clearRecipients( elPrefix + subObjectCodeSuffix );
        clearRecipients( elPrefix + subObjectCodeNameSuffix );
    }
	if (objectCode=="") {
		clearRecipients(objectNameFieldName);
	} else if (coaCode=="") {
		setRecipientValue(objectNameFieldName, wrapError( "chart code is empty" ), true );
	} else if (fiscalYear=="") {
		setRecipientValue(objectNameFieldName, wrapError( "fiscal year is missing" ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( objectNameFieldName, data.financialObjectCodeName );
				setRecipientValue( objectTypeCodeRecipient, data.financialObjectTypeCode );
				setRecipientValue( objectTypeNameRecipient, data.financialObjectType.name );
			} else {
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );			
				clearRecipients( objectTypeCodeRecipient );
				clearRecipients( objectTypeNameRecipient );
			} },
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );
				clearRecipients( objectTypeCodeRecipient );
				clearRecipients( objectTypeNameRecipient );
			}
		};
		ObjectCodeService.getByPrimaryId( fiscalYear, coaCode, objectCode, dwrReply );
	}
}

function loadObjectCodeInfo(objectCodeFieldName, objectNameFieldName) {
    var elPrefix = findElPrefix( objectCodeFieldName );
    var fiscalYear = getElementValue( elPrefix + universityFiscalYearSuffix);
    var coaCode = getElementValue( elPrefix + chartCodeSuffix );
    var objectCode = getElementValue( objectCodeFieldName );
    //alert("loadObjectCodeInfo:\nfiscalYear = " + fiscalYear + "\ncoaCode = " + coaCode + "\nobjectCode = " + objectCode);    

    if (valueChanged( objectCodeFieldName )) {
        clearRecipients(objectNameFieldName );
    }
	if (objectCode=="") {
		clearRecipients(objectNameFieldName);
	} else if (coaCode=="") {
		setRecipientValue(objectNameFieldName, wrapError( "chart code is empty" ), true );
	} else if (fiscalYear=="") {
		setRecipientValue(objectNameFieldName, wrapError( "fiscal year is missing" ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( objectNameFieldName, data.financialObjectCodeName );
			} else {
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( objectNameFieldName, wrapError( "object not found" ), true );
			}
		};
		ObjectCodeService.getByPrimaryId( fiscalYear, coaCode, objectCode, dwrReply );
	}
}

function loadSubObjectInfo(fiscalYear, subObjectCodeFieldName, subObjectNameFieldName) {
    var elPrefix = findElPrefix( subObjectCodeFieldName );
    var coaCode = getElementValue( elPrefix + chartCodeSuffix);
    var accountCode = getElementValue( elPrefix + accountNumberSuffix );
    var objectCode = getElementValue( elPrefix + objectCodeSuffix );
    var subObjectCode = getElementValue( subObjectCodeFieldName );        
    //alert("loadSubObjectInfo:\nfiscalYear = " + fiscalYear + "\ncoaCode = " + coaCode + "\naccountCode = " + accountCode + "\nobjectCode = " + objectCode + "\nsubObjectCode = " + subObjectCode);

    if (subObjectCode=="") {
		clearRecipients(subObjectNameFieldName);
	} else if (coaCode=="") {
		setRecipientValue(subObjectNameFieldName, wrapError( "chart is empty" ), true);
	} else if (fiscalYear=="") {
		setRecipientValue(subObjectNameFieldName, wrapError( "fiscal year is missing" ), true);
	} else if (accountCode=="") {
		setRecipientValue(subObjectNameFieldName, wrapError( "account is empty" ), true );
	} else if (objectCode=="") {
		setRecipientValue(subObjectNameFieldName, wrapError( "object code is empty" ), true );
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( subObjectNameFieldName, data.financialSubObjectCodeName );
			} else {
				setRecipientValue( subObjectNameFieldName, wrapError( "sub-object not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( subObjectNameFieldName, wrapError( "sub-object not found" ), true );
			}
		};
		SubObjectCodeService.getByPrimaryId( fiscalYear, coaCode, accountCode, objectCode, subObjectCode, dwrReply );
	}
}

function loadProjectInfo(projectCodeFieldName, projectNameFieldName) {
    var projectCode = getElementValue( projectCodeFieldName );

	if (projectCode=="") {
		clearRecipients(projectNameFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( projectNameFieldName, data.name );
			} else {
				setRecipientValue( projectNameFieldName, wrapError( "project not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( projectNameFieldName, wrapError( "project not found" ), true );
			}
		};
		ProjectCodeService.getByPrimaryId( projectCode, dwrReply );
	}
}

function loadObjectTypeInfo(objectTypeCodeFieldName, objectTypeNameFieldName) {
    var objectTypeCode = getElementValue( objectTypeCodeFieldName );

    if (objectTypeCode=="") {
        clearRecipients(objectTypeNameFieldName);
    } else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( objectTypeNameFieldName, data.name );
			} else {
				setRecipientValue( objectTypeNameFieldName, wrapError( "object type not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( objectTypeNameFieldName, wrapError( "object type not found" ), true );
			}
		};
		ObjectTypeService.getByPrimaryKey( objectTypeCode, dwrReply );
    }
}

function loadOriginationInfo(originationCodeFieldName, originationCodeNameFieldName) {
    var originationCode = getElementValue(originationCodeFieldName);

    if (originationCode == "") {
        clearRecipients(originationCodeNameFieldName);
    } else {
		var dwrReply = {
			callback:function(data) {
			if ( data != null && typeof data == "object" ) {
				setRecipientValue( originationCodeNameFieldName, data.financialSystemDatabaseName );
			} else {
				setRecipientValue( originationCodeNameFieldName, wrapError( "origin code not found" ), true );			
			} },
			errorHandler:function( errorMessage ) { 
				setRecipientValue( originationCodeNameFieldName, wrapError( "origin code not found" ), true );
			}
		};
		OriginationCodeService.getByPrimaryKey( originationCode, dwrReply );
    }
}

function loadEmplInfo( emplIdFieldName, userNameFieldName ) {
    var userId = dwr.util.getValue( emplIdFieldName );
    var containerDiv = document.getElementById(userNameFieldName + divSuffix);

    if (userId == "") {
        dwr.util.setValue( containerDiv.id, "" );
    } else {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == "object" ) {
                dwr.util.setValue(containerDiv.id, data.name, {escapeHtml:true} );
            } else {
                dwr.util.setValue(containerDiv.id, wrapError( "person not found" ), { escapeHtml:false });
            } },
            errorHandler:function( errorMessage ) { 
                dwr.util.setValue(containerDiv.id, wrapError( "person not found" ), { escapeHtml:false });
            }
        };
        PersonService.getPersonByEmployeeId( userId, dwrReply );
    }
}

/**
 * Gets chart code field value for the given chart code field name. 
 * It takes care of the case when chart code is readOnly, thus not one of the input fields and can't be retrieved using getElementValue.
 * It also filters out white spaces as well as any URL links associated with the chart code.
 * @param coaCodeFieldName the given chart code field name.
 * @return the chart code field value
 *
function getChartCode(coaCodeFieldName) {
	// retrieve chart code html element using coaCodeFieldName as id
	var coaCodeField = document.getElementById(coaCodeFieldName);
	var coaCode = null;
	//alert("getElementById coaCodeField = " + coaCodeField);
	
	if ( coaCodeField != null ) {
		// if that element id exists, then chart code field is a select drop down list
		coaCode = coaCodeField.value;
		//alert("Field value coaCode = " + coaCode);
	}
	else {
		// otherwise chart code field is readOnly and its id is coaCodeFieldName+".div" 
		coaCode = dwr.util.getValue(coaCodeFieldName + ".div");
		//alert("DWR getValue coaCode = " + coaCode);
		
		// after accounting line is added chart code is rendered with a URL link, need to strip that off
		var index = coaCode.indexOf("</a>");
		if (index > 0) {			
			coaCode = coaCode.substring(index-2,index);
			//alert("After striping URL, coaCode = " + coaCode);
		}

		// remove &nbsp's and white spaces
		coaCode = coaCode.replace("&nbsp;", "").replace(/^\s+|\s+$/g,"");
		//alert("After striping spaces, coaCode = " + coaCode);		
	}
	
	return coaCode;
}
*/

/** searchs for all child nodes recursively and executes the specified function **/
function NodeIterator() {
	this.iterate = function iterate(func, node) {
		for (var x in node) {
			var childNode = node.childNodes[x];
			if (childNode[x].id && childNode[x].id.substring(0, 4) === 'tab-' 
				&& childNode[x].id.substring(childNode[x].id.length - 4, childNode[x].id.length) === '-div') {
				func(document, childNode[x].id.substring(4, childNode[x].id.length - 4));
			}
			if (childNode.childNodes.length > 0) {
				this.iterate(func, childNode);
			}
		}
	}			
}

