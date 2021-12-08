package org.launchcode.techjobs.persistent.controllers;

import org.launchcode.techjobs.persistent.models.Employer;
import org.launchcode.techjobs.persistent.models.Job;
import org.launchcode.techjobs.persistent.models.Skill;
import org.launchcode.techjobs.persistent.models.data.EmployerRepository;
import org.launchcode.techjobs.persistent.models.data.JobRepository;
import org.launchcode.techjobs.persistent.models.data.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by LaunchCode
 */
@Controller
public class HomeController {

    @Autowired
    EmployerRepository employerRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    SkillRepository skillRepository;

    @RequestMapping("")
    public String index(Model model) {
        model.addAttribute("title", "My Jobs");
        model.addAttribute("jobs", jobRepository.findAll());
        return "index";
    }

    @GetMapping("add")
    public String displayAddJobForm(Model model) {
        model.addAttribute("title", "Add Job");
        model.addAttribute("job", new Job());
        model.addAttribute("employers", employerRepository.findAll());
        model.addAttribute("skills", skillRepository.findAll());
        return "add";
    }

    @PostMapping("add")
    public String processAddJobForm(
                    @ModelAttribute @Valid Job newJob,
                    Errors errors,
                    Model model,
                    @RequestParam(required=false) int employerId,
                    @RequestParam(required=false) List<Integer> skills) {


        if (skills.isEmpty() || employerId == 0) {
            model.addAttribute("title", "Add Job");
            model.addAttribute("job", newJob);
            model.addAttribute("employers", employerRepository.findAll());
            model.addAttribute("skills", skillRepository.findAll());
            return "add";
        }

        // NOTE: maybe be able to combine this code with above
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Job");
            model.addAttribute("job", newJob);
            model.addAttribute("employers", employerRepository.findAll());
            model.addAttribute("skills", skillRepository.findAll());
            return "add";
        }

        Optional<Employer> employerOptional = employerRepository.findById(employerId);

        if (employerOptional.isEmpty()) {
            model.addAttribute("title", "My Jobs");
            model.addAttribute("jobs", jobRepository.findAll());
            return "redirect:";
        }

        Iterable<Skill> skillObjs = skillRepository.findAllById(skills);

        if (!(skillObjs.iterator().hasNext())) {
            model.addAttribute("title", "My Jobs");
            model.addAttribute("jobs", jobRepository.findAll());
            return "redirect:";
        }

        Employer employer = (Employer) employerOptional.get();
        newJob.setEmployer(employer);
        newJob.setSkills((ArrayList<Skill>) skillObjs);

        jobRepository.save(newJob);

        model.addAttribute("job", newJob);
        model.addAttribute("title", "Job: " + newJob.getName());
        return "view";

    }

    @GetMapping("view/{jobId}")
    public String displayViewJob(Model model, @PathVariable int jobId) {
        model.addAttribute("job", jobRepository.findById(jobId).get());
        return "view";
    }


    @GetMapping("list-jobs")
    public String displayListJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "list-jobs";
    }

}
