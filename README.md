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

An example of what a Closure template looks like
[can be found here](https://github.com/codahale/soy-clj/blob/master/test/example.soy).

### Security

In addition, variables in Closure Templates are contextually escaped to almost
entirely prevent XSS attacks. For example, consider the following template
snippet:

```html
<a href="#" onclick="setName('{$name}')">{$name}</a>
```

The first use of `name` is as a Javascript string inside a HTML attribute; the
second use is as the content of an HTML element. These two contexts have very
different syntactic requirements, which is what makes preventing XSS attacks so
hard.

An attacker, seeing this contextual nesting, could rename her profile to
something like `', alert('XSS'), '`. A naive templating system will auto-escape
everything as if it's the content of an HTML element:

```html
<a href="#" onclick="setName('&#39;, alert(&#39;XSS&#39;), &#39;')">&#39;, alert(&#39;XSS&#39;), &#39;</a>
```

Because the browser parses HTML entities *before* parsing the Javascript,
though, this is structurally equivalent to the following:

```html
<a href="#" onclick="setName('', alert('XSS'), '')">', alert('XSS'), '</a>
```

The attacker, then, can run arbitrary Javascript when someone clicks that link.

Closure templates, on the other hand, will automatically escape based on the
context of the variable usage:

```html
<a href="#" onclick="setName('\x27, alert(\x27XSS\x27), \x27')">&#39;, alert(&#39;XSS&#39;), &#39;</a>
```

This allows developers to nest CSS in Javascript in HTML in Javascript (etc.
etc.) without needing to keep track of the context-specific syntax rules and
their interaction effects. For more information on the security model behind
Closure templates,
[read this](http://js-quasis-libraries-and-repl.googlecode.com/svn/trunk/safetemplate.html).

`soy-clj` is hard-coded to only render strictly-escaped templates. If you
_absolutely_ must poke a hole in that, use the `ordain-as-safe` function to
ordain bits of content as safe in a given context.

If you have user input which must support some HTML markup for formatting
purposes, use the `clean-html` function to pass all HTML tags through a strict
whitelist.

### Performance

Google Closure templates are compiled to actual bytecode, making them incredibly
fast. Rendering a list of 1000 elements, for example, takes ~450µs on a laptop.
A simple template with a single variable takes ~4µs. To reproduce this, run any
of the [criterium](https://github.com/hugoduncan/criterium) benchmarks in
`soy-clj/bench-test` or run `lein bench` on the command line.

`soy-clj` uses Guava to cache compiled templates. By default it will cache every
template for the lifetime of the process, which makes for fast rendering in
production environments. For development environments, the cache can be disabled
so that templates are recompiled on every request:

```clojure
(soy-clj.core/set-cache-options! {:disabled? true})
```

More information on Guava cache parameters can be found [here](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/cache/CacheBuilder.html).

Parsing and compiling templates takes 10-100ms, which makes using live templates
in development relatively painless.

To avoid a slow load on the first request, applications can pre-parse template
files on startup:

```clojure
;;; preload templates
(soy-clj.core/parse "main.soy")
(soy-clj.core/parse "marketing.soy")
```

This has the additional benefit of preventing an application with malformed
templates (e.g. a typo) from starting up successfully.

### Gotchas

There are a few quirks of working with Google Closure templates.

#### Curly Brackets

Curly brackets (i.e. `{` and `}`) are used _exclusively_ for template directives
(e.g. `{$name}` or `{if}`). If you need to use a curly bracket in your template,
use the `{lb}` or `{rb}` directives.

You can also enclose parts of your template with the `{literal}` directive:

```html
<script type="text/javascript">
 var name = "{$name}"; // automatically escaped
 {literal}
 function blah() {
   console.log(name);
 }
 {/literal}
</script>
```

#### Whitespace

Google Closure templates normalizes template text with something it calls
[line joining](https://developers.google.com/closure/templates/docs/concepts#linejoining).
You may notice this when it removes whitespace in your template that is
semantically meaningful (e.g. the space in `<i>foo</i> <i>bar</i>`). You can add
explicit whitespace using the `{sp}` directive:

```html
<i>foo</i>{sp}<i>bar</i>
```

## Usage

```clojure
(require '[soy-clj.core :as soy])

;;; compile a set of template files to Javascript
(def js (soy/compile-to-js ["marketing.soy" "main.soy"]))

;;; parse a set of template files from ./resources
(def web-templates (soy/parse ["marketing.soy" "main.soy"]))

;;; render a template
(let [[content kind] (soy/render web-templates
                                 "com.example.marketing.newSplash"
                                 {:title "Hello World"
                                  :good-stuff ["Puppies" "Kitties"]})]
  (prn content) ; the rendered template as a string
  (prn kind)    ; a symbol indicating the template's type (e.g. :html, :js, etc)

  ;; the MIME content type of the kind of template
  (prn (soy/content-type kind)))
```

## License

Copyright © 2015-2016 Coda Hale

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
