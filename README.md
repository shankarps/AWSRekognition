# Amazon Rekognition sample project

This is a sample project to work & showcase use cases for the image Amazon Rekognition service.

## Getting Started

Install Git, Java 8, Maven and any IDE of your choice (Eclipse, IntelliJ, etc)

Create an AWS IAM user with admin access.

### Prerequisites

AWS account & basic AWS knowledge is needed.


### Installing & running

Check out the source code

```
$git clone https://github.com/shankarps/AWSRekognition.git
```

Compile the project

```
$mvn clean install 
```
Run the program with default file.

```
mvn clean install exec:java 
```

Run the program with specific file.

```
$mvn clean install exec:java -Dexec.args="white-land-rover.jpg"

```

## Running the tests

TODO


## Built With

* [Java 8] 
* AWS Java SDK
* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Shankar P** - *Initial work* - [(https://github.com/shankarps)


## Acknowledgments

* All image files in this project are from www.pexels.com/. They are used for non-commercial purposes.
* Documentation reference from AWS Rekognition http://docs.aws.amazon.com/rekognition/latest/dg/what-is.html

