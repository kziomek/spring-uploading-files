# How to upload big files with Spring
@Author Krzysztof Ziomek

## What will you learn ?

* upload files via Spring REST
* implement client to upload huge files (streaming)

Implementation is based on Spring Boot 1.3.5.RELEASE

## Spring tutorial - small files example

Recently I was searching for Spring solution to upload files via rest service.
I found nice Spring MVC tutorial exemplifying file uploading.
    
    https://spring.io/guides/gs/uploading-files/

Class *MultipartFile* is a representation of an uploaded file received in a multipart request.
Spring converts *multipart/form-data* data to *MultipartFile* representation.
Tutorial contains also example of multipart/form-data form.

    <form method="POST" enctype="multipart/form-data" action="/">

Example provided by Spring is not bad to start with. Just be aware of its restrictions which is memory.
Be aware that every file is buffered in memory while uploading.
If you upload a lot of files or huge files then you need a lot memory for your application to work. 
**You will be always at risk of memory issue.** 

## What about big files then ? 

If you are building application supporting high load and/or huge files you need more robust solution.

**Your solution is to stream files instead of buffering them in memory.**

Continue reading this document to learn how to upload huge files.
If you prefer more standardized Spring REST than Spring MVC you will benefit even more from reading this document.
  
#### Run automatic tests

    ./gradlew test
  
#### Start microservice app ?

    ./gradlew bootRun

#### Manual testing
You can play a bit with uploader manually. Curl and html form examples are described below. 

##### Test with curl.

Upload README.md file based in current directory

    curl -F "file=@README.md" http://localhost:8080/files
   
Add _name_ param to set new name for uploaded file. 
 
    curl -F "name=ChangeFilename.md" -F "file=@README.md" http://localhost:8080/files
   
##### Test with html form

Under html directory you will find plain html form enctype="multipart/form-data".
( This form is not part of microservice jar so run it as separate application. )

1. Run microservice
2. Upload file with uploadform.html
3. Verify that file was uploaded into upload-dir


## How to configure streaming working ?
By default RestTemplate buffers file in memory before sending it to output stream.
If you want to stream data directly from your resource
you can create new RestTemplate bean with HttpComponentsClientHttpRequestFactory factory
and set buffering request body to false.

Don't forget to set BufferRequestBody to false. 

Look at LocalClientConfig configuration.

    @Bean
        public RestTemplate formRestTemplate() {
    
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setBufferRequestBody(false);
    
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(requestFactory);
    
            return restTemplate;
        }

**On server side file is buffered in tmp file on disc.**

## Library

Simple Spring tutorial
    
    https://spring.io/guides/gs/uploading-files/
    
multipart/form-data specification
    
    https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2