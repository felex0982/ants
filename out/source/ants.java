import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ants extends PApplet {

// ANTS
// Felix Wagner http://maeoc.de
//
// Illustration by D!ve http://diveonreentry.com
// Flowfield and Particle-Behaviours taken from Daniel Shiffman http://codingtra.in

Animation ant;
int antCount = 1000;
FlowField flowfield;
ArrayList<Particle> particles;

boolean debug = false;

public void setup() {

  
  //size(800, 800);
  background(255);
  frameRate(15);
  ant = new Animation("ant_", 7);

  flowfield = new FlowField(10);
  flowfield.update();

  particles = new ArrayList<Particle>();
  for (int i = 0; i < antCount; i++) {
    PVector start = new PVector(random(width), random(height));
    particles.add(new Particle(start, random(2, 8)));
  }
}

public void draw() {   
  background(255);

  //flowfield.update();
  
  if (debug) flowfield.display();
  
  for (Particle p : particles) {
    p.follow(flowfield);
    p.run();
  }
}

// Class for animating a sequence of GIFs

class Animation {
  PImage[] images;
  int imageCount;
  int frame;
  
  Animation(String imagePrefix, int count) {
    imageCount = count;
    images = new PImage[imageCount];

    for (int i = 0; i < imageCount; i++) {
      // Use nf() to number format 'i' into four digits
      String filename = imagePrefix + nf(i, 4) + ".gif";
      images[i] = loadImage(filename);
    }
  }

  public void display(float xpos, float ypos) {
    frame = (frame+1) % imageCount;
    image(images[frame], -getWidth()/2+xpos, -getHeight()/2+ypos);
  }
  
  public int getWidth() {
    return images[0].width;
  }
  public int getHeight() {
    return images[0].height;
  }
}

public class Particle {
  PVector pos;
  PVector vel;
  PVector acc;
  PVector previousPos;
  float maxSpeed;
   
  Particle(PVector start, float maxspeed) {
    maxSpeed = maxspeed;
    pos = start;
    vel = new PVector(0, 0);
    acc = new PVector(0, 0);
    previousPos = pos.copy();
  }

  public void run() {
    update();
    edges();
    show();
  }

  public void update() {
    pos.add(vel);
    vel.limit(maxSpeed);
    vel.add(acc);
    acc.mult(0);
  }

  public void applyForce(PVector force) {
    acc.add(force); 
  }

  public void show() {
    stroke(0);
    strokeWeight(1);
    //line(pos.x, pos.y, previousPos.x, previousPos.y);
    float dx = previousPos.x - pos.x;
    float dy = previousPos.y - pos.y;
    float theta = atan2(dy, dx) - PI/2;
    //stroke(255,0,0);
    pushMatrix();
      translate(pos.x, pos.y);
      rotate(theta);
      ant.display(0,0);
      //line(0,0,0,20);
    popMatrix();
    updatePreviousPos();
  }

  public void edges() {
    if (pos.x > width) {
      pos.x = 0;
      updatePreviousPos();
    }
    if (pos.x < 0) {
      pos.x = width;    
      updatePreviousPos();
    }
    if (pos.y > height) {
      pos.y = 0;
      updatePreviousPos();
    }
    if (pos.y < 0) {
      pos.y = height;
      updatePreviousPos();
    }
  }

  public void updatePreviousPos() {
    this.previousPos.x = pos.x;
    this.previousPos.y = pos.y;
  }

  public void follow(FlowField flowfield) {
    int x = floor(pos.x / flowfield.scl);
    int y = floor(pos.y / flowfield.scl);
    int index = x + y * flowfield.cols;
    
    PVector force = flowfield.vectors[index];
    applyForce(force);
  }
}
// Daniel Shiffman
// http://youtube.com/thecodingtrain
// http://codingtra.in
//
// Coding Challenge #24: Perlin Noise Flow  Field
// https://youtu.be/BjoM9oKOAKY

public class FlowField {
  PVector[] vectors;
  int cols, rows;
  float inc = 0.1f;
  float zoff = 0;
  int scl;
  
  FlowField(int res) {
    scl = res;
    cols = floor(width / res) + 1;
    rows = floor(height / res) + 1;
    vectors = new PVector[cols * rows];
  }
  public void update() {
    float xoff = 0;
    for (int y = 0; y < rows; y++) { 
      float yoff = 0;
      for (int x = 0; x < cols; x++) {
        float angle = noise(xoff, yoff, zoff) * TWO_PI * 4;
        
        PVector v = PVector.fromAngle(angle);
        v.setMag(1);
        int index = x + y * cols;
        vectors[index] = v;
       
        xoff += inc;
      }
      yoff += inc;
    }
    zoff += 0.004f;
  }
  public void display() {
    for (int y = 0; y < rows; y++) { 
      for (int x = 0; x < cols; x++) {
        int index = x + y * cols;
        PVector v = vectors[index];
        
        stroke(0, 0, 0, 40);
        strokeWeight(0.1f);
        pushMatrix();
        translate(x * scl, y * scl);
        rotate(v.heading());
        line(0, 0, scl, 0);
        popMatrix();
      }
    }
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ants" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
