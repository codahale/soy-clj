# soy-clj

An idiomatic Clojure wrapper for Google's Closure templating system.

## Usage

```clojure
(require '[soy-clj.core :as soy])

;; parse a set of template files from ./resources
(def web-templates (soy/parse ["marketing.soy" "main.soy"]))

;; render a template
(let [[content kind] (soy/render web-templates
                                 "com.example.marketing.newSplash"
                                 {:title "Hello World"
                                  :good-stuff ["Puppies" "Kitties"]})]
  (prn content) ; the rendered template as a string
  (prn kind)) ; a symbol indicating the template's type (e.g. :html, :js, :css)

;; disable caching
(soy/set-cache (clojure.core.cache/ttl-cache-factory {} :ttl 0))
```

Parsed templates are cached in memory. By default the 32 most-used template sets
are kept cached, but you can supply `soy-clj` with any `core.cache`
implementation you want.

## Security

`soy-clj` is hard-coded to only render strictly-escaped templates. The main
benefit of Closure templates is the fact that they do contextual escaping, which
effectively eliminates XSS attacks. If you _absolutely_ must poke a hole in
that, use the `ordain-as-safe` function to ordain bits of content as safe.

## License

Copyright © 2015 Coda Hale

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
