package com.example.doanmp3.Interface;

import com.example.doanmp3.Models.Album;
import com.example.doanmp3.Models.DetailSinger;
import com.example.doanmp3.Models.Genre;
import com.example.doanmp3.Models.Playlist;
import com.example.doanmp3.Models.ResultSearch;
import com.example.doanmp3.Models.Singer;
import com.example.doanmp3.Models.Slide;
import com.example.doanmp3.Models.Song;
import com.example.doanmp3.Models.UserPlaylistData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {

    @POST("testApi.php")
    Call<ArrayList<String>> testAPI(@Body ArrayList<String> strings);

    @GET("get4GenreAndTheme.php")
    Call<List<Genre>> get4GenreAndTheme();

    @GET("getAllGenreAndTheme.php")
    Call<List<List<Genre>>> getAllGenreAndTheme();


    @FormUrlEncoded
    @POST("search.php")
    Call<List<ResultSearch>> search(@Field("keyWord") String keyWord);

    @GET("getSlides.php")
    Call<List<Slide>> getSlides();

    @GET("getRandomAlbums.php")
    Call<List<Album>> getRandomAlbums();

    @GET("getRandomSingers.php")
    Call<List<Singer>> getRandomSingers();

    @GET("getRandomPlaylists.php")
    Call<List<Playlist>> getRandomPlaylists();

    @GET("getNewSongs.php")
    Call<List<Song>> getNewSongs();

    @FormUrlEncoded
    @POST("getSongsFromAlbumId.php")
    Call<List<Song>> getSongsFromAlbumId(@Field("id") String id);

    @FormUrlEncoded
    @POST("getSongsFromPlaylistId.php")
    Call<List<Song>> getSongsFromPlaylistId(@Field("id") String id);

    @FormUrlEncoded
    @POST("getDetailsSinger.php")
    Call<DetailSinger> getDetailsSinger(@Field("id") String id);

    @FormUrlEncoded
    @POST("loveSong.php")
    Call<Void> loveSong(@Field("idUser") String idUser, @Field("idSong") String idSong);

    @FormUrlEncoded
    @POST("getUserPlaylists.php")
    Call<List<Playlist>> getUserPlaylists(@Field("idUser") String idUser);


    @FormUrlEncoded
    @POST("addUserPlaylist.php")
    Call<Playlist> addUserPlaylist(@Field("idUser") String idUser, @Field("name") String name);

    @FormUrlEncoded
    @POST("updateUserPlaylist.php")
    Call<Playlist> updateUserPlaylist(@Field("id") String id, @Field("name") String name);

    @FormUrlEncoded
    @POST("updateUserPlaylist.php")
    Call<Playlist> updateUserPlaylist(@Field("id") String id, @Field("name") String name, @Field("thumbnail") String thumbnail);

    @FormUrlEncoded
    @POST("deleteUserPlaylist.php")
    Call<Playlist> deleteUserPlaylist(@Field("id") String id);

    @FormUrlEncoded
    @POST("getSongsOfUserPlaylist.php")
    Call<List<Song>> getSongsOfUserPlaylist(@Field("id") String id);

    @FormUrlEncoded
    @POST("getUserLoveSongs.php")
    Call<List<Song>> getUserLoveSongs(@Field("id") String id);

    @GET("getTopLoveSongs.php")
    Call<List<Song>> getTopLoveSongs();

    @GET("getTop50LoveSongs.php")
    Call<List<Song>> getTop50LoveSongs();

    @FormUrlEncoded
    @POST("searchSong.php")
    Call<List<Song>> searchSong(@Field("keyWord") String keyWord);

    @POST("updateSongOfUserPlaylist.php")
    Call<UserPlaylistData> updateSongOfUserPlaylist(@Body UserPlaylistData userPlaylistData);

    @FormUrlEncoded
    @POST("getCategoryPlaylist.php")
    Call<List<Playlist>> getCategoryPlaylist(@Field("id") String id);

    @FormUrlEncoded
    @POST("getThemePlaylist.php")
    Call<List<Playlist>> getThemePlaylist(@Field("id") String id);

    @FormUrlEncoded
    @POST("getUserFavoriteAlbum.php")
    Call<List<Album>> getUserFavoriteAlbums(@Field("id") String id);
}
