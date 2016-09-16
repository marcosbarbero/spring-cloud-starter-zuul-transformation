Spring Cloud Zuul - Request Transformation
---
Request transformation starter for Spring Cloud Netflix Zuul

Usage
---
>This project is available on maven central

Add the dependency on pom.xml
```
<dependency>
    <groupId>com.marcosbarbero.cloud</groupId>
    <artifactId>spring-cloud-zuul-transformation</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

Sample Configuration
---
```
zuul:
  transformer:
    enabled: true
    policies:
      the-service-name:
        request:
          add:
            methods: get,post
            ignored-paths: /xpto/**
            headers: header-name:default-value
            query-string: param1:defaul-value
            body: param-name:default-value
          replace:
            methods: get,post
            ignored-paths: /xpto/**
            headers: old-header-name:new-header-name
            query-string: param1:new-param-name
            body: old-param-name:new-param-name
          remove:
            methods: get,post
            ignored-paths: /xpto/**
            headers: any-http-header
            query-string: param1,param2,param3
            body: param1,param2,param3
        response:
          add:
            headers: header-name:default-value
            body: param-name:default-value
          replace:
            headers: old-header-name:new-header-name
            body: old-param-name:new-param-name
          remove:
            headers: header-name
            body: param1,param2
```

Any doubt open an [issue](https://github.com/marcosbarbero/spring-cloud-starter-zuul-transformation/issues).  
Any fix send me a [Pull Request](https://github.com/marcosbarbero/spring-cloud-starter-zuul-transformation/pulls).