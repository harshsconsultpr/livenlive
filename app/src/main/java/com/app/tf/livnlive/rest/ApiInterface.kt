package mekotlinapps.dnyaneshwar.`in`.restdemo.rest

import com.app.tf.livnlive.model.ResponseCategory
import mekotlinapps.dnyaneshwar.`in`.restdemo.model.Response
import mekotlinapps.dnyaneshwar.`in`.restdemo.model.ResponseBottom
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Dnyaneshwar Dalvi on 22/11/17.
 */
interface ApiInterface {


    @POST("check_promo_code")
    @FormUrlEncoded
    fun checkPromo( @Field("user_id") userid: String,@Field("promo_code") promo: String): Call<Response>

    @POST("check_subscription")
    @FormUrlEncoded
    fun checkSubscription( @Field("user_id") userid: String): Call<Response>

    @GET("get_location_banner_category")
    fun getCategory( ): Call<ResponseCategory>

    @POST("get_bottom_banner_by_lat_long")
    @FormUrlEncoded
    fun bottom( @Field("latitude") latitude: String, @Field("longitude") longitude: String): Call<ResponseBottom>

    @POST("get_location_banner_by_lat_long")
    @FormUrlEncoded
    fun loc( @Field("latitude") latitude: String, @Field("longitude") longitude: String, @Field("categoryID") categoryID: String, @Field("miles") miles: String): Call<ResponseBottom>

}