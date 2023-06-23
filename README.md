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

### Overview

Let's see the most useful classes in this library.

#### `ru.mrgrd56.mgutils.logging.ScopedLogger`

`ScopedLogger` is a class that augments traditional logging by adding scope to logging operations. This functionality helps group related log messages together by attaching a `scope name` and a unique `scope ID` to each log message. This is particularly useful when tracking the flow of control in the logs, especially in cases where there are nested scopes.

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

The `scope name` typically represents a method or block of code, while the `scope ID` is a unique identifier created for each new instance of a `ScopedLogger` at the time of a new invocation of a block of code represented by the `scope name`. When a `ScopedLogger` is created from another `ScopedLogger`, all the scope names and IDs are included in the log messages, which assists in tracking nested and interdependent log entries​.

#### ru.mrgrd56.mgutils.concurrent.TaskInvoker

The `TaskInvoker` class is designed to execute a specific set of tasks, distributing them among threads using an ExecutorService. Tasks can be submitted for execution, but the execution doesn't start immediately. Instead, all tasks are stored and later executed when the `completeAll()` method is called. This method also waits for all tasks to finish and returns the results. `TaskInvoker` supports the submission of both `Runnable` and `Callable` tasks, with or without return values​.

