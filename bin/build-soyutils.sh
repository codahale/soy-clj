#!/bin/bash
npm --no-progress --no-color install

version=`npm --no-color info google-closure-templates version`

mkdir -p resources/META-INF/resources/webjars/soy-clj/$version

./node_modules/google-closure-library/closure/bin/build/closurebuilder.py \
    --root=node_modules/google-closure-library \
    --root=node_modules/google-closure-templates/javascript \
    --namespace="goog.soy" \
    --namespace="soydata" \
    --namespace="soy" \
    --output_mode=compiled \
    --compiler_jar=node_modules/google-closure-compiler/compiler.jar \
    > resources/META-INF/resources/webjars/soy-clj/$version/soyutils.js
