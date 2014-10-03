# rewrite-java-ee-7-primefaces-5

This repository shows a minimal setup code needed to run powerful url routing and rewriting tool [ocpsoft rewrite](http://ocpsoft.org/rewrite/).

Two methods of 404 handling are introduced. The minimal code to run rewrite is in the Initial commit of this repository.
 
Three things happen in here:

1. ``http://localhost:8080/rewrite-java-ee-7-primefaces-5-1.0-SNAPSHOT/`` gets you a normal page rendering ``index.xhtml`` with status code 200.
2. ``http://localhost:8080/rewrite-java-ee-7-primefaces-5-1.0-SNAPSHOT/second-page`` renders ``404byRewrite.xhtml`` defined in ``RewriteConfig.java`` with status code 404 and also the string ``second-page`` is injected into ``errorBean.page`` property. 
3. ``http://localhost:8080/rewrite-java-ee-7-primefaces-5-1.0-SNAPSHOT/third-page`` renders ``404.xhtml`` defined in ``web.xml`` with status code 404.

The work is based on [java-ee-7-primefaces-5](https://github.com/cilf/java-ee-7-primefaces-5) example.

I'm deploying it locally to Wildfly 8.1.0.Final using IntelliJ IDEA 13.1.5.