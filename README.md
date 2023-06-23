# mgutils - Common Java Utils

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ru.mrgrd56/mgutils/badge.svg)](https://central.sonatype.com/artifact/ru.mrgrd56/mgutils)

> **Note**  
> The version displayed here may be outdated, click on the badge to view the latest version

## Description

`mgutils` is a Java library that provides a collection of utilities designed to simplify and enhance your Java development experience. The utilities provided range from data manipulation, multithreading, logging, and more. This library is designed to be lightweight, efficient, and easy to integrate into any Java project.

## Getting Started

### Prerequisites

The library requires Java 8 or above.

### Installation

1. Go to the [Maven Central page](https://central.sonatype.com/artifact/ru.mrgrd56/mgutils) of this library.
2. Copy the snippet for your package manager. E.g. for Maven you can copy code looking like this:
```xml
<dependency>
  <groupId>ru.mrgrd56</groupId>
  <artifactId>mgutils</artifactId>
  <version>CURRENT_VERSION_HERE</version>
</dependency>
```
3. Paste this fragment into your dependency list. In case of Maven, it's the `<dependencies>` section in `pom.xml`.

## Overview

Let's see the most useful classes in this library.

- [ScopedLogger](https://github.com/MRGRD56/mgutils/tree/master#scopedLogger)
- [TaskInvoker](https://github.com/MRGRD56/mgutils/tree/master#taskinvoker)

### ScopedLogger

_ru.mrgrd56.mgutils.logging.ScopedLogger_

`ScopedLogger` is a class that augments traditional logging by adding scope to logging operations. This functionality helps group related log messages together by attaching a `scope name` and a unique `scope ID` to each log message. This is particularly useful when tracking the flow of control in the logs, especially in cases where there are nested scopes.

It's important to note that `ScopedLogger` is a decorator class on the [slf4j](https://mvnrepository.com/artifact/org.slf4j/slf4j-api) `Logger` interface. This means that `ScopedLogger` is always created using a "basic"/"outer" logger and just changes its behavior. Any `Logger` can be used as a base, even another `ScopedLogger` (which lets you create nested `ScopedLogger`s).

```java
public class Example {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public Post fetchPost(int postId) {
        Logger logger = ScopedLogger.of(log, "fetchPost:");
        logger.trace("fetching postId={}", postId);
        
        Response response = http.request("http://localhost:8080/posts/" + postId);
        logger.trace("got the response {}", response);
        
        Post post = response.getBody();
        logger.trace("got the post {}", post);
        for (Comment comment : post.getComments()) {
            Logger commentLogger = ScopedLogger.of(logger, "fetchComment:", comment.getId());
            commentLogger.trace("fetching commentId={}", comment.getId());
            
            Response commentResponse = http.request("http://localhost:8080/posts/" + postId);
            commentLogger.trace("got the response {}", response);
            
            commentsHelper.populateComment(comment, commentResponse.getBody());
            commentLogger.trace("populated comment");
        }
        logger.trace("successfully fetched the post");
        return post;
    }
}
```

Calling `Example#fetchPost` will output something like:

```
TRACE [48y9zeqq2c2d] fetchPost: fetching postId=2690201
TRACE [48y9zeqq2c2d] fetchPost: got the response Response@3769fecf
TRACE [48y9zeqq2c2d] fetchPost: got the post Post@5d244b79
TRACE [48y9zeqq2c2d] fetchPost: [52] fetchComment: fetching commentId=52
TRACE [48y9zeqq2c2d] fetchPost: [52] fetchComment: got the response Response@14cb4e66
TRACE [48y9zeqq2c2d] fetchPost: [52] fetchComment: populated comment
TRACE [48y9zeqq2c2d] fetchPost: [63] fetchComment: fetching commentId=63
TRACE [48y9zeqq2c2d] fetchPost: [63] fetchComment: got the response Response@4a7b18fd
TRACE [48y9zeqq2c2d] fetchPost: [63] fetchComment: populated comment
TRACE [48y9zeqq2c2d] fetchPost: successfully fetched the post
```

Second calling `Example#fetchPost` will output logs with different random `scopeId`:

```
TRACE [juo0n1nal8m5] fetchPost: fetching postId=2690201
TRACE [juo0n1nal8m5] fetchPost: got the response Response@3769fecf
...
TRACE [juo0n1nal8m5] fetchPost: [63] fetchComment: populated comment
TRACE [juo0n1nal8m5] fetchPost: successfully fetched the post
```

The identifiers used by `ScopedLogger` are called `scopeName` and `scopeId`. In the case above, in `logger`, the `scopeName` is `"fetchPost:"` and the `scopeName` is `"48y9zeqq2c2d"` (randomly generated).

The `scopeName` typically represents a method or block of code, while the `scopeId` is a unique identifier created for each new instance of a `ScopedLogger` at the time of a new invocation of a block of code represented by the `scope name`. When a `ScopedLogger` is created from another `ScopedLogger`, all the scope names and IDs are included in the log messages, which assists in tracking nested and interdependent log entries​.

Note that `scopeId` can be set manually, like in the comments loop in the code above. Also, `null` value can be passed to the `scopeId` parameter. In this case, `scopeId` won't be shown. If you don't pass the `scopeId`, it's randomly generated using `ScopedLogger.createScopeId()` by default.

It's possible to create `ScopedLogger`s another way as well. You can use `ScopedLoggerFactory` to create `ScopedLogger`s with the same source logger:

```java
public class Example {
    private final ScopedLogger logs = new ScopedLogger(LoggerFactory.getLogger(this.getClass()));
    
    public Post fetchPost(int postId) {
        Logger logger = logs.createLogger("fetchPost:");
        logger.trace("fetching postId={}", postId);
        // <...>
    }
}
```

It will work the same way.

### TaskInvoker

_ru.mrgrd56.mgutils.concurrent.TaskInvoker_

The `TaskInvoker` class is designed to execute a specific set of tasks, distributing them among threads using an ExecutorService. Tasks can be submitted for execution, but the execution doesn't start immediately. Instead, all tasks are stored and later executed when the `completeAll()` method is called. This method also waits for all tasks to finish and returns the results. `TaskInvoker` supports the submission of both `Runnable` and `Callable` tasks, with or without return values​.

It can be considered as an alternative to the `ExecutorService#invokeAll` method without having to create a `Collection` of tasks and results explicitly.

Let's see some examples.

We'll use this `doStuff` method as one that performs some long task.

```java
String doStuff(int number) throws Exception {
    // Here's a task returning some data
    Thread.sleep(150);
    return "Number " + number;
}
```

Using plain `ExecutorService`:

```java
ExecutorService executor = Executors.newFixedThreadPool(50);
List<Callable<String>> tasks = new ArrayList<>();

for (int i = 0; i < 60; i++) {
    int number = i;
    tasks.add(() -> doStuff(number));
}

List<Future<String>> resultFutures;
try {
    resultFutures = executor.invokeAll(tasks);
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}

List<String> results = resultFutures.stream()
        .map(stringFuture -> {
            try {
                return stringFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        })
        .collect(Collectors.toList());
```

Using `TaskInvoker`:

```java
ExecutorService executor = Executors.newFixedThreadPool(50);
TaskInvoker<String> invoker = new TaskInvoker<>(executor);
for (int i = 0; i < 60; i++) {
    int number = i;
    invoker.submit(() -> doStuff(number));
}
List<String> results = invoker.completeAll();
```

Also, tasks in `TaskInvoker` can be cancelled using the `cancelAll` method. Here's an example:

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
TaskInvoker<Void> invoker = new TaskInvoker<>(executor);

List<String> results = Collections.synchronizedList(new ArrayList<>());
AtomicInteger counter = new AtomicInteger(0); // just to know when to cancel the tasks

final int MAX_COUNT = 100;

for (int i = 0; i < 100; i++) {
    int number = i;
    invoker.submit(() -> {
        if (counter.getAndIncrement() == 6) {
            invoker.cancelAll(); // cancelling the remaining tasks
        }
        results.add(doStuff(number));
    });
}
try {
    invoker.completeAll();
} catch (CancellationException e) {
    // an exception will be thrown
}

// the results list contains less than 100 items
```

As soon as `completeAll()` is called, the remaining tasks are immediately marked as cancelled and an attempt to execute them by `TaskInvoker` will lead to `CancellationException`.

Because `completeAll()` throws an exception when the tasks are cancelled, we have to collect the results manually, if needed. Note that it's also possible to pass a function without a returning value to `invoker.submit(...)`, that is not possible with `executor.invokeAll` (`Callable<Void>` still requires returning `null`).
