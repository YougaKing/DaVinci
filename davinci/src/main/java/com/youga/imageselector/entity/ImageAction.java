package com.youga.imageselector.entity;

import java.util.ArrayList;

/**
 * Created by Youga on 2015/11/20.
 */
public enum ImageAction {
    OPEN_CAMERA("OpenCamera"),
    CHOICE_IMAGE("ChoiceImage", 1, true, null);
    private String type;
    private int number;
    private boolean showCamera;
    private ArrayList<String> imagePathList;

    ImageAction(String type) {
        this.type = type;
    }

    ImageAction(String type, int number, boolean showCamera, ArrayList<String> imagePathList) {
        this.type = type;
        this.number = number;
        this.showCamera = showCamera;
        this.imagePathList = imagePathList;
    }

    public String getType() {
        return type;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public int getNumber() {
        return number;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setImagePathList(ArrayList<String> imagePathList) {
        this.imagePathList = imagePathList == null ? new ArrayList<String>() : imagePathList;
    }

    public ArrayList<String> getImagePathList() {
        if (imagePathList == null) imagePathList = new ArrayList<>();
        return imagePathList;
    }
}
