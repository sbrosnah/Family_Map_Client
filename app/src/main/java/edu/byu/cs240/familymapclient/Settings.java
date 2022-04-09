package edu.byu.cs240.familymapclient;

public class Settings {
    private boolean showSpouseLine;
    private boolean showFamilyLines;
    private boolean showLifeEventLines;
    private boolean showFatherSide;
    private boolean showMotherSide;
    private boolean showMale;
    private boolean showFemale;

    public Settings() {
        showSpouseLine = true;
        showFamilyLines = true;
        showLifeEventLines = true;
        showFatherSide = true;
        showMotherSide = true;
        showMale = true;
        showFemale = true;
    }

    public void toggleShowSpouseLine() {
        if(showSpouseLine == false) {
            showSpouseLine = true;
        } else {
            showSpouseLine = false;
        }
    }

    public void toggleShowFamilyLines() {
        if(showFamilyLines == false) {
            showFamilyLines = true;
        } else {
            showFamilyLines = false;
        }
    }

    public void toggleShowLifeEventLines() {
        if(showLifeEventLines == false) {
            showLifeEventLines = true;
        } else {
            showLifeEventLines = false;
        }
    }

    public void toggleShowFatherSide() {
        if(showFatherSide == false) {
            showFatherSide = true;
        } else {
            showFatherSide = false;
        }
    }

    public void toggleShowMotherSide() {
        if(showMotherSide == false) {
            showMotherSide = true;
        } else {
            showMotherSide = false;
        }
    }

    public void toggleShowMale() {
        if(showMale == false) {
            showMale = true;
        } else {
            showMale = false;
        }
    }

    public void toggleShowFemale() {
        if(showFemale == false) {
            showFemale = true;
        } else {
            showFemale = false;
        }
    }

    public boolean isShowSpouseLine() {
        return showSpouseLine;
    }

    public void setShowSpouseLine(boolean showSpouseLine) {
        this.showSpouseLine = showSpouseLine;
    }

    public boolean isShowFamilyLines() {
        return showFamilyLines;
    }

    public void setShowFamilyLines(boolean showFamilyLines) {
        this.showFamilyLines = showFamilyLines;
    }

    public boolean isShowLifeEventLines() {
        return showLifeEventLines;
    }

    public void setShowLifeEventLines(boolean showLifeEventLines) {
        this.showLifeEventLines = showLifeEventLines;
    }

    public boolean isShowFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean isShowMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean isShowMale() {
        return showMale;
    }

    public void setShowMale(boolean showMale) {
        this.showMale = showMale;
    }

    public boolean isShowFemale() {
        return showFemale;
    }

    public void setShowFemale(boolean showFemale) {
        this.showFemale = showFemale;
    }
}
