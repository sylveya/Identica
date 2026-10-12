---
version: 3.0.0
---

# Update 3.0.0 - [Short subtitle]

[1-2 sentence summary of the release.]

## Statistics

* Identica also sends anonymous usage statistics and error reports to FastStats, next to bStats. Player names, unique ids and addresses are never sent. Turn them off in `plugins/faststats/config.properties`; the Statistics page of the documentation lists what is sent.

## For addon developers

* `IdenticaAPI` is now an interface. Get it with `IdenticaAPI.get()`, or `IdenticaAPI.whenReady(...)` when your addon may start first, and call the same getters on it: `IdenticaAPI.getEventManager()` becomes `IdenticaAPI.get().getEventManager()`. `getService(...)` became `findService(...)`, which returns an `Optional`, and `getPresenceService()` became `getIdentityService()`.
