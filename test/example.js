if(typeof goog == 'undefined') {var goog = {};}
// This file was automatically generated from example.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace examples.simple.
 * @public
 */

if (typeof examples == 'undefined') { var examples = {}; }
if (typeof examples.simple == 'undefined') { examples.simple = {}; }


examples.simple.example = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<a href="#" onclick="setName(\'' + soy.$$escapeHtmlAttribute(soy.$$escapeJsString(opt_data.name)) + '\')">' + soy.$$escapeHtml(opt_data.name) + '</a>');
};
if (goog.DEBUG) {
  examples.simple.example.soyTemplateName = 'examples.simple.example';
}


examples.simple.exampleText = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.markUnsanitizedText('Hello, ' + ('' + opt_data.name) + '!');
};
if (goog.DEBUG) {
  examples.simple.exampleText.soyTemplateName = 'examples.simple.exampleText';
}


examples.simple.helloWorld = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('Hello world!');
};
if (goog.DEBUG) {
  examples.simple.helloWorld.soyTemplateName = 'examples.simple.helloWorld';
}


examples.simple.helloName = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<p><a href="/welcome?name=' + soy.$$escapeUri(opt_data.name) + '">Welcome</a>' + ((! opt_data.greetingWord) ? 'Hello ' + soy.$$escapeHtml(opt_data.name) + '!' : soy.$$escapeHtml(opt_data.greetingWord) + ' ' + soy.$$escapeHtml(opt_data.name) + '!') + '</p>');
};
if (goog.DEBUG) {
  examples.simple.helloName.soyTemplateName = 'examples.simple.helloName';
}


examples.simple.helloNames = function(opt_data, opt_ignored, opt_ijData) {
  var output = examples.simple.helloName(opt_data, null, opt_ijData) + '<br>';
  var additionalNameList38 = opt_data.additionalNames;
  var additionalNameListLen38 = additionalNameList38.length;
  if (additionalNameListLen38 > 0) {
    for (var additionalNameIndex38 = 0; additionalNameIndex38 < additionalNameListLen38; additionalNameIndex38++) {
      var additionalNameData38 = additionalNameList38[additionalNameIndex38];
      output += examples.simple.helloName({name: additionalNameData38}, null, opt_ijData) + ((! (additionalNameIndex38 == additionalNameListLen38 - 1)) ? '<br>' : '');
    }
  } else {
    output += 'No additional people to greet.';
  }
  return soydata.VERY_UNSAFE.ordainSanitizedHtml(output);
};
if (goog.DEBUG) {
  examples.simple.helloNames.soyTemplateName = 'examples.simple.helloNames';
}


examples.simple.autoNonce = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<script type="text/javascript">alert(1);<\/script>');
};
if (goog.DEBUG) {
  examples.simple.autoNonce.soyTemplateName = 'examples.simple.autoNonce';
}


examples.simple.basic = function(opt_data, opt_ignored, opt_ijData) {
  return soydata.VERY_UNSAFE.ordainSanitizedHtml('<span class="foo">' + soy.$$escapeHtml(opt_data.bar) + '</span>');
};
if (goog.DEBUG) {
  examples.simple.basic.soyTemplateName = 'examples.simple.basic';
}


examples.simple.list = function(opt_data, opt_ignored, opt_ijData) {
  var output = '<ul>';
  var itemList52 = opt_data.items;
  var itemListLen52 = itemList52.length;
  for (var itemIndex52 = 0; itemIndex52 < itemListLen52; itemIndex52++) {
    var itemData52 = itemList52[itemIndex52];
    output += '<li>' + soy.$$escapeHtml(itemData52) + '</li>';
  }
  output += '</ul>';
  return soydata.VERY_UNSAFE.ordainSanitizedHtml(output);
};
if (goog.DEBUG) {
  examples.simple.list.soyTemplateName = 'examples.simple.list';
}
