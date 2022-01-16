package com.examples.kafka.producer;

import java.util.Scanner;

public class MessageInserter {
    private static final Scanner scanner = new Scanner(System.in);

    public static String input() {
        return scanner.next();
    }
}
