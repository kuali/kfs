/*
Copyright 2007-2009 University of Toronto
Copyright 2007-2010 University of Cambridge
Copyright 2010-2011 OCAD University
Copyright 2010-2011 Lucendo Development Ltd.

Licensed under the Educational Community License (ECL), Version 2.0 or the New
BSD license. You may not use this file except in compliance with one these
Licenses.

You may obtain a copy of the ECL 2.0 License and BSD License at
https://github.com/fluid-project/infusion/raw/master/Infusion-LICENSE.txt
*/

// Declare dependencies
/*global fluid_1_4:true, jQuery*/

// JSLint options 
/*jslint white: true, funcinvoke: true, undef: true, newcap: true, nomen: true, regexp: true, bitwise: true, browser: true, forin: true, maxerr: 100, indent: 4 */

var fluid_1_4 = fluid_1_4 || {};

/* ReordererDOMUtilities.js */

(function ($, fluid) {
    /**
     * Returns the absolute position of a supplied DOM node in pixels.
     * Implementation taken from quirksmode http://www.quirksmode.org/js/findpos.html
     */
    fluid.dom.computeAbsolutePosition = function (element) {
        var curleft = 0, curtop = 0;
        if (element.offsetParent) {
            do {
                curleft += element.offsetLeft;
                curtop += element.offsetTop;
                element = element.offsetParent;
            } while (element);
            return [curleft, curtop];
        }
    };

    /**
     * Cleanse the children of a DOM node by removing all <script> tags.
     * This is necessary to prevent the possibility that these blocks are
     * reevaluated if the node were reattached to the document.
     */
    fluid.dom.cleanseScripts = function (element) {
        var cleansed = $.data(element, fluid.dom.cleanseScripts.MARKER);
        if (!cleansed) {
            fluid.dom.iterateDom(element, function (node) {
                return node.tagName.toLowerCase() === "script" ? "delete" : null;
            });
            $.data(element, fluid.dom.cleanseScripts.MARKER, true);
        }
    };
    fluid.dom.cleanseScripts.MARKER = "fluid-scripts-cleansed";

    /**
     * Inserts newChild as the next sibling of refChild.
     * @param {Object} newChild
     * @param {Object} refChild
     */
    fluid.dom.insertAfter = function (newChild, refChild) {
        var nextSib = refChild.nextSibling;
        if (!nextSib) {
            refChild.parentNode.appendChild(newChild);
        } else {
            refChild.parentNode.insertBefore(newChild, nextSib);
        }
    };

    // The following two functions taken from http://developer.mozilla.org/En/Whitespace_in_the_DOM
    /**
     * Determine whether a node's text content is entirely whitespace.
     *
     * @param node  A node implementing the |CharacterData| interface (i.e.,
     *              a |Text|, |Comment|, or |CDATASection| node
     * @return     True if all of the text content of |nod| is whitespace,
     *             otherwise false.
     */
    fluid.dom.isWhitespaceNode = function (node) {
       // Use ECMA-262 Edition 3 String and RegExp features
        return !(/[^\t\n\r ]/.test(node.data));
    };

    /**
     * Determine if a node should be ignored by the iterator functions.
     *
     * @param nod  An object implementing the DOM1 |Node| interface.
     * @return     true if the node is:
     *                1) A |Text| node that is all whitespace
     *                2) A |Comment| node
     *             and otherwise false.
     */
    fluid.dom.isIgnorableNode = function (node) {
        return (node.nodeType === 8) || // A comment node
            ((node.nodeType === 3) && fluid.dom.isWhitespaceNode(node)); // a text node, all ws
    };

})(jQuery, fluid_1_4);

/* GeometricManager.js */

