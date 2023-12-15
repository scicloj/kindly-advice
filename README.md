# kindly-advice

[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/kindly-advice.svg)](https://clojars.org/org.scicloj/kindly-advice)

Kindly-advice is a small library to advise Clojure data visualization and notebook tools how to display forms and values, following the [Kindly](https://github.com/scicloj/kindly) convention.

## Status
Kindly-advice will stabilize soon and is currently getting feedback from tool-makers.

## Usage

```clj
(require '[scicloj.kindly.v4.kind :as kind]
         '[scicloj.kindly-advice.v1.api :as kindly-advice])
```

Asking for advice for a given value (optionally annotated by Kindly):
```clj
(kindly-advice/advise {:value (kind/hiccup
                               [:div [:h1 "hello"]])})

;; => 
{:value [:div [:h1 "hello"]],
 :meta-kind :kind/hiccup,
 :kind :kind/hiccup,
 :advice
 [[:kind/hiccup {:reason :metadata}]
  [:kind/vector {:reason :predicate}]
  [:kind/seq {:reason :predicate}]]}

```

The `:kind` field is the important one, expressing the bottom line of the inference: Kindly-advice recommends the tool handles this value as Hiccup.

The tool's job will usually be to display the `:value` field based on the `:kind` field.

In the following exmple, we are asking for advice for given form (annotated by Kindly in this example). 
Kindly-advice evaluates the form and adds the resulting value to complete the context. This completion will only take place if the value is missing. It is recommended that tools will take care of evaluation themselves and pass the complete context to Kindly-advice. Doing so allows the tool to handle Exceptions better, among other things.
Kindly-advice checks both the form and value for metadata. The metadata might not be present on the value.

```clj
(kindly-advice/advise {:form
                       ^:kind/hiccup
                       [:div [:h1 "hello"]]})

;; => 
{:form [:div [:h1 "hello"]],
 :value [:div [:h1 "hello"]],
 :meta-kind :kind/hiccup,
 :kind :kind/hiccup,
 :advice
 [[:kind/hiccup {:reason :metadata}]
  [:kind/vector {:reason :predicate}]
  [:kind/seq {:reason :predicate}]]}
```

Sometimes, there is no inferred kind, as no metadata or relevant predicates say anything useful:

```clj

(kindly-advice/advise {:form '(+ 1 2)})
;; =>
{:form (+ 1 2), :value 3, :meta-kind nil, :advice []}
```

In some situations, the kind inferred by predicates. Kindly-advice has a list of default predicates, which can be extended by the user. In the following eexample, it recognizes a dataset created by Tablecloth.

```clj
(require '[tablecloth.api :as tc])
(kindly-advice/advise {:value (tc/dataset {:x (range 4)})})

;; =>
{:value _unnamed [4 1]:

 | :x |
 |---:|
 |  0 |
 |  1 |
 |  2 |
 |  3 |
 ,
 :meta-kind nil,
 :kind :kind/dataset,
 :advice
 [[:kind/dataset {:reason :predicate}]
  [:kind/map {:reason :predicate}]]}

```

## Examples

Kindly-advice is used by the following projects:
* [kind-portal](https://github.com/scicloj/kind-portal)
* [kind-clerk](https://github.com/scicloj/kind-clerk)
* [read-kinds](https://github.com/scicloj/read-kinds)
* [Clay](https://scicloj.github.io/clay/)


## License

Copyright © 2023 Scicloj

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
