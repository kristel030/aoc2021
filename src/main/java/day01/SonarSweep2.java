package day01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://adventofcode.com/2021/day/1
public class SonarSweep2 {

    public static void main(String[] args) {

        String fileName = "src/main/resources/day01/sonarsweep.txt";
//        String fileName = "src/main/resources/day01/sonarsweep-test.txt";

        // first increases compared to start values don't actually count as an increase,
        // as we're still building the first full window during the first 3 iterations
        // of the while loop below, so we start the counter at -3
        int countWindowIncrease = -3;
        // initialize the recent readings
        List<Integer> recentReadings = new ArrayList<>(4);
        recentReadings.add(0);
        recentReadings.add(0);
        recentReadings.add(0);
        recentReadings.add(0);

        int round = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                int value = Integer.parseInt(line);
                recentReadings.set(3, value);

                int prevFullWindow = sumList(recentReadings, 0, 3);
                int currentFullWindow = sumList(recentReadings, 1, 4);

//                System.out.println("round " + round + ": " + prevFullWindow + " - " + currentFullWindow);
                round++;

                if (prevFullWindow < currentFullWindow) {
                    countWindowIncrease++;
                }
                recentReadings.remove(0);
                recentReadings.add(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("answer day 1, part 2: " + countWindowIncrease);
    }

    static int sumList(List<Integer> list, int startIndex, int endIndex) {
        int result = 0;
        for (int i = startIndex; i < endIndex; i++) {
            result += list.get(i);
        }
        return result;
    }
}
