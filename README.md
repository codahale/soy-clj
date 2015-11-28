# soy-clj

![Build Status](https://travis-ci.org/codahale/soy-clj.svg)

An idiomatic Clojure wrapper for [Google's Closure templating system](https://developers.google.com/closure/templates/).

## What's A Google Closure Template?

> Closure Templates are a client- and server-side templating system that helps
> you dynamically build reusable HTML and UI elements. They have a simple syntax
> that is natural for programmers, and you can customize them to fit your
> application's needs. In contrast to traditional templating systems,in which
> you must create one monolithic template per page, you can think of Closure
> Templates as small components that you compose to form your user
> interface. You can also use the built-in message support to easily localize
> your applications.

### Security

In addition, variables in Closure Templates are contextually escaped to almost
entirely prevent XSS attacks. For example, consider the following template
snippet:

```html
<a href="/people/${name}">{$name}</a>
```

The second use of `name` is as HTML text, but the first use is inside an
element's attribute. If the value of `name` is `Ben Franklin`, your template
will render as follows:

```html
<a href="/people/Ben%20Franklin">Ben Franklin</a>
```

Closure templates will automatically escape based on the context of the variable
usage, which allows developers to nest CSS in Javascript in HTML in Javascript
(etc. etc.) without needing to keep track of the context-specific syntax rules
and their interaction effects.

`soy-clj` is hard-coded to only render strictly-escaped templates. If you
_absolutely_ must poke a hole in that, use the `ordain-as-safe` function to
ordain bits of content as safe in a given context.

### Performance

Google Closure templates are compiled to actual bytecode, making them incredibly
fast. Rendering a list of 1000 elements, for example, takes ~450µs on a laptop.
A simple template with a single variable takes ~5µs.

`soy-clj` uses `core.cache` to cache compiled templates. By default it retains
the 32 most-used templates in memory, which makes for fast rendering in
production environments. For development environments, a TTL cache factory may
be provided with a very low time-to-live so that templates are recompiled on
every request:

```clojure
(soy-clj.core/set-cache (clojure.core.cache/ttl-cache-factory {} :ttl 0))
```

Parsing and compiling the templates takes 10-100ms, which makes using live
templates in development painless.

To avoid a slow load on the first request, applications can pre-parse template
files on startup:

```clojure
;;; preload templates
(soy-clj.core/parse "main.soy")
(soy-clj.core/parse "marketing.soy")
```

This has the additional benefit of preventing an application with malformed
templates (e.g. a typo) from starting up successfully.

## Usage

```clojure
(require '[soy-clj.core :as soy])

;; parse a set of template files from ./resources
(def web-templates (soy-clj.core/parse ["marketing.soy" "main.soy"]))

;; render a template
(let [[content kind] (soy-clj.core/render web-templates
                                          "com.example.marketing.newSplash"
                                          {:title "Hello World"
                                           :good-stuff ["Puppies" "Kitties"]})]
  (prn content) ; the rendered template as a string
  (prn kind)) ; a symbol indicating the template's type (e.g. :html, :js, :css)
```

## License

Copyright © 2015 Coda Hale

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
