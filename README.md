# Corbihttp: everything you need to build HTTP applications in Clojure

Corbihttp is a set of components, functions and interceptors which help you creating full-featured HTTP applications in [Clojure](https://clojure.org/). It leverages battled tested liraries:

- [Aero](https://github.com/juxt/aero) for configuration
- [Reitit](https://github.com/mesotin/reitit) for routing
- [Cheshire](https://github.com/dakrone/cheshire) for JSON handling
- [Component](https://github.com/stuartsierra/component) for state management
- [Cloak](https://github.com/exoscale/cloak) for secrets
- [Clojure Spec](https://clojure.org/guides/spec) for input validation
- [Coax](https://github.com/exoscale/coax) for payloads coercion from specs
- [Ex](https://github.com/exoscale/ex) for error handling
- [Interceptor](https://github.com/exoscale/interceptor) to handle HTTP requests
- [Micrometer](https://micrometer.io/) for metrics and Prometheus integration
- [Ring](https://github.com/ring-clojure/ring) and Jetty for the HTTP server

Corbihttp provides an opiniated way of creating web applications in Clojure. It's flexible (you choose which part you want to include or not) and simple to use.

## Example
