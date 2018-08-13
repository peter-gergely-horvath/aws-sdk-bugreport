package bugreport;


import bugreport.filter.CognitoIdentityFilter;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.Timer;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;


public class FooBarHandler implements RequestStreamHandler {
    private final static SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> HANDLER;

    static {
        try {
            HANDLER = SpringLambdaContainerHandler.getAwsProxyHandler(FooBarSpringAppConfig.class);

            // we use the onStartup method of the HANDLER to register our custom filter
            HANDLER.onStartup(servletContext -> {
                FilterRegistration.Dynamic registration = servletContext.addFilter("CognitoIdentityFilter", CognitoIdentityFilter.class);
                registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
            });

        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring framework", e);
        }
    }

    public FooBarHandler() {
        Timer.disable();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        HANDLER.proxyStream(inputStream, outputStream, context);

        // just in case it wasn't closed by the mapper
        outputStream.close();
    }
}
