package pathfinding;

import entity.Entity;
import main.GamePanel;

import java.util.ArrayList;

public class Pathfinder {
    GamePanel gp;
    Node[][] node;
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    boolean finished = false;
    int step = 0;

    public Pathfinder(GamePanel gp){
        this.gp = gp;
        instantiateNodes();
    }

    public void instantiateNodes(){

        // Initialize the 2D array of Node objects with dimensions based on the world columns and rows
        node = new Node[gp.maxWorldCol][gp.maxWorldRow];

        // Variables to keep track of the current column and row
        int col = 0;
        int row = 0;

        // Loop through all columns and rows to instantiate each Node object
        while(col < gp.maxWorldCol && row < gp.maxWorldRow){

            // Create a new Node at the current column and row
            node[col][row] = new Node(col, row);

            // Move to the next column
            col++;

            // If the end of the current row is reached, reset column to 0 and move to the next row
            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }
        }
    }

    // Resetting the nodes to its default values
    public void resetNodes(){
        // Column and row variables starting from the top left corner
        int col = 0;
        int row = 0;

        while(col < gp.maxWorldCol && row < gp.maxWorldRow){

            // Reset the properties of each node in the grid.
            node[col][row].open = false;
            node[col][row].solid = false;
            node[col][row].checked = false;

            col++;
            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }
        }

        // Clear the openList and pathList, which hold nodes being processed and the final path respectively
        openList.clear();
        pathList.clear();

        // Reset the state variables for pathfinding.
        finished = false; // Mark that the pathfinding process is not finished.
        step = 0; // Reset the step counter.
    }

    // Settings the nodes' properties
    public void setNodes(int startCol, int startRow, int goalCol, int goalRow, Entity entity){
        resetNodes(); // Reset all the settings

        // Set the start, current, and goal nodes
        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);

        // Initialize the column and row variables
        int col = 0;
        int row = 0;

        // Looping through all columns and rows
        while(col < gp.maxWorldCol && row < gp.maxWorldRow){

            // Get the tile number at the current position
            int tileNum = gp.tileM.mapTileNum[col][row];

            // Checking for solid tiles
            if(gp.tileM.tile[tileNum].collision){
                node[col][row].solid = true;
            }

            // Calculate the cost for each node
            getCost(node[col][row]);

            // Move to the next column
            col++;

            // If the end of the current row is reached, reset column to 0 and move to the next row
            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }

        }
    }

    // Finding the G, H, and F costs of the nodes
    private void getCost(Node node) {

        // Calculate the gCost (distance from the start node to the current node)
        int x = Math.abs(node.col - startNode.col); // Horizontal distance to start node
        int y = Math.abs(node.row - startNode.row); // Vertical distance to start node
        node.gCost = x + y;

        // Calculate the hCost (heuristic estimate from the current node to the goal node)
        x = Math.abs(node.col - goalNode.col); // Horizontal distance to goal node
        y = Math.abs(node.row - goalNode.row); // Vertical distance to goal node
        node.hCost = x + y;

        // Calculate the fCost which is the sum of G and H costs
        node.fCost = node.gCost + node.hCost;
    }

    // The main searching/pathfinding algorithm
    public boolean search() {
        // Continue searching until finished or a maximum number of steps is reached
        while(!finished && step < 500){

            // Initialize the column and row variables
            int col = currentNode.col;
            int row = currentNode.row;

            // Mark the current node as checked and remove it from the open list
            currentNode.checked = true;
            openList.remove(currentNode);

            // Open adjacent nodes (up, left, down, right) if within bounds

            if (row-1>=0){openNode(node[col][row-1]);} // Open the node above

            if (col-1 >= 0){ openNode(node[col-1][row]);} // Open the node to the left

            if (row+1<gp.maxWorldRow){ openNode(node[col][row+1]);} // Open the node below

            if (col+1 <gp.maxWorldCol){ openNode(node[col+1][row]);} // Open the node to the right

            // Initialize variables to find the best node in the open list
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            // Iterate through the open list to find the node with the lowest fCost
            for(int i = 0; i < openList.size(); i++){

                if(openList.get(i).fCost < bestNodefCost){
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }

                // If all fCost are the same, compare the gCost
                else if(openList.get(i).fCost == bestNodefCost){
                    if(openList.get(i).gCost < openList.get(bestNodeIndex).gCost){
                        bestNodeIndex = i;
                    }
                }
            }

            // If the open list is empty, exit the loop as no path is found
            if(openList.isEmpty()){
                break;
            }

            // Set the current node to the best node found
            currentNode = openList.get(bestNodeIndex);

            // If the goal node is reached, mark as finished and backtrack to create the path
            if(currentNode == goalNode){
                finished = true;
                backTrackingPath();
            }


            step++; // Increment the step counter
        }
        return finished; // Return whether the pathfinding is finished
    }

    // Opening the nodes
    public void openNode(Node node){
        // Check if the node is not already open, not checked, and not solid
        if(!node.open && !node.checked && !node.solid){

            node.open = true; // Mark the node as open

            node.parent = currentNode; // Set the parent of the node to the current node

            openList.add(node); // Add the node to the open list for further exploration

        }
    }

    // Backtracking using the shortest path from the goal node to the start node
    public void backTrackingPath(){
        // Start backtracking from the goal node
        Node current = goalNode;

        // Continue backtracking until the start node is reached
        while(current != startNode){
            pathList.add(0, current); // Add the current node to the beginning of the path list
            current = current.parent; // Move to the parent node
        }
    }
}
