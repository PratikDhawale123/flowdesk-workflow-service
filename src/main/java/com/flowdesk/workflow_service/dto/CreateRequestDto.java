package com.flowdesk.workflow_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull(message = "Please Enter Amount")
    @Positive(message = "Please Enter Valid Amount")
    private Double amount;
    private String quotationUrl;


    public CreateRequestDto(String title, String description, Double amount, String quotationUrl) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.quotationUrl = quotationUrl;
    }

    public CreateRequestDto(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getQuotationUrl() {
        return quotationUrl;
    }

    public void setQuotationUrl(String quotationUrl) {
        this.quotationUrl = quotationUrl;
    }




}
