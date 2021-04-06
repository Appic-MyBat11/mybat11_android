package com.img.mybat11.Api;


import com.img.mybat11.GetSet.CategoriesGetSet;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.GetSet.FinalQuizResultGetSet;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.JoinChallengeGetSet;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.GetSet.LeaderboardByUserGetSet;
import com.img.mybat11.GetSet.LeagueDetailsGetSet;
import com.img.mybat11.GetSet.LiveChallengesGetSet;
import com.img.mybat11.GetSet.MainLeaderboardGetSet;
import com.img.mybat11.GetSet.MatchListGetSet;
import com.img.mybat11.GetSet.MyQuizListGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.NotificationGetSet;
import com.img.mybat11.GetSet.PlayAnswersGetSet;
import com.img.mybat11.GetSet.PlayQuizGetSet;
import com.img.mybat11.GetSet.PlayerListGetSet;
import com.img.mybat11.GetSet.PlayerStatsGetSet;
import com.img.mybat11.GetSet.QuizListGetSet;
import com.img.mybat11.GetSet.ReferuserGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.SelectedTeamsGetSet;
import com.img.mybat11.GetSet.SeriesRankListGetSet;
import com.img.mybat11.GetSet.TransactionGetSet;
import com.img.mybat11.GetSet.VerificationGetSet;
import com.img.mybat11.GetSet.addAmountGetSet;
import com.img.mybat11.GetSet.avatarGetSet;
import com.img.mybat11.GetSet.bannersGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.GetSet.fantasyScorecardGetSet;
import com.img.mybat11.GetSet.msgStatusGetSet;
import com.img.mybat11.GetSet.offersGetSet;
import com.img.mybat11.GetSet.playerMatchStatsGetSet;
import com.img.mybat11.GetSet.seriesGetSet;
import com.img.mybat11.GetSet.versionGetSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

