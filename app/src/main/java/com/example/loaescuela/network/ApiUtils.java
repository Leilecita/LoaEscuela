package com.example.loaescuela.network;

public class ApiUtils {

    private ApiUtils() {}

     //public static final String BASE_URL = "http://school.loasurf.com.ar/";

    public static final String BASE_URL = "http://192.168.0.36/loa_school/";
   // public static final String BASE_URL = "http://192.168.52.198/loa_school/";

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
