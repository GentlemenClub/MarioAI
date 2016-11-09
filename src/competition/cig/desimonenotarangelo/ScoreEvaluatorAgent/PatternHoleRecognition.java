package competition.cig.desimonenotarangelo.ScoreEvaluatorAgent;

public class PatternHoleRecognition {
    public static final String[] patterns = {"[-10][  0][-10]", "[-10][  0][  0][-10]", "[-10][  0][  0][  0][-10]"};
    public static final String terrainTile = "[-10]";

    public static void main(String[] args) {
        String[] matrix = {
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]",
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]",
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]",
                "[-10][  0][  0][  0][  0][  0][  0][  0][  0][-10][-10][-10][-10][  0][  0][  0][-10][-10][-10][-10][  0][  0]",
                "[-10][-10][-10][-10][  0][  0][  0][  0][  0][-10][-10][-10][-10][  0][  0][  0][-10][-10][-10][-10][-10][-10]",
                "[-10][-10][-10][-10][  0][  0][  0][  0][  0][-10][-10][-10][-10][  0][  0][  0][-10][-10][-10][-10][-10][-10]",
                "[-10][-10][-10][-10][-10][-10][-10][-10][-10][-10][-10][-10][-10][  0][  0][  0][-10][-10][-10][-10][-10][-10]",
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]" ,
                "[  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0][  0]"};
        int marioX = matrix[0].length() / 2, marioY = 11;

        PatternHoleMatchingResult result = getMatchedPosition(matrix, patterns, marioY);
        if (result.matchedRow != -1 && result.matchedColumn != -1) {
            System.out.println("X: " + result.matchedRow + ", Y: " + result.matchedColumn + ", Pattern found: " + PatternHoleRecognition.patterns[result.patternIndex]);

            System.out.println("isMarioOnTheHole: " + isMarioOnTheHole(marioX, marioY, result));
            System.out.println("isMarioBeforeTheHole: " + isMarioBeforeTheHole(marioX, marioY, result));
            System.out.println("isMarioAfterTheHole: " + isMarioAfterTheHole(marioX, marioY, result));
        }
    }

    public static boolean isMarioOnTheHole(int marioX, int marioY, PatternHoleMatchingResult result) {
        int holeStart = result.matchedColumn + PatternHoleRecognition.terrainTile.length();
        int holeEnd = result.matchedColumn + PatternHoleRecognition.patterns[result.patternIndex].length() - PatternHoleRecognition.terrainTile.length();
        /*String hole = matrix[result.matchedRow].substring(holeStart, holeEnd);
        System.out.println("Hole: " + hole);*/

        return marioX <= holeEnd && marioX >= holeStart;
    }

    public static boolean isMarioBeforeTheHole(int marioX, int marioY, PatternHoleMatchingResult result) {
        int holeStart = result.matchedColumn + PatternHoleRecognition.terrainTile.length();

        return marioX < holeStart;
    }

    public static boolean isMarioAfterTheHole(int marioX, int marioY, PatternHoleMatchingResult result) {
        int holeEnd = result.matchedColumn + PatternHoleRecognition.patterns[result.patternIndex].length() - PatternHoleRecognition.terrainTile.length();

        return marioX > holeEnd;
    }

    public static PatternHoleMatchingResult getMatchedPosition(String[] matrix, String[] patterns, int marioY) {
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < patterns.length; column++) {
                int matchedColumn = matrix[row].indexOf(patterns[column]);
                if ((matchedColumn != -1 && (row + 1 < matrix.length) && matrix[row + 1].indexOf(patterns[column]) != -1) ||
                    (matchedColumn != -1 && (row == marioY - 1))) {
                    return new PatternHoleMatchingResult(row, matchedColumn, column);
                }
            }
        }

        return new PatternHoleMatchingResult(-1,-1,-1);
    }

    public static class PatternHoleMatchingResult {
        public int matchedRow;
        public int matchedColumn;
        public int patternIndex;

        public PatternHoleMatchingResult(int matchedRow, int matchedColumn, int patternIndex) {
            this.matchedRow = matchedRow;
            this.matchedColumn = matchedColumn;
            this.patternIndex = patternIndex;
        }
    }
}
