Exception handling readable way

1. __* exception handling*__
```
Try.toCall(() -> getSomeObject())
   .acceptRuntimeException(RuntimeException.class)
   .thenCall((exception) -> cleanup everything here)
   .done()
```