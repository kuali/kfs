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
function getSelectedPropositionInput() {
    return jq('input[id="proposition_selected_attribute"]');
}

function getCutPropositionInput() {
    return jq('input[id="proposition_cut_attribute"]');
}

function getPropositionIdFromParentLi(parentLiNode) {
    return jq(parentLiNode).find('input.hiddenId').first().attr('value');
}


function ajaxCallPropositionTree(controllerMethod, collectionGroupId) {

    var collectionGroupDivLocator = '#' + collectionGroupId + '_div';

    var elementToBlock = jq(collectionGroupDivLocator);
    var selectedItemInput = getSelectedPropositionInput();
    var selectedItemId = selectedItemInput.val();
    var selectedItemInputName = selectedItemInput.attr('name');

    var updateCollectionCallback = function(htmlContent){
        var component = jq(collectionGroupDivLocator, htmlContent);

        elementToBlock.unblock({onUnblock: function(){
            //replace component
            if(jq(collectionGroupDivLocator).length){
                jq(collectionGroupDivLocator).replaceWith(component);
            }
            runHiddenScripts(collectionGroupId + '_div');
            propButtonsInit(); // disableButtons.js
        }
        });

    };

    ajaxSubmitForm(controllerMethod, updateCollectionCallback,
            {reqComponentId: collectionGroupId, skipViewInit: 'true', selectedItemInputName: selectedItemId},
            elementToBlock);
}

function ajaxCutPropositionTree(controllerMethod, collectionGroupId) {
    jq('a.ruleTreeNode').each( function() {
        var propositionId = getPropositionIdFromParentLi(this.parentNode);
        var selectedItemTracker = getSelectedPropositionInput();
        var selectedItemId = selectedItemTracker.val();
        var cutItemTracker = getCutPropositionInput();

        if (selectedItemId == propositionId) {
            // simulate click, which will mark it
            //jq(this).click();
//            jq(this.parentNode).addClass('ruleCutSelected');
//            jq(this.parentNode).removeClass('ruleBlockSelected');
            cutItemTracker.val(propositionId);
        }
    });
    ajaxCallPropositionTree(controllerMethod, collectionGroupId);
}

function ajaxPastePropositionTree(controllerMethod, collectionGroupId) {
    jq('a.ruleTreeNode').each( function() {
        jq(this.parentNode).removeClass('ruleCutSelected');
    });
    var cutItemTracker = getCutPropositionInput();
    cutItemTracker.val(null);
    ajaxCallPropositionTree(controllerMethod, collectionGroupId);
}

function markNodeAsSelected(parentLiNode) {
    if (!jq(parentLiNode).hasClass('ruleCutSelected')) {
        jq(parentLiNode).addClass('ruleBlockSelected');
    }

    if (!propositionAddInProgress()) {
        enableTreeButtons(); // disableButtons.js
        // show hidden edit image link
        jq(parentLiNode).find(".actionReveal").first().show();
    }
}

function handlePropositionNodeClick(parentLiNode) {
    var propositionId = getPropositionIdFromParentLi(parentLiNode);
    var selectedItemTracker = getSelectedPropositionInput();

    // Don't allow other propositions to be selected when the proposition description is blank
    if (propositionWithoutDescription(parentLiNode)) {
        jQuery(".editDescription").focusout()
        return;
    }

    // make li show containment of children
    jq('li').each(function() {
        jq(this).removeClass('ruleBlockSelected');
        // hide edit image links
        jq(this.parentNode).find(".actionReveal").hide();
    });

    if (selectedItemTracker.val() == propositionId) {
        // if this item is already selected, deselect it
        selectedItemTracker.val('');
        if (!propositionAddInProgress()) {
            disableTreeButtons(); // disableButtons.js
            enableAddButton(); // disableButtons.js
            enableRefreshButton(); // disableButtons.js
        }
    } else {
        selectedItemTracker.val(propositionId);
        markNodeAsSelected(parentLiNode);
    }
}

/**
 * Check if a proposition is missing a description.
 *
 * When a different proposition is selected and the added/edited proposition has a blank description then this method
 * returns true
 *
 * @return true if description is missing, false otherwise
 */
function propositionWithoutDescription(parentLiNode) {
    var description =  propositionAddInProgress();
    // check if edit is in progress
    if (description) {
        // check if edited proposition is the selected proposition
        if (parentLiNode.find && parentLiNode.find('.editDescription').attr('id') == description.attr('id')) {
            return false;
        } else {
            return true;
        }
    } else {
        return false;
    }
}

