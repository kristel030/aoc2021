package day01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// https://adventofcode.com/2021/day/1
public class SonarSweep1 {

    public static void main(String[] args) {

        String fileName = "src/main/resources/day01/sonarsweep.txt";

        int prev = -1;
        int countIncrease = -1; // first increase compared to start value of prev (-1) doesn't actually count as an increase, so we start the counter at -1

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                int next = Integer.parseInt(line);
                if (next > prev) {
                    countIncrease++;
                }
                prev = next;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("answer day 1, part 1: " + countIncrease);
    }
}
