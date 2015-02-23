/*
 * Copyright 2005-2014 The Kuali Foundation
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
// global vars
var $dialog = null;
var jq = jQuery.noConflict();

//clear out blockUI css, using css class overrides
jQuery.blockUI.defaults.css = {};
jQuery.blockUI.defaults.overlayCSS = {};

// validation init
var pageValidatorReady = false;
var validateClient = true;

//sets up the validator with the necessary default settings and methods
//also sets up the dirty check and other page scripts
//note the use of onClick and onFocusout for on the fly validation client side
function setupPage(validate){
	jq('#kualiForm').dirty_form({changedClass: 'dirty'});

    validateClient = validate;
	//Make sure form doesn't have any unsaved data when user clicks on any other portal links, closes browser or presses fwd/back browser button
	jq(window).bind('beforeunload', function(evt){
        var validateDirty = jq("[name='validateDirty']").val();
		if (validateDirty == "true")
		{
			var dirty = jq(".uif-field").find("input.dirty");
			//methodToCall check is needed to skip from normal way of unloading (cancel,save,close)
			var methodToCall = jq("[name='methodToCall']").val();
			if (dirty.length > 0 && methodToCall == null)
			{
				return "Form has unsaved data. Do you want to leave anyway?";
			}
		}
	});

	jq('#kualiForm').validate(
	{
		onsubmit: false,
		ignore: ".ignoreValid",
		onclick: function(element) {
            if(validateClient){
                if(element.type != "select-one") {
                    var valid = jq(element).valid();
                    dependsOnCheck(element, new Array());
                }
            }
		},
		onfocusout: function(element) {
            if(validateClient){
                var valid = jq(element).valid();
                dependsOnCheck(element, new Array());
                if((element.type == "select-one" || element.type == "select-multiple") && jq(element).hasClass("valid")){
                    applyErrorColors(getAttributeId(element.id, element.type) + "_errors_div", 0, 0, 0, true);
                    showFieldIcon(getAttributeId(element.id, element.type) + "_errors_div", 0);
                }
            }
		},
		wrapper: "li",
		highlight: function(element, errorClass, validClass) {
			jq(element).addClass(errorClass).removeClass(validClass);
            jq(element).attr("aria-invalid", "true");
			applyErrorColors(getAttributeId(element.id, element.type) + "_errors_div", 1, 0, 0, true);
			showFieldIcon(getAttributeId(element.id, element.type) + "_errors_div", 1);
		},
		unhighlight: function(element, errorClass, validClass) {
			jq(element).removeClass(errorClass).addClass(validClass);
            jq(element).attr("aria-invalid", "false");
			applyErrorColors(getAttributeId(element.id, element.type) + "_errors_div", 0, 0, 0, true);
			showFieldIcon(getAttributeId(element.id, element.type) + "_errors_div", 0);
		},
		errorPlacement: function(error, element) {
			var id = getAttributeId(element.attr('id'), element[0].type);
			//check to see if the option to use labels is on
			if (!jq("#" + id + "_errors_div").hasClass("noLabels")) {
				var label = getLabel(id);
				label = jq.trim(label);
				if (label) {
					if (label.charAt(label.length - 1) == ":") {
						label = label.slice(0, -1);
					}
					error.find("label").before(label + " - ");
				}
			}
			jq("#" + id + "_errors_div").show();
			jq("#" + id + "_errors_errorMessages").show();
			var errorList = jq("#" + id + "_errors_errorMessages ul");
			error.appendTo(errorList);
		}
	});

    jq(".required").each(function(){
        jq(this).attr("aria-required", "true");
    });

	jq(document).trigger('validationSetup');
	pageValidatorReady = true;

	jq.watermark.showAll();
}

jQuery.validator.addMethod("minExclusive", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || value > param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("maxInclusive", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || value <= param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("minLengthConditional", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || this.getLength(jq.trim(value), element) >= param[0];
	}
	else{
		return true;
	}
});
jQuery.validator.addMethod("maxLengthConditional", function(value, element, param){
	if (param.length == 1 || param[1]()) {
		return this.optional(element) || this.getLength(jq.trim(value), element) <= param[0];
	}
	else{
		return true;
	}
});

// data table initialize default sorting
jQuery.fn.dataTableExt.oSort['kuali_date-asc']  = function(a,b) {
	var date1 = a.split('/');
	var date2 = b.split('/');
	var x = (date1[2] + date1[0] + date1[1]) * 1;
	var y = (date2[2] + date2[0] + date2[1]) * 1;
	return ((x < y) ? -1 : ((x > y) ?  1 : 0));
};

jQuery.fn.dataTableExt.oSort['kuali_date-desc'] = function(a,b) {
	var date1 = a.split('/');
	var date2 = b.split('/');
	var x = (date1[2] + date1[0] + date1[1]) * 1;
	var y = (date2[2] + date2[0] + date2[1]) * 1;
	return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
};

jQuery.fn.dataTableExt.oSort['kuali_percent-asc'] = function(a,b) {
	var num1 = a.replace(/[^0-9]/g, '');
	var num2 = b.replace(/[^0-9]/g, '');
	num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1*1;
	num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2*1;
	return num1 - num2;
};

jQuery.fn.dataTableExt.oSort['kuali_percent-desc'] = function(a,b) {
	var num1 = a.replace(/[^0-9]/g, '');
	var num2 = b.replace(/[^0-9]/g, '');
	num1 = (num1 == "-" || num1 === "" || isNaN(num1)) ? 0 : num1*1;
	num2 = (num2 == "-" || num2 === "" || isNaN(num2)) ? 0 : num2*1;
	return num2 - num1;
};

jQuery.fn.dataTableExt.oSort['kuali_currency-asc'] = function(a,b) {
	/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
	var x = a == "-" ? 0 : a.replace( /,/g, "" );
	var y = b == "-" ? 0 : b.replace( /,/g, "" );
	/* Remove the currency sign */
	x = x.substring( 1 );
	y = y.substring( 1 );
	/* Parse and return */
	x = parseFloat( x );
	y = parseFloat( y );
	
	x = isNaN(x) ? 0 : x*1;
	y = isNaN(y) ? 0 : y*1;
	
	return x - y;
};

