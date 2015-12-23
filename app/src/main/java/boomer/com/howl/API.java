package boomer.com.howl;
import java.util.List;

import boomer.com.howl.Objects.Howl ;

import boomer.com.howl.Objects.UserProfile;
import boomer.howl.Constants;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

public interface API {
    @Headers(Constants.CONTENT_TYPE)
    @POST("/api/v1/login")
    Call<UserProfile> login(@Header("x-token") String token);

    @Headers(Constants.CONTENT_TYPE)
    @GET("/api/v1/howls")
    Call<List<Howl>> get_howls(@Header("x-token") String token);

    @Headers(Constants.CONTENT_TYPE)
    @GET("/api/v1/howls/{id}")
    Call<List<Howl>> get_feed(@Header("x-token") String token, @Path("id") String id);
}
