
public class InfoClient {
    public static void main(String args[]) {
		try {
			Info myInfo = (Info) NamingService.getObjectReference("MyInfoImpl:InfoImpl");
			int retVal = myInfo.get_temp("Timisoara");
			System.out.println("Returned value is: "+retVal);
			String retValS = myInfo.get_road_info(2);
			System.out.println("Returned value is: "+retValS);
			NamingService.sendStopMessage("MyInfoImpl:InfoImpl");
		} catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
    }

}