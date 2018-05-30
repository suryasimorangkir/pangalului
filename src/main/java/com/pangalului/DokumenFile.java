package com.pangalului;

/**
 * Created by solo on 21 Mei 2018.
 */
public class DokumenFile {
    private String title;
    private String path;
    private float score;
    private String explanation;

    public DokumenFile(String title, String path, float score, String explanation) {
        this.title = title;
        this.path = path;
        this.score = score;
        this.explanation = explanation;
    }

    public String getExplanation() {

        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public DokumenFile(String title, String path, float score) {

        this.title = title;
        this.path = path;
        this.score = score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
