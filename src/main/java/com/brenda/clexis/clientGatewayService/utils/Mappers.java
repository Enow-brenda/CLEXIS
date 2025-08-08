package com.brenda.clexis.clientGatewayService.utils;

import com.brenda.clexis.clientGatewayService.model.dto.FrequentTaskDto;
import com.brenda.clexis.clientGatewayService.model.dto.ModuleDto;
import com.brenda.clexis.clientGatewayService.model.dto.TaskDto;
import com.brenda.clexis.clientGatewayService.model.dto.application.Module;
import com.brenda.clexis.clientGatewayService.model.dto.application.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
@RequiredArgsConstructor
public class Mappers {

    public List<Task> convertFrequentTasks(List<FrequentTaskDto> frequentTaskDtos) {
        List<Task> tasks = new ArrayList<>();
        for (FrequentTaskDto frequentTaskDto : frequentTaskDtos) {
            var task = new Task();
            String dateString = "";
            task.setFrequentTask(true);
            task.setDescription(frequentTaskDto.getDescription());
            task.setTitle(frequentTaskDto.getTitle());
            if(frequentTaskDto.isDaily()){
                task.setDaily(true);
                dateString ="Every Day";
            }else if(frequentTaskDto.isWeekly()){
                task.setWeekly(true);
                task.setScheduledDays(frequentTaskDto.getScheduledDays());
                dateString =getDayNames(frequentTaskDto.getScheduledDays());
            }else if(frequentTaskDto.isMonthly()){
                task.setMonthly(true);
                task.setFrequency(frequentTaskDto.getFrequency());
                dateString = "Every " + frequentTaskDto.getFrequency() +" of the Month";
            }
            task.setDate(dateString);
            tasks.add(task);

        }
        return tasks;
    }

    public List<Task> convertTasks(List<TaskDto> taskDtos) {
        List<Task> tasks = new ArrayList<>();
        for (TaskDto taskDto : taskDtos) {
            var task = new Task();
            String dateString = "";
            task.setFrequentTask(true);
            task.setDescription(taskDto.getDescription());
            task.setTitle(taskDto.getTitle());
            task.setDate(taskDto.getDate());
            tasks.add(task);

        }
        return tasks;
    }

    public List<Module> convertModules(List<ModuleDto> moduleDtos) {
        List<Module> modules = new ArrayList<>();
        for (ModuleDto moduleDto : moduleDtos) {
            Module module = Module.builder()
                    .tasks(convertTasks(moduleDto.getTasks()))
                    .objective(moduleDto.getObjective())
                    .moduleName(moduleDto.getModuleName())
                    .build();
            modules.add(module);
        }
        return modules;
    }

    public static String getDayNames(List<Integer> dayIndices) {
        String[] days = {
                "Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };
        String result = "Every ";
        for (Integer index : dayIndices) {
            if (index >= 1 && index <= 7) {
                result += days[index];
            }
        }
        return result;
    }

}
