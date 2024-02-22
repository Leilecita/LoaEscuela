package com.example.loaescuela.network;

public class ApiUtils {

    private ApiUtils() {}

    //USAR HTTPS EN VEZ DE HTTP SIN LA con la S porque usamos el servidor loasurf



     public static final String BASE_URL = "https://school.loasurf.com.ar/";

    //public static final String BASE_URL = "http://192.168.0.36/loa_school/";
   // public static final String BASE_URL = "http://192.168.88.21/loa_school/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static APIService getAPISessionService() {
        return RetrofitClient.getSessionClient(BASE_URL).create(APIService.class);
    }

    public static final String getImageUrl(String imagePath){
        if(imagePath.startsWith("/")){
            imagePath = imagePath.replaceFirst("/","");
        }
        return BASE_URL + imagePath;
    }
}
