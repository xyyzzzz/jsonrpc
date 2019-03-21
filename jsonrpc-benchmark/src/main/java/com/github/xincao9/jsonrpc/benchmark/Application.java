/*
 * Copyright 2018 xincao9@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.xincao9.jsonrpc.benchmark;

import com.github.xincao9.jsonrpc.benchmark.constant.ConfigConsts;
import com.github.xincao9.jsonrpc.benchmark.server.FibonacciSequenceServiceImpl;
import com.github.xincao9.jsonrpc.benchmark.server.SleepServiceImpl;
import com.github.xincao9.jsonrpc.core.client.JsonRPCClient;
import com.github.xincao9.jsonrpc.core.server.JsonRPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author xincao9@gmail.com
 */
@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static String role;
    private static JsonRPCClient jsonRPCClient;

    public static void main(String... args) throws Throwable {
        role = System.getProperty(ConfigConsts.ROLE, ConfigConsts.PROVIDER);
        if (ConfigConsts.PROVIDER.equalsIgnoreCase(role)) {
            LOGGER.info("bootstrap service provider");
            JsonRPCServer jsonRPCServer = JsonRPCServer.defaultJsonRPCServer();
            jsonRPCServer.register(new FibonacciSequenceServiceImpl());
            jsonRPCServer.register(new SleepServiceImpl());
            jsonRPCServer.start();
        } else if (ConfigConsts.CONSUMER.equalsIgnoreCase(role)) {
            LOGGER.info("bootstrap service consumer");
            jsonRPCClient = JsonRPCClient.defaultJsonRPCClient();
            jsonRPCClient.start();
            SpringApplication.run(Application.class, args);
        } else {
            LOGGER.warn("java -Drole=[provider | consumer] -jar jsonrpc-benchmark.jar");
        }
    }

    @Bean
    public FibonacciSequenceService fibonacciSequenceService () {
        return jsonRPCClient.proxy(FibonacciSequenceService.class);
    }
    
    @Bean
    public SleepService sleepService () {
        return jsonRPCClient.proxy(SleepService.class);
    }
}