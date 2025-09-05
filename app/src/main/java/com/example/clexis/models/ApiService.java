package com.example.clexis.models;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.clexis.models.dto.ChangePasswordRequest;
import com.example.clexis.models.dto.CommunityStats;
import com.example.clexis.models.dto.LearningPathDto;
import com.example.clexis.models.dto.StudentProfileObject;
import com.example.clexis.models.entity.BuddyProgram;
import com.example.clexis.models.entity.Discussion;
import com.example.clexis.models.entity.LearningPath;
import com.example.clexis.models.entity.Notification;
import com.example.clexis.models.entity.Resource;
import com.example.clexis.models.entity.Student;
import com.example.clexis.models.entity.User;
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
    Call<ResponseDto<User>> getUser(@Path("userId") String userId);


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

    @GET("/api/v1/gateway/student/buddyProgram/get")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<List<BuddyProgram>>> getBuddyPrograms();


    @POST("/api/v1/gateway/student/discussion/add")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<Discussion>> addDiscussion(@Body Discussion newDiscussion);

    @GET("/api/v1/gateway/student/buddyProgram/get/{id}")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<BuddyProgram>> getBuddyProgram(@Path("id") String id);

    @PUT("/api/v1/gateway/student/buddyProgram/update")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<BuddyProgram>> updateBuddyProgram(@Body BuddyProgram program);

    @GET("/api/v1/gateway/student/profile/get/{userId}")
    @Headers("Content-Type: application/json")
    Call<ResponseDto<StudentProfileObject>> getStudentProfile(@Path("userId") String userId);

    @DELETE("/api/v1/gateway/student/learningPath/delete")
    Call<ResponseDto<Object>> deleteLearningPath();
    @GET("/api/v1/gateway/student/notification/get")
    Call<ResponseDto<List<Notification>>> getNotifications();

    @POST("/api/v1/gateway/student/changePassword")
    Call<ResponseDto<Student>> changePassword(ChangePasswordRequest request);

    @PUT("/api/v1/gateway/student/updateInfo")
    Call<ResponseDto<Student>> updateStudent(Student student);

    @PUT("/api/v1/gateway/student/user/updateInfo")
    Call<ResponseDto<User>> updateUser(User user);
}
