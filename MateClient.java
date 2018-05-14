public class MateClient {
    public static void main(String args[]) {
        try {
            Mate myMate = (Mate) NamingService.getObjectReference("MyMateImpl:MateImpl");
            double retValD = myMate.do_sqr(4);
            System.out.println("do_sqr(4): "+retValD);
            float retValF = myMate.do_add(2,10);
            System.out.println("do_add(2,10): "+retValF);
            int retValInt = myMate.do_diff(5,1);
            System.out.println("do_diff(5,1): "+retValInt);
            NamingService.sendStopMessage("MyMateImpl:MateImpl");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}