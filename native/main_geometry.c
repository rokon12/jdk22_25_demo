#include <stdio.h>
#include "geometry.h" // Include our custom header file

// Function to create a Point
Point createPoint(double x, double y) {
    Point newPoint;
    newPoint.x = x;
    newPoint.y = y;
    return newPoint;
}

// Function to display a Point
void displayPoint(Point p) {
    printf("Point(x: %.2f, y: %.2f)\n", p.x, p.y);
}

// Function to calculate distance from origin
double distanceToOrigin(Point p) {
    // Distance formula: sqrt((x2-x1)^2 + (y2-y1)^2)
    // Here, (x1,y1) is the origin (ORIGIN_X, ORIGIN_Y)
    double deltaX = p.x - ORIGIN_X;
    double deltaY = p.y - ORIGIN_Y;
    return sqrt(deltaX * deltaX + deltaY * deltaY);
}

// Function to add two Points
Point addPoints(Point p1, Point p2) {
    Point result;
    result.x = p1.x + p2.x;
    result.y = p1.y + p2.y;
    return result;
}

int main() {
    Point p1, p2, sumPoint;
    double dist;

    // Create Point objects
    p1 = createPoint(3.0, 4.0);
    p2 = createPoint(-1.0, 2.5);

    printf("Point p1: ");
    displayPoint(p1);

    printf("Point p2: ");
    displayPoint(p2);

    // Calculate distance of p1 from origin
    dist = distanceToOrigin(p1);
    printf("Distance of p1 from origin: %.2f\n", dist);

    // Add p1 and p2
    sumPoint = addPoints(p1, p2);
    printf("Sum of p1 and p2: ");
    displayPoint(sumPoint);

    // Calculate distance of sumPoint from origin
    dist = distanceToOrigin(sumPoint);
    printf("Distance of sumPoint from origin: %.2f\n", dist);


    return 0; // Indicate successful execution
}

///gcc main_geometry.c -o geometry_program -lm
//clang -shared -O2 -o libcomplex.dylib main_geometry.c


//../jextract-22/bin/jextract -l complex                 \
//                 --include-function createPoint \
//                 --include-function displayPoint \
//                 --include-function   distanceToOrigin     \
//                 --include-function   addPoints     \
//                 --include-struct   Point       \
//                 -t ca.bazlur.math                 \
//                 --output ../src/main/java                 \
//                 ../native/geometry.h


../jextract-22/bin/jextract -l complex -t ca.bazlur.math   --output ../src/main/java ../native/geometry.h
