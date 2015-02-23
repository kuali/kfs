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
/**
 * Runs the validation script if the validator is already setup, otherwise adds a handler
 * to the document which will run once when the 'validationSetup' event is fired
 *
 * @param scriptFunction
 */
function runValidationScript(scriptFunction){
	if(pageValidatorReady){
		scriptFunction();
	}
	else{
		jq(document).bind('validationSetup', function(event){
			jq(this).unbind(event);
			scriptFunction();
		});
	}
}

//checks to see if any fields depend on the field being validated, if they do calls validate
//on them as well which will either add errors or remove them
//Note: with the way that validation work the field must have been previously validated (ie validated)
function dependsOnCheck(element, nameArray){
	var name;
	if(jq(element).is("option")){
		name = jq(element).parent().attr('name');
	}
	else{
		name = jq(element).attr('name');
	}
    name = escapeName(name);
	jq("[name='"+ name + "']").trigger("checkReq");
	nameArray.push(name);
	

	jq(".dependsOn-" + name).each(function(){
		
		var elementName;
		if(jq(this).is("option")){
			elementName = jq(this).parent().attr('name');
		}
		else{
			elementName = jq(this).attr('name');
		}
		elementName = escapeName(elementName);
		
		if (jq(this).hasClass("valid") || jq(this).hasClass("error")) {
			jq.watermark.hide(this);
			var valid = jq(this).valid();
            jq(this).attr("aria-invalid", !valid );
			jq.watermark.show(this);
			var namePresent = jq.inArray(elementName, nameArray);
			if(namePresent == undefined || namePresent == -1){
				dependsOnCheck(this, nameArray);
			}
		}
	});
}

/**
 * Sets up a req indicator check for the controlName, when it changes, checks to see if it satisfies
 * some booleanFunction and then shows an indicator on the now required field (identified by requiredName).
 * If not satisfied, removes the indicator
 *
 * @param controlName
 * @param requiredName
 * @param booleanFunction
 */
function setupShowReqIndicatorCheck(controlName, requiredName, booleanFunction){
	if(jq("[name='"+ escapeName(controlName) + "']").length){

		var id = jq("[name='"+ escapeName(requiredName) + "']").attr("id");
		var indicator;
		if(id){
			var label = jq("#" + id + "_label_span");
			if(label.length){
				indicator = label.find(".required");
			}
		}

		//check right now if it satisfies the condition, only if an indicator is not shown
		//(indicators that are shown stay shown for this check, as the server or another check must have shown them)
		if(indicator != null && indicator.length && indicator.is(':hidden')){
			checkForRequiredness(controlName, requiredName, booleanFunction, indicator);
		}

		//also check condition when corresponding control is changed
		jq("[name='"+ escapeName(controlName) + "']").change(function(){
			checkForRequiredness(controlName, requiredName, booleanFunction, indicator);
		});
		
		jq("[name='"+ escapeName(controlName) + "']").bind("checkReq", function(){
			checkForRequiredness(controlName, requiredName, booleanFunction, indicator);
		});
	}
}

function checkForRequiredness(controlName, requiredName, booleanFunction, indicator){
	if(indicator != null && indicator.length){
		if(booleanFunction()){
			indicator.show();
            jq("[name='"+ escapeName(requiredName) + "']").attr("aria-required", "true");
		}
		else{
			indicator.hide();
            jq("[name='"+ escapeName(requiredName) + "']").attr("aria-required", "false");
		}
	}
}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns the total if the num of fields matched
function mustOccurTotal(nameArray, min, max){
	var total = 0;
	for(i=0; i < nameArray.length; i++){
		if(coerceValue(nameArray[i])){
			total++;
		}
	}

	return total;

}

//checks to see if the fields with names specified in the name array contain a value
//if they do - returns 1 if the num of fields with values are between min/max
//this function is used to for mustoccur constraints nested in others
function mustOccurCheck(total, min, max){

	if (total >= min && total <= max) {
		return 1;
	}
	else {
		return 0;
	}
}