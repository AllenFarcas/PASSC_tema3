interface Info {
	int get_temp(String city);
	String get_road_info(int road_ID);
}

class InfoImpl implements Info {
	@Override
	public int get_temp(String city) {
		System.out.println("get_temp method is executing");
		return 20;
	}

	@Override
	public String get_road_info(int road_ID) {
		System.out.println("get_road_info method is executing");
		switch (road_ID) {
			case 1: return "Blocat";
			case 2: return "Liber";
			case 3: return "Aglomerat";
			default : return "Eroare";
		}
	}
}

public class InfoServer {
	public static void main(String args[]) {
		InfoImpl infoImpl = new InfoImpl();
		System.out.println("InfoServer main started");
		try {
			NamingService.registerMethod("MyInfoImpl", infoImpl);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
