# kindly-advice

[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/kindly-advice.svg)](https://clojars.org/org.scicloj/kindly-advice)

Kindly-advice is a small library to advise Clojure data visualization and notebook tools how to display forms and values, following the [Kindly](https://github.com/scicloj/kindly) convention.

## Status

Initial draft WIP

## Notes for tool authors

Handling vars, atom, refs, futures, delays needs some care.
To help with this, there is `derefing-advise` which will take care of that.
Using `derefing-advise` overwrites the original value, and possibly the kind.
Bear in mind that visualizations may be nested.
Also consider that top level vars may or may not need expanding depending on the context.
For example:

```
(def icon (kind/hiccup [:svg [:circle {:r 50}]]))
```

Probably the user would like the SVG displayed immediately.

```
(def x (delay 10))
```

Probably the user would not want anything displayed, or maybe just `#'x`, but unlikely `10`.

For this reason there is also `top-level-advise` which provides reasonable defaults.


## License

Copyright © 2021 Scicloj

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
