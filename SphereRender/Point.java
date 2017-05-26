public class Point {

   /* Represents a point in 3Dimensional space */
      public float x;
      public float y;
      public float z;
      
      public Point(float x, float y, float z) {
      /* Construct a point object. */
         this.x = x;
         this.y = y;
         this.z = z;
      }
      
      public String toString() {
         return "x = " + x + " y = " + y + " z = " + z;
      }
      
      public Point() {
         z = Float.NEGATIVE_INFINITY;
      }
   }
