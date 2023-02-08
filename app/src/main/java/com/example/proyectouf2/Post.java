package com.example.proyectouf2;

import java.util.HashMap;
import java.util.Map;

public class Post {
    public String uid;
    public String author;
    public String authorPhotoUrl;
    public String content;

    // Constructor vacio requerido por Firestore
    public Post() {}

    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, Boolean> retweets = new HashMap<>();
    public Map<String, Boolean> comentarios = new HashMap<>();
    public Map<String, Boolean> borrar = new HashMap<>();

    public Post(String uid, String author, String authorPhotoUrl, String content) {
        this.uid = uid;
        this.author = author;
        this.authorPhotoUrl = authorPhotoUrl;
        this.content = content;
    }
}