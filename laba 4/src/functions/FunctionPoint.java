package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x;
    private double y;

    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y;
    }
    public FunctionPoint(FunctionPoint point){
        x = point.x;
        y = point.y;
    }
    FunctionPoint(){
        x = 0;
        y = 0;
    }
    public double getY(){
        return y;
    }
    public double getX(){
        return x;
    }
    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public void showPoint(){
        System.out.println("[" + x + "," + y + "]") ;
    }
}
