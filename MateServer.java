import static java.lang.Math.*;

interface Mate {
    float do_add(float a, float b);
    double do_sqr(float a);
    int do_diff(int a, int b);
}

class MateImpl implements Mate {
    @Override
    public double do_sqr(float a) {
        System.out.println("do_sqr method is executing");
        return sqrt(a);
    }

    @Override
    public float do_add(float a, float b) {
        System.out.println("do_add method is executing");
        return a+b;
    }
    @Override
    public int do_diff(int a, int b){
        if (a<b){
            return b-a;
        }
        return a-b;
    }
}

public class MateServer {
    public static void main(String args[]) {
        MateImpl mateImpl = new MateImpl();
        System.out.println("MateServer main started");
        try {
            NamingService.registerMethod("MyMateImpl",mateImpl);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
