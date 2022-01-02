package day07;

import util.FileUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Whale {
    static boolean TEST = false;

    public static void main(String[] args) {
        String input = null;
        if (TEST) {
            input = "16,1,2,0,4,2,7,1,2,14";
        } else {
            input = FileUtil.readLine("src/main/resources/day07/crabs-input.txt");
        }

        int crabPositions[] = Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).toArray();

        // part 1
        long result1[] = determineOptimalPositionAndFuelUsage(crabPositions, false);
        String appendix = TEST ? " (TEST!)" : "";
        System.out.println("answer day 7, part #1: " + result1[1] + " (position " + result1[0] + ") " + appendix);

        // part 2
        long result2[] = determineOptimalPositionAndFuelUsage(crabPositions, true);
        System.out.println("answer day 7, part #2: " + result2[1] + " (position " + result2[0] + ") " + appendix);
    }

    // result:
    //  1st element: optimal position
    //  2nd element: fuel usage
    static long[] determineOptimalPositionAndFuelUsage(int crabPositions[], boolean useComplexFuelCalculation) {
        int minPosition = Arrays.stream(crabPositions).boxed().collect(Collectors.toList())
                .stream()                     // Stream<Integer>
                .min(Comparator.naturalOrder()) // Optional<Integer>
                .get();

        int maxPosition = Arrays.stream(crabPositions).boxed().collect(Collectors.toList())
                .stream()                     // Stream<Integer>
                .max(Comparator.naturalOrder()) // Optional<Integer>
                .get();

        // p = target position to align on
        // c = crab index in crab position list
        long minFuel = -1;
        int minFuelPosition = -1;
        for (int p = minPosition; p<=maxPosition; p++) {
            long fuel = 0;
            for (int c = 0; c < crabPositions.length; c++) {
                fuel += useComplexFuelCalculation ? computeComplexFuelUsage(crabPositions[c],p) : computeSimpleFuelUsage(crabPositions[c],p);
            }
            if (fuel < minFuel || minFuel == -1) {
                minFuel = fuel;
                minFuelPosition = p;
                //System.out.println("new minimum fuel position found: " + p + " (fuel: " + fuel + ")");
            }
        }

        long result[] = {minFuelPosition, minFuel};
        return result;
    }

    // it costs x fuel to travel x distance
    private static int computeSimpleFuelUsage(int from, int to) {
        return Math.abs(from-to);
    }

    // longer distances require progressively more fuel
    // when distance is x, the fuel costs are 1 + 2 + 3 + ... + x
    private static long computeComplexFuelUsage(int from, int to) {
        int distance = Math.abs(from-to);

        // distance between from and to is even
        if (distance % 2 == 0) {
            // as 'distance' is an even number by definition, the result of '* 0.5' cannot be a non-integer by default
            // so rounding is a safe method to get a result of type long
            // concept: 1 + 2 + 3 + 4 + 5 + 6 = (1+6) + (2+5) + (3+4) = (6+1) * 3
            return Math.round((distance + 1) * distance * 0.5);
        // distance between from and even is odd
            // concept: 1 + 2 + 3 + 4 + 5 + 6 + 7 = (1+7) + (2+6) + (3+5) + 4 = ((7+1) * 3) + 4 (where 4 is the half-way value between 1 and 7)
        } else {
            return Math.round((distance + 1) * (distance-1) * 0.5) + Math.round(distance * 0.5);
        }
    }

}
