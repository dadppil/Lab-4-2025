package functions;
import functions.meta.*;

public final class Functions {
    private Functions(){}
    public static Function shift(Function f, double shiftX, double shiftY){
        Shift shift = new Shift(f,shiftX,shiftY);
        return shift;
    }
    public static Function scale(Function f, double scaleX, double scaleY){
        Scale scale = new Scale(f,scaleX,scaleY);
        return scale;
    }
    public static Function power(Function f, double power){
        Power powerF = new Power(f,power);
        return powerF;
    }
    public static Function sum(Function f1, Function f2){
        Sum sum = new Sum(f1,f2);
        return sum;
    }
    public static Function mult(Function f1, Function f2){
        Mult mult = new Mult(f1,f2);
        return mult;
    }
    public static Function composition(Function f1, Function f2){
        Composition composition = new Composition(f1,f2);
        return composition;
    }
}
