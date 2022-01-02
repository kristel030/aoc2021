package day08;

import util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

public class SevenSegmentDisplay {

    static boolean TEST = false;

    private static final int LENGTH1 = 2;
    private static final int LENGTH4 = 4;
    private static final int LENGTH7 = 3;
    private static final int LENGTH8 = 7;

    static final String PATTERN_0 = "ABCEFG";
    static final String PATTERN_1 = "CF";
    static final String PATTERN_2 = "ACDEG";
    static final String PATTERN_3 = "ACDFG";
    static final String PATTERN_4 = "BCDF";
    static final String PATTERN_5 = "ABDFG";
    static final String PATTERN_6 = "ABDEFG";
    static final String PATTERN_7 = "ACF";
    static final String PATTERN_8 = "ABCDEFG";
    static final String PATTERN_9 = "ABCDFG";

    static Map<Integer, String> patternLookup;
    static {
        // init pattern lookup
        patternLookup = new HashMap<>();
        patternLookup.put(0, PATTERN_0);
        patternLookup.put(1, PATTERN_1);
        patternLookup.put(2, PATTERN_2);
        patternLookup.put(3, PATTERN_3);
        patternLookup.put(4, PATTERN_4);
        patternLookup.put(5, PATTERN_5);
        patternLookup.put(6, PATTERN_6);
        patternLookup.put(7, PATTERN_7);
        patternLookup.put(8, PATTERN_8);
        patternLookup.put(9, PATTERN_9);
    }

    public static void main(String[] args) {
        String path = "src/main/resources/day08/display-input.txt";
        if (TEST) {
            path = "src/main/resources/day08/display-test-input.txt";
        }

        List<String> lines = FileUtil.readAllLines(path);

        doPart1(lines);
        doPart2(lines);
    }

    public static void doPart1(List<String> lines) {
        int count1 = 0;
        int count4 = 0;
        int count7 = 0;
        int count8 = 0;

        // Line sample:
        // be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        for (String line:lines) {
            String tuple[] = line.split("\\|");
            String[] outputValues = tuple[1].trim().split(" ");
            for (int i = 0; i < outputValues.length; i++) {
                switch (outputValues[i].length()) {
                    case LENGTH1:
                        count1++;
                        break;
                    case LENGTH4:
                        count4++;
                        break;
                    case LENGTH7:
                        count7++;
                        break;
                    case LENGTH8:
                        count8++;
                        break;
                }
            }
        }

