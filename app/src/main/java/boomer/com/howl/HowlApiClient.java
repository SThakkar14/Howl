package boomer.com.howl;

import java.util.List;

import boomer.com.howl.Objects.Howl;
import boomer.com.howl.Objects.HowlCommentBody;
import boomer.com.howl.Objects.HowlCommentResponse;
import boomer.com.howl.Objects.ResponseStatus;
import boomer.com.howl.Objects.UserProfile;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface HowlApiClient {
    @Headers(Constants.CONTENT_TYPE)
    @POST("/api/v1/login")
    Call<UserProfile> login(@Header("x-token") String token);

    @Headers(Constants.CONTENT_TYPE)
    @GET("/api/v1/howls")
    Call<List<Howl>> get_howls(@Header("x-token") String token);

    @Headers(Constants.CONTENT_TYPE)
    @GET("/api/v1/howls/{id}")
    Call<List<Howl>> get_feed(@Header("x-token") String token, @Path("id") String id);

    @Headers(Constants.CONTENT_TYPE)
    @POST("/api/v1/howls/{howl_id}/comment")
    Call<HowlCommentResponse> post_comment(@Header("x-token") String token, @Path("howl_id") String howl_id, @Body HowlCommentBody howlCommentBody);

    @Headers(Constants.CONTENT_TYPE)
    @PUT("/api/v1/howls/{id}/follow")
    Call<ResponseStatus> follow_a_feed(@Header("x-token") String token, @Path("id") String id);

    @Headers(Constants.CONTENT_TYPE)
    @DELETE("/api/v1/howls/{id}/follow")
    Call<ResponseStatus> unfollow_a_feed(@Header("x-token") String token, @Path("id") String id);

    @Headers(Constants.CONTENT_TYPE)
    @GET("/api/v1/follow")
    Call<List<Howl>> get_following_feeds(@Header("x-token") String token);
}
