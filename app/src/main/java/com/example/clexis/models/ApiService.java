package com.example.clexis.models;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.clexis.models.dto.CommunityStats;
import com.example.clexis.models.dto.LearningPathDto;
import com.example.clexis.models.entity.Discussion;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.request.LoginRequest;
import com.example.clexis.models.request.RegisterRequest;
import com.example.clexis.models.response.LoginResponse;
import com.example.clexis.models.response.ResponseDto;

import java.util.List;

public interface ApiService {

    @POST("/api/v1/gateway/authentication/student/register")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Object>> registerUser(@Body RegisterRequest request);

    @GET("/api/v1/gateway/student/students/getInfo/{userId}")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Object>> getUser(@Path("userId") String userId);


    @GET("/api/v1/gateway/student/learningPath/get")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<LearningPath>> getLearningPath();

    @GET("/api/v1/gateway/student/resources/getValid")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<List<Resource>>> getLearningResources();

    @GET("/api/v1/gateway/student/socials/getDiscussions")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<List<Discussion>>> getCommunityDiscussions();

    @GET("/api/v1/gateway/student/socials/getStats")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<CommunityStats>> getCommunityStats();

    @PUT("/api/v1/gateway/student/learningPath/update")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<LearningPath>> updateLearningPath(@Body LearningPath learningPath);

    @PUT("/api/v1/gateway/student/social/updateDiscussion")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Discussion>> updateDiscussion(@Body Discussion discussion);

    @POST("/api/v1/gateway/student/learningPath/add")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<LearningPath>> addLearningPath(@Body LearningPathDto learningPath);

    @POST("/api/v1/gateway/student/resources/add")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Resource>> addResource(@Body Resource resource);


    @POST("/api/v1/gateway/authentication/login")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<LoginResponse>> loginUser(@Body LoginRequest request);

    @GET("/api/v1/gateway/student/students/getInfo/{userId}")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Student>> getStudent(@Path("userId") String userId);


    @POST("/api/v1/gateway/student/discussion/add")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Discussion>> addDiscussion(@Body Discussion newDiscussion);
}
