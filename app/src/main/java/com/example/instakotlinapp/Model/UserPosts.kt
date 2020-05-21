package com.example.instakotlinapp.Model

class UserPosts {
    var userID:String?=null
    var postID:String?=null
    var postAciklama:String?=null
    var postURL:String?=null
    var userName:String?=null
    var userPhotoURL:String?=null
    var PostYuklemeTarihi:Long?=null

    constructor(userID: String?) {
        this.userID = userID
        this.userName=userName
        this.userPhotoURL=userPhotoURL
        this.postID=postID
        this.postAciklama=postAciklama
        this.postURL=postURL


    }

    constructor(PostYuklemeTarihi: Long?) {
        this.PostYuklemeTarihi = PostYuklemeTarihi
    }

    constructor(){

    }

    override fun toString(): String {
        return "UserPosts(userID=$userID, postID=$postID, postAciklama=$postAciklama, postURL=$postURL, userName=$userName, userPhotoURL=$userPhotoURL, PostYuklemeTarihi=$PostYuklemeTarihi)"
    }


}