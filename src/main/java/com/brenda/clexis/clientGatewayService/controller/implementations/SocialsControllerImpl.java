package com.brenda.clexis.clientGatewayService.controller.implementations;

import com.brenda.clexis.clientGatewayService.controller.interfaces.SocialsController;
import com.brenda.clexis.clientGatewayService.model.dto.DiscussionDto;
import com.brenda.clexis.clientGatewayService.model.dto.application.Review;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;
import com.brenda.clexis.clientGatewayService.model.entity.Discussion;
import com.brenda.clexis.clientGatewayService.service.interfaces.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialsControllerImpl implements SocialsController {

    private final SocialService socialService;

    @Override
    public ResponseEntity<ResponseDto> addDiscussion(DiscussionDto discussionDto) {
        log.info("adding discussion: {}", discussionDto);
        return socialService.addDiscussion(discussionDto);
    }

    @Override
    public ResponseEntity<ResponseDto> updateDiscussion(Discussion discussion) {
        log.info("updating discussion: {}", discussion);
        return socialService.updateDiscussion(discussion);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllDiscussions() {
        log.info("getting all discussions");
        return socialService.getAllDiscussion();
    }

    @Override
    public ResponseEntity<ResponseDto> getDiscussion(String id) {
        log.info("getting discussion with id: {}", id);
        return socialService.getDiscussion(id);
    }

    @Override
    public ResponseEntity<ResponseDto> addResponse(Review review, String discussionId) {
        log.info("adding discussion response {} to discussion with id {}", review, discussionId);
        return socialService.addResponse(review,discussionId);
    }

    @Override
    public ResponseEntity<ResponseDto> addProgram(BuddyProgram buddyProgram) {
        log.info("adding program: {}", buddyProgram);
        return socialService.addProgram(buddyProgram);
    }

    @Override
    public ResponseEntity<ResponseDto> getProgram(String id) {
        log.info("getting program: {}", id);
        return socialService.getProgram(id);
    }

    @Override
    public ResponseEntity<ResponseDto> updateProgram(BuddyProgram buddyProgram) {
        log.info("updating program: {}", buddyProgram);
        return socialService.updateProgram(buddyProgram);
    }

    @Override
    public ResponseEntity<ResponseDto> joinProgram(String programId) {
        log.info("joining program with id: {}", programId);
        return socialService.joinProgram(programId);
    }

    @Override
    public ResponseEntity<ResponseDto> getAllBUddyPrograms() {
        log.info("getting all buddy programs");
        return socialService.getAllBuddyProgram();
    }
}