jQuery.fn.dataTableExt.oSort['kuali_currency-desc'] = function(a,b) {
	/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
	var x = a == "-" ? 0 : a.replace( /,/g, "" );
	var y = b == "-" ? 0 : b.replace( /,/g, "" );
	/* Remove the currency sign */
	x = x.substring( 1 );
	y = y.substring( 1 );
	/* Parse and return */
	x = parseFloat( x );
	y = parseFloat( y );
	
	x = isNaN(x) ? 0 : x;
	y = isNaN(y) ? 0 : y;
	
	return y - x;
};

jQuery.fn.dataTableExt.afnSortData['dom-text'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+')', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		var input = jq(this).find('input:text');
		if(input.length != 0){
			aData.push( input.val() );	
		}else{
            // match DataField or InputField CSS classes - respectively
			var input1 = jq(this).find('.uif-field');
			if(input1.length != 0){
				aData.push(jq.trim(input1.find("span:first").text()));
			}else{
				aData.push("");
			}
		}
		
	} );
	return aData;
}

/* Create an array with the values of all the select options in a column */
jQuery.fn.dataTableExt.afnSortData['dom-select'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+')', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		var selected = jq(this).find('select option:selected:first');
		if(selected.length != 0){
			aData.push( selected.text() );	
		}else{
			var input1 = jq(this).find('.uif-inputField');
			if(input1.length != 0){
				aData.push(jq.trim(input1.text()));
			}else{
				aData.push( "");
			}
		}
		
	} );
	return aData;
}

/* Create an array with the values of all the checkboxes in a column */
jQuery.fn.dataTableExt.afnSortData['dom-checkbox'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+')', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		var checkboxes = jq(this).find('input:checkbox');
		if(checkboxes.length != 0){
			var str = "";
			for(i=0; i < checkboxes.length; i++){
				var check = checkboxes[i]; 
				if (check.checked == true && check.value.length > 0){
					str += check.value + " ";
				}
			}
			aData.push( str );
		}else{
			var input1 = jq(this).find('.uif-inputField');
			if(input1.length != 0){
				aData.push(jq.trim(input1.text()));
			}else{
				aData.push( "");
			}
		}
		
	} );
	return aData;
	
}

jQuery.fn.dataTableExt.afnSortData['dom-radio'] = function  ( oSettings, iColumn )
{
	var aData = [];
	jq( 'td:eq('+iColumn+')', oSettings.oApi._fnGetTrNodes(oSettings) ).each( function () {
		var radioButtons = jq(this).find('input:radio');
		if(radioButtons.length != 0){
			var value = "";
			for(i=0; i < radioButtons.length; i++){
				var radio = radioButtons[i];
				if (radio.checked == true){
					value = radio.value;
					break;
				}
			}
			aData.push( value );
		}else{
			var input1 = jq(this).find('.uif-inputField');
			if(input1.length != 0){
				aData.push(jq.trim(input1.text()));
			}else{
				aData.push( "");
			}
		}
		
	} );
	return aData;
	
}

// setup window javascript error handler
window.onerror = errorHandler;

function errorHandler(msg,url,lno)
{
  jq("#view_div").show();
  jq("#viewpage_div").show();
  var context = getContext();
  context.unblockUI();
  showGrowl(msg + '<br/>' + url + '<br/>' + lno, 'Javascript Error', 'errorGrowl');
  return false;
}

// common event registering done here through JQuery ready event
jq(document).ready(function() {
	setPageBreadcrumb();

	// buttons
	jq("input:submit").button();
	jq("input:button").button();
    jq("a.button").button();

    // common ajax setup
	jq.ajaxSetup({
		  beforeSend: function() {
		     createLoading(true);
		  },
		  complete: function(){
			 createLoading(false);
		  },
		  error: function(jqXHR, textStatus, errorThrown){
			 createLoading(false);
			 showGrowl('Status: ' + textStatus + '<br/>' + errorThrown, 'Server Response Error', 'errorGrowl');
		  }
	});

	runHiddenScripts("");
    jq("#view_div").show();
    createLoading(false);

    // hide the ajax progress display screen if the page is replaced e.g. by a login page when the session expires
    jq(window).unload(function() {
        createLoading(false);
    });
});

// script that should execute when the page unloads
jq(window).bind('beforeunload', function (evt) {
    // clear server form if closing the browser tab/window or going back
    // TODO: work out back button problem so we can add this clearing
//    if (!event.clientY || (event.clientY < 0)) {
//        clearServerSideForm();
//    }
});



