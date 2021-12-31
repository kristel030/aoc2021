package day02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// https://adventofcode.com/2021/day/2
public class Dive {

    static boolean TEST = false;

    public static void main (String[] args) {
        String path = "src/main/resources/day02/dive-test-input.txt";
        if (!TEST) {
            path = "src/main/resources/day02/dive-input.txt";
        }
        doPart1(path);
        doPart2(path);
    }

    static void doPart1(String path) {
        Submarine submarine = new Submarine1();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String command;
            while ((command = br.readLine()) != null) {
                submarine.executeCommand(command);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String appendix = TEST ? " (TEST!)" : "";
        int answer = submarine.getHorizontalPosition() * submarine.getDepth();
        System.out.println("answer day 2, part #1: " + answer + " (" + submarine.getHorizontalPosition() + " * " + submarine.getDepth() + ") " + appendix);
    }

    static void doPart2(String path) {
        Submarine submarine = new Submarine2();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String command;
            while ((command = br.readLine()) != null) {
                submarine.executeCommand(command);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String appendix = TEST ? " (TEST!)" : "";
        int answer = submarine.getHorizontalPosition() * submarine.getDepth();
        System.out.println("answer day 2, part #2: " + answer + " (" + submarine.getHorizontalPosition() + " * " + submarine.getDepth() + ") " + appendix);
    }

    static abstract class Submarine {
        protected int horizontal = 0;
        protected int depth = 0;

        abstract public void executeCommand(String command) ;

        public int getHorizontalPosition() {
            return horizontal;
        }

        public int getDepth() {
            return depth;
        }
    }

    static class Submarine1 extends Submarine {

        public void executeCommand(String command) {
            String[] commandParts = command.split(" ");
            String direction = commandParts[0];
            int units = Integer.parseInt(commandParts[1]);

            switch (direction) {
                case "forward":
                    horizontal += units;
                    break;
                case "down":
                    depth += units;
                    break;
                case "up":
                    depth -= units;
                    break;
            }
        }
    }

    static class Submarine2 extends Submarine {

        private int aim = 0;

        public void executeCommand(String command) {
            String[] commandParts = command.split(" ");
            String direction = commandParts[0];
            int units = Integer.parseInt(commandParts[1]);

            switch (direction) {
                // forward X does two things:
                //   - It increases your horizontal position by X units.
                //   - It increases your depth by your aim multiplied by X
                case "forward":
                    horizontal += units;
                    depth += aim * units;
                    break;

                // down X increases your aim by X units
                case "down":
                    aim += units;
                    break;

                // up X decreases your aim by X units.
                case "up":
                    aim -= units;
                    break;
            }
        }
    }
}
