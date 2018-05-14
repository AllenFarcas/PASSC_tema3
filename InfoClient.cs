using System;

namespace CevaTema {
    public static class InfoClient {
	   
        public static void Main()
        {
	        const string name = "MyInfoImpl:InfoImpl";
	        try
	        {
		        var myInfo = (IInfo) NamingService.GetObjectReference(name);
		        var retVal1 = myInfo.get_temp("Timisoara");
		        Console.Write("get_temp(\"Timisoara\"): ");
		        Console.WriteLine(retVal1);
		        var retVal2 = myInfo.get_road_info(1);
		        Console.Write("get_road_info(1): ");
		        Console.WriteLine(retVal2);
		        var retVal3 = myInfo.get_road_info(3);
		        Console.Write("get_road_info(3): ");
		        Console.WriteLine(retVal3);
		        NamingService.SendStopMessage();
	        }
	        catch (Exception e) {
		        Console.WriteLine(e.ToString());
	        }
        }
    }
}