//    @GET("contestcategory")
//    Call<ArrayList<CategoriesGetSet>> contestcategory(@Header("Authorization") String Auth);
//
    @GET("getmatchlist")
    Call<ArrayList<MatchListGetSet>> matchList(@Header("Authorization") String Auth);

    @GET("getversion")
    Call<ArrayList<versionGetSet>> checkVersion();

    @GET("getmainbanner")
    Call<ArrayList<bannersGetSet>> getmainbanner(@Header("Authorization") String Auth);

    @GET("getContests")
    Call<ArrayList<CategoriesGetSet>> getContests(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("type") String type);

    @GET("getAllContestsByType")
    Call<ArrayList<challengesGetSet>> AllChallenges(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("type") String type);

    @GET("getContestByCategory")
    Call<ArrayList<challengesGetSet>> remain(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("category_id") String category_id, @Query("player_type") String player_type);

    @GET("getMyTeams")
    Call<ArrayList<MyTeamsGetSet>> getMyTeams(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("challengeid") int challengeid, @Query("type") String type);

    @GET("getMyTeams")
    Call<ArrayList<MyTeamsGetSet>> getMyTeams2(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("challengeid") int challengeid);

    @GET("getMyTeams")
    Call<ArrayList<MyTeamsGetSet>> getMyTeams1(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("type") String type);

    @GET("getUsableBalance")
    Call<ArrayList<JoinChallengeBalanceGetSet>> checkBalance(@Header("Authorization") String Auth, @Query("challengeid") String challengeid);

    @FormUrlEncoded
    @POST("joinleauge")
    Call<ArrayList<JoinChallengeGetSet>> JoinChallenge(@Field("matchkey") String matchkey, @Field("challengeid") String challengeid, @Header("Authorization") String Auth, @Field("teamid") String teamid);

    @GET("getMyTeams")
    Call<ArrayList<SelectedTeamsGetSet>> findJoinedTeam(@Query("matchkey") String matchkey, @Header("Authorization") String Auth);

    @GET("myjoinedleauges")
    Call<ArrayList<JoinedChallengesGetSet>> FindJoinedChallenges(@Query("matchkey") String matchkey, @Header("Authorization") String Auth);

    @GET("getallplayers")
    Call<ArrayList<PlayerListGetSet>> PlayersList(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("myteam")
    Call<ArrayList<MyTeamsGetSet>> myTeams(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("leaugesdetails")
    Call<ArrayList<LeagueDetailsGetSet>> leagueDetails(@Query("challengeid") String challengeid, @Header("Authorization") String Auth);

//    @GET("viewjointeam_get")
//    Call<ArrayList<teamsGetSet>> joinedTeams(@Header("Authorization") String Auth, @Query("challengeid") int challengeid);
//
    @GET("joinedmatches")
    Call<ArrayList<ContestMatchListGetSet>> ContestsmatchList(@Header("Authorization") String Auth);

    @GET("livescores")
    Call<ArrayList<LiveChallengesGetSet>> liveScores(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("challengeid") int challengeid);

    @GET("viewteam")
    Call<ArrayList<SelectedPlayersGetSet>> viewteam(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("teamid") String teamid, @Query("teamnumber") String teamnumber);

    @GET("dreamteam")
    Call<ArrayList<SelectedPlayersGetSet>> dreamteam(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("viewAvatar")
    Call<ArrayList<msgStatusGetSet>> vAvatar(@Header("Authorization") String Auth);

    @FormUrlEncoded
    @POST("request_withdrow")
    Call<ArrayList<msgStatusGetSet>> WithdrawCash(@Header("Authorization") String Auth, @Field("amount") String amount, @Field("withdrawFrom") String withdrawfrom, @Field("deviceid") String deviceid);

    @FormUrlEncoded
    @POST("addAvatar")
    Call<ArrayList<msgStatusGetSet>> AddAvatar(@Header("Authorization") String Auth, @Field("avatar_id") String avatar_id);

//    @FormUrlEncoded
//    @POST("request_withdrow")
//    Call<ArrayList<msgStatusGetSet>> WithdrawCash(@Header("Authorization") String Auth, @Field("amount") String amount, @Field("type") String type, @Field("withdrawfrom") String withdrawfrom, @Field("paytm_number") String paytm_number);

    @GET("mytransactions")
    Call<ArrayList<TransactionGetSet>> transactionList(@Header("Authorization") String Auth);

    @GET("allmatchplayers")
    Call<ArrayList<fantasyScorecardGetSet>> fantasyscorecards(@Header("Authorization") String Auth, @Query("matchkey") String matchkey);

    @GET("getjointeamplayers")
    Call<ArrayList<playerMatchStatsGetSet>> getjointeamplayers(@Header("Authorization") String Auth, @Query("matchkey") String matchkey, @Query("teamid") String teamid);

  //  @GET("getPlayerInfo")
 //   Call<ArrayList<PlayerStatsGetSet>> PlayerStats(@Header("Authorization") String Auth, @Query("playerid") String playerid, @Query("matchkey") String matchkey);

    @GET("getnotification")
    Call<ArrayList<NotificationGetSet>> Notification(@Header("Authorization") String Auth, @Query("start") int start, @Query("limit") int limit);

    @GET("getallseries")
    Call<ArrayList<seriesGetSet>> series(@Header("Authorization") String Auth);

    @GET("getleaderboard")
    Call<ArrayList<MainLeaderboardGetSet>> getleaderboard(@Header("Authorization") String Auth, @Query("series_id") int series_id);

    @GET("getleaderboardbyuser")
    Call<ArrayList<LeaderboardByUserGetSet>> getleaderboardbyuser(@Header("Authorization") String Auth, @Query("series_id") int series_id, @Query("userid") int userid);

    @GET("getreferuser")
    Call<ArrayList<ReferuserGetSet>> getreferuser(@Header("Authorization") String Auth);

    @GET("offerdeposits")
    Call<ArrayList<offersGetSet>> offers(@Header("Authorization") String Auth);

    // to check all the verification done or not //
    @GET("allverify")
    Call<ArrayList<VerificationGetSet>> allverify(@Header("Authorization") String Auth);

    @GET("addcashnew")
    Call<ArrayList<addAmountGetSet>> PaymentSucess(@Header("Authorization") String Auth);
//
//    @GET("addcashnew")
//    Call<ArrayList<addAmountGetSet>> PaymentSucess(@Header("Authorization") String Auth, @Query("amount") String amount, @Query("paymentby") String paymentby, @Query("returnid") String returnid, @Query("txnid") String txnid);

    @FormUrlEncoded
    @POST("invoicetransaction")
    Call<ArrayList<msgStatusGetSet>> invoicegenerate(@Header("Authorization") String Auth, @Field("transaction_id") String transaction_id);


    @GET("getPlayerInfo")
    Call<ArrayList<PlayerStatsGetSet>> PlayerStats(@Header("Authorization") String Auth, @Query("playerid") String playerid, @Query("matchkey") String matchkey);

    @GET("getavatar")
    Call<ArrayList<avatarGetSet>> profileAvatar(@Header("Authorization") String Auth);



    @GET("getquiz")
    Call<ArrayList<QuizListGetSet>> quizList(@Header("Authorization") String Auth);

    @GET("getquizcontest")
    Call<ArrayList<CategoriesGetSet>> getquizContests(@Header("Authorization") String Auth, @Query("quiz_id") String quiz_id);

    @GET("getquizallcontest")
    Call<ArrayList<challengesGetSet>> quizAllChallenges(@Header("Authorization") String Auth, @Query("quiz_id") String quiz_id);

    @GET("getQuizContestByCategory")
    Call<ArrayList<challengesGetSet>> quizremain(@Header("Authorization") String Auth, @Query("quiz_id") String quiz_id, @Query("category_id") String category_id);

    @GET("getQuizUsableBalance")
    Call<ArrayList<JoinChallengeBalanceGetSet>> quizcheckBalance(@Header("Authorization") String Auth, @Query("challengeid") String challengeid);

    @GET("quiz_leaugesdetails")
    Call<ArrayList<LeagueDetailsGetSet>> QuizContestDetails(@Query("challengeid") String challengeid, @Header("Authorization") String Auth);

    @GET("joinedQuiz")
    Call<ArrayList<MyQuizListGetSet>> joinedQuiz(@Header("Authorization") String Auth);

    @GET("quiz_myjoinedleauges")
    Call<ArrayList<JoinedChallengesGetSet>> FindJoinedQuizChallenges(@Query("quiz_id") String quiz_id, @Header("Authorization") String Auth);

    @GET("Quetionslist")
    Call<ArrayList<PlayQuizGetSet>> Questionslst(@Query("quiz_id") String quiz_id, @Header("Authorization") String Auth);

    @FormUrlEncoded
    @POST("giveQuestionAnswer")
    Call<ArrayList<msgStatusGetSet>> QuestionAnswer(@Header("Authorization") String Auth, @Field("questions") String questions, @Field("quiz_id") String quiz_id, @Field("time") String time, @Field("language") String language);

    @GET("quiz_livescores")
    Call<ArrayList<LiveChallengesGetSet>> qliveScores(@Header("Authorization") String Auth, @Query("challengeid") String challengeid, @Query("quiz_id") String quiz_id);

    @GET("quizFinalResult")
    Call<ArrayList<FinalQuizResultGetSet>> quizFinalResult(@Header("Authorization") String Auth, @Query("userid") String userid, @Query("quiz_id") String quiz_id);

}