        String appendix = TEST ? " (TEST!)" : "";
        int answer = count1 + count4 + count7 + count8;
        System.out.println("answer day 8, part #1: " + answer + appendix);
    }

    public static void doPart2(List<String> lines) {
        List<Entry> entries = new ArrayList<>();
        // Line sample:
        // be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
        for (String line:lines) {
            Entry entry = new Entry(line.replace("| ", "").split(" "));
            entries.add(entry);
            entry.solve();
        }

        String appendix = TEST ? " (TEST!)" : "";
        int answer = entries.stream().map(e -> e.output).collect(Collectors.summingInt(Integer::intValue));
        System.out.println();
        System.out.println("answer day 8, part #2: " + answer + appendix);
    }

    public static class Entry {
        // input properties -------------------------------------------------------------
        // 10 signal patterns
        String signalPatterns[];
        // the same 10 signal patterns, but now with the characters in each individual signal pattern sorted alphabetically
        String sortedSignalPatterns[] = new String[10];

        // 4 display signals
        String displaySignals[];
        // the same 4 display signals, but now with the characters in each individual signal pattern sorted alphabetically
        String sortedDisplaySignals[] = new String[4];

        // intermediate result (or helper) properties -----------------------------------
        // mapping of output signals A, B, C, D, E, F, G to input signals a, b, c, d, e, f, g
        Map<Character, Character> wireMapping = new HashMap<>();
        // mapping of signals (e.g. 'bge') to numerical value (e.g. 3)
        Map<String, Integer> signalMapping = new HashMap<>();
        // inverse of signalMapping, mas a numerical value (e.g. 3) to a signal (e.g. 'bge')
        Map<Integer, String> valueMapping = new HashMap<>();

        // output property --------------------------------------------------------------
        // numerical value shown in the display
        int output;

        // constructor ------------------------------------------------------------------
        public Entry(String[] signals) {
            this.signalPatterns = Arrays.copyOfRange(signals, 0, 10);
            for (int i = 0; i < signalPatterns.length; i++) {
                sortedSignalPatterns[i] = sortString(signalPatterns[i]);
            }

            this.displaySignals = Arrays.copyOfRange(signals, 10, 14);
            for (int i = 0; i < displaySignals.length; i++) {
                sortedDisplaySignals[i] = sortString(displaySignals[i]);
            }
        }

        // solve ------------------------------------------------------------------------
        public void solve() {
            // print the line
//            for (int i = 0; i < signalPatterns.length; i++) {
//                System.out.print(signalPatterns[i] + " ");
//            }
//            System.out.print ("| ");
//            for (int i = 0; i < displaySignals.length; i++) {
//                System.out.print(displaySignals[i] + " ");
//            }
//            System.out.println();

            // step 1: use unique signal lengths
            for (int i = 0; i < sortedSignalPatterns.length; i++) {
                String sortedSignal = sortedSignalPatterns[i];
                switch (sortedSignalPatterns[i].length()) {
                    case LENGTH1:
                        signalMapping.put(sortedSignal, 1);
                        valueMapping.put(1, sortedSignal);
                        break;
                    case LENGTH4:
                        signalMapping.put(sortedSignal, 4);
                        valueMapping.put(4, sortedSignal);
                        break;
                    case LENGTH7:
                        signalMapping.put(sortedSignal, 7);
                        valueMapping.put(7, sortedSignal);
                        break;
                    case LENGTH8:
                        signalMapping.put(sortedSignal, 8);
                        valueMapping.put(8, sortedSignal);
                        break;
                }
            }

            /* output wires:

                 AAA
                B   C
                B   C
                 DDD
                E   F
                E   F
                 GGG
             */

            // step 2: determine input wire for output wire A, by comparing the 1 and 7 signal patters
            Character inputForA = difference(valueMapping.get(7), valueMapping.get(1)).charAt(0);
            wireMapping.put('A', inputForA);

            // step 3: there are 3 signals with length 6: the signals for 0, 6 and 9
            // find the signal with NO full overlap with the signal for 1 -> that signal represents 6
            for (int i = 0; i < sortedSignalPatterns.length; i++) {
                String sortedSignal = sortedSignalPatterns[i];
                if (sortedSignal.length() == 6) {
                    if (difference (sortedSignal, valueMapping.get(1)).length() == 5) {
                        signalMapping.put(sortedSignal, 6);
                        valueMapping.put(6, sortedSignal);
                        break;
                    }
                }
            }

            // step 4: the difference between the signal for 6 and 8 is segment C
            Character inputForC = difference(valueMapping.get(8), valueMapping.get(6)).charAt(0);
            wireMapping.put('C', inputForC);

            // step 5: now we know the input for C, we can use the signal for value 1 to determine the input for segment F
            // (because 1 is C and F)
            Character inputForF = difference(valueMapping.get(1), ""+inputForC).charAt(0);
            wireMapping.put('F', inputForF);

            // step 6: now we know the signal for 6, we can look for a signal of length 5 (this represents either 2, 3 or 5)
            // which has only one segment difference with signal 6, that signal is 5
            for (int i = 0; i < sortedSignalPatterns.length; i++) {
                String sortedSignal = sortedSignalPatterns[i];
                if (sortedSignal.length() == 5) {
                    if (difference (valueMapping.get(6), sortedSignal).length() == 1) {
                        signalMapping.put(sortedSignal, 5);
                        valueMapping.put(5, sortedSignal);
                        break;
                    }
                }
            }

            // step 7: the segment difference between the signals for 6 and 5 is the E segment
            Character inputForE = difference(valueMapping.get(6), valueMapping.get(5)).charAt(0);
            wireMapping.put('E', inputForE);

            // step 8: there are 3 signals with length 6 (0, 6, 9)
            // only for the signal which represents 9 the following is true:
            //  when you remove the segments for 4 and 7, you are only left with a single segment and that segment is G
            for (int i = 0; i < sortedSignalPatterns.length; i++) {
                String sortedSignal = sortedSignalPatterns[i];
                if (sortedSignal.length() == 6) {
                    String dif = difference(difference(sortedSignal,valueMapping.get(7)), valueMapping.get(4));
                    if (dif.length() == 1) {
                        signalMapping.put(sortedSignal, 9);
                        valueMapping.put(9, sortedSignal);
                        wireMapping.put('G', dif.charAt(0));
                        break;
                    }
                }
            }

            // step 9:  there are 3 signals with length 6 (0, 6, 9) of which the signals for 6 and 9 are known
            // meaning that the unknown signal with length 6 represents 0
            for (int i = 0; i < sortedSignalPatterns.length; i++) {
                String sortedSignal = sortedSignalPatterns[i];
                if (sortedSignal.length() == 6) {
                    if (! signalMapping.keySet().contains(sortedSignal)) {
                        signalMapping.put(sortedSignal, 0);
                        valueMapping.put(0, sortedSignal);
                        break;
                    }
                }
            }

            // step 10: the only difference between the signal for 0 and 8 is segment D
            Character inputForD = difference(valueMapping.get(8), valueMapping.get(0)).charAt(0);
            wireMapping.put('D', inputForD);

            // step 10: we know have 9 entries in the wireMapping, the missing entry is for segment B
            // so if we take the signal for value 8 and 'substract' all known segments,
            // we are left with the input value for B
            String knownInputs = "";
            for (Character inputChar : wireMapping.values()) {
                knownInputs += inputChar;
            }
            Character inputForB = difference(valueMapping.get(8), knownInputs).charAt(0);
            wireMapping.put('B', inputForB);

            // step 11: if all is well, the wireMapping is now complete, meaning we can use it to fill
            // in missing entries in the valueMapping and signalMapping maps for the values 2 and 3
            String signalFor2 = "";
            for (int i = 0; i < PATTERN_2.length(); i++) {
                signalFor2 += wireMapping.get(PATTERN_2.charAt(i));
            }
            String sortedSignalFor2 = sortString(signalFor2);
            signalMapping.put(sortedSignalFor2, 2);
            valueMapping.put(2, sortedSignalFor2);

            String signalFor3 = "";
            for (int i = 0; i < PATTERN_3.length(); i++) {
                signalFor3 += wireMapping.get(PATTERN_3.charAt(i));
            }
            String sortedSignalFor3 = sortString(signalFor3);
            signalMapping.put(sortedSignalFor3, 3);
            valueMapping.put(3, sortedSignalFor3);

            // the end: fill in the values array
            String outputString = "";
            for (int i=0; i < sortedDisplaySignals.length; i++) {
                outputString += signalMapping.get(sortedDisplaySignals[i]);
            }
            output = Integer.parseInt(outputString);
//            System.out.println("--> " + outputString);
        }
    }

    private static String sortString(String inputString)
    {
        // Converting input string to character array
        char tempArray[] = inputString.toCharArray();

        // Sorting temp array using
        Arrays.sort(tempArray);

        // Returning new sorted string
        return new String(tempArray);
    }

    // remove the characters appearing in 'littleSting' from 'bigString'
    static String difference (String bigString, String littleString) {
        String bigRemainder = bigString;
        for (char c:littleString.toCharArray()) {
            bigRemainder = bigRemainder.replace(""+c, "");
        }
        return bigRemainder;
    }

}
