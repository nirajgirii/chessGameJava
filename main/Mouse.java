package main;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Mouse extends MouseAdapter {
    public int x, y;
    public ConcurrentLinkedQueue<Point> clickQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        clickQueue.offer(new Point(x, y));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    public boolean hasClick() {
        return !clickQueue.isEmpty();
    }

    public Point getNextClick() {
        return clickQueue.poll();
    }
}