(function ($, fluid) {

    fluid.orientation = {
        HORIZONTAL: 4,
        VERTICAL: 1
    };

    fluid.rectSides = {
        // agree with fluid.orientation
        4: ["left", "right"],
        1: ["top", "bottom"],
        // agree with fluid.direction
        8: "top",
        12: "bottom",
        2: "left",
        3: "right"
    };

    /**
     * This is the position, relative to a given drop target, that a dragged item should be dropped.
     */
    fluid.position = {
        BEFORE: -1,
        AFTER: 1,
        INSIDE: 2,
        REPLACE: 3
    };

    /**
     * For incrementing/decrementing a count or index, or moving in a rectilinear direction.
     */
    fluid.direction = {
        NEXT: 1,
        PREVIOUS: -1,
        UP: 8,
        DOWN: 12,
        LEFT: 2,
        RIGHT: 3
    };

    fluid.directionSign = function (direction) {
        return direction === fluid.direction.UP || direction === fluid.direction.LEFT ?
            fluid.direction.PREVIOUS : fluid.direction.NEXT;
    };

    fluid.directionAxis = function (direction) {
        return direction === fluid.direction.LEFT || direction === fluid.direction.RIGHT ?
            0 : 1;
    };

    fluid.directionOrientation = function (direction) {
        return fluid.directionAxis(direction) ? fluid.orientation.VERTICAL : fluid.orientation.HORIZONTAL;
    };

    fluid.keycodeDirection = {
        up: fluid.direction.UP,
        down: fluid.direction.DOWN,
        left: fluid.direction.LEFT,
        right: fluid.direction.RIGHT
    };

    // moves a single node in the DOM to a new position relative to another
    fluid.moveDom = function (source, target, position) {
        source = fluid.unwrap(source);
        target = fluid.unwrap(target);

        var scan;
        // fluid.log("moveDom source " + fluid.dumpEl(source) + " target " + fluid.dumpEl(target) + " position " + position);
        if (position === fluid.position.INSIDE) {
            target.appendChild(source);
        } else if (position === fluid.position.BEFORE) {
            for (scan = target.previousSibling;; scan = scan.previousSibling) {
                if (!scan || !fluid.dom.isIgnorableNode(scan)) {
                    if (scan !== source) {
                        fluid.dom.cleanseScripts(source);
                        target.parentNode.insertBefore(source, target);
                    }
                    break;
                }
            }
        } else if (position === fluid.position.AFTER) {
            for (scan = target.nextSibling;; scan = scan.nextSibling) {
                if (!scan || !fluid.dom.isIgnorableNode(scan)) {
                    if (scan !== source) {
                        fluid.dom.cleanseScripts(source);
                        fluid.dom.insertAfter(source, target);
                    }
                    break;
                }
            }
        } else {
            fluid.fail("Unrecognised position supplied to fluid.moveDom: " + position);
        }
    };

    // unsupported, NON-API function
    fluid.normalisePosition = function (position, samespan, targeti, sourcei) {
        // convert a REPLACE into a primitive BEFORE/AFTER
        if (position === fluid.position.REPLACE) {
            position = samespan && targeti >= sourcei ? fluid.position.AFTER : fluid.position.BEFORE;
        }
        return position;
    };

    fluid.permuteDom = function (element, target, position, sourceelements, targetelements) {
        element = fluid.unwrap(element);
        target = fluid.unwrap(target);
        var sourcei = $.inArray(element, sourceelements);
        if (sourcei === -1) {
            fluid.fail("Error in permuteDom: source element " + fluid.dumpEl(element) +
                " not found in source list " + fluid.dumpEl(sourceelements));
        }
        var targeti = $.inArray(target, targetelements);
        if (targeti === -1) {
            fluid.fail("Error in permuteDom: target element " + fluid.dumpEl(target) +
                " not found in source list " + fluid.dumpEl(targetelements));
        }
        var samespan = sourceelements === targetelements;
        position = fluid.normalisePosition(position, samespan, targeti, sourcei);

        //fluid.log("permuteDom sourcei " + sourcei + " targeti " + targeti);
        // cache the old neighbourhood of the element for the final move
        var oldn = {};
        oldn[fluid.position.AFTER] = element.nextSibling;
        oldn[fluid.position.BEFORE] = element.previousSibling;
        fluid.moveDom(sourceelements[sourcei], targetelements[targeti], position);

        // perform the leftward-moving, AFTER shift
        var frontlimit = samespan ? targeti - 1 : sourceelements.length - 2;
        var i;
        if (position === fluid.position.BEFORE && samespan) {
            // we cannot do skip processing if the element was "fused against the grain"
            frontlimit--;
        }
        if (!samespan || targeti > sourcei) {
            for (i = frontlimit; i > sourcei; --i) {
                fluid.moveDom(sourceelements[i + 1], sourceelements[i], fluid.position.AFTER);
            }
            if (sourcei + 1 < sourceelements.length) {
                fluid.moveDom(sourceelements[sourcei + 1], oldn[fluid.position.AFTER], fluid.position.BEFORE);
            }
        }
        // perform the rightward-moving, BEFORE shift
        var backlimit = samespan ? sourcei - 1 : targetelements.length - 1;
        if (position === fluid.position.AFTER) {
            // we cannot do skip processing if the element was "fused against the grain"
            targeti++;
        }
        if (!samespan || targeti < sourcei) {
            for (i = targeti; i < backlimit; ++i) {
                fluid.moveDom(targetelements[i], targetelements[i + 1], fluid.position.BEFORE);
            }
            if (backlimit >= 0 && backlimit < targetelements.length - 1) {
                fluid.moveDom(targetelements[backlimit], oldn[fluid.position.BEFORE], fluid.position.AFTER);
            }
        }

    };

    var curCss = function (a, name) {
        return window.getComputedStyle ? window.getComputedStyle(a, null).getPropertyValue(name) :
            a.currentStyle[name];
    };

    var isAttached = function (node) {
        while (node && node.nodeName) {
            if (node.nodeName === "BODY") {
                return true;
            }
            node = node.parentNode;
        }
        return false;
    };

    var generalHidden = function (a) {
        return "hidden" === a.type || curCss(a, "display") === "none" || curCss(a, "visibility") === "hidden" || !isAttached(a);
    };


    var computeGeometry = function (element, orientation, disposition) {
        var elem = {};
        elem.element = element;
        elem.orientation = orientation;
        if (disposition === fluid.position.INSIDE) {
            elem.position = disposition;
        }
        if (generalHidden(element)) {
            elem.clazz = "hidden";
        }
        var pos = fluid.dom.computeAbsolutePosition(element) || [0, 0];
        var width = element.offsetWidth;
        var height = element.offsetHeight;
        elem.rect = {left: pos[0], top: pos[1]};
        elem.rect.right = pos[0] + width;
        elem.rect.bottom = pos[1] + height;
        return elem;
    };

    // A "suitable large" value for the sentinel blocks at the ends of spans
    var SENTINEL_DIMENSION = 10000;

    function dumprect(rect) {
        return "Rect top: " + rect.top +
                 " left: " + rect.left +
               " bottom: " + rect.bottom +
                " right: " + rect.right;
    }

    function dumpelem(cacheelem) {
        if (!cacheelem || !cacheelem.rect) {
            return "null";
        } else {
            return dumprect(cacheelem.rect) + " position: " +
                cacheelem.position +
                " for " +
                fluid.dumpEl(cacheelem.element);
        }
    }


    // unsupported, NON-API function
    fluid.dropManager = function () {
        var targets = [];
        var cache = {};
        var that = {};

        var lastClosest;
        var lastGeometry;
        var displacementX, displacementY;

        that.updateGeometry = function (geometricInfo) {
            lastGeometry = geometricInfo;
            targets = [];
            cache = {};
            var mapper = geometricInfo.elementMapper;
            for (var i = 0; i < geometricInfo.extents.length; ++i) {
                var thisInfo = geometricInfo.extents[i];
                var orientation = thisInfo.orientation;
                var sides = fluid.rectSides[orientation];

                var processElement = function (element, sentB, sentF, disposition, j) {
                    var cacheelem = computeGeometry(element, orientation, disposition);
                    cacheelem.owner = thisInfo;
                    if (cacheelem.clazz !== "hidden" && mapper) {
                        cacheelem.clazz = mapper(element);
                    }
                    cache[fluid.dropManager.cacheKey(element)] = cacheelem;
                    var backClass = fluid.dropManager.getRelativeClass(thisInfo.elements, j, fluid.position.BEFORE, cacheelem.clazz, mapper);
                    var frontClass = fluid.dropManager.getRelativeClass(thisInfo.elements, j, fluid.position.AFTER, cacheelem.clazz, mapper);
                    if (disposition === fluid.position.INSIDE) {
                        targets[targets.length] = cacheelem;
                    } else {
                        fluid.dropManager.splitElement(targets, sides, cacheelem, disposition, backClass, frontClass);
                    }
                    // deal with sentinel blocks by creating near-copies of the end elements
                    if (sentB && geometricInfo.sentinelize) {
                        fluid.dropManager.sentinelizeElement(targets, sides, cacheelem, 1, disposition, backClass);
                    }
                    if (sentF && geometricInfo.sentinelize) {
                        fluid.dropManager.sentinelizeElement(targets, sides, cacheelem, 0, disposition, frontClass);
                    }
                    //fluid.log(dumpelem(cacheelem));
                    return cacheelem;
                };

                var allHidden = true;
                for (var j = 0; j < thisInfo.elements.length; ++j) {
                    var element = thisInfo.elements[j];
                    var cacheelem = processElement(element, j === 0, j === thisInfo.elements.length - 1,
                            fluid.position.INTERLEAVED, j);
                    if (cacheelem.clazz !== "hidden") {
                        allHidden = false;
                    }
                }
                if (allHidden && thisInfo.parentElement) {
                    processElement(thisInfo.parentElement, true, true,
                            fluid.position.INSIDE);
                }
            }
        };

        that.startDrag = function (event, handlePos, handleWidth, handleHeight) {
            var handleMidX = handlePos[0] + handleWidth / 2;
            var handleMidY = handlePos[1] + handleHeight / 2;
            var dX = handleMidX - event.pageX;
            var dY = handleMidY - event.pageY;
            that.updateGeometry(lastGeometry);
            lastClosest = null;
            displacementX = dX;
            displacementY = dY;
            $("body").bind("mousemove.fluid-dropManager", that.mouseMove);
        };

        that.lastPosition = function () {
            return lastClosest;
        };

        that.endDrag = function () {
            $("body").unbind("mousemove.fluid-dropManager");
        };

        that.mouseMove = function (evt) {
            var x = evt.pageX + displacementX;
            var y = evt.pageY + displacementY;
            //fluid.log("Mouse x " + x + " y " + y );

            var closestTarget = that.closestTarget(x, y, lastClosest);
            if (closestTarget && closestTarget !== fluid.dropManager.NO_CHANGE) {
                lastClosest = closestTarget;

                that.dropChangeFirer.fire(closestTarget);
            }
        };

        that.dropChangeFirer = fluid.event.getEventFirer();

        var blankHolder = {
            element: null
        };

        that.closestTarget = function (x, y, lastClosest) {
            var mindistance = Number.MAX_VALUE;
            var minelem = blankHolder;
            var minlockeddistance = Number.MAX_VALUE;
            var minlockedelem = blankHolder;
            for (var i = 0; i < targets.length; ++i) {
                var cacheelem = targets[i];
                if (cacheelem.clazz === "hidden") {
                    continue;
                }
                var distance = fluid.geom.minPointRectangle(x, y, cacheelem.rect);
                if (cacheelem.clazz === "locked") {
                    if (distance < minlockeddistance) {
                        minlockeddistance = distance;
                        minlockedelem = cacheelem;
                    }
                } else {
                    if (distance < mindistance) {
                        mindistance = distance;
                        minelem = cacheelem;
                    }
                    if (distance === 0) {
                        break;
                    }
                }
            }
            if (!minelem) {
                return minelem;
            }
            if (minlockeddistance >= mindistance) {
                minlockedelem = blankHolder;
            }
            //fluid.log("PRE: mindistance " + mindistance + " element " +
            //   fluid.dumpEl(minelem.element) + " minlockeddistance " + minlockeddistance
            //    + " locked elem " + dumpelem(minlockedelem));
            if (lastClosest && lastClosest.position === minelem.position &&
                    fluid.unwrap(lastClosest.element) === fluid.unwrap(minelem.element) &&
                    fluid.unwrap(lastClosest.lockedelem) === fluid.unwrap(minlockedelem.element)
                    ) {
                return fluid.dropManager.NO_CHANGE;
            }
            //fluid.log("mindistance " + mindistance + " minlockeddistance " + minlockeddistance);
            return {
                position: minelem.position,
                element: minelem.element,
                lockedelem: minlockedelem.element
            };
        };

        that.shuffleProjectFrom = function (element, direction, includeLocked, disableWrap) {
            var togo = that.projectFrom(element, direction, includeLocked, disableWrap);
            if (togo) {
                togo.position = fluid.position.REPLACE;
            }
            return togo;
        };

        that.projectFrom = function (element, direction, includeLocked, disableWrap) {
            that.updateGeometry(lastGeometry);
            var cacheelem = cache[fluid.dropManager.cacheKey(element)];
            var projected = fluid.geom.projectFrom(cacheelem.rect, direction, targets, includeLocked, disableWrap);
            if (!projected.cacheelem) {
                return null;
            }
            var retpos = projected.cacheelem.position;
            return {element: projected.cacheelem.element,
                     position: retpos ? retpos : fluid.position.BEFORE
                     };
        };

        that.logicalFrom = function (element, direction, includeLocked, disableWrap) {
            var orderables = that.getOwningSpan(element, fluid.position.INTERLEAVED, includeLocked);
            return {element: fluid.dropManager.getRelativeElement(element, direction, orderables, disableWrap),
                position: fluid.position.REPLACE};
        };

        that.lockedWrapFrom = function (element, direction, includeLocked, disableWrap) {
            var base = that.logicalFrom(element, direction, includeLocked, disableWrap);
            var selectables = that.getOwningSpan(element, fluid.position.INTERLEAVED, includeLocked);
            var allElements = cache[fluid.dropManager.cacheKey(element)].owner.elements;
            if (includeLocked || selectables[0] === allElements[0]) {
                return base;
            }
            var directElement = fluid.dropManager.getRelativeElement(element, direction, allElements, disableWrap);
            if (lastGeometry.elementMapper(directElement) === "locked") {
                base.element = null;
                base.clazz = "locked";
            }
            return base;
        };

        that.getOwningSpan = function (element, position, includeLocked) {
            var owner = cache[fluid.dropManager.cacheKey(element)].owner;
            var elements = position === fluid.position.INSIDE ? [owner.parentElement] : owner.elements;
            if (!includeLocked && lastGeometry.elementMapper) {
                elements = $.makeArray(elements);
                fluid.remove_if(elements, function (element) {
                    return lastGeometry.elementMapper(element) === "locked";
                });
            }
            return elements;
        };

        that.geometricMove = function (element, target, position) {
            var sourceElements = that.getOwningSpan(element, null, true);
            var targetElements = that.getOwningSpan(target, position, true);
            fluid.permuteDom(element, target, position, sourceElements, targetElements);
        };

        return that;
    };


    fluid.dropManager.NO_CHANGE = "no change";

    fluid.dropManager.cacheKey = function (element) {
        return fluid.allocateSimpleId(element);
    };

    fluid.dropManager.sentinelizeElement = function (targets, sides, cacheelem, fc, disposition, clazz) {
        var elemCopy = $.extend(true, {}, cacheelem);
        elemCopy.rect[sides[fc]] = elemCopy.rect[sides[1 - fc]] + (fc ? 1 : -1);
        elemCopy.rect[sides[1 - fc]] = (fc ? -1 : 1) * SENTINEL_DIMENSION;
        elemCopy.position = disposition === fluid.position.INSIDE ?
            disposition : (fc ? fluid.position.BEFORE : fluid.position.AFTER);
        elemCopy.clazz = clazz;
        targets[targets.length] = elemCopy;
    };

    fluid.dropManager.splitElement = function (targets, sides, cacheelem, disposition, clazz1, clazz2) {
        var elem1 = $.extend(true, {}, cacheelem);
        var elem2 = $.extend(true, {}, cacheelem);
        var midpoint = (elem1.rect[sides[0]] + elem1.rect[sides[1]]) / 2;
        elem1.rect[sides[1]] = midpoint;
        elem1.position = fluid.position.BEFORE;

        elem2.rect[sides[0]] = midpoint;
        elem2.position = fluid.position.AFTER;

        elem1.clazz = clazz1;
        elem2.clazz = clazz2;
        targets[targets.length] = elem1;
        targets[targets.length] = elem2;
    };

    // Expand this configuration point if we ever go back to a full "permissions" model
    fluid.dropManager.getRelativeClass = function (thisElements, index, relative, thisclazz, mapper) {
        index += relative;
        if (index < 0 && thisclazz === "locked") {
            return "locked";
        }
        if (index >= thisElements.length || mapper === null) {
            return null;
        } else {
            relative = thisElements[index];
            return mapper(relative) === "locked" && thisclazz === "locked" ? "locked" : null;
        }
    };

    fluid.dropManager.getRelativeElement = function (element, direction, elements, disableWrap) {
        var folded = fluid.directionSign(direction);

        var index = $(elements).index(element) + folded;
        if (index < 0) {
            index += elements.length;
        }

        // disable wrap
        if (disableWrap) {
            if (index === elements.length || index === (elements.length + folded)) {
                return element;
            }
        }

        index %= elements.length;
        return elements[index];
    };

    fluid.geom = fluid.geom || {};

    // These distance algorithms have been taken from
    // http://www.cs.mcgill.ca/~cs644/Godfried/2005/Fall/fzamal/concepts.htm

    /** Returns the minimum squared distance between a point and a rectangle **/
    fluid.geom.minPointRectangle = function (x, y, rectangle) {
        var dx = x < rectangle.left ? (rectangle.left - x) :
                  (x > rectangle.right ? (x - rectangle.right) : 0);
        var dy = y < rectangle.top ? (rectangle.top - y) :
                  (y > rectangle.bottom ? (y - rectangle.bottom) : 0);
        return dx * dx + dy * dy;
    };

    /** Returns the minimum squared distance between two rectangles **/
    fluid.geom.minRectRect = function (rect1, rect2) {
        var dx = rect1.right < rect2.left ? rect2.left - rect1.right :
                 rect2.right < rect1.left ? rect1.left - rect2.right : 0;
        var dy = rect1.bottom < rect2.top ? rect2.top - rect1.bottom :
                 rect2.bottom < rect1.top ? rect1.top - rect2.bottom : 0;
        return dx * dx + dy * dy;
    };

    var makePenCollect = function () {
        return {
            mindist: Number.MAX_VALUE,
            minrdist: Number.MAX_VALUE
        };
    };

    /** Determine the one amongst a set of rectangle targets which is the "best fit"
     * for an axial motion from a "base rectangle" (commonly arising from the case
     * of cursor key navigation).
     * @param {Rectangle} baserect The base rectangl from which the motion is to be referred
     * @param {fluid.direction} direction  The direction of motion
     * @param {Array of Rectangle holders} targets An array of objects "cache elements"
     * for which the member <code>rect</code> is the holder of the rectangle to be tested.
     * @param disableWrap which is used to enable or disable wrapping of elements
     * @return The cache element which is the most appropriate for the requested motion.
     */
    fluid.geom.projectFrom = function (baserect, direction, targets, forSelection, disableWrap) {
        var axis = fluid.directionAxis(direction);
        var frontSide = fluid.rectSides[direction];
        var backSide = fluid.rectSides[axis * 15 + 5 - direction];
        var dirSign = fluid.directionSign(direction);

        var penrect = {left: (7 * baserect.left + 1 * baserect.right) / 8,
                       right: (5 * baserect.left + 3 * baserect.right) / 8,
                       top: (7 * baserect.top + 1 * baserect.bottom) / 8,
                       bottom: (5 * baserect.top + 3 * baserect.bottom) / 8};

        penrect[frontSide] = dirSign * SENTINEL_DIMENSION;
        penrect[backSide] = -penrect[frontSide];

        function accPen(collect, cacheelem, backSign) {
            var thisrect = cacheelem.rect;
            var pdist = fluid.geom.minRectRect(penrect, thisrect);
            var rdist = -dirSign * backSign * (baserect[backSign === 1 ? frontSide : backSide] -
                                                thisrect[backSign === 1 ? backSide : frontSide]);
            // fluid.log("pdist: " + pdist + " rdist: " + rdist);
            // the oddity in the rdist comparison is intended to express "half-open"-ness of rectangles
            // (backSign === 1 ? 0 : 1) - this is now gone - must be possible to move to perpendicularly abutting regions
            if (pdist <= collect.mindist && rdist >= 0) {
                if (pdist === collect.mindist && rdist * backSign > collect.minrdist) {
                    return;
                }
                collect.minrdist = rdist * backSign;
                collect.mindist = pdist;
                collect.minelem = cacheelem;
            }
        }
        var collect = makePenCollect();
        var backcollect = makePenCollect();
        var lockedcollect = makePenCollect();

        for (var i = 0; i < targets.length; ++i) {
            var elem = targets[i];
            var isPure = elem.owner && elem.element === elem.owner.parentElement;
            if (elem.clazz === "hidden" || (forSelection && isPure)) {
                continue;
            } else if (!forSelection && elem.clazz === "locked") {
                accPen(lockedcollect, elem, 1);
            } else {
                accPen(collect, elem, 1);
                accPen(backcollect, elem, -1);
            }
            //fluid.log("Element " + i + " " + dumpelem(elem) + " mindist " + collect.mindist);
        }
        var wrap = !collect.minelem || backcollect.mindist < collect.mindist;

        // disable wrap
        wrap = wrap && !disableWrap;

        var mincollect = wrap ? backcollect : collect;

        var togo = {
            wrapped: wrap,
            cacheelem: mincollect.minelem
        };
        if (lockedcollect.mindist < mincollect.mindist) {
            togo.lockedelem = lockedcollect.minelem;
        }
        return togo;
    };
})(jQuery, fluid_1_4);

