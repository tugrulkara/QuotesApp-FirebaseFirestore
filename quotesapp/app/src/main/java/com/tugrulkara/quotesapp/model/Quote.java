package com.tugrulkara.quotesapp.model;

public class Quote {

    private String quote_id;
    private String quote_txt;
    private String category;
    private String author;

    public String getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(String quote_id) {
        this.quote_id = quote_id;
    }

    public String getQuote_txt() {
        return quote_txt;
    }

    public void setQuote_txt(String quote_txt) {
        this.quote_txt = quote_txt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
