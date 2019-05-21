package com.example.digitalreceipt.model;

import java.io.Serializable;

public class ReceiptPDF implements Serializable {
    private String title;
    private byte[] pdf;

    public ReceiptPDF(byte[] pdf, String title) {
        this.pdf = pdf;
        this.title = title;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public String getTitle() {
        return title;
    }
}
