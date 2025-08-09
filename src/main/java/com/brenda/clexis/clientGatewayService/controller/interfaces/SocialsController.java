package com.brenda.clexis.clientGatewayService.controller.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.DiscussionDto;
import com.brenda.clexis.clientGatewayService.model.dto.application.Review;
import com.brenda.clexis.clientGatewayService.model.dto.application.Submission;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;
import com.brenda.clexis.clientGatewayService.model.entity.Discussion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/gateway/student/socials/")
public interface SocialsController {

    //forum
    @PostMapping("discussion/add")
    ResponseEntity<ResponseDto> addDiscussion(@RequestBody DiscussionDto discussionDto);

    @PutMapping("discussion/update")
    ResponseEntity<ResponseDto> updateDiscussion(@RequestBody Discussion discussion);

    @GetMapping("discussion/getAll")
    ResponseEntity<ResponseDto> getAllDiscussions();

    @GetMapping("discussion/get/{id}")
    ResponseEntity<ResponseDto> getDiscussion(@PathVariable String id);

    @PostMapping("discussion/response/add/{discussionId}")
    ResponseEntity<ResponseDto> addResponse(@RequestBody Review review,@PathVariable String discussionId);

    //study buddies
    @PostMapping("buddy/program/add")
    ResponseEntity<ResponseDto> addProgram(@RequestBody BuddyProgram buddyProgram);

    @GetMapping("buddy/program/get/{id}")
    ResponseEntity<ResponseDto> getProgram(@PathVariable String id);

    @PutMapping("buddy/program/update")
    ResponseEntity<ResponseDto> updateProgram(@RequestBody BuddyProgram buddyProgram);

    @PostMapping("buddy/program/join/{programId}")
    ResponseEntity<ResponseDto> joinProgram(@PathVariable String programId);

    @GetMapping("buddy/program/getAll")
    ResponseEntity<ResponseDto> getAllBUddyPrograms();


}
