import java.awt.Color;

public class PerimeterGoal extends Goal{

    public PerimeterGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        if (board == null) return 0;

        Color[][] arr = board.flatten();
        int score = 0;
        int size = arr.length;
        if (size == 1) return 2;

        for (int x = 0; x < size; x++) {
            if (arr[0][x] != null && arr[0][x].equals(targetGoal)) {
                if (x == 0 || x == size - 1) score += 2;
                else score += 1;
            }
            if (arr[size - 1][x] != null && arr[size - 1][x].equals(targetGoal)) {
                if (x == 0 || x == size - 1) score += 2;
                else score += 1;
            }
        }

        for (int y = 1; y < size - 1; y++) {
            if (arr[y][0] != null && arr[y][0].equals(targetGoal)) score += 1;
            if (arr[y][size - 1] != null && arr[y][size - 1].equals(targetGoal)) score += 1;
        }

        return score;
    }

    @Override
    public String description() {
        return "Place the highest number of " + GameColors.colorToString(targetGoal)
                + " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
    }

}

