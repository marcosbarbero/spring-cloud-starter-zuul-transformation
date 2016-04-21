Request & Response Transformation
---
Transformation starter for Spring Cloud Netflix Zuul

Configuration
---
```
zuul:
  transformer:
    enabled: true
    policies:
      the-service-name:
        request:
          remove:
            methods: get,post
            ignored-paths: /xpto/**
            headers: any-http-header
            query-string: param1,param2,param3
            body: param1,param2,param3
          replace:
            methods: get,post
            ignored-paths: /xpto/**
            headers: old-header-name:new-header-name
            query-string: param1:new-param-name
            body: old-param-name:new-param-name
          add:
            methods: get,post
            ignored-paths: /xpto/**
            headers: header-name:default-value
            query-string: param1:defaul-value
            body: param-name:default-value
        response:
          remove:
            headers: header-name
            body: param1,param2
          replace:
            headers: old-header-name:new-header-name
            body: old-param-name:new-param-name
          add:
            headers: header-name:default-value
            body: param-name:default-value
```
