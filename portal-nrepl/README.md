A small experiment in sending code to [Portal](github.com/djblue/portal) through an nREPL middleware (implementation roughly extracted out of [Notespace](github.com/scicloj/notespace/)).

![screenshot](./screehshot1.png)

## Usage

Run the Clojure REPL with the `nrepl` alias:
```bash
clj -M:nrepl
```

In the REPL, start the pipeline system listening to nREPL events and passing them to Portal (this will also open the Portal UI).
```clj
(require '[portal-nrepl-1.pipeline :as pipeline])
(pipeline/start)
```

Then, evaluate expressions and see them rendered in Portal:
```clj
(+ 1 2)

[:portal.viewer/hiccup [:h1 "hi!"]]

[:portal.viewer/code "{:x 9 :y [(+ 1 2)]}"]

[:portal.viewer/tree {:x 9 :y [(+ 1 2)]}]
```

See also `src/example.clj`.


