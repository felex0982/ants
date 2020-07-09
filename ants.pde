Animation ant;

float xpos;
float ypos;
float drag = 30.0;

void setup() {
  size(800, 800);
  background(255);
  frameRate(15);
  ant = new Animation("ant_", 7);
  ypos = height * 0.25;
}

void draw() {   
  background(255);
  ant.display(100, 100);
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
    image(images[frame], -getWidth()+xpos, -getHeight()+ypos);
  }
  
  int getWidth() {
    return images[0].width;
  }
  int getHeight() {
    return images[0].height;
  }
}