interface StockMarket {
    float get_price (String numeCompanie, int numar, float suma, boolean tip);
}


class StockMarketImpl implements StockMarket {
    public float get_price (String numeCompanie, int numar, float suma, boolean tip) {
        if (tip) {
            if (numar % 2 == 0) {
                return suma;
            } else {
                return suma/10;
            }
        } else {
            if(numeCompanie.equals("ABC.SRL")) {
                return suma/100;
            } else {
                return suma/1000;
            }
        }
    }
}

class StockMarketServer  {
    public static void main(String[] args) {
        System.out.println("StockMarketServer main started");
        StockMarketImpl stockMarketImpl = new StockMarketImpl();
        try {
            NamingService.registerMethod("NASDAQ",stockMarketImpl);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

class StockMarketClient  {
    public static void main(String[] args) {
        try {
            StockMarket market=  (StockMarket) NamingService.getObjectReference("NASDAQ:StockMarketImpl");
            float price = market.get_price("ABC.SRL",3,1234567,false);
            System.out.println("Price is "+price);
        }
        catch (Exception e) {
            System.out.println("Exception !");
        }
    }
}