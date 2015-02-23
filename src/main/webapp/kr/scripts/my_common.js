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
function setIframeAnchor(iframeName) {
  var iframeWin = window.frames[iframeName];
  try {
    // For security reasons the browsers will not allow cross server scripts and
    // throw an exception instead.
    // Note that bad browsers (e.g. google chrome) will not catch the exception
    if (iframeWin && iframeWin.location.href.indexOf("#") > -1) {
      iframeWin.location.replace(iframeWin.location);
    }
  }
  catch (e) {
      // ignoring error
  }
}

function jumpToAnchorName(anchor){
	var anchors = document.getElementsByName(anchor);
	if (anchors != null)
		location.href = '#'+anchors[0].name;
}
