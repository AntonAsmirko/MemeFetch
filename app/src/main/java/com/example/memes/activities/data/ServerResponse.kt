package com.example.memes.activities.data

class ServerResponse(var success: Boolean, var data: Data){
    class Data(var memes: Array<Mem>)
}