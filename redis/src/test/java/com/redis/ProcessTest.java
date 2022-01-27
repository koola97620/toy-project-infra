package com.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("h2")
public class ProcessTest {

    @Test
    void test() throws IOException {
        int redisPort = 6300;
        String command = String.format("netstat -nat | findstr LISTEN|findstr %d", redisPort);
        //String[] shell = {"/bin/sh", "-c", command};
//        Process p = new ProcessBuilder("cmd.exe /c " + String.format("netstat -nat | findstr LISTEN|findstr %d", redisPort))
//                .start();
//        String[] shell = {command};
//        Process exec = Runtime.getRuntime().exec(shell);

        ProcessBuilder processBuilder = new ProcessBuilder("netstat.exe", "-nat", "findstr LISTEN|findstr 6300");
        processBuilder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
        Process netstat = processBuilder.start();

        //System.out.println(process);



        System.out.println(netstat.toString());
        System.out.println("end");
    }

    @Test
    void test2() throws IOException {
        ProcessBuilder p = new ProcessBuilder("netstat.exe", "-noa", "-p", "tcp");
        p.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
        Process netstat = p.start();

        try (BufferedReader output =
                     new BufferedReader(new InputStreamReader(netstat.getInputStream()))) {

            List<String> collect = output.lines()
                    .filter(line -> line.contains(":6300"))
                    .toList();

            System.out.println(collect.size());
            collect.forEach( str -> System.out.println(str));


        } catch (Exception e) {

        }

    }
}
