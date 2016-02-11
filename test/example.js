if(typeof goog == 'undefined') {var goog = {};}
// This file was automatically generated from example.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace examples.simple.
 * @public
 */

if (typeof examples == 'undefined') { var examples = {}; }
if (typeof examples.simple == 'undefined') { examples.simple = {}; }


examples.simple.example = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<a href="#" onclick="setName(\'' + soy.$$escapeHtmlAttribute(soy.$$escapeJsString(opt_data.name)) + '\')">' + soy.$$escapeHtml(opt_data.name) + '</a>');
};
if (goog.DEBUG) {
  examples.simple.example.soyTemplateName = 'examples.simple.example';
}


examples.simple.helloWorld = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('Hello world!');
};
if (goog.DEBUG) {
  examples.simple.helloWorld.soyTemplateName = 'examples.simple.helloWorld';
}


examples.simple.helloName = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<p><a href="/welcome?name=' + soy.$$escapeUri(opt_data.name) + '">Welcome</a>' + ((! opt_data.greetingWord) ? 'Hello ' + soy.$$escapeHtml(opt_data.name) + '!' : soy.$$escapeHtml(opt_data.greetingWord) + ' ' + soy.$$escapeHtml(opt_data.name) + '!') + '</p>');
};
if (goog.DEBUG) {
  examples.simple.helloName.soyTemplateName = 'examples.simple.helloName';
}


examples.simple.helloNames = function(opt_data, opt_ignored) {
  var output = examples.simple.helloName(opt_data) + '<br>';
  var additionalNameList34 = opt_data.additionalNames;
  var additionalNameListLen34 = additionalNameList34.length;
  if (additionalNameListLen34 > 0) {
    for (var additionalNameIndex34 = 0; additionalNameIndex34 < additionalNameListLen34; additionalNameIndex34++) {
      var additionalNameData34 = additionalNameList34[additionalNameIndex34];
      output += examples.simple.helloName({name: additionalNameData34}) + ((! (additionalNameIndex34 == additionalNameListLen34 - 1)) ? '<br>' : '');
    }
  } else {
    output += 'No additional people to greet.';
  }
  return soydata.VERY_UNSAFE.ordainSanitizedHtml(output);
};
if (goog.DEBUG) {
  examples.simple.helloNames.soyTemplateName = 'examples.simple.helloNames';
}


examples.simple.autoNonce = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<script type="text/javascript">alert(1);<\/script>');
};
if (goog.DEBUG) {
  examples.simple.autoNonce.soyTemplateName = 'examples.simple.autoNonce';
}


examples.simple.basic = function(opt_data, opt_ignored) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<span class="foo">' + soy.$$escapeHtml(opt_data.bar) + '</span>');
};
if (goog.DEBUG) {
  examples.simple.basic.soyTemplateName = 'examples.simple.basic';
}


examples.simple.list = function(opt_data, opt_ignored) {
  var output = '<ul>';
  var itemList48 = opt_data.items;
  var itemListLen48 = itemList48.length;
  for (var itemIndex48 = 0; itemIndex48 < itemListLen48; itemIndex48++) {
    var itemData48 = itemList48[itemIndex48];
    output += '<li>' + soy.$$escapeHtml(itemData48) + '</li>';
  }
  output += '</ul>';
  return soydata.VERY_UNSAFE.ordainSanitizedHtml(output);
};
if (goog.DEBUG) {
  examples.simple.list.soyTemplateName = 'examples.simple.list';
}
