package wang.leal.moment.friend;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MomentApi {

    @POST("/v1/create")
    @FormUrlEncoded
    Observable<String> createMoment(@Field("lock_resource_id")String lockResourceId,@Field("open_resource_id")String openResourceId,@Field("location")String location,
                                    @Field("open_resource_type")String openType,@Field("lock_resource_type")String lockType,@Field("duration")long duration);

}
