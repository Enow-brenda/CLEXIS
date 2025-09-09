package com.brenda.clexis.clientGatewayService.service.implementations;




import com.brenda.clexis.clientGatewayService.interfaces.interfaces.AiInterface;
import com.brenda.clexis.clientGatewayService.interfaces.interfaces.NotificationInterface;
import com.brenda.clexis.clientGatewayService.model.dto.NotificationRequest;
import com.brenda.clexis.clientGatewayService.model.dto.application.StudentProfileObject;
import com.brenda.clexis.clientGatewayService.model.dto.application.Task;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.Notification;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.repository.*;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.service.interfaces.StudentService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {


    private final NotificationInterface notificationInterface;
    private final JWTUtils jWTUtils;
    private final LearningPathService learningPathService;
    private final StudentRepository studentRepository;
    private final AiInterface aiInterface;
    private final NotificationRepository notificationRepository;
    private final StudentProfileService studentProfileService;
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;
    private final ResourceRepository resourceRepository;
    private final DiscussionRepository discussionRepository;
    private final BuddyProgramRepository buddyProgramRepository;

    @Override
    public ResponseEntity<ResponseDto> notifyStudents(NotificationRequest notificationRequest) {
        try{
            notificationInterface.sendCustomEmail(notificationRequest);
            return MainResponse.responseOk(null,"Students Successfully Notified");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentInfo(String userId) {
        try{
            var student = studentRepository.findStudentByUserId(userId);
            if(student == null){
                return MainResponse.responseNotFound("Student Not Found");
            }
            return MainResponse.responseOk(student);
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateStudent(Student student) {
        try{

            student.setUserId(jWTUtils.extractUserId(learningPathService.getToken()));
            String userId = student.getUserId();
            if(studentRepository.existsById(student.getId())){
                studentRepository.save(student);

                studentProfileService.updateProfile(userId,student.getBioOrInterest(),student.getAcademicLevel().getCode(), student.getProfession(), student.getLanguage());
                studentProfileService.updateScore(userId,student.getPoints(),student.getCommunityPoints(), student.getResourcePoints());
                return MainResponse.responseOk(student,"Student Successfully Updated");
            }
            return MainResponse.responseNotFound("Student Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> findMatches() {
        try{
            var userId = jWTUtils.extractUserId(learningPathService.getToken());
            if(studentRepository.findStudentByUserId(userId)!=null){
                List<String> matches =  aiInterface.findMatches(userId);
                List<Student> students = new ArrayList<>();
                for(String match : matches){
                    Student student = studentRepository.findStudentByUserId(match);
                    students.add(student);
                }
                return MainResponse.responseOk(students,"Matches Found");

            }
            return MainResponse.responseNotFound("Student Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getNotifications() {
        try{
            var userId = jWTUtils.extractUserId(learningPathService.getToken());
            List<Notification> notifications= notificationRepository.findNotificationsByUserId(userId);
            return MainResponse.responseOk(notifications,"Notifications Found");

        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getStudentProfile(String userId) {
        try{
            var student = studentRepository.findStudentByUserId(userId);
            var user = userRepository.findUserById(userId);
            if(student == null){
                return MainResponse.responseNotFound("Student Not Found");
            }


            StudentProfileObject studentProfileObject = StudentProfileObject.builder()
                    .email(user.getEmail())
                    .userId(userId)
                    .bioOrInterest(student.getBioOrInterest())
                    .academicLevel(student.getAcademicLevel().toString())
                    .profession(student.getProfession())
                    .language(student.getLanguage())
                    .points(student.getPoints())
                    .completedTasks(0)
                    .fullName(student.getFullName())
                    .rank(0)
                    .programs(0)
                    .discussions(0)
                    .resourceShared(0)
                    .tasks(0)
                    .dateCreated(student.getDateCreated())
                    .recentPrograms(List.of())
                    .recentResources(List.of())
                    .learningPath(0)
                    .phoneNumber(student.getPhoneNumber())
                    .build();
            var activeL = learningPathRepository.findLearningPathByActiveIsTrueAndUserId(student.getUserId());

            if (activeL != null) {
                LocalDate now = LocalDate.now();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
                int currentYear = now.getYear();

                // Define formatter matching your string format dd-MM-yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                // Flatten all tasks from all modules
                List<Task> allTasks = activeL.getModules()
                        .stream()
                        .flatMap(module -> module.getTasks().stream())
                        .toList();

                // Filter tasks belonging to the current week
                List<Task> tasksThisWeek = allTasks.stream()
                        .filter(task -> {
                            try {
                                LocalDate taskDate = LocalDate.parse(task.getDate(), formatter);
                                int taskWeek = taskDate.get(weekFields.weekOfWeekBasedYear());
                                int taskYear = taskDate.getYear();
                                return taskWeek == currentWeek && taskYear == currentYear;
                            } catch (Exception e) {
                                return false; // ignore invalid date strings
                            }
                        })
                        .toList();

                long totalTasksThisWeek = tasksThisWeek.size();
                long doneTasksThisWeek = tasksThisWeek.stream()
                        .filter(task -> task.isCompleted()) // adjust to your status field
                        .count();

                studentProfileObject.setCompletedTasks((int)doneTasksThisWeek);
                studentProfileObject.setTasks((int)totalTasksThisWeek);
            }
            var learningPaths = learningPathRepository.findLearningPathsByUserId(userId);
            studentProfileObject.setLearningPath(learningPaths.size());
            var allResources = resourceRepository.findResourcesByUserId(userId);
            var firstFive = allResources.stream().limit(5).toList();
            studentProfileObject.setRecentResources(firstFive);
            studentProfileObject.setResourceShared(allResources.size());

            var discussions = discussionRepository.findByAuthorIdOrResponseUserId(userId);
            studentProfileObject.setDiscussions(discussions.size());

            var recentPrograms = buddyProgramRepository.findByBuddiesUserId(userId);
            var firstFiveP = recentPrograms.stream().limit(5).toList();
            studentProfileObject.setRecentPrograms(firstFiveP);
            studentProfileObject.setPrograms(recentPrograms.size());

            List<Student> students = studentRepository.findAll();
            // Sort students descending by points
            students.sort((s1, s2) -> Double.compare(s2.getPoints(), s1.getPoints()));

            // Assign ranks
            int rank = 1;
            double previousPoints = -1;
            int sameRankCount = 0;

            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);

                if (s.getPoints() == previousPoints) {
                    sameRankCount++;
                } else {
                    rank += sameRankCount;
                    sameRankCount = 1;
                    previousPoints = s.getPoints();
                }
            }

            studentProfileObject.setRank(rank);





            return MainResponse.responseOk(studentProfileObject);
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }


}
