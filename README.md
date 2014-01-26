# incise-core
An extensible static site generator written in Clojure.

This is the core component of incise.
If you want the batteries included version see [incise][].

## Running specs

Unfortunately the speclj leiningen plugin does not work well with clj-v8 because
it requires custom JVM options (this seems to be a bug with speclj). However,
you can still run the tests using `lein run`:

```bash
lein run -m specj.main
# The vigilant runner works too
lein run -m specj.main -a
```

## What's next?

I have been adding [issues][] with ideas.
Open an issues with your own ideas or contribute.
I would love some feedback and/or collaboration!

## Donate

If you find incise valuable and are feeling particularly generous you may send
some BTC to the address below.

    16QAD8aVDkQYqT8WehSQtfQp1xRjbwxK3Q

## License

Copyright © 2013 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.

[incise]: http://www.ryanmcg.com/incise/
[issues]: https://github.com/RyanMcG/incise-core/issues?state=open
