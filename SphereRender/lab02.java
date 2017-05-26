import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.Math;
import java.io.File;
import java.io.FileNotFoundException;

// Author: Tracy Medcalf

@SuppressWarnings("serial")
public class lab02 extends Frame {
   static final int WIDTH = 512; // The width is also the height.
   static final int HEIGHT = WIDTH;
   static Point camera; // 3D point representing the camera
   static int d; // Camera distance
   static Point light; // Light source
   static Point normalLight; // The light vector but normalized
   static Color backgroundColor;
   static Grid<Sphere> grid;
   static float imagePlaneSize = 10; // greatest value on the x-axis of the image plane
   
   public static void main(String args[]) throws FileNotFoundException {
      backgroundColor = new Color(.9f, 1.0f,0.5f);
      // Get the file and process the spheres into a grid tree
      Grid<Sphere> grid = processSpheres(processWorldFile(args[0]));
      new lab02();
      //System.out.println("dot product" + dotProduct(new Point(1,2,3), new Point(4,-5,6)));
   }
   
   public void paint(Graphics g) {
   /* Cast Graphics object to Graphics2D */
      //for each pixel in the image
      for (int u = 0 ; u < WIDTH; u++) {
         for (int v = 0 ; v < HEIGHT; v++) {
            // find the ray from the camera.
            Ray rayFromCam = new Ray(u, v);
            // Go to the region of grid that ray intersects, get the list at that region, and get an iterator of the elements in list
            Iterator<?> sphereIter = grid.tree.findLeaf(rayFromCam.v).getList().iterator();
            // Create 2 spheres with z values of NEGATIVE_INFINITY
            Sphere nearestSphere = new Sphere();
            Point nearestPoint = new Point();
            // for each sphere in the region
            while (sphereIter.hasNext()) {
               // intersect the ray with the sphere
               Sphere s = (Sphere)sphereIter.next();
               Ray pairOfIntersectionPts = new Ray(rayFromCam,s);
               // Keep the point and sphere with the largest z value
               //System.out.println("x y " + u + " " + v + " pairOfIntersectionPts.p " + pairOfIntersectionPts.p.toString());
               if (pairOfIntersectionPts.p.z > nearestPoint.z) {
               //if (pairOfIntersectionPts.p.z > nearestPoint.z) {
                  // This is the point with the LARGEST z value so far encountered
                  nearestPoint = pairOfIntersectionPts.p;
                  nearestSphere = s; // Keep the sphere
               } else if (pairOfIntersectionPts.v.z > nearestPoint.z) {
                  nearestPoint = pairOfIntersectionPts.v;
                  nearestSphere = s;
               }
            }
            // if there were no intersections
            if (nearestSphere.c.z == Float.NEGATIVE_INFINITY) {
               // put the background color in the pixel
               g.setColor(backgroundColor);
               g.drawLine(u,v,u,v);
            } else {
               // color the pixel with Lambertian shading of the intersection point.
               // Normalize the vector
               g.setColor(calcLambertianShading(nearestPoint, nearestSphere));
               g.drawLine(u,v,u,v);
            }
         }
      }
   }
   
   public static Color calcLambertianShading(Point point, Sphere sphere) {
   /* Return the color for the sphere at a given point */
      // To get the surface normal of the point on the sphere, n = normalize(point - sphere.center)
      Point diff = vectorSubtract(point, sphere.c);
      Point normalized = normalize(diff);
      // Get the dot product of the normalized vector and the light vector
      float dotProd = dotProduct(normalized, normalLight);
      if (dotProd < 0) {
         // If the dot product is < 0 then we want black
         return new Color(0.0f, 0.0f, 0.0f);
      }
      if (dotProd > 1.0f) {
         return backgroundColor;
      }
      // Otherwise we multiply the number by the color of the sphere and return
      // Reminder: Color(red, green, blue)
      return new Color(sphere.color.red * dotProd, sphere.color.green * dotProd, sphere.color.blue * dotProd);
   }
   
