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
 * Submits the form through an ajax submit, the response is the new page html
 * runs all hidden scripts passed back (this is to get around a bug with premature script evaluation)
 *
 * If a form has the properties enctype or encoding set to multipart/form-data, an iframe is created to hold the response
 * If the returned response contains scripts that are meant to be run on page load,
 * they will be executed within the iframe since the jquery ready event is triggered
 *
 * For the above reason, the renderFullView below is set to false so that the script content between <head></head> is left out
 */

function ajaxSubmitForm(methodToCall, successCallback, additionalData, elementToBlock){
	var data;
    //methodToCall checks
	if(methodToCall != null){
		data = {methodToCall: methodToCall, renderFullView: false};
	}
	else{
        var methodToCallInput = jq("input[name='methodToCall']");
        if(methodToCallInput.length > 0){
            methodToCall = jq("input[name='methodToCall']").val();
        }
        //check to see if methodToCall is still null
        if(methodToCall == null || methodToCall === ""){
            data = {renderFullView: false};
        }
        else{
            data = {methodToCall: methodToCall, renderFullView: false};
        }
	}
    //remove this since the methodToCall was passed in or extracted from the page, to avoid issues
    jq("input[name='methodToCall']").remove();
	
	if(additionalData != null){
		jq.extend(data, additionalData);
	}

    var viewState = jq(document).data("ViewState");
    if (!jq.isEmptyObject(viewState)) {
        var jsonViewState = jq.toJSON(viewState);

        // change double quotes to single because escaping causes problems on URL
        jsonViewState = jsonViewState.replace(/"/g, "'");
        jq.extend(data, {clientViewState: jsonViewState});
    }
	
	var submitOptions = {
			data: data, 
			success: function(response){
				var tempDiv = document.createElement('div');
				tempDiv.innerHTML = response;
				var hasError = handleIncidentReport(response);
				if(!hasError){
                    var newServerErrors = jq("#errorsFieldForPage_div", tempDiv).clone();
					successCallback(tempDiv);
                    if(successCallback !== replacePage){
                        jq("#errorsFieldForPage_div").replaceWith(newServerErrors);
                        runHiddenScripts("errorsFieldForPage_div");
                    }
				}
				jq("#formComplete").html("");
			},
            error: function(jqXHR, textStatus) {
                alert( "Request failed: " + textStatus );
            }
	};
	
	if(elementToBlock != null && elementToBlock.length){
		var elementBlockingOptions = {
				beforeSend: function() {
					if(elementToBlock.hasClass("unrendered")){
						elementToBlock.append('<img src="' + getConfigParam("kradImageLocation") + 'loader.gif" alt="working..." /> Loading...');
						elementToBlock.show();
					}
					else{
						elementToBlock.block({
			                message: '<img src="' + getConfigParam("kradImageLocation") + 'loader.gif" alt="working..." /> Updating...',
			                fadeIn:  400,
			                fadeOut:  800
			            });
					}
				},
				complete: function(){
					// note that if you want to unblock simultaneous with showing the new retrieval
					// you must do so in the successCallback
					elementToBlock.unblock();
				},
				error: function(){
					if(elementToBlock.hasClass("unrendered")){
						elementToBlock.hide();
					}
					else{
						elementToBlock.unblock();
					}
				}
		};
	}
	
	jq.extend(submitOptions, elementBlockingOptions);
	var form = jq("#kualiForm");
	form.ajaxSubmit(submitOptions);
}

//Called when a form is being persisted to assure all validation passes
function validateAndSubmit(methodToCall, successCallback){
	jq.watermark.hideAll();

    var validForm = true;
    if(validateClient){
        validForm = jq("#kualiForm").valid();
    }

	if(validForm){
		jq.watermark.showAll();
		ajaxSubmitForm(methodToCall, successCallback, null, null);
	}
	else{
		jq.watermark.showAll();
		jq("#formComplete").html("");
		jumpToTop();
		alert("The form contains errors.  Please correct these errors and try again.");
	}
}

/**
 * Validate form.  When no validation errors exists the form is submitted with the methodToCall of the form.
 * The page is then replaced with the result of the ajax call.
 */
function validateAndSubmitUsingFormMethodToCall(){
    validateAndSubmit(null, replacePage);
}

/**
 * Submits a form via ajax using the jquery form plugin
 * The methodToCall parameter is used to determine the controller method to invoke
 */
function submitForm(){
	var methodToCall = jq("input[name='methodToCall']").val();
	ajaxSubmitForm(methodToCall, replacePage, null, null);
}

function replacePage(contentDiv){
	var page = jq("#viewpage_div", contentDiv);
    page.hide();
	jq("#viewpage_div").replaceWith(page);

	setPageBreadcrumb();

	pageValidatorReady = false;
	runHiddenScripts("viewpage_div");

    jq("#viewpage_div").show();
}

/**
 * Handles a link that should post the form. Should be called from the methods
 * onClick event
 *
 * @param methodToCall -
 *          the value that should be set for the methodToCall parameter
 * @param navigateToPageId -
 *          the id for the page that the link should navigate to
 */
function handleActionLink(methodToCall, navigateToPageId) {
	ajaxSubmitForm(methodToCall, replacePage, {navigateToPageId: navigateToPageId}, null);
}

/**
 * Calls the updateComponent method on the controller with component id passed in.  This id is
 * the component id with any/all suffixes on it not the dictionary id.
 * Retrieves the component with the matching id from the server and replaces a matching
 * _refreshWrapper marker span with the same id with the result.  In addition, if the result contains a label
 * and a displayWith marker span has a matching id, that span will be replaced with the label content
 * and removed from the component.  This allows for label and component content seperation on fields
 *
 * @param id - id for the component to retrieve
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function retrieveComponent(id, baseId, methodToCall){
	var elementToBlock = jq("#" + id + "_refreshWrapper");

	var updateRefreshableComponentCallback = function(htmlContent){
		var component = jq("#" + id + "_refreshWrapper", htmlContent);

        var displayWithId = id;
        if (id.indexOf('_attribute') > 0) {
            displayWithId = id.replace('_attribute', '');
        }

		// special label handling, if any
		var theLabel = jq("#" + displayWithId + "_label_span", htmlContent);
		if(jq(".displayWith-" + displayWithId).length && theLabel.length){
			theLabel.addClass("displayWith-" + displayWithId);
			jq("span.displayWith-" + displayWithId).replaceWith(theLabel);
			component.remove("#" + displayWithId + "_label_span");
		}

		elementToBlock.unblock({onUnblock: function(){
                var origColor = jq(component).css("background-color");
                jq(component).css("background-color", "");
                jq(component).addClass("uif-progressiveDisclosure-highlight");

				// replace component
				if(jq("#" + id + "_refreshWrapper").length){
					jq("#" + id + "_refreshWrapper").replaceWith(component);
				}

				runHiddenScripts(id + "_refreshWrapper");
                if(origColor == ""){
                    origColor = "transparent";
                }

                jq("#" + id + "_refreshWrapper").animate({backgroundColor: origColor}, 5000);
			}
		});

		jq(".displayWith-" + displayWithId).show();
	};

    if (!methodToCall) {
        methodToCall = "updateComponent";
    }
	
	ajaxSubmitForm(methodToCall, updateRefreshableComponentCallback,
			{reqComponentId: id, skipViewInit: "true"}, elementToBlock);
}

/**
 * Invoked when the Show/Hide Inactive button is clicked for a collection to toggle the
 * display of inactive records within the collection. A request is made with ajax to update
 * the collection flag on the server and render the collection group. The updated collection
 * groups contents are then updated in the dom
 *
 * @param collectionGroupId - id for the collection group to update
 * @param showInactive - boolean indicating whether inactive records should be displayed (true) or
 * not displayed (false)
 */
function toggleInactiveRecordDisplay(collectionGroupId, showInactive) {
    var elementToBlock = jq("#" + collectionGroupId + "_div");
    var updateCollectionCallback = function(htmlContent){
    	var component = jq("#" + collectionGroupId + "_div", htmlContent);

		elementToBlock.unblock({onUnblock: function(){
				//replace component
				if(jq("#" + collectionGroupId + "_div").length){
					jq("#" + collectionGroupId + "_div").replaceWith(component);
				}
				runHiddenScripts(collectionGroupId + "_div");
			}
		});
    };
    
    ajaxSubmitForm("toggleInactiveRecordDisplay", updateCollectionCallback, 
			{reqComponentId: collectionGroupId, skipViewInit: "true", showInactiveRecords : showInactive}, 
			elementToBlock);
}

function performCollectionAction(collectionGroupId){
	if(collectionGroupId){
		var elementToBlock = jq("#" + collectionGroupId + "_div");
	    var updateCollectionCallback = function(htmlContent){
	    	var component = jq("#" + collectionGroupId + "_div", htmlContent);

			elementToBlock.unblock({onUnblock: function(){
					//replace component
					if(jq("#" + collectionGroupId + "_div").length){
						jq("#" + collectionGroupId + "_div").replaceWith(component);
					}
					runHiddenScripts(collectionGroupId + "_div");
				}
			});
	    };
	    
	    var methodToCall = jq("input[name='methodToCall']").val();
		ajaxSubmitForm(methodToCall, updateCollectionCallback, {reqComponentId: collectionGroupId, skipViewInit: "true"},
				elementToBlock);
	}
}



//called when a line is added to a collection
function addLineToCollection(collectionGroupId, collectionBaseId){
	if(collectionBaseId){
		var addFields = jq("." + collectionBaseId + "-addField:visible");
		jq.watermark.hideAll();

		var valid = true;
		addFields.each(function(){
			jq(this).removeClass("ignoreValid");
			jq(this).valid();
			if(jq(this).hasClass("error")){
				valid = false;
			}
			jq(this).addClass("ignoreValid");
		});

		jq.watermark.showAll();

		if(valid){
			performCollectionAction(collectionGroupId);
		}
		else{
			jq("#formComplete").html("");
			alert("This addition contains errors.  Please correct these errors and try again.");
		}
	}
}

/** Progressive Disclosure */

/**
 * Same as setupRefreshCheck except the condition will always be true (always refresh when
 * value changed on control)
 *
 * @param controlName - value for the name attribute for the control the event should be generated for
 * @param refreshId - id for the component that should be refreshed when change occurs
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function setupOnChangeRefresh(controlName, refreshId, baseId, methodToCall){
	setupRefreshCheck(controlName, refreshId, baseId, function(){return true;}, methodToCall);
}

/**
 * Sets up the conditional refresh mechanism in js by adding a change handler to the control
 * which may satisfy the conditional refresh condition passed in.  When the condition is satisfied,
 * refresh the necessary content specified by id by making a server call to retrieve a new instance
 * of that component
 *
 * @param controlName - value for the name attribute for the control the event should be generated for
 * @param refreshId - id for the component that should be refreshed when condition occurs
 * @param baseId - base id (without suffixes) for the component that should be refreshed
 * @param condition - function which returns true to refresh, false otherwise
 * @param methodToCall - name of the method that should be invoked for the refresh call (if custom method is needed)
 */
function setupRefreshCheck(controlName, refreshId, baseId, condition, methodToCall){
	jq("[name='"+ escapeName(controlName) +"']").live('change', function() {
		// visible check because a component must logically be visible to refresh
		var refreshComp = jq("#" + refreshId + "_refreshWrapper");
		if(refreshComp.length){
			if(condition()){
				retrieveComponent(refreshId, baseId, methodToCall);
			}
		}
	});
}

/**
 * Sets up the progressive disclosure mechanism in js by adding a change handler to the control
 * which may satisfy the progressive disclosure condition passed in.  When the condition is satisfied,
 * show the necessary content, otherwise hide it.  If the content has not yet been rendered then a server
 * call is made to retrieve the content to be shown.  If alwaysRetrieve is true, the component
 * is always retrieved from the server when disclosed.
 * Do not add check if the component is part of the "old" values on a maintanance document (endswith _c0).
 *
 * @param controlName
 * @param disclosureId
 * @param condition - function which returns true to disclose, false otherwise
 * @param methodToCall - name of the method that should be invoked for the retrieve call (if custom method is needed)
 */
function setupProgressiveCheck(controlName, disclosureId, baseId, condition, alwaysRetrieve, methodToCall){
	if (!baseId.match("\_c0$")) {
		jq("[name='"+ escapeName(controlName) +"']").live('change', function() {
			var refreshDisclosure = jq("#" + disclosureId + "_refreshWrapper");
			if(refreshDisclosure.length){
                var displayWithId = disclosureId;
                if (disclosureId.indexOf('_attribute') > 0) {
                    displayWithId = disclosureId.replace('_attribute', '');
                }

				if(condition()){
					if(refreshDisclosure.hasClass("unrendered") || alwaysRetrieve){
						retrieveComponent(disclosureId, baseId, methodToCall);
					}
					else{
                        var origColor = refreshDisclosure.css("background-color");
                        refreshDisclosure.css("background-color", "");
                        refreshDisclosure.addClass("uif-progressiveDisclosure-highlight");
						refreshDisclosure.show();
                        if(origColor == ""){
                           origColor = "transparent";
                        }
                        refreshDisclosure.animate({backgroundColor: origColor}, 5000);

						//re-enable validation on now shown inputs
						hiddenInputValidationToggle(disclosureId + "_refreshWrapper");
						jq(".displayWith-" + displayWithId).show();

					}
				}
				else{
					refreshDisclosure.hide();
					// ignore validation on hidden inputs
					hiddenInputValidationToggle(disclosureId + "_refreshWrapper");
					jq(".displayWith-" + displayWithId).hide();
				}
			}
		});
	}
}

/**
 * Disables client side validation on any inputs within the element(by id) passed in , if
 * that element is hidden.  Otherwise, it turns input validation back on if the element and
 * its children are visible
 *
 * @param id - id for the component for which the input hiddens should be processed
 */
function hiddenInputValidationToggle(id){
	var element = jq("#" + id);
	if(element.length){
		if(element.css("display") == "none"){
			jq(":input:hidden", element).each(function(){
				jq(this).addClass("ignoreValid");
			});
		}
		else{
			jq(":input:visible", element).each(function(){
				jq(this).removeClass("ignoreValid");
			});
		}
	}
}

/**
 * Makes an get request to the server so that the form for the page we are leaving will
 * be cleared server side
 */
function clearServerSideForm() {
    // make sure we are actually leaving the page and not submitting the form (in which case
    // the methodToCall hidden will be set
    var methodToCall = jq("[name='methodToCall']").val();
    if (methodToCall == null) {
        var queryData = {};

        queryData.methodToCall = 'clearForm';
        queryData.skipViewInit = 'true';
        queryData.formKey = jq("input#formKey").val();

        var postUrl = getConfigParam("kradUrl") + "/listener";

        jq.ajax({
            url:postUrl,
            dataType:"json",
            data:queryData,
            async:false,
            beforeSend:null,
            complete:null,
            error:null,
            success:null
        });
    }
}
