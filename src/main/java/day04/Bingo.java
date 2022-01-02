package day04;

import util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Bingo {

    static boolean TEST = false;

    public static void main(String[] args) {
        String path = "src/main/resources/day04/bingo-input.txt";
        if (TEST) {
            path = "src/main/resources/day04/bingo-test-input.txt";
        }

        List<String> lines = FileUtil.readAllLines(path);

        // read drawn numbers
        int drawnNumbers[] = Arrays.stream(lines.get(0).split(",")).mapToInt(Integer::parseInt).toArray();

        doPart1(lines, drawnNumbers);
        doPart2(lines, drawnNumbers);
    }

    private static void doPart1(List<String> lines, int drawnNumbers[]) {

        // prepare the bingo cards
        Game game = prepare(lines);

        // lets play!
        Card winningCard = null;
        int winningNumber = -1;
        for (int i = 0; i < drawnNumbers.length && winningCard==null; i++) {
            winningNumber = drawnNumbers[i];
            winningCard = game.playNumber1(drawnNumbers[i]);
        }

        // print all cards
//        for (Card card:game.cards) {
//            card.print();
//        }

        // wrap-up
        if (winningCard != null) {
            // print winning card
            // winningCard.print();

            // print answer
            String appendix = TEST ? " (TEST!)" : "";
            int sumNonDrawnNumbers = winningCard.sumNonDrawnNumbers();
            int answer = winningNumber * sumNonDrawnNumbers;
            System.out.println("answer day 4, part #1 : " + answer + " (" + sumNonDrawnNumbers + " * " + winningNumber + ")" + appendix);
        } else {
            System.err.println("Oops, we do not have a winning card...");
        }
    }

    private static void doPart2 (List<String> lines, int drawnNumbers[]) {

        // prepare the bingo cards
        Game game = prepare(lines);

        // lets play!
        List<Card> lastWinningCards = null;
        int lastDrawnNumber = -1;
        boolean allCardsWon = false;
        for (int i = 0; i < drawnNumbers.length && !allCardsWon; i++) {
            lastDrawnNumber = drawnNumbers[i];
            lastWinningCards = game.playNumber2(drawnNumbers[i]);
            allCardsWon = game.haveAllCardsWon();
            if (allCardsWon) {
                if (lastWinningCards.size() > 1) {
                    System.err.println("Oops, we have multiple cards winning in the last round");
                }
                lastWinningCards.get(0).setLastWinner(true);
            }
        }

        // print all cards
//        for (Card card:game.cards) {
//            card.print();
//        }

        // wrap-up
        if (allCardsWon) {
            // print answer
            String appendix = TEST ? " (TEST!)" : "";
            int sumNonDrawnNumbers = lastWinningCards.get(0).sumNonDrawnNumbers();
            int answer = lastDrawnNumber * sumNonDrawnNumbers;
            System.out.println("answer day 4, part #2 : " + answer + " (" + sumNonDrawnNumbers + " * " + lastDrawnNumber + ")" + appendix);
        } else {
            System.err.println("Oops, we do not an all winning cards game... (last drawn number " + lastDrawnNumber + ")");
        }
    }

    public static Game prepare(List<String> lines) {
        Game game = new Game();

        // read bingo cards
        // sl = index of start line of a bingo card
        for (int sl = 2; sl < lines.size(); sl+=6){
            // process 5 lines starting from sl
            List<Number> numbersOnCard = new ArrayList<>();
            for(int i = sl; i < sl+5; i++) {
                //System.out.println("read line " + i + ": " + lines.get(i));
                // parse space separated line of number into an int array
                int numbers[] = Arrays.stream(lines.get(i).trim().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                // transform int array int List<Number>
                List<Number> numberList = Arrays.stream(numbers).mapToObj(n -> game.getNumber(n)).collect(Collectors.toList());
                //and add to the total list of number on the card
                numbersOnCard.addAll(numberList);
            }
            //System.out.println();
            game.addCard(new Card(numbersOnCard));
        }
        return game;
    }

    static public class Game {

        private List<Number> numberPool = initNumberPool();

        private List<Card> cards = new ArrayList<>();

        private List<Number> initNumberPool() {
            List<Number> pool = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                pool.add(new Number(i));
            }
            return pool;
        }

        public Number getNumber(int n) {
            return numberPool.get(n);
        }

        // part 1:
        // When the number returns in a winning card, that card is returned.
        // The first card checked which has a bingo is the winning card, even when there are more cards
        // how have bingo with this number.
        // Otherwise null is returned
        public Card playNumber1(int number) {
            // mark the number as drawn
            numberPool.get(number).setDrawn();

            // check the bingo cards which contain the drawn number
            // added for part 2: only play the bingo cards which have not won so far!
            List<Card> cards = numberPool.get(number).getCardsWithNumber();
            Card winningCard = null;
            for (Card card:cards) {
                if (!card.winner && card.hasBingo()) {
                    winningCard = card;
                    break;
                }
            }

            return winningCard;
        }

        // part 2:
        // When the number returns in winning cards, those cards are returned.
        // Otherwise null is returned
        public List<Card> playNumber2(int number) {
            // mark the number as drawn
            numberPool.get(number).setDrawn();

            // check the bingo cards which contain the drawn number
            // added for part 2: only play the bingo cards which have not won so far
            List<Card> cards = numberPool.get(number).getCardsWithNumber();
            List<Card> winningCards = new ArrayList<>();
            for (Card card:cards) {
                if (!card.winner && card.hasBingo()) {
                    winningCards.add(card);
                }
            }

            return winningCards;
        }

        public boolean haveAllCardsWon() {
            return cards.size() == cards.stream().filter(c -> c.winner).count();
        }

        public void addCard(Card card) {
            this.cards.add(card);
        }

        public List<Card> getCards() {
            return cards;
        }

        public List<Number> getNumberPool() {
            return numberPool;
        }
    }

    static public class Card {
        List<Number> numbers;
        boolean winner = false;
        boolean lastWinner = false;

        int rowDefinitions[][] = new int[][]{{0,1,2,3,4},{5,6,7,8,9},{10,11,12,13,14},{15,16,17,18,19},{20,21,22,23,24}};
        int columnDefinitions[][] = new int[][]{{0,5,10,15,20},{1,6,11,16,21},{2,7,12,17,22},{3,8,13,18,23},{4,9,14,19,24}};

        public Card(List<Number> numbers) {
            this.numbers = numbers;

            // let each number know that it is present on this card
            for (Number number:numbers) {
                number.addCardWithNumber(this);
            }
        }

        public boolean hasBingo() {
            boolean bingo = false;

            // check rows
            for (int i = 0; i < rowDefinitions.length && !bingo; i++) {
                bingo = allDrawn(rowDefinitions[i]);
            }

            // check columns
            for (int i = 0; i < columnDefinitions.length && !bingo; i++) {
                bingo = allDrawn(columnDefinitions[i]);
            }
            this.winner = bingo;
            return bingo;
        }

        // check if each of the card positions contains a drawn number
        private boolean allDrawn(int[] positions) {
            boolean allDrawn = true;
            for (int i = 0; i < positions.length && allDrawn; i++) {
                allDrawn = allDrawn && numbers.get(positions[i]).isDrawn();
            }
            return allDrawn;
        }

        public int sumNonDrawnNumbers() {
            return numbers.stream().filter(n -> !n.isDrawn()).map(Number::getValue)
                    .collect(Collectors.summingInt(Integer::intValue));
        }

        public void print() {
            if (lastWinner) {
                System.out.println("*** LAST WINNING CARD *******************************");
            }
            // r = row, c = column
            for (int r=0; r<5; r+=1) {
                StringBuffer buf = new StringBuffer();
                for (int c=0; c<5; c+=1) {
                    buf.append(numbers.get(r*5 + c).toString());
                }
                System.out.println(buf.toString());
            }
            System.out.println();
        }

        public boolean isLastWinner() {
            return lastWinner;
        }

        public void setLastWinner(boolean lastWinner) {
            this.lastWinner = lastWinner;
        }
    }

    static public class Number {
        int value;
        boolean drawn = false;

        List<Card> cardsWithNumber = new ArrayList<>();

        public Number(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setDrawn() {
            this.drawn = true;
        }

        public boolean isDrawn() {
            return this.drawn;
        }

        public void addCardWithNumber(Card card){
            cardsWithNumber.add(card);
        }

        public List<Card> getCardsWithNumber() {
            return cardsWithNumber;
        }

        public String toString() {
            String result = String.format("%1$3s", this.getValue());
            if (isDrawn()) {
                result += "*";
            } else {
                result += " ";
            }
            return result;
        }
    }
}
