
This experiment records an interesting behaviour that emerges in a certain combination of [Clerk's](https://github.com/nextjournal/clerk) cache, [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset)'s [Columns](https://github.com/techascent/tech.ml.dataset/blob/master/src/tech/v3/dataset/impl/column.clj), and [Tablecloth](https://github.com/scicloj/tablecloth).

The behaviour was discovered by Ethan Miller on his work on Tablecloth's upcoming column API together with [Clay](https://github.com/scicloj/clay). Eventually we peeled out the phenomenon to a more basic setup, where Clay is not involved.

## Brief description

A brief description of what we experienced: When loaded from Clerk's cache on disk, Columns are displayed differently if the Tablecloth API is `require`d.

## Detailed description

1. Run the REPl once, and show the notebook:

```clj
user> (require 'nextjournal.clerk)
nil
user> (nextjournal.clerk/serve! {})
Clerk webserver started on 7777...
{}
user> (nextjournal.clerk/show! "src/notebook.clj")
Clerk evaluated 'src/notebook.clj' in 6012.471483ms.
nil
notebook>
```

2. Restart the REPl once, and show the notebook again (by the same command).

3. Clear Clerk's cache, and show the notebook again.

```clj
notebook> (nextjournal.clerk/clear-cache!)
:cache-dir/deleted ".clerk/cache"
nil
notebook> (nextjournal.clerk/show! "src/notebook.clj")
Clerk evaluated 'src/notebook.clj' in 82.630991ms.
nil
notebook> 
```

In 1, 3, the Column created in `srcnotebook.clj` is displayed as a list:

```clj
(0 1 2 3)
```

In 2, it is displayed as a map, supposedly a certain map representation of the tech.ml.dataset Column:

```clj
{:tech.v3.dataset/data #array-buffer<int64>[4] [0, 1, 2, 3] :tech.v3.dataset/force-datatype? true :tech.v3.dataset/missing {} :tech.v3.dataset/name nil}
```

Interestingly, if we remove `tablecloth.api` from the `require`d namespaces at `src/notebook.clj`, we do not experience this difference.

