package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.CpuUtilizationScalingProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;

public class LearningCdkInfraStack extends Stack {
    public LearningCdkInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LearningCdkInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, "LearningCdkVpc")
                .maxAzs(2)
                .natGateways(1)
                .build();

        Cluster cluster = Cluster.Builder.create(this, "LeaningCdkCluster")
                .clusterName("learning-cdk")
                .vpc(vpc)
                .build();

        ApplicationLoadBalancedFargateService app = ApplicationLoadBalancedFargateService.Builder
                .create(this, "LearningCdkFargate")
                .cluster(cluster)
                .desiredCount(2)
                .cpu(256)
                .memoryLimitMiB(512)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromAsset("../learning-cdk"))
                                .containerPort(8080)
                                .build())
                .build();

        app.getTargetGroup().configureHealthCheck(HealthCheck.builder()
                .port("8080")
                .path("/actuator/health")
                .interval(Duration.seconds(5))
                .timeout(Duration.seconds(4))
                .healthyThresholdCount(2)
                .unhealthyThresholdCount(2)
                .healthyHttpCodes("200,301,302")
                .build());

        var autoScale = app.getService().autoScaleTaskCount(
                EnableScalingProps.builder()
                        .maxCapacity(4)
                        .minCapacity(2)
                        .build());

        autoScale.scaleOnCpuUtilization("cpu-autoscaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(45)
                .policyName("cpu-autoscaling-policy")
                .scaleInCooldown(Duration.seconds(30))
                .scaleOutCooldown(Duration.seconds(30))
                .build());

    }
}