   public static ArrayDeque<Sphere> processWorldFile(String fileName) throws FileNotFoundException {
   /* Read the .wld file
   Create spheres, initiate camera, initiate light */
      ArrayDeque<Sphere> sphereList = new ArrayDeque<Sphere>();
      Scanner input = new Scanner(new File(fileName));
      input.next();
      d = input.nextInt(); // Get cameras location along z-axis from file (camera distance)
      camera = new Point(0f, 0f, 1.0f * d);
      //camera = new Point(0f, 0f, 0f);
      input.next();
      // Initiate light source
      light = new Point(input.nextFloat(), input.nextFloat(), input.nextFloat());
      // Initiate normalLight (normalized, or unit vector, light). Will be useful in calcLambertianShading()
      normalLight = normalize(light);
      // Sphere input format:  x  y  z  radius red green blue
      while (input.hasNext()) {
         input.next();
         // Build the spheres 1 by 1
         // Get the six values needed to build this sphere
         Sphere s = new Sphere(input.nextFloat(), input.nextFloat(), input.nextFloat(), input.nextFloat(), input.nextFloat(), input.nextFloat(), input.nextFloat());
         sphereList.push(s);
      }
      return sphereList;
   }
   
   public static Grid<Sphere> processSpheres(ArrayDeque<Sphere> sphereList) {
   /* Assign each sphere to one or more quadrants (represented by a node of QuadTree) based on location */
      grid = new Grid<Sphere>(4, imagePlaneSize); // Binary tree with 64 leaves
      float widthOfLeaf = imagePlaneSize / 4.0f; // The side length of the box represented by each of the leaves in grid
      // while sphereList has a sphere
      while (!sphereList.isEmpty()) {
         // Get a sphere from the stack
         Sphere tSphere = sphereList.pop();
         // Calcuate the bounding box of this sphere
         float[] box = getBoundingBox(tSphere);
         // boundingBoxList.push(box)
         // Insert each sphere into each leaf (region) that it is belongs in
         float lenX = Math.abs(box[2] - box[0]); // < Length of the x-axis of the bounding box
         //System.out.println("lenX " + lenX);
         int multiplierX = 0;
         
         while (lenX >= 0) {
         // Walk through the bounding box at steps equal to widthOfLeaf and insert at each point
            //System.out.println("Walking through box.");
            // reset the length of the y-axis at the beginning of each loop
            float lenY = Math.abs(box[3] - box[1]);
            int multiplierY = 0;
            while (lenY >= 0) {
               // Calculate a point and insert into grid
               // .findLeaf(x, y).addToList(sphere);
               grid.tree.findLeaf(box[2] - widthOfLeaf * multiplierX, box[3] - widthOfLeaf * multiplierY).addToList(tSphere);
               // Increment multiplier, decrement lenX and lenY
               multiplierY++;
               lenY -= widthOfLeaf;
            }
            multiplierX++;
            lenX -= widthOfLeaf;
         }
      }
      return grid;
   }
   
   public static float[] getBoundingBox(Sphere s) {
   /* If a ray does not intersect this box, we know that it cannot hit the sphere */
      
      // Calculate the x values
      double aX = Math.sqrt(s.c.x * s.c.x + (s.c.z - d) * (s.c.z - d) );
      double thetaX = Math.atan(s.radius / aX);
      double phiX = Math.asin(s.c.x / aX) - thetaX;
      double x1 = d * Math.tan(phiX);
      double x2 = d * Math.tan(phiX + 2 * thetaX);
      
      // Calculate the y values
      double a = Math.sqrt(s.c.y * s.c.y + (s.c.z - d) * (s.c.z - d) );
      double theta = Math.atan(s.radius / a);
      double phi = Math.asin(s.c.y / a) - theta;
      double y1 = d * Math.tan(phi);
      double y2 = d * Math.tan(phi + 2 * theta);
      
      // List containing the co-ordinates of the bounding box: x1, y1, x2, y2  
      float[] box = {(float)x1, (float)y1, (float)x2, (float)y2};
      return box;
   }
   
