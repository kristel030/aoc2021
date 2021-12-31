package day06;

import util.FileUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// https://adventofcode.com/2021/day/6
public class Lanternfish {

    static boolean TEST = false;

    public static void main(String[] args) {
        String input = null;
        if (TEST) {
            input = "3,4,3,1,2";
        } else {
            input = FileUtil.readLine("src/main/resources/day06/input.txt");
        }

        doPart1(input, 80);
        doPart2(input, 256);
    }

    static void doPart1(String input, int days) {
        School1 school = new School1(input);

        for (int i = 1; i <= days; i++) {
            school.newDay();
        }

        String appendix = TEST ? " (TEST!)" : "";
        System.out.println("answer day 6, part #1 (" + days + " days): " + school.size() + appendix);
    }

    static void doPart2(String input, int days) {
        School2 school = new School2(input);

        for (int i = 1; i <= days; i++) {
            school.newDay();
        }

        String appendix = TEST ? " (TEST!)" : "";
        System.out.println("answer day 6, part #2 (" + days + " days): " + school.size().toString() + appendix);
    }

    static class School1 {

        List<Integer> school = null;

        School1(String input) {
            school = new ArrayList<>(9);
            for (int i = 0; i <= 8; i++) {
                school.add(0);
            }

            String[] fishes = input.split("[,]", 0);
            for (int i = 0; i < fishes.length; i++) {
                int fishAge = Integer.parseInt(fishes[i]);
                school.set(fishAge, school.get(fishAge) + 1);
            }
        }

        void newDay() {
            // remove the 0-day fish from the school
            int zeroDayFish = school.remove(0);

            // add the number of 0-day fish as new (8-day) fish to the end of the array
            school.add(zeroDayFish);

            // increase the number of 6-day fish with the number of 0-day fish
            school.set(6, school.get(6) + zeroDayFish);
        }

        int size() {
            return school.stream()
                    .collect(Collectors.summingInt(Integer::intValue));
        }
    }

    static class School2 {

        List<BigInteger> school = null;

        School2(String input) {
            school = new ArrayList<>(9);
            for (int i = 0; i <= 8; i++) {
                school.add(BigInteger.ZERO);
            }

            String[] fishes = input.split("[,]", 0);
            for (int i = 0; i < fishes.length; i++) {
                int fishAge = Integer.parseInt(fishes[i]);
                school.set(fishAge, school.get(fishAge).add(BigInteger.ONE));
            }
        }

        void newDay() {
            // remove the 0-day fish from the school
            BigInteger zeroDayFish = school.remove(0);

            // add the number of 0-day fish as new (8-day) fish to the end of the array
            school.add(zeroDayFish);

            // increase the number of 6-day fish with the number of 0-day fish
            school.set(6, school.get(6).add(zeroDayFish));
        }

        BigInteger size() {
            BigInteger size = BigInteger.ZERO;
            for (BigInteger fishPerAge : school) {
                size = size.add(fishPerAge);
            }
            return size;
        }
    }
}
