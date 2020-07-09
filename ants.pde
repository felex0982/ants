// ANTS
// Felix Wagner http://maeoc.de
//
// Illustration by D!ve http://diveonreentry.com
// Flowfield and Particle-Behaviours taken from Daniel Shiffman http://codingtra.in

Animation ant;
int antCount = 200;
FlowField flowfield;
ArrayList<Particle> particles;

boolean debug = false;

void setup() {

  fullScreen();
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

void draw() {   
  background(255);

  flowfield.update();
  
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

  void display(float xpos, float ypos) {
    frame = (frame+1) % imageCount;
    image(images[frame], -getWidth()/2+xpos, -getHeight()/2+ypos);
  }
  
  int getWidth() {
    return images[0].width;
  }
  int getHeight() {
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

  void run() {
    update();
    edges();
    show();
  }

  void update() {
    pos.add(vel);
    vel.limit(maxSpeed);
    vel.add(acc);
    acc.mult(0);
  }

  void applyForce(PVector force) {
    acc.add(force); 
  }

  void show() {
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

  void edges() {
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

  void updatePreviousPos() {
    this.previousPos.x = pos.x;
    this.previousPos.y = pos.y;
  }

  void follow(FlowField flowfield) {
    int x = floor(pos.x / flowfield.scl);
    int y = floor(pos.y / flowfield.scl);
    int index = x + y * flowfield.cols;
    
    PVector force = flowfield.vectors[index];
    applyForce(force);
  }
}