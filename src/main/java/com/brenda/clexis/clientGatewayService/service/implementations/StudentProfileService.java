package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.entity.StudentProfile;
import com.brenda.clexis.clientGatewayService.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository repository;

    // 1. Create profile after registration
    public void createProfile(String userId, int academicLevel, String profession,
                              String languageCode, String interest) {
        StudentProfile profile = new StudentProfile();
        profile.setStudentId(userId);
        profile.setAcademicLevel(academicLevel);
        profile.setProfession(profession);
        profile.setLanguageCode(languageCode);
        profile.setBioOrInterest(interest);
        profile.setStudyBuddyActivityScore(0.0);
        profile.setForumEngagementScore(0.0);
        profile.setSharingScore(0.0);
        profile.setPersonalScore(0.0);
        profile.setPreferredStudyTopics(new java.util.ArrayList<>());
        profile.setLearningPathSubjects(new java.util.ArrayList<>());
        profile.setResourceCategoriesShared(new java.util.ArrayList<>());

        repository.save(profile);
    }

    // 2. Update study buddy score & topics
    public void recordStudyBuddySession(String userId, String topic) {
        StudentProfile profile = repository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        if (!profile.getPreferredStudyTopics().contains(topic)) {
            profile.getPreferredStudyTopics().add(topic);
        }

        repository.save(profile);
    }

    // 3. Update forum engagement score
    public void recordForumParticipation(String userId) {
        StudentProfile profile = repository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setForumEngagementScore(Math.min(1.0, profile.getForumEngagementScore() + 0.02));
        repository.save(profile);
    }

    // 4. Add learning path subject
    public void recordLearningPathProgress(String userId, String subject) {
        StudentProfile profile = repository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (!profile.getLearningPathSubjects().contains(subject)) {
            profile.getLearningPathSubjects().add(subject);
        }
        repository.save(profile);
    }

    // 5. Update resource sharing
    public void recordResourceShared(String userId, String category) {
        StudentProfile profile = repository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (!profile.getResourceCategoriesShared().contains(category)) {
            profile.getResourceCategoriesShared().add(category);
        }
        repository.save(profile);
    }

    // 6. Update interests from bio
    public void updateProfile(String userId, String bio, int academicLevel , String profession,String language) {
        StudentProfile profile = repository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setAcademicLevel(academicLevel);
        profile.setProfession(profession);
        profile.setLanguageCode(language);
        profile.setBioOrInterest(bio);
        repository.save(profile);
    }

    public void updateScore(String userId,int points ,int community,int resource) {
        StudentProfile profile = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        int maxPoints = 500;     // maximum points possible

        double normalizedScore1 = (double) points / maxPoints;
        double normalizedScore2 = (double) community / maxPoints;
        double normalizedScore3 = (double) resource / maxPoints;

       // Clamp between 0 and 1 (optional but recommended)
        normalizedScore1 = Math.min(1.0, Math.max(0.0, normalizedScore1));
        normalizedScore2 = Math.min(1.0, Math.max(0.0, normalizedScore2));
        normalizedScore3 = Math.min(1.0, Math.max(0.0, normalizedScore3));
        
        profile.setPersonalScore(normalizedScore1);
        profile.setForumEngagementScore(normalizedScore2);
        profile.setSharingScore(normalizedScore3);
        repository.save(profile);
    }


}
