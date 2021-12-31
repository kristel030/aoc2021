package day03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://adventofcode.com/2021/day/3
public class BinaryDiagnostic {

    static boolean TEST = false;

    public static void main (String[] args) {
        String path = "src/main/resources/day03/diagnostic-test-input.txt";
        if (!TEST) {
            path = "src/main/resources/day03/diagnostic-input.txt";
        }
        doPart1(path);
        doPart2(path);
    }

    static void doPart1(String path) {
        DiagnosticInterpreter1 interpreter = new DiagnosticInterpreter1();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String binaryNumber;
            while ((binaryNumber = br.readLine()) != null) {
                interpreter.processBinaryNumber(binaryNumber);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String appendix = TEST ? " (TEST!)" : "";
        int answer = interpreter.getPowerConsumption();
        System.out.println("answer day 3, part #1: " + answer + " " +
                "(gamma: " + interpreter.getGamma() + " or " + interpreter.getGammaInDecimal() +
                "; epsilon: " + interpreter.getEpsilon() + " or " + interpreter.getEpsilonInDecimal() + ")" + appendix);

    }

    static void doPart2(String path) {

        // initialize the interpreter
        DiagnosticInterpreter2 interpreter = new DiagnosticInterpreter2();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String binaryNumber;
            while ((binaryNumber = br.readLine()) != null) {
                interpreter.feedBinaryNumber(binaryNumber);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // determine the life support rating
        int answer = interpreter.determineLifeSupportRating();

        String appendix = TEST ? " (TEST!)" : "";
        System.out.println("answer day 3, part #2: " + answer + " " +
                "(oxygen: " + interpreter.getOxygenRating() + " or " + interpreter.getOxygenRatingInDecimal() +
                "; CO2: " + interpreter.getCo2Rating() + " or " + interpreter.getCo2RatingInDecimal() + ")" + appendix);

    }

    static class DiagnosticInterpreter1 {
        // count the appearances of the '1' digit for each position
        int oneDigits[] = null;
        int lineCounter = 0;

        private void initOneDigits(String firstBinaryNumber) {
            List<Integer> oneDigitsList = new ArrayList<>();
            for (int i = 0; i < firstBinaryNumber.length(); i++) {
                oneDigitsList.add(0);
            }
            oneDigits = oneDigitsList.stream().mapToInt(Integer::intValue).toArray();
        }

        public void processBinaryNumber(String binaryNumber) {
            if (oneDigits == null) {
                initOneDigits(binaryNumber);
            }
            for (int i = 0; i < binaryNumber.length(); i++) {
                oneDigits[i] += Integer.parseInt(""+binaryNumber.charAt(i));
            }
            lineCounter++;
        }

        public String getGamma() {
            String gamma = "";

            for (int i = 0; i < oneDigits.length; i++) {
                if (oneDigits[i] > lineCounter*0.5) {
                    gamma += "1";
                } else {
                    gamma += "0";
                }
            }
            return gamma;
        }

        public int getGammaInDecimal() {
            return Integer.parseInt(getGamma(), 2);
        }

        public String getEpsilon() {
            String epsilon = "";

            String gamma = getGamma();
            // now 'inverse' gamma: replace every 1 with a 0 and vice versa
            for (int i = 0; i < gamma.length(); i++) {
                if (gamma.charAt(i) == '1') {
                    epsilon += "0";
                } else {
                    epsilon += "1";
                }
            }
            return epsilon;
        }

        public int getEpsilonInDecimal() {
            return Integer.parseInt(getEpsilon(), 2);
        }

        public int getPowerConsumption() {
            return getGammaInDecimal() * getEpsilonInDecimal();
        }
    }

    static class DiagnosticInterpreter2 {
        private List<String> oxygenCandidates = new ArrayList<>();
        private List<String> co2Candidates = new ArrayList<>();

        private String oxygenRating = "0";
        private String co2Rating = "0";

        public void feedBinaryNumber(String binaryNumber) {
            oxygenCandidates.add(binaryNumber);
            co2Candidates.add(binaryNumber);
        }

        public int determineLifeSupportRating() {
            determineOxygenRating();
            determineCo2Rating();
            return getOxygenRatingInDecimal() * getCo2RatingInDecimal();
        }

        private void determineOxygenRating() {
            doOxygenIteration(0);
            oxygenRating = oxygenCandidates.get(0);
        }

        private void doOxygenIteration(int bitToCheck) {
            // determine the most common bit value in the 'bitToCheck'th position of all remaining oxygen candidate values
            // (we do this by counting the number of candidate values which have a 1 in the position,
            //  and then check if this number if more or less of half of the total number of values)
            int countOnes = 0;
            char mostCommonBitValue;
            for (String oxygenCandidate:oxygenCandidates) {
               countOnes += Integer.parseInt("" + oxygenCandidate.charAt(bitToCheck));
            }
            if (countOnes >= oxygenCandidates.toArray().length * 0.5) {
                mostCommonBitValue = '1';
            } else {
                mostCommonBitValue = '0';
            }

            // keep the candidates who have the 'bitToCheck'th bit set to 'mostCommonBitValue'
            List<String> filteredOxygenCandidates = new ArrayList<>();
            for (String oxygenCandidate:oxygenCandidates) {
                if (oxygenCandidate.charAt(bitToCheck) == mostCommonBitValue) {
                    filteredOxygenCandidates.add(oxygenCandidate);
                }
            }
            oxygenCandidates = filteredOxygenCandidates;

            // proceed to the next bit as long as the number of candidates has not yet been reduced to a single value
            if (oxygenCandidates.size() > 1) {
                doOxygenIteration(++bitToCheck);
            }
        }

        private void determineCo2Rating() {
            doCo2Iteration(0);
            co2Rating = co2Candidates.get(0);
        }

        private void doCo2Iteration(int bitToCheck) {
            // determine the most common bit value in the 'bitToCheck'th position of all remaining oxygen candidate values
            // (we do this by counting the number of candidate values which have a 1 in the position,
            //  and then check if this number if more or less of half of the total number of values)
            int countOnes = 0;
            char leastCommonBitValue;
            for (String co2Candidate:co2Candidates) {
                countOnes += Integer.parseInt("" + co2Candidate.charAt(bitToCheck));
            }
            if (countOnes >= co2Candidates.toArray().length * 0.5) {
                leastCommonBitValue = '0';
            } else {
                leastCommonBitValue = '1';
            }

            // keep the candidates who have the 'bitToCheck'th bit set to 'leastCommonBitValue'
            List<String> filteredCo2Candidates = new ArrayList<>();
            for (String co2Candidate:co2Candidates) {
                if (co2Candidate.charAt(bitToCheck) == leastCommonBitValue) {
                    filteredCo2Candidates.add(co2Candidate);
                }
            }
            co2Candidates = filteredCo2Candidates;

            // proceed to the next bit as long as the number of candidates has not yet been reduced to a single value
            if (co2Candidates.size() > 1) {
                doCo2Iteration(++bitToCheck);
            }
        }

        public String getOxygenRating() {
            return oxygenRating;
        }

        public String getCo2Rating() {
            return co2Rating;
        }

        public int getOxygenRatingInDecimal() {
            return Integer.parseInt(oxygenRating, 2);
        }

        public int getCo2RatingInDecimal() {
            return Integer.parseInt(co2Rating, 2);
        }
    }

}
