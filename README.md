## Things not handled

* Timezones. Timezones are very important in booking situations. There is the timezone of the booker and the timezone of the motel that must be considered. It is very possible that those two are not the same.
* Unique IDs (e.x. DB sequence or UUIDs). For simplicity in this exercise, room number is treated as a unique ID. In practice I would advocate for using a separate, internally generated ID for determining uniqueness - even if room number is meant to be unique.
* Exception handling is very basic, just using RuntimeException for all errors. In practice, custom exceptions tend to be better so that they can be mapped to specific error codes or be specifically caught higher up the call stack.
