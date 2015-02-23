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
var ADD = '.kr-add-button';
var CUT='.kr-cut-button';
var PASTE='.kr-paste-button';
var DELETE='.kr-delete-button';
var REFRESH='.kr-refresh-button';
var TREE = '.tree-bar-button';

var ENABLED = true;
var pasting = false;

function disableButton(id) {
    if (ENABLED && jq(id) != null) {
        jq(id).attr('disabled', true);
//        not using grayed out images yet..
//        jq(id + ' > img').attr('src', 'yourdisabledimg.jpg');
//        did the css stuff in a new css-class
//        jq(id).css('cursor', 'default');
//        jq(id).css('color', 'gray');
        jq(id).removeClass('kr-button-primary');
        jq(id).removeClass('kr-button-secondary1');
        jq(id).addClass('kr-button-primary-disabled');
    }    
}

function enableButton(id) {
    if (ENABLED && jq(id) != null) {
        jq(id).removeAttr('disabled');
        jq(id).removeClass('kr-button-primary-disabled');
        jq(id).addClass('kr-button-primary');
    }
}

function enableAddButton() {
    enableButton(ADD);
}

function enablePasteButton() {
    pasting = true;
    enableButton(PASTE);
}

function enableDeleteButton() {
    enableButton(DELETE);
}

function enableRefreshButton() {
    enableButton(REFRESH);
}

function enableTreeButtons() {
    enableButton(TREE);
    if (!pasting) {
        disablePasteButton();
    }
}

function disablePasteButton() {
    pasting = false;
    disableButton(PASTE);
}

function disableTreeButtons() {
    disableButton(TREE);
}


function cutPasteButtonInit() {
    // CUT
    if (jq('.kr-cut-button') != undefined && jq('.kr-cut-button') != null) {
        jq('.kr-cut-button').click(function() {
            enablePasteButton();
        });
    }
    // PASTE
    if (jq('.kr-paste-button') != undefined && jq('.kr-paste-button') != null) {
        jq('.kr-paste-button').click(function() {
            disablePasteButton();
        });
    }
}


function propButtonsInit() {
    disableTreeButtons();

    if (propositionAddInProgress()) {
        disablePasteButton();
        enableDeleteButton();
    } else {
        cutPasteButtonInit();
        enableAddButton();
        enableRefreshButton();
        selectedCheck();
        selectedPropCheck();
    }
}

var onProp = false;
function enabledCheck(command) {
    if (onProp) return true;

    if (command == 'edit') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'add') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'addparent') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'left') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'right') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'up') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'down') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'cut') {
        onProp = true;
        propButtonsInit();
        enablePasteButton(); // clicks have not been inited yet, enable paste button
    } else if (command == 'paste') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'refresh') {
        onProp = true;
        propButtonsInit();
    } else if (command == 'delete') {
        onProp = true;
        propButtonsInit();
    }
    return onProp;
}

function selectedCheck() {
    if (getSelectedItemInput() != null) {
        if (getSelectedItemInput().val() != "" && getSelectedItemInput().val() != undefined) {
            enableTreeButtons();
        }
    }
}

function selectedPropCheck() {
    if (getSelectedPropositionInput() != null) {
        if (getSelectedPropositionInput().val() != "" && getSelectedPropositionInput().val() != undefined) {
            enableTreeButtons();
        }
    }
}

function loadControlsInit() {
    if (ENABLED) {
        disablePasteButton();
        propButtonsInit();
    }
}