/* Reorderer.js */

(function ($, fluid) {
    
    var defaultAvatarCreator = function (item, cssClass, dropWarning) {
        fluid.dom.cleanseScripts(fluid.unwrap(item));
        var avatar = $(item).clone();
        
        fluid.dom.iterateDom(avatar.get(0), function (node) {
            node.removeAttribute("id");
            if (node.tagName.toLowerCase() === "input") {
                node.setAttribute("disabled", "disabled");
            }
        });
        
        avatar.removeProp("id");
        avatar.removeClass("ui-droppable");
        avatar.addClass(cssClass);
        
        if (dropWarning) {
            // Will a 'div' always be valid in this position?
            var avatarContainer = $(document.createElement("div"));
            avatarContainer.append(avatar);
            avatarContainer.append(dropWarning);
            avatar = avatarContainer;
        }
        $("body").append(avatar);
        if (!$.browser.safari) {
            // FLUID-1597: Safari appears incapable of correctly determining the dimensions of elements
            avatar.css("display", "block").width(item.offsetWidth).height(item.offsetHeight);
        }
        
        if ($.browser.opera) { // FLUID-1490. Without this detect, curCSS explodes on the avatar on Firefox.
            avatar.hide();
        }
        return avatar;
    };
    
    function bindHandlersToContainer(container, keyDownHandler, keyUpHandler, mouseMoveHandler) {
        var actualKeyDown = keyDownHandler;
        var advancedPrevention = false;

        // FLUID-1598 and others: Opera will refuse to honour a "preventDefault" on a keydown.
        // http://forums.devshed.com/javascript-development-115/onkeydown-preventdefault-opera-485371.html
        if ($.browser.opera) {
            container.keypress(function (evt) {
                if (advancedPrevention) {
                    advancedPrevention = false;
                    evt.preventDefault();
                    return false;
                }
            });
            actualKeyDown = function (evt) {
                var oldret = keyDownHandler(evt);
                if (oldret === false) {
                    advancedPrevention = true;
                }
            };
        }
        container.keydown(actualKeyDown);
        container.keyup(keyUpHandler);
    }
    
    function addRolesToContainer(that) {
        that.container.attr("role", that.options.containerRole.container);
        that.container.attr("aria-multiselectable", "false");
        that.container.attr("aria-readonly", "false");
        that.container.attr("aria-disabled", "false");
        // FLUID-3707: We require to have BOTH application role as well as our named role
        // This however breaks the component completely under NVDA and causes it to perpetually drop back into "browse mode"
        //that.container.wrap("<div role=\"application\"></div>");
    }
    
    function createAvatarId(parentId) {
        // Generating the avatar's id to be containerId_avatar
        // This is safe since there is only a single avatar at a time
        return parentId + "_avatar";
    }
    
    var adaptKeysets = function (options) {
        if (options.keysets && !(options.keysets instanceof Array)) {
            options.keysets = [options.keysets];    
        }
    };
    
    /**
     * @param container - A jQueryable designator for the root node of the reorderer (a selector, a DOM node, or a jQuery instance)
     * @param options - an object containing any of the available options:
     *                  containerRole - indicates the role, or general use, for this instance of the Reorderer
     *                  keysets - an object containing sets of keycodes to use for directional navigation. Must contain:
     *                            modifier - a function that returns a boolean, indicating whether or not the required modifier(s) are activated
     *                            up
     *                            down
     *                            right
     *                            left
     *                  styles - an object containing class names for styling the Reorderer
     *                                  defaultStyle
     *                                  selected
     *                                  dragging
     *                                  hover
     *                                  dropMarker
     *                                  mouseDrag
     *                                  avatar
     *                  avatarCreator - a function that returns a valid DOM node to be used as the dragging avatar
     */
    fluid.reorderer = function (container, options) {
        if (!container) {
            fluid.fail("Reorderer initialised with no container");
        }
        var thatReorderer = fluid.initView("fluid.reorderer", container, options);
        options = thatReorderer.options;
                
        var dropManager = fluid.dropManager();   
                
        thatReorderer.layoutHandler = fluid.initSubcomponent(thatReorderer,
            "layoutHandler", [thatReorderer.container, options, dropManager, thatReorderer.dom]);
        
        thatReorderer.activeItem = undefined;

        adaptKeysets(options);
 
        var kbDropWarning = thatReorderer.locate("dropWarning");
        var mouseDropWarning;
        if (kbDropWarning) {
            mouseDropWarning = kbDropWarning.clone();
        }

        var isMove = function (evt) {
            var keysets = options.keysets;
            for (var i = 0; i < keysets.length; i++) {
                if (keysets[i].modifier(evt)) {
                    return true;
                }
            }
            return false;
        };
        
        var isActiveItemMovable = function () {
            return $.inArray(thatReorderer.activeItem, thatReorderer.dom.fastLocate("movables")) >= 0;
        };
        
        var setDropEffects = function (value) {
            thatReorderer.dom.fastLocate("dropTargets").attr("aria-dropeffect", value);
        };
        
        var styles = options.styles;
        
        var noModifier = function (evt) {
            return (!evt.ctrlKey && !evt.altKey && !evt.shiftKey && !evt.metaKey);
        };
        
        var handleDirectionKeyDown = function (evt) {
            var item = thatReorderer.activeItem;
            if (!item) {
                return true;
            }
            var keysets = options.keysets;
            for (var i = 0; i < keysets.length; i++) {
                var keyset = keysets[i];
                var keydir = fluid.keyForValue(keyset, evt.keyCode);
                if (!keydir) {
                    continue;
                }
                var isMovement = keyset.modifier(evt);
                
                var dirnum = fluid.keycodeDirection[keydir];
                var relativeItem = thatReorderer.layoutHandler.getRelativePosition(item, dirnum, !isMovement);  
                if (!relativeItem) {
                    continue;
                }
                
                if (isMovement) {
                    var prevent = thatReorderer.events.onBeginMove.fire(item);
                    if (prevent === false) {
                        return false;
                    }
                    if (kbDropWarning.length > 0) {
                        if (relativeItem.clazz === "locked") {
                            thatReorderer.events.onShowKeyboardDropWarning.fire(item, kbDropWarning);
                            kbDropWarning.show();                       
                        } else {
                            kbDropWarning.hide();
                        }
                    }
                    if (relativeItem.element) {
                        thatReorderer.requestMovement(relativeItem, item);
                    }
            
                } else if (noModifier(evt)) {
                    item.blur();
                    $(relativeItem.element).focus();
                }
                return false;
            }
            return true;
        };

        // unsupported, NON-API function
        thatReorderer.handleKeyDown = function (evt) {
            if (!thatReorderer.activeItem || thatReorderer.activeItem !== evt.target) {
                return true;
            }
            // If the key pressed is ctrl, and the active item is movable we want to restyle the active item.
            var jActiveItem = $(thatReorderer.activeItem);
            if (!jActiveItem.hasClass(styles.dragging) && isMove(evt)) {
               // Don't treat the active item as dragging unless it is a movable.
                if (isActiveItemMovable()) {
                    jActiveItem.removeClass(styles.selected);
                    jActiveItem.addClass(styles.dragging);
                    jActiveItem.attr("aria-grabbed", "true");
                    setDropEffects("move");
                }
                return false;
            }
            // The only other keys we listen for are the arrows.
            return handleDirectionKeyDown(evt);
        };

        // unsupported, NON-API function
        thatReorderer.handleKeyUp = function (evt) {
            if (!thatReorderer.activeItem || thatReorderer.activeItem !== evt.target) {
                return true;
            }
            var jActiveItem = $(thatReorderer.activeItem);
            
            // Handle a key up event for the modifier
            if (jActiveItem.hasClass(styles.dragging) && !isMove(evt)) {
                if (kbDropWarning) {
                    kbDropWarning.hide();
                }
                jActiveItem.removeClass(styles.dragging);
                jActiveItem.addClass(styles.selected);
                jActiveItem.attr("aria-grabbed", "false");
                setDropEffects("none");
                return false;
            }
            
            return false;
        };

        var dropMarker;

        var createDropMarker = function (tagName) {
            var dropMarker = $(document.createElement(tagName));
            dropMarker.addClass(options.styles.dropMarker);
            dropMarker.hide();
            return dropMarker;
        };
        // unsupported, NON-API function
        thatReorderer.requestMovement = function (requestedPosition, item) {
            item = fluid.unwrap(item);
          // Temporary censoring to get around ModuleLayout inability to update relative to self.
            if (!requestedPosition || fluid.unwrap(requestedPosition.element) === item) {
                return;
            }
            var activeItem = $(thatReorderer.activeItem);
            
            // Fixes FLUID-3288.
            // Need to unbind the blur event as safari will call blur on movements.
            // This caused the user to have to double tap the arrow keys to move.
            activeItem.unbind("blur.fluid.reorderer");
            
            thatReorderer.events.onMove.fire(item, requestedPosition);
            dropManager.geometricMove(item, requestedPosition.element, requestedPosition.position);
            //$(thatReorderer.activeItem).removeClass(options.styles.selected);
           
            // refocus on the active item because moving places focus on the body
            activeItem.focus();
            
            thatReorderer.refresh();
            
            dropManager.updateGeometry(thatReorderer.layoutHandler.getGeometricInfo());

            thatReorderer.events.afterMove.fire(item, requestedPosition, thatReorderer.dom.fastLocate("movables"));
        };

        var hoverStyleHandler = function (item, state) {
            thatReorderer.dom.fastLocate("grabHandle", item)[state ? "addClass" : "removeClass"](styles.hover);
        };
        /**
         * Takes a $ object and adds 'movable' functionality to it
         */
        function initMovable(item) {
            var styles = options.styles;
            item.attr("aria-grabbed", "false");

            item.mouseover(
                function () {
                    thatReorderer.events.onHover.fire(item, true);
                }
            );
        
            item.mouseout(
                function () {
                    thatReorderer.events.onHover.fire(item, false);
                }
            );
            var avatar;
        
            thatReorderer.dom.fastLocate("grabHandle", item).draggable({
                refreshPositions: false,
                scroll: true,
                helper: function () {
                    var dropWarningEl;
                    if (mouseDropWarning) {
                        dropWarningEl = mouseDropWarning[0];
                    }
                    avatar = $(options.avatarCreator(item[0], styles.avatar, dropWarningEl));
                    avatar.prop("id", createAvatarId(thatReorderer.container.id));
                    return avatar;
                },
                start: function (e, ui) {
                    var prevent = thatReorderer.events.onBeginMove.fire(item);
                    if (prevent === false) {
                        return false;
                    }
                    var handle = thatReorderer.dom.fastLocate("grabHandle", item)[0];
                    var handlePos = fluid.dom.computeAbsolutePosition(handle);
                    var handleWidth = handle.offsetWidth;
                    var handleHeight = handle.offsetHeight;
                    item.focus();
                    item.removeClass(options.styles.selected);
                    item.addClass(options.styles.mouseDrag);
                    item.attr("aria-grabbed", "true");
                    setDropEffects("move");
                    dropManager.startDrag(e, handlePos, handleWidth, handleHeight);
                    avatar.show();
                },
                stop: function (e, ui) {
                    item.removeClass(options.styles.mouseDrag);
                    item.addClass(options.styles.selected);
                    $(thatReorderer.activeItem).attr("aria-grabbed", "false");
                    var markerNode = fluid.unwrap(dropMarker);
                    if (markerNode.parentNode) {
                        markerNode.parentNode.removeChild(markerNode);
                    }
                    avatar.hide();
                    ui.helper = null;
                    setDropEffects("none");
                    dropManager.endDrag();
                    
                    thatReorderer.requestMovement(dropManager.lastPosition(), item);
                    // refocus on the active item because moving places focus on the body
                    thatReorderer.activeItem.focus();
                },
                handle: thatReorderer.dom.fastLocate("grabHandle", item)
            });
        }
           
        function changeSelectedToDefault(jItem, styles) {
            jItem.removeClass(styles.selected);
            jItem.removeClass(styles.dragging);
            jItem.addClass(styles.defaultStyle);
            jItem.attr("aria-selected", "false");
        }
           
        var selectItem = function (anItem) {
            thatReorderer.events.onSelect.fire(anItem);
            var styles = options.styles;
            // Set the previous active item back to its default state.
            if (thatReorderer.activeItem && thatReorderer.activeItem !== anItem) {
                changeSelectedToDefault($(thatReorderer.activeItem), styles);
            }
            // Then select the new item.
            thatReorderer.activeItem = anItem;
            var jItem = $(anItem);
            jItem.removeClass(styles.defaultStyle);
            jItem.addClass(styles.selected);
            jItem.attr("aria-selected", "true");
        };
   
        var initSelectables = function () {
            var handleBlur = function (evt) {
                changeSelectedToDefault($(this), options.styles);
                return evt.stopPropagation();
            };
        
            var handleFocus = function (evt) {
                selectItem(this);
                return evt.stopPropagation();
            };
            
            var selectables = thatReorderer.dom.fastLocate("selectables");
            for (var i = 0; i < selectables.length; ++i) {
                var selectable = $(selectables[i]);
                if (!$.data(selectable[0], "fluid.reorderer.selectable-initialised")) { 
                    selectable.addClass(styles.defaultStyle);
            
                    selectable.bind("blur.fluid.reorderer", handleBlur);
                    selectable.focus(handleFocus);
                    selectable.click(function (evt) {
                        var handle = fluid.unwrap(thatReorderer.dom.fastLocate("grabHandle", this));
                        if (fluid.dom.isContainer(handle, evt.target)) {
                            $(this).focus();
                        }
                    });
                    
                    selectable.attr("role", options.containerRole.item);
                    selectable.attr("aria-selected", "false");
                    selectable.attr("aria-disabled", "false");
                    $.data(selectable[0], "fluid.reorderer.selectable-initialised", true);
                }
            }
            if (!thatReorderer.selectableContext) {
                thatReorderer.selectableContext = fluid.selectable(thatReorderer.container, {
                    selectableElements: selectables,
                    selectablesTabindex: thatReorderer.options.selectablesTabindex,
                    direction: null
                });
            }
        };
    
        var dropChangeListener = function (dropTarget) {
            fluid.moveDom(dropMarker, dropTarget.element, dropTarget.position);
            dropMarker.css("display", "");
            if (mouseDropWarning) {
                if (dropTarget.lockedelem) {
                    mouseDropWarning.show();
                } else {
                    mouseDropWarning.hide();
                }
            }
        };
    
        var initItems = function () {
            var movables = thatReorderer.dom.fastLocate("movables");
            var dropTargets = thatReorderer.dom.fastLocate("dropTargets");
            initSelectables();
        
            // Setup movables
            for (var i = 0; i < movables.length; i++) {
                var item = movables[i];
                if (!$.data(item, "fluid.reorderer.movable-initialised")) { 
                    initMovable($(item));
                    $.data(item, "fluid.reorderer.movable-initialised", true);
                }
            }

            // In order to create valid html, the drop marker is the same type as the node being dragged.
            // This creates a confusing UI in cases such as an ordered list. 
            // drop marker functionality should be made pluggable. 
            if (movables.length > 0 && !dropMarker) {
                dropMarker = createDropMarker(movables[0].tagName);
            }
            
            dropManager.updateGeometry(thatReorderer.layoutHandler.getGeometricInfo());
            
            dropManager.dropChangeFirer.addListener(dropChangeListener, "fluid.Reorderer");
            // Set up dropTargets
            dropTargets.attr("aria-dropeffect", "none");  

        };


        // Final initialization of the Reorderer at the end of the construction process 
        if (thatReorderer.container) {
            bindHandlersToContainer(thatReorderer.container, 
                thatReorderer.handleKeyDown,
                thatReorderer.handleKeyUp);
            addRolesToContainer(thatReorderer);
            fluid.tabbable(thatReorderer.container);
            initItems();
        }

        if (options.afterMoveCallbackUrl) {
            thatReorderer.events.afterMove.addListener(function () {
                var layoutHandler = thatReorderer.layoutHandler;
                var model = layoutHandler.getModel ? layoutHandler.getModel() :
                        options.acquireModel(thatReorderer);
                $.post(options.afterMoveCallbackUrl, JSON.stringify(model));
            }, "postModel");
        }
        thatReorderer.events.onHover.addListener(hoverStyleHandler, "style");

        thatReorderer.refresh = function () {
            thatReorderer.dom.refresh("movables");
            thatReorderer.dom.refresh("selectables");
            thatReorderer.dom.refresh("grabHandle", thatReorderer.dom.fastLocate("movables"));
            thatReorderer.dom.refresh("stylisticOffset", thatReorderer.dom.fastLocate("movables"));
            thatReorderer.dom.refresh("dropTargets");
            thatReorderer.events.onRefresh.fire();
            initItems();
            thatReorderer.selectableContext.selectables = thatReorderer.dom.fastLocate("selectables");
            thatReorderer.selectableContext.selectablesUpdated(thatReorderer.activeItem);
        };
        
        fluid.initDependents(thatReorderer);

        thatReorderer.refresh();

        return thatReorderer;
    };
    
    /**
     * Constants for key codes in events.
     */    
    fluid.reorderer.keys = {
        TAB: 9,
        ENTER: 13,
        SHIFT: 16,
        CTRL: 17,
        ALT: 18,
        META: 19,
        SPACE: 32,
        LEFT: 37,
        UP: 38,
        RIGHT: 39,
        DOWN: 40,
        i: 73,
        j: 74,
        k: 75,
        m: 77
    };
    
    /**
     * The default key sets for the Reorderer. Should be moved into the proper component defaults.
     */
    fluid.reorderer.defaultKeysets = [
        {
            modifier : function (evt) {
                return evt.ctrlKey;
            },
            up : fluid.reorderer.keys.UP,
            down : fluid.reorderer.keys.DOWN,
            right : fluid.reorderer.keys.RIGHT,
            left : fluid.reorderer.keys.LEFT
        },
        {
            modifier : function (evt) {
                return evt.ctrlKey;
            },
            up : fluid.reorderer.keys.i,
            down : fluid.reorderer.keys.m,
            right : fluid.reorderer.keys.k,
            left : fluid.reorderer.keys.j
        }
    ];
    
    /**
     * These roles are used to add ARIA roles to orderable items. This list can be extended as needed,
     * but the values of the container and item roles must match ARIA-specified roles.
     */  
    fluid.reorderer.roles = {
        GRID: { container: "grid", item: "gridcell" },
        LIST: { container: "list", item: "listitem" },
        REGIONS: { container: "main", item: "article" }
    };
    
    // Simplified API for reordering lists and grids.
    var simpleInit = function (container, layoutHandler, options) {
        options = options || {};
        options.layoutHandler = layoutHandler;
        return fluid.reorderer(container, options);
    };
    
    fluid.reorderList = function (container, options) {
        return simpleInit(container, "fluid.listLayoutHandler", options);
    };
    
    fluid.reorderGrid = function (container, options) {
        return simpleInit(container, "fluid.gridLayoutHandler", options); 
    };
    
    fluid.reorderer.SHUFFLE_GEOMETRIC_STRATEGY = "shuffleProjectFrom";
    fluid.reorderer.GEOMETRIC_STRATEGY         = "projectFrom";
    fluid.reorderer.LOGICAL_STRATEGY           = "logicalFrom";
    fluid.reorderer.WRAP_LOCKED_STRATEGY       = "lockedWrapFrom";
    fluid.reorderer.NO_STRATEGY = null;
    
    // unsupported, NON-API function
    fluid.reorderer.relativeInfoGetter = function (orientation, coStrategy, contraStrategy, dropManager, dom, disableWrap) {
        return function (item, direction, forSelection) {
            var dirorient = fluid.directionOrientation(direction);
            var strategy = dirorient === orientation ? coStrategy : contraStrategy;
            return strategy !== null ? dropManager[strategy](item, direction, forSelection, disableWrap) : null;
        };
    };
    
    fluid.defaults("fluid.reorderer", {
        styles: {
            defaultStyle: "fl-reorderer-movable-default",
            selected: "fl-reorderer-movable-selected",
            dragging: "fl-reorderer-movable-dragging",
            mouseDrag: "fl-reorderer-movable-dragging",
            hover: "fl-reorderer-movable-hover",
            dropMarker: "fl-reorderer-dropMarker",
            avatar: "fl-reorderer-avatar"
        },
        selectors: {
            dropWarning: ".flc-reorderer-dropWarning",
            movables: ".flc-reorderer-movable",
            grabHandle: "",
            stylisticOffset: ""
        },
        avatarCreator: defaultAvatarCreator,
        keysets: fluid.reorderer.defaultKeysets,
        layoutHandler: {
            type: "fluid.listLayoutHandler"
        },
        
        events: {
            onShowKeyboardDropWarning: null,
            onSelect: null,
            onBeginMove: "preventable",
            onMove: null,
            afterMove: null,
            onHover: null,
            onRefresh: null
        },
        
        mergePolicy: {
            keysets: "replace",
            "selectors.labelSource": "selectors.grabHandle",
            "selectors.selectables": "selectors.movables",
            "selectors.dropTargets": "selectors.movables"
        },
        components: {
            labeller: {
                type: "fluid.reorderer.labeller",
                options: {
                    dom: "{reorderer}.dom",
                    getGeometricInfo: "{reorderer}.layoutHandler.getGeometricInfo",
                    orientation: "{reorderer}.options.orientation",
                    layoutType: "{reorderer}.options.layoutHandler" // TODO, get rid of "global defaults"
                }          
            }
        },
        
        // The user option to enable or disable wrapping of elements within the container
        disableWrap: false        
        
    });


    /*******************
     * Layout Handlers *
     *******************/

    // unsupported, NON-API function
    fluid.reorderer.makeGeometricInfoGetter = function (orientation, sentinelize, dom) {
        return function () {
            var that = {
                sentinelize: sentinelize,
                extents: [{
                    orientation: orientation,
                    elements: dom.fastLocate("dropTargets")
                }],
                elementMapper: function (element) {
                    return $.inArray(element, dom.fastLocate("movables")) === -1 ? "locked" : null;
                },
                elementIndexer: function (element) {
                    var selectables = dom.fastLocate("selectables");
                    return {
                        elementClass: that.elementMapper(element),
                        index: $.inArray(element, selectables),
                        length: selectables.length
                    };
                }
            };
            return that;
        };
    };
    
    fluid.defaults(true, "fluid.listLayoutHandler", 
        {
            orientation:         fluid.orientation.VERTICAL,
            containerRole:       fluid.reorderer.roles.LIST,
            selectablesTabindex: -1,
            sentinelize:         true
        });
    
    // Public layout handlers.
    fluid.listLayoutHandler = function (container, options, dropManager, dom) {
        var that = {};

        that.getRelativePosition = 
            fluid.reorderer.relativeInfoGetter(options.orientation, 
                    fluid.reorderer.LOGICAL_STRATEGY, null, dropManager, dom, options.disableWrap);
        
        that.getGeometricInfo = fluid.reorderer.makeGeometricInfoGetter(options.orientation, options.sentinelize, dom);
        
        return that;
    }; // End ListLayoutHandler

    fluid.defaults(true, "fluid.gridLayoutHandler", 
        {
            orientation:         fluid.orientation.HORIZONTAL,
            containerRole:       fluid.reorderer.roles.GRID,
            selectablesTabindex: -1,
            sentinelize:         false
        });
    /*
     * Items in the Lightbox are stored in a list, but they are visually presented as a grid that
     * changes dimensions when the window changes size. As a result, when the user presses the up or
     * down arrow key, what lies above or below depends on the current window size.
     * 
     * The GridLayoutHandler is responsible for handling changes to this virtual 'grid' of items
     * in the window, and of informing the Lightbox of which items surround a given item.
     */
    fluid.gridLayoutHandler = function (container, options, dropManager, dom) {
        var that = {};

        that.getRelativePosition = 
            fluid.reorderer.relativeInfoGetter(options.orientation, 
                 options.disableWrap ? fluid.reorderer.SHUFFLE_GEOMETRIC_STRATEGY : fluid.reorderer.LOGICAL_STRATEGY, fluid.reorderer.SHUFFLE_GEOMETRIC_STRATEGY, 
                 dropManager, dom, options.disableWrap);
        
        that.getGeometricInfo = fluid.reorderer.makeGeometricInfoGetter(options.orientation, options.sentinelize, dom);
        
        return that;
    }; // End of GridLayoutHandler

    fluid.defaults("fluid.reorderer.labeller", {
        strings: {
            overallTemplate: "%recentStatus %item %position %movable",
            position:        "%index of %length",
            position_moduleLayoutHandler: "%index of %length in %moduleCell %moduleIndex of %moduleLength",
            moduleCell_0:    "row", // NB, these keys must agree with fluid.a11y.orientation constants
            moduleCell_1:    "column",
            movable:         "movable",
            fixed:           "fixed",
            recentStatus:    "moved from position %position"
        },
        components: {
            resolver: {
                type: "fluid.messageResolver",
                options: {
                    messageBase: "{labeller}.options.strings"
                }
            }
        },
        invokers: {
            renderLabel: {
                funcName: "fluid.reorderer.labeller.renderLabel",
                args: ["{labeller}", "@0", "@1"]
            }  
        }
    });

    // unsupported, NON-API function
    // Convert from 0-based to 1-based indices for announcement
    fluid.reorderer.indexRebaser = function (indices) {
        indices.index++;
        if (indices.moduleIndex !== undefined) {
            indices.moduleIndex++;
        }
        return indices;
    };

    /*************
     * Labelling *
     *************/
     
    fluid.reorderer.labeller = function (options) {
        var that = fluid.initLittleComponent("fluid.reorderer.labeller", options);
        fluid.initDependents(that);
        that.dom = that.options.dom;
        
        that.moduleCell = that.resolver.resolve("moduleCell_" + that.options.orientation);
        var layoutType = fluid.computeNickName(that.options.layoutType);
        that.positionTemplate = that.resolver.lookup(["position_" + layoutType, "position"]);
        
        var movedMap = {};
        
        that.returnedOptions = {
            listeners: {
                onRefresh: function () {
                    var selectables = that.dom.locate("selectables");
                    fluid.each(selectables, function (selectable) {
                        var labelOptions = {};
                        var id = fluid.allocateSimpleId(selectable);
                        var moved = movedMap[id];
                        var label = that.renderLabel(selectable);
                        var plainLabel = label;
                        if (moved) {
                            moved.newRender = plainLabel;
                            label = that.renderLabel(selectable, moved.oldRender.position);
                            $(selectable).one("focusout", function () {
                                if (movedMap[id]) {
                                    var oldLabel = movedMap[id].newRender.label;
                                    delete movedMap[id];
                                    fluid.updateAriaLabel(selectable, oldLabel);
                                }
                            });
                            labelOptions.dynamicLabel = true;
                        }
                        fluid.updateAriaLabel(selectable, label.label, labelOptions);
                    });
                },
                onMove: function (item, newPosition) {
                    fluid.clear(movedMap); // if we somehow were fooled into missing a defocus, at least clear the map on a 2nd move
                    var movingId = fluid.allocateSimpleId(item);
                    movedMap[movingId] = {
                        oldRender: that.renderLabel(item)
                    };
                }
            }
        };
        return that;
    };
    
    fluid.reorderer.labeller.renderLabel = function (that, selectable, recentPosition) {
        var geom = that.options.getGeometricInfo();
        var indices = fluid.reorderer.indexRebaser(geom.elementIndexer(selectable));
        indices.moduleCell = that.moduleCell;
            
        var elementClass = geom.elementMapper(selectable);
        var labelSource = that.dom.locate("labelSource", selectable);
        var recentStatus;
        if (recentPosition) {
            recentStatus = that.resolver.resolve("recentStatus", {position: recentPosition});
        }
        var topModel = {
            item: typeof (labelSource) === "string" ? labelSource : fluid.dom.getElementText(fluid.unwrap(labelSource)),
            position: that.positionTemplate.resolveFunc(that.positionTemplate.template, indices),
            movable: that.resolver.resolve(elementClass === "locked" ? "fixed" : "movable"),
            recentStatus: recentStatus || ""
        };
        
        var template = that.resolver.lookup(["overallTemplate"]);
        var label = template.resolveFunc(template.template, topModel);
        return {
            position: topModel.position,
            label: label
        };
    };

})(jQuery, fluid_1_4);




