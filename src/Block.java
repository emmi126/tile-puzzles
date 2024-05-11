import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
    private int xCoord;
    private int yCoord;
    private int size; // height/width
    private int level;
    private int maxDepth;
    private Color color;

    private Block[] children; // {UR, UL, LL, LR}

    public static Random gen = new Random();

    public Block() {}

    public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
        this.xCoord=x;
        this.yCoord=y;
        this.size=size;
        this.level=lvl;
        this.maxDepth = maxD;
        this.color=c;
        this.children = subBlocks;
    }

    public Block(int lvl, int maxDepth) {
        this.level = lvl;
        this.maxDepth = maxDepth;
        this.size = 0;
        this.xCoord = 0;
        this.yCoord = 0;

        if (lvl < maxDepth && gen.nextDouble() < Math.exp(-0.25 * lvl)) {
            this.children = new Block[4];
            this.color = null;
            for (int i = 0; i < 4; i++) {
                this.children[i] = new Block(lvl + 1, maxDepth);
            }
        } else {
            this.children = new Block[0];
            int i = gen.nextInt(GameColors.BLOCK_COLORS.length);
            this.color = GameColors.BLOCK_COLORS[i];
        }
    }

    public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
        if (size <= 0) throw new IllegalArgumentException("Size should be positive");
        int halveSize = size;
        for (int i = 0; i < this.maxDepth - this.level; i++) {
            if (halveSize % 2 != 0) throw new IllegalArgumentException("Block size is invalid");
            halveSize /= 2;
        }

        this.size = size;
        this.xCoord = xCoord;
        this.yCoord = yCoord;

        if (this.children != null && this.children.length == 4) {
            int childSize = size / 2;
            this.children[0].updateSizeAndPosition(childSize, xCoord + childSize, yCoord);  // UR
            this.children[1].updateSizeAndPosition(childSize, xCoord, yCoord);  // UL
            this.children[2].updateSizeAndPosition(childSize, xCoord, yCoord + childSize);  // LL
            this.children[3].updateSizeAndPosition(childSize, xCoord + childSize, yCoord + childSize);  // LR
        }
    }

    public ArrayList<BlockToDraw> getBlocksToDraw() {
        ArrayList<BlockToDraw> drawBlocks = new ArrayList<>();
        helperGetBlocksToDraw(this, drawBlocks);
        return drawBlocks;
    }

    private void helperGetBlocksToDraw(Block block, ArrayList<BlockToDraw> drawBlocks) {
        if (block.children == null || block.children.length == 0) {
            drawBlocks.add(new BlockToDraw(block.color, block.xCoord, block.yCoord, block.size, 0));
            drawBlocks.add(new BlockToDraw(GameColors.FRAME_COLOR, block.xCoord, block.yCoord, block.size, 3));
        } else {
            for (Block child : block.children) {
                helperGetBlocksToDraw(child, drawBlocks);
            }
        }
    }

    public BlockToDraw getHighlightedFrame() {
        return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
    }

    public Block getSelectedBlock(int x, int y, int lvl) {
        if (lvl < this.level || lvl > this.maxDepth) throw new IllegalArgumentException("Level is out of bounds");

        if (x < this.xCoord || x >= (this.xCoord + this.size) || y < this.yCoord || y >= (this.yCoord + this.size)) {
            return null;
        }

        if (this.children == null || this.children.length == 0 || this.level == lvl) return this;

        int middleX = this.xCoord + this.size / 2;
        int middleY = this.yCoord + this.size / 2;
        if (x < middleX) {
            if (y < middleY) return this.children[1].getSelectedBlock(x, y, lvl);
            else return this.children[2].getSelectedBlock(x, y, lvl);
        } else {
            if (y < middleY) return this.children[0].getSelectedBlock(x, y, lvl);
            else return this.children[3].getSelectedBlock(x, y, lvl);
        }
    }

    public void reflect(int direction) {
        if (direction != 0 && direction != 1) throw new IllegalArgumentException("Invalid direction value");

        if (this.children != null && this.children.length == 4) {
            if (direction == 0) {
                swap(1, 2);
                swap(0, 3);
            } else {
                swap(1, 0);
                swap(2, 3);
            }
            this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);

            for (Block child : this.children) {
                child.reflect(direction);
            }
        }
    }

    private void swap(int i1, int i2) {
        Block tmp = this.children[i1];
        this.children[i1] = this.children[i2];
        this.children[i2] = tmp;
    }

    public void rotate(int direction) {
        if (direction != 0 && direction != 1) throw new IllegalArgumentException("Invalid rotation direction value");

        if (this.children != null && this.children.length == 4) {
            Block tmp;
            if (direction == 0) {
                tmp = this.children[3];
                this.children[3] = this.children[2];
                this.children[2] = this.children[1];
                this.children[1] = this.children[0];
                this.children[0] = tmp;
            } else {
                tmp = this.children[0];
                this.children[0] = this.children[1];
                this.children[1] = this.children[2];
                this.children[2] = this.children[3];
                this.children[3] = tmp;
            }
            this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);

            for (Block child : this.children) {
                child.rotate(direction);
            }
        }
    }

    public boolean smash() {
        if (this.level == 0 || this.level >= this.maxDepth) return false;

        this.children = new Block[4];
        for (int i = 0; i < this.children.length; i++) {
            this.children[i] = new Block(this.level + 1, this.maxDepth);
        }
        this.color = null;
        this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);

        return true;
    }

    public Color[][] flatten() {
        int size = (int) Math.pow(2, this.maxDepth);
        Color[][] arr = new Color[size][size];
        helperFlatten(this, arr, 0, 0, size);
        return arr;
    }

    private void helperFlatten(Block block, Color[][] arr, int startX, int startY, int size) {
        if (block.children != null && block.children.length == 4) {
            int childSize = size / 2;
            helperFlatten(block.children[0], arr, startX + childSize, startY, childSize);
            helperFlatten(block.children[1], arr, startX, startY, childSize);
            helperFlatten(block.children[2], arr, startX, startY + childSize, childSize);
            helperFlatten(block.children[3], arr, startX + childSize, startY + childSize, childSize);
        } else {
            for (int i = startX; i < startX + size; i++) {
                for (int j = startY; j < startY + size; j++) {
                    if (block.color != null) arr[j][i] = block.color;
                }
            }
        }
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getLevel() {
        return this.level;
    }

    public String toString() {
        return String.format("pos=(%d,%d), size=%d, level=%d"
                , this.xCoord, this.yCoord, this.size, this.level);
    }

    public void printBlock() {
        this.printBlockIndented(0);
    }

    private void printBlockIndented(int indentation) {
        String indent = "";
        for (int i=0; i<indentation; i++) {
            indent += "\t";
        }

        if (this.children.length == 0) {
            String colorInfo = GameColors.colorToString(this.color) + ", ";
            System.out.println(indent + colorInfo + this);
        } else {
            System.out.println(indent + this);
            for (Block b : this.children)
                b.printBlockIndented(indentation + 1);
        }
    }

    private static void coloredPrint(String message, Color color) {
        System.out.print(GameColors.colorToANSIColor(color));
        System.out.print(message);
        System.out.print(GameColors.colorToANSIColor(Color.WHITE));
    }

    public void printColoredBlock(){
        Color[][] colorArray = this.flatten();
        for (Color[] colors : colorArray) {
            for (Color value : colors) {
                String colorName = GameColors.colorToString(value).toUpperCase();
                if(colorName.length() == 0){
                    colorName = "\u2588";
                }else{
                    colorName = colorName.substring(0, 1);
                }
                coloredPrint(colorName, value);
            }
            System.out.println();
        }
    }

}