   public lab02() {
   /* Constructor for a lab02 object
   (which is implicitly a world object) */
      
      // Title our frame
      super("Java 2D lab01");
      
      // Set the size for the frame
      setSize(WIDTH, HEIGHT);
      
      // Turn on visibility for our frame
      setVisible(true);
      
      // Dispose of resources used by this frame
      addWindowListener(new WindowAdapter()
         {public void windowClosing(WindowEvent e)
            {dispose(); System.exit(0);}
          }
          );
   }
   
   public static Point vectorAdd(Point a, Point b) {
      return new Point(a.x + b.x, a.y + b.y, a.z + b.z);
   }
   
   public static Point vectorSubtract(Point minuend, Point subtrahend) {
      return new Point(minuend.x - subtrahend.x, minuend.y - subtrahend.y, minuend.z - subtrahend.z);
   }
   
   public static float dotProduct(Point vec1, Point vec2) {
   /* Return the dot product of two vectors */
      return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z; 
   }
   
   public static Point scalarXVector(float a, Point v) {
   /* Return the product of a scalar, a, and a vector, v */
      return new Point(a * v.x, a * v.y, a * v.z);
   }
   
   public static Point normalize(Point vector) {
   /* Normalize the vector */
      float vectorLength = (float)Math.sqrt((double)dotProduct(vector, vector));
      return new Point(vector.x / vectorLength, vector.y / vectorLength, vector.z / vectorLength);
   }
   
   public class Ray {
   /* Represents a ray (vector) from the camera to the screen (co-ordinate plane). */
      public Point p; // Camera point
      public Point v; // Point on canvas
      
      public Ray (int x, int y) {
      /* Constructor for ray consisting of p and normalized vector
      pre: u and v coordinates of a pixel */
         this.p = camera;
         //float imagePlaneSize = 10;
         // Point on the image plane
         Point imagePlanePoint = new Point((imagePlaneSize * (2 *((float)x) / WIDTH - 1)),-1 * imagePlaneSize * (2 * ((float)y) / HEIGHT - 1), 0);
         // Normalize(q-p)
         Point diff = vectorSubtract(imagePlanePoint, p); // < The difference between camera p and point on canvas q
         float vectorLen = (float)Math.sqrt(dotProduct(diff, diff)); // < The length of the vector u
         this.v = new Point(diff.x / vectorLen, diff.y / vectorLen, diff.z / vectorLen); // < The normalized (unit) vector, v
      }
      
      public Ray(Point p, Point v) {
         this.p = p;
         this.v = v;
      }
      
      public Ray(Ray r, Sphere sphere) {
      /* Construct an object consisting of the two points where r intersects this sphere */
         // Make a new ray where v = r.v - sphere.c
         Ray ray = new Ray(vectorSubtract(r.p,sphere.c), r.v);
         // Find a, b, and c for a quadratic equation
         double a = dotProduct(ray.v, ray.v);
         double b = 2 * dotProduct(ray.p, ray.v); // dot product of the camera point and v
         double c = dotProduct(ray.p, ray.p) - sphere.radius * sphere.radius; // dot product of the camera point - the radius of the sphere squared
         
         // Solve quadratic at^2 + bt + c using quadratic formula
         float quadSolution1 = (float)((-1 * b - Math.sqrt(b * b - 4 * a * c)) / (2 * a)); // t1
         float quadSolution2 = (float)((-1 * b + Math.sqrt(b * b - 4 * a * c)) / (2 * a)); // t2
         
         // plug the two solutions into the expression p + tv
         this.p = vectorAdd(r.p, scalarXVector(quadSolution1, r.v));
         this.v = vectorAdd(r.p, scalarXVector(quadSolution2, r.v));

      }
   }
}
