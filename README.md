# java-cdk

The purpose of this project is to practice using CDK by deploying a Spring Boot application into AWS Fargate.

## Why Fargate?

see [this](https://aws.amazon.com/fargate/)

## Requirements to use this project

 - Install CDK, follow the [Getting Started with the AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html)
 - To deploy or update the stack, make sure you are in directory `learnin-cdk-infra`
 - Since there is no pipeline, when changing the Spring Boot App, you need to build the app before update the stack.