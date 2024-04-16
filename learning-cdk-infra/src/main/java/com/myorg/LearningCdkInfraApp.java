package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;


public class LearningCdkInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        new LearningCdkInfraStack(app, "LearningCdkInfraStack", StackProps.builder()
                .build());

        app.synth();
    }
}

