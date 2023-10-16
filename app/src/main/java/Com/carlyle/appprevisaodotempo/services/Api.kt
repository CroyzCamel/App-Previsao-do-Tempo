package Com.carlyle.appprevisaodotempo.services

import Com.carlyle.appprevisaodotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Api {
    @GET("weather")

    fun weatherMap(
        @Query("q") cityName:String,
        @Query("appid") api_key:String
     ): Call<Main>

}