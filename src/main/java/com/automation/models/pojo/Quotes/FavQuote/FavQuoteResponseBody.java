package com.automation.models.pojo.Quotes.FavQuote;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FavQuoteResponseBody {
    /*{
        "id": 4,
        "dialogue": false,
         "private": false,
         "tags": [
        "simplicity",
                "minimalism",
                "physicist"
    ],
        "url": "https://favqs.com/quotes/anonymous/4-make-everything-a-",
         "favorites_count": 41,
         "upvotes_count": 10,
         "downvotes_count": 2,
            "author": "Anonymous",
            "author_permalink": "anonymous",
            "body": "Make everything as simple as possible, but not simpler.",
            "user_details": {
        "favorite": true,
                "upvote": false,
                "downvote": false,
                "hidden": false
    }
    }*/

    int id;
    Boolean dialogue;
    @JsonProperty("private")
    Boolean pvate;
    String url;
    int favorites_count;
    int upvotes_count;
    int downvotes_count;
    String author;
    String author_permalink;
    String body;
    User_Details user_details;
    List<String> tags;
}
