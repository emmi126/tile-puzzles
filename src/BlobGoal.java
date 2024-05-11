import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        if (board == null) return 0;

        Color[][] arr = board.flatten();
        boolean[][] visited = new boolean[arr.length][arr[0].length];

        int maxBlobSize = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int currBlobSize = undiscoveredBlobSize(i, j, arr, visited);
                maxBlobSize = Math.max(maxBlobSize, currBlobSize);
            }
        }
        return maxBlobSize;
    }

    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }


    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
        if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length ||
                visited[i][j] || unitCells[i][j] == null || !unitCells[i][j].equals(targetGoal)) {
            return 0;
        }
        visited[i][j] = true;
        int blobSize = 1;

        blobSize += undiscoveredBlobSize(i - 1, j, unitCells, visited);
        blobSize += undiscoveredBlobSize(i + 1, j, unitCells, visited);
        blobSize += undiscoveredBlobSize(i, j - 1, unitCells, visited);
        blobSize += undiscoveredBlobSize(i, j + 1, unitCells, visited);

        return blobSize;
    }

}
