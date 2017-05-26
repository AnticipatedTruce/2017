import java.awt.Color;

public class Sphere {
   
   // Each sphere has a center point c, a radius, and a color.
   public Point c;
   public float radius;
   public sColor color;
   
   public class sColor {
   /* Color for this sphere */
      float red;
      float green;
      float blue;
      
      public sColor(float red, float green, float blue) {
         this.red = red;
         this.green = green;
         this.blue = blue;
      }
   }
   
   public Sphere(float x, float y, float z, float radius, sColor color) {
   /* Construct a sphere centered on the given x, y, z and with the given radius and color */
      this.c = new Point(x,y,z);
      this.radius = radius;
      this.color = color;
   }
   
   public Sphere(float x, float y, float z, float radius, float red, float green, float blue) {
   /* Construct a sphere centered on the given x, y, z and with the given radius and color */
      this.c = new Point(x,y,z);
      this.radius = radius;
      this.color = new sColor(red, green, blue);
   }
   
   public Sphere() {
      c = new Point();
   }
}
