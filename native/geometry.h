#ifndef GEOMETRY_H
#define GEOMETRY_H

#include <math.h> // For sqrt() in distance calculation

// Define a constant (optional, but good practice for some values)
#define ORIGIN_X 0
#define ORIGIN_Y 0

// Structure definition for a 2D point
typedef struct {
    double x;
    double y;
} Point;

// Function declarations
Point createPoint(double x, double y);
void displayPoint(Point p);
double distanceToOrigin(Point p);
Point addPoints(Point p1, Point p2); // New function to add two points

#endif // GEOMETRY_H