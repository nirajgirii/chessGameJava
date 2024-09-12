package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {

    public int x, y;
    public boolean pressed;

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;  // Set pressed to true when the mouse is pressed
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;  // Set pressed to false when the mouse is released
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Update the position while dragging
        x = e.getX();
        y = e.getY();
        pressed = true;  // Ensure that the mouse is considered "pressed" while dragging
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Update position when mouse is moved (not pressed)
        x = e.getX();
        y = e.getY();
    }
}
