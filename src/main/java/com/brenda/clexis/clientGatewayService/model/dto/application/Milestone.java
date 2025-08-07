package com.brenda.clexis.clientGatewayService.model.dto.application;

import java.util.List;

public class Milestone {
    private String name;
    private String description;
    private boolean quiz;
    private String taskOrQuizCode;
    private List<Submission> submissionList;
}
