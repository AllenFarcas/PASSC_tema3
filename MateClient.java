public class MateClient {
    public static void main(String args[]) {
        try {
            Mate myMate = (Mate) NamingService.getObjectReference("MyMateImpl:MateImpl");
            double retValD = myMate.do_sqr(4);
            System.out.println("Returned value is: "+retValD);
            float retValF = myMate.do_add(2,10);
            System.out.println("Returned value is: "+retValF);
            int retValInt = myMate.do_diff(5,1);
            System.out.println("Returned value is: "+retValInt);
            NamingService.sendStopMessage("MyMateImpl:MateImpl");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}