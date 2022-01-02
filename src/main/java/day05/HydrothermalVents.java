package day05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HydrothermalVents {

    static boolean TEST = false;

    public static void main(String[] args) {
        String path = "src/main/resources/day05/vents-input.txt";
        if (TEST) {
            path = "src/main/resources/day05/vents-test-input.txt";
        }

        doPart1(path);
        doPart2(path);
    }

    public static void doPart1(String path) {
        // initialize
        VentsMap ventsMap = new VentsMap();
        VentsInterpreter interpreter = new VentsInterpreter(ventsMap);

        // process input
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String ventLine;
            while ((ventLine = br.readLine()) != null) {
                interpreter.processLine(ventLine, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // print answer
        String appendix = TEST ? " (TEST!)" : "";
        long answer = ventsMap.countHighWindCoordinates();
        System.out.println("answer day 5, part #1 : " + answer + appendix);
    }

    public static void doPart2(String path) {
        // initialize
        VentsMap ventsMap = new VentsMap();
        VentsInterpreter interpreter = new VentsInterpreter(ventsMap);

        // process input
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String ventLine;
            while ((ventLine = br.readLine()) != null) {
                interpreter.processLine(ventLine, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // print answer
        String appendix = TEST ? " (TEST!)" : "";
        long answer = ventsMap.countHighWindCoordinates();
        System.out.println("answer day 5, part #2 : " + answer + appendix);
    }

    public static class VentsInterpreter {
        VentsMap ventsMap;

        public VentsInterpreter(VentsMap ventsMap) {
            this.ventsMap = ventsMap;
        }

        // sample: 0,9 -> 5,9
        // 'includeDiagonal' parameter added to support part #2
        public void processLine(String line, boolean includeDiagonal) {
            String coordinates[] = line.split(" -> ");
            String from[] = coordinates[0].split(",");
            String to[] = coordinates[1].split(",");
            int xFrom = Integer.parseInt(from[0]);
            int yFrom = Integer.parseInt(from[1]);
            int xTo = Integer.parseInt(to[0]);
            int yTo = Integer.parseInt(to[1]);

            // horizontal line
            if (xFrom == xTo) {
                int ySmall = Math.min(yFrom, yTo);
                int yLarge = Math.max(yFrom, yTo);
                for (int y = ySmall; y<=yLarge; y++) {
                    ventsMap.registerWindOnCoordinate(xFrom, y);
                }
            }

            // vertical line
            else if (yFrom == yTo) {
                int xSmall = Math.min(xFrom, xTo);
                int xLarge = Math.max(xFrom, xTo);
                for (int x = xSmall; x<=xLarge; x++) {
                    ventsMap.registerWindOnCoordinate(x, yFrom);
                }
            }

            else if (includeDiagonal && Math.abs(xFrom-xTo) == Math.abs(yFrom-yTo)) {
                int xStep = 1;
                if (xTo < xFrom) xStep = -1;
                int yStep = 1;
                if (yTo < yFrom) yStep = -1;
                int distance = Math.abs(xFrom-xTo);
                for (int i = 0; i<=distance; i++) {
                    ventsMap.registerWindOnCoordinate(xFrom + i*xStep, yFrom + i*yStep);
                }
            }
        }
    }

    public static class VentsMap {
        private Map<String, Integer> ventsMap = new HashMap();

        public void registerWindOnCoordinate(int x, int y) {
            String key = x + "-" + y;
            Integer coordinateCounter = ventsMap.get(key);
            if (coordinateCounter == null) {
                coordinateCounter = 0;
            };
            ventsMap.put(key, ++coordinateCounter);
        }

        // a high wind coordinate is a coordinate with a value equal to or higher than 2
        public long countHighWindCoordinates() {
            return ventsMap.values().stream().filter(v -> v>=2).count();
        }
    }
}
