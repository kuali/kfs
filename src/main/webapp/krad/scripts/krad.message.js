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
 * Show growl with message, title and theme passed in
 *
 * @param message message of this jGrowl
 * @param title title of this jGrowl, can be empty string for none
 * @param theme class to append to jGrowl classes, can be empty string for none
 */
function showGrowl(message, title, theme) {
    var context = getContext();
    if (theme) {
        context.jGrowl(message, { header: title, theme: theme});
    }
    else {
        context.jGrowl(message, { header: title});
    }
}

/**
 * Set default growl options for this view
 *
 * @param options
 */
function setGrowlDefaults(options) {
    var context = getContext();
    context.jGrowl.defaults = context.extend(context.jGrowl.defaults, options);
}

/**
 * Uses jQuery plug-in to show a loading notification for a page request. See
 * <link>http://plugins.jquery.com/project/showLoading</link> for documentation
 * on options.
 *
 * @param showLoading -
 *          boolean that indicates whether the loading indicator should be shown
 *          (true) or hidden (false)
 */
function createLoading(showLoading) {
    var loadingMessage =  '<h1><img src="' + getConfigParam("kradImageLocation") + 'loading.gif" alt="working..." />Loading...</h1>';
    var savingMessage = '<h1><img src="' + getConfigParam("kradImageLocation") + 'loading.gif" alt="working..." />Saving...</h1>';

    var methodToCall = jq("input[name='methodToCall']").val();
    var unblockUIOnLoading = jq("input[name='unblockUIOnLoading']").val();

    if (unblockUIOnLoading == null || unblockUIOnLoading.toUpperCase() == "false".toUpperCase()) {
        if (showLoading) {
            if (methodToCall && methodToCall.toUpperCase() == "save".toUpperCase()) {
                getContext().blockUI({message: savingMessage});
            }
            else {
                getContext().blockUI({message: loadingMessage});
            }
        }
        else {
            getContext().unblockUI();
        }
    }
}

function clearServerErrorColors(errorDivId){
    if (errorDivId) {
        var div = jq("#" + errorDivId);
        var label = jq("#" + errorDivId.replace("errors_div", "label"));
        var highlightLine = "";

        //check to see if the option to highlight fields is on
        if (div.length > 0 && !div.hasClass("noHighlight")) {
            if (div.parent().is("td") || (div.parent().is(".refreshWrapper") && div.parent().parent().is("td"))) {
                highlightLine = div.closest("td");
            }
            else {
                highlightLine = div.closest(".fieldLine");
            }

            if (highlightLine.length > 0) {
                highlightLine.removeClass("kr-serverError");
            }
        }
    }
}

/**
 * Applies the error coloring for fields with errors, warnings, or information
 */
function applyErrorColors(errorDivId, errorNum, warningNum, infoNum, clientSide) {
    if (errorDivId) {
        var div = jq("#" + errorDivId);
        var label = jq("#" + errorDivId.replace("errors_div", "label"));
        var highlightLine = "";

        //check to see if the option to highlight fields is on
        if (div.length > 0 && !div.hasClass("noHighlight")) {
            if (div.parent().is("td") || (div.parent().is(".refreshWrapper") && div.parent().parent().is("td"))) {
                highlightLine = div.closest("td");
            }
            else {
                highlightLine = div.closest(".fieldLine");
            }

            if (highlightLine.length > 0) {

                if (errorNum && !clientSide) {

                    highlightLine.addClass("kr-serverError");
                    label.addClass("kr-serverError");
                }
                else if (errorNum) {
                    highlightLine.addClass("kr-clientError");
                    label.addClass("kr-clientError");
                }
                else if (warningNum) {
                    highlightLine.addClass("kr-warning");
                    label.addClass("kr-warning");
                }
                else if (infoNum) {
                    highlightLine.addClass("kr-information");
                    label.addClass("kr-information");
                }
                else {
                    //we are only removing errors client side - no knowledge of warnings/infos
                    if (div.parent().hasClass("kr-errorsField")) {
                        var error_ul = div.parent().find(".kr-errorMessages").find("ul.errorLines");
                        var moreErrors = false;
                        error_ul.each(function() {
                            jq(this).children().each(function() {
                                if (jq(this).css("display") != "none") {
                                    moreErrors = true;
                                    return false;
                                }
                            });
                            if (moreErrors) {
                                return false;
                            }
                        });

                        label.removeClass("kr-clientError");
                        if (!moreErrors) {
                            highlightLine.removeClass("kr-clientError");
                        }
                    }
                    else {
                        highlightLine.removeClass("kr-clientError");
                        label.removeClass("kr-clientError");
                    }
                }
            }
        }

        //highlight tab that contains errors - no setting to turn this off because it is necessary
        var tabDiv = div.closest(".ui-tabs-panel");
        if (tabDiv.length > 0) {
            var tabId = tabDiv.attr("id");
            var tabAnchor = jq("a[href='#" + tabId + "']");
            var errorIcon = jq("#" + tabId + "_errorIcon");

            if (tabAnchor.length > 0) {
                var hasErrors = false;
                if (errorNum) {
                    hasErrors = true;
                }
                else {
                    var error_li = tabDiv.find(".kr-errorMessages").find("li");
                    error_li.each(function() {
                        if (jq(this).css("display") != "none") {
                            hasErrors = true;
                        }
                    });
                }

                if (hasErrors) {
                    tabAnchor.addClass("kr-clientError");
                    if (errorIcon.length == 0) {
                        tabAnchor.append("<img id='" + tabId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "errormark.gif'>");
                    }
                }
                else if (!hasErrors) {
                    tabAnchor.removeClass("kr-clientError");
                    errorIcon.remove();
                }
            }
        }
    }
}

