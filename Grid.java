// What: a quaternary tree containing containing up to one generic object and an arraylist.
// Author: Tracy Medcalf

import java.util.ArrayList;
//import java.util.ArrayDeque;
import java.lang.Math;

public class Grid<T> {
/* Class containing the quadtree meant to represent the grid */

   public float halfWidth; // The width (and height, because this is square) of this grid divided by 2
   public QuadTree tree;

public class QuadTree {
   //private double[] array;
   private ArrayList<T> list; // An arraylist containing
   private QuadTree upperLeft;
   private QuadTree upperRight;
   private QuadTree lowerLeft;
   private QuadTree lowerRight;
   
   public QuadTree (QuadTree upperLeft, QuadTree upperRight, QuadTree lowerLeft, QuadTree lowerRight) {
   /* Construct tree with four children. */
      this.upperLeft = upperLeft;
      this.upperRight = upperRight;
      this.lowerLeft = lowerLeft;
      this.lowerRight = lowerRight;
   }
   
   public QuadTree () {
      list = new ArrayList<T>();
   }
   
   public QuadTree (int numLevels) {
   /* Construct tree with the specified number of levels and a list at each of the leaves */
      numLevels--;
      //System.out.println("Created tree");
      if (numLevels > 1) {
         this.upperLeft = new QuadTree(numLevels);
         this.upperRight = new QuadTree(numLevels);
         this.lowerLeft = new QuadTree(numLevels);
         this.lowerRight = new QuadTree(numLevels);
      } else {
         this.upperLeft = new QuadTree();
         this.upperRight = new QuadTree();
         this.lowerLeft = new QuadTree();
         this.lowerRight = new QuadTree();
      }
   }
   
   @SuppressWarnings({"unchecked"})
   public void addToList(T thing) {
   /* Add an object to the list of the given tree. pre: tree has a list */
      assert list != null : "Tried to add to a tree without a list";
      //System.out.println("Added sphere");
      list.add(thing);
   }
   
   public ArrayList getList() {
      assert list != null : "Tried to get a null list.";
      return this.list;
   }
   
   public QuadTree findLeaf(Point p) {
   /* Call findLeaf with the x and y coordinates of a point */
      return findLeaf(p.x, p.y);
   }
   
   public QuadTree findLeaf(float x, float y) {
   /* pre: x and y co-ordinates. post: a tree representing the 64th of the grid in which the point is located */
      // if (x + y <= ll)
      if (x + y <= 0f) {
         // Point is in the lower left quadrant
         return lowerLeft.findLeafAux(x, y, -1 * halfWidth, -1 * halfWidth);
      } else if (x <= 0f) {
         // Point is in the upper left quadrant
         return upperLeft.findLeafAux(x, y, -1 * halfWidth, halfWidth); 
      } else if (y <= 0f) {
         // Point is in the lower right quadrant
         return lowerRight.findLeafAux(x, y, halfWidth, -1 * halfWidth);
      } else {
         // Point is in the upper right corner
         return findLeafAux(x, y, halfWidth, halfWidth);
      }
   }
   
   public QuadTree findLeafAux(float x, float y, float boxX, float boxY) {
   /* We compare the point x,y to the point at the upper left corner of each quadrant */
   
      if (!this.hasUpperLeft()) {
         // This tree has no children, therefore we have reached a leaf. 
         return this;
      }
   
      if (x + y <= boxX / 2 + boxY / 2) {
         // The point is in the lower left quadrant
         return lowerLeft.findLeafAux(x, y, boxX / 2, boxY);
      } else if (x <= boxX / 2) {
         // the point is in the upper left quadrant
         return upperLeft.findLeafAux(x, y, boxX / 2, boxY);
      } else if (y <= boxY / 2) {
         // the point is in the lower right quadrant
         return lowerRight.findLeafAux(x, y, boxX, boxY / 2);
      } else {
         // the point is in the upper right quadrant
         return upperRight.findLeafAux(x, y, boxX, boxY);
      }
   }
   
   public int getHeight() {
   /* pre: the tree is perfectly full */
      if (hasUpperLeft()) {
         return 1 + upperLeft.getHeight();
      }
      return 1;
   }
   
   public boolean hasUpperLeft() {
   /* Check if this tree has an upperleft child */
      if (this.upperLeft != null) {
         return true;
      }
      return false;
   }
}

   // Grid methods (and constructor)

   public Grid (int numLevels, float halfWidth) {
   /* Constructor */
      this.halfWidth = halfWidth;
      this.tree = new QuadTree(numLevels);
   }
   
   public static int roundUp(int a, int b) {
   /* Divide a by b and round up */
      if (a % b > 0) {
         return a / b + 1;
      }
      return a / b;
   }
   
}