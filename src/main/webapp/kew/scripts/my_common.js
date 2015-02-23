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
var isChanged = 0;
function ieKeyPress() {
   return;
}
function reloadChannelFromKey(key) {
  if (window.opener != null) {
    eval("window.opener.iframe_" + key + ".location.reload();");
  }
}
function post_to_action(formname, action) {
  document.forms[formname].action = action; 
  document.forms[formname].submit();
  return;
}
function drawForm(file, name, width, height){
  if (!win1 || win1.closed) {
    var win1;
    win1 = open(file,name,"status=yes,scrollbars=yes,resizable=yes,width=" + width + ",height=" + height);
    if (!(document.all && !document.getElementById))
      win1.focus();
  } else {
    if (!(document.all && !document.getElementById))
      win1.focus();
  }
  return;
}
function openWithToolbar(url, width, height) {
  window.open(url, "_blank", "toolbar=yes,status=yes,location=yes,menubar=yes,scrollbars=yes,resizable=yes,directories=yes,width=" + width + ",height=" + height);
  return;
}
function my_o(url, name) {
  openWithToolbar(url, 800, 600);
  return;
}
function replace(argvalue, x, y) {
  if ((x == y) || (parseInt(y.indexOf(x)) > -1)) {
    return false;
  }

  while (argvalue.indexOf(x) != -1) {
    var leading = argvalue.substring(0, argvalue.indexOf(x));
    var trailing = argvalue.substring(argvalue.indexOf(x) + x.length, argvalue.length);
    argvalue = leading + y + trailing;
  }
  return argvalue;
}
