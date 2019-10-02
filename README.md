# Daily Metro System

Generate a new metro system everyday, and tweet it.

![](https://github.com/davidomarf/daily-metro-system/workflows/Clojure%20CI/badge.svg)

This is still a work in progress and there's no v1.0.0 released yet. In the
meantime, this README will work as roadmap for a v1.0.0 and report of current
features.

## Table of Contents

- [Features](#features)
- [Tools](#tools)
- [Installation](#installation)
- [License](#license)

## Features

### Current

- Generate networks using hard-coded values.

### v1.0.0

- Connect to Twitter API and schedule daily tweets.
- Generate a different non-degenerate network.
  - Generate lines incrementally.
- Use Markov Chains to generate station names.

### v2.0.0

- Convert a network to a schmematic map locking angles and using a grid.

## Dependencies

This project is being built using:

- [Twitter API][twitter-api]. 
- [Quil][quil]
- [Environ][environ]

## Installation

- You'll need to have [`leiningen`][lein] installed.

- Fork the repository.

- Clone and open the directory
  ```sh
  $ git clone git@github.com:{user}/daily-metro-system
            # https://github.com/{user}/daily-metro-system
  $ cd daily-metro-system
  ```

- Install dependencies
    ```sh
    $ lein deps
    ```
- Available scripts

  - `lein run`
    -  Runs the app.
  - `lein test`
    - Launches the test suite inside `test`.

Alternatively, you can start a REPL an interact using the rich comments.

## License

[MIT](https://tldrlegal.com/license/mit-license)

[lein]:https://leiningen.org/
[twitter-api]:https://github.com/adamwynne/twitter-api
[quil]:http://quil.info/
[environ]:https://github.com/weavejester/environ