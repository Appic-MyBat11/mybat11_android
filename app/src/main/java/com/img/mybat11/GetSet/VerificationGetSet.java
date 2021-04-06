package com.img.mybat11.GetSet;

public class VerificationGetSet {

    public int getMobile_verify() {
        return mobile_verify;
    }

    public void setMobile_verify(int mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    public int getEmail_verify() {
        return email_verify;
    }

    public void setEmail_verify(int email_verify) {
        this.email_verify = email_verify;
    }

    public int getBank_verify() {
        return bank_verify;
    }

    public void setBank_verify(int bank_verify) {
        this.bank_verify = bank_verify;
    }

    public int getPan_verify() {
        return pan_verify;
    }

    public void setPan_verify(int pan_verify) {
        this.pan_verify = pan_verify;
    }

    public String getBank_comment() {
        return bank_comment;
    }

    public void setBank_comment(String bank_comment) {
        this.bank_comment = bank_comment;
    }

    public String getPan_comment() {
        return pan_comment;
    }

    public void setPan_comment(String pan_comment) {
        this.pan_comment = pan_comment;
    }

    int mobile_verify,email_verify,bank_verify,pan_verify;
    String bank_comment,pan_comment,email,mobile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