/**
 * Shows the field error icon if errorCount is greater than one and errorsField
 * has the option turned on
 */
function showFieldIcon(errorsDivId, errorCount) {
    if (errorsDivId) {
        var div = jq("#" + errorsDivId);
        var inputId = errorsDivId.replace("_errors_div", "");

        if (inputId) {
            var input = jq("#" + inputId);
            var errorIcon = jq("#" + inputId + "_errorIcon");

            if (div.length > 0 && div.hasClass("addFieldIcon") && errorCount && errorIcon.length == 0) {
                if (input.length > 0) {
                    input.after("<img id='" + inputId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "errormark.gif'>");
                }
                else {
                    // try for radios and checkboxes
                    input = jq("#" + errorDivId.replace("errors_div", "attribute1"));
                    if (input.length > 0) {
                        input.after("<img id='" + inputId + "_errorIcon' alt='error' src='" + getConfigParam("kradImageLocation") + "errormark.gif'>");
                    }
                }
            }
            else if (div.length > 0 && div.hasClass("addFieldIcon") && errorCount == 0) {
                if (errorIcon.length > 0) {
                    errorIcon.remove();
                }
            }
        }
    }
}

/**
 * Adds the icon that indicates the contents of a field have changed from the compared value (for instance the new side
 * on maintenance documents) to the field markers span
 *
 * @param fieldId - id for the field the icon should be added to
 */
function showChangeIcon(fieldId) {
    var fieldMarkerSpan = jq("#" + fieldId + "_attribute_markers");
    var fieldIcon = jq("#" + fieldId + "_changeIcon");

    if (fieldMarkerSpan.length > 0 && fieldIcon.length == 0) {
        fieldMarkerSpan.append("<img id='" + fieldId + "_changeIcon' alt='change' src='" + getConfigParam("kradImageLocation") + "asterisk_orange.png'>");
    }
}

/**
 * Add icon to a group header that indicates the data for the group has changed
 *
 * @param headerFieldId - id for the header field the icon should be added to
 */
function showChangeIconOnHeader(headerFieldId) {
    var headerSpan = jq("#" + headerFieldId + "_header");
    var headerIcon = jq("#" + headerFieldId + "_changeIcon");

    if (headerSpan.length > 0 && headerIcon.length == 0) {
        headerSpan.append("<img id='" + headerFieldId + "_changeIcon' alt='change' src='" + getConfigParam("kradImageLocation") + "asterisk_orange.png'>");
    }
}

// Applies the watermark to the input with the id specified
function createWatermark(id, watermark) {
    jq("#" + id).watermark(watermark);
}

/**
 * If the content is an incident report view, replaces the current view with the incident report and
 * returns true, otherwise returns false
 *
 * @param content
 * @returns {Boolean} true if there was an incident, false otherwise
 */
function handleIncidentReport(content) {
    var viewId = jq("#viewId", content);
    if (viewId.length && viewId.val() === "Uif-IncidentReportView") {
        jq('#view_div').replaceWith(content);
        runHiddenScriptsAgain("");
        return true;
    }
    else {
        return false;
    }
}
