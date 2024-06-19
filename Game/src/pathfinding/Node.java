package pathfinding;

public class Node {
    Node parent;

    // LOCATION
    public int col;
    public int row;

    // COSTS
    int gCost;
    int hCost;
    int fCost;

    // STATUS
    boolean solid;
    boolean open;
    boolean checked;

    public Node(int col, int row){
        this.col = col;
        this.row = row;
    }
}