/**
 * Check if a proposition is currently being added.
 *
 * As long as the proposition does not have a description the proposition is considered as being
 * in an add status.
 *
 * @return description jQuery object of the proposition that is being added, null if none is currently being added
 */
function propositionAddInProgress() {
    var description =  jQuery(".editDescription");
    return ((description.length > 0) && (jQuery.trim(description.val()) == "")) ? description : null;
}

function initRuleTree(componentId){

    // binding to tree loaded event
    jq('#' + componentId).bind('loaded.jstree', function (event, data) {
        /* make the tree load with all nodes expanded */
        jq('#' + componentId).jstree('open_all');

        // refresh the proposition tree on category selection
        jq('select.categorySelect').change( function() {
            ajaxCallPropositionTree('ajaxRefresh', 'RuleEditorView-Tree')
        });

        // refresh the proposition tree on parameterized term selection
        jq('select.termSelect').change( function() {
            if (this.value.match(/parameterized:.*/)) {
                retrieveComponent('RuleEditorView-TreeGroup', 'RuleEditorView-TreeGroup', 'ajaxRefresh');
            }
        });

        // hide quick action icons (edit and add parent) on proposition tree nodes
        jq(this).find(".actionReveal").hide();

        // selecting the description on an edit node should set it to be selected
        jq('input.editDescription').click( function() {

            var parentLiNode = jq(this).closest('li');
            handlePropositionNodeClick(parentLiNode);
        });

        // rule node clicks should set the selected item
        jq('a.ruleTreeNode').click( function() {
            var parentLiNode = this.parentNode;
            handlePropositionNodeClick(parentLiNode);
        });

        // set type to 'logic' on logic nodes -- this prevents them from being selected
        jq('a.compoundOpCodeNode').each( function() {
            jq('#' + componentId).jstree('set_type', 'logic', this.parentNode);
        });

        /* mark the selected node */
        jq('a.ruleTreeNode').each( function() {
            var propositionId = getPropositionIdFromParentLi(this.parentNode);
            var selectedItemTracker = getSelectedPropositionInput();
            var selectedItemId = selectedItemTracker.val();

            if ((typeof selectedItemId !== "undefined") && (selectedItemId == propositionId)) {
                markNodeAsSelected(this.parentNode);
            }

            var cutItemTracker = getCutPropositionInput();
            var cutItemId = cutItemTracker.val();
            if ((typeof cutItemId !== "undefined") && (cutItemId == propositionId)) {
                jq(this.parentNode).addClass('ruleCutSelected');
                cutItemTracker.val(cutItemId);
            } else {
                jq(this.parentNode).removeClass('ruleCutSelected');
            }
        });

        /* update sister compound operators and update proposition summary */
        jq("[name$='data.proposition.compoundOpCode']").change(function(){
            var onChangeElementId = this.id;

            jq("select").filter(function() {
                return this.id.match(
                        new RegExp(onChangeElementId.replace(/^(\d+_node_)(\d+)(_.*)$/, '^$1\\d+$3$$'))
                );
            }).val(jq(this).val());

            ajaxCallPropositionTree('updateCompoundOperator', 'RuleEditorView-PropositionSummary');
        })

    });


    /* create the tree */
    createTree(componentId, {
        'plugins' : ['themes','html_data', 'ui', 'crrm', 'types' /*, 'dnd' */ ], // disabled drag and drop plugin
        'ui' : { 'select_limit' : 1 },
        'themes' : { 'theme':'krms','dots': true ,'icons': false },
        'crrm' : {
            /* This is where you can control what is draggable onto what within the tree: */
            'move' : {
                /*
                 * m.o - the node being dragged
                 * m.r - the target node
                 */
                'check_move' : function (m) {
                    var p = this._get_parent(m.o);
                    if(!p) return false;
                    p = p == -1 ? this.get_container() : p;

                    if (m.o.hasClass('logicNode')) return false;

                    if(p === m.np) return true;
                    if(p[0] && m.np[0] && p[0] === m.np[0]) return true;
                    return false;
                }
            }
        },
        'types' : {
            'types' : {
                /* nodes set to type 'logic' will not be selectable */
                'logic' : { 'select_node' : false }
            }
        },
        'dnd' : { 'drag_target' : false, 'drop_target' : false }
    } );

}

 


