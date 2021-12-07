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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            @RequestParam int employerId,
            @RequestParam List<Integer> skills) {

        if (errors.hasErrors()) {
            return "add";
        }
//
//        skillRepository.findAllById((Iterable<Integer>) any);
//        job.setSkills((List<Skill>) any);
//



        Optional<Employer> result = employerRepository.findById(employerId);
        if (result.isEmpty()) {
            return "redirect:";
        }

        List<Skill> skillObjs = (List<Skill>) skillRepository.findAllById(skills);

        if (skillObjs.isEmpty()) {
            return "redirect:";
        }

    //newJob.setName(name);
        newJob.setEmployer(result.get());
        newJob.setSkills(skillObjs);

        jobRepository.save(newJob);

        model.addAttribute("job", newJob);
        model.addAttribute("employer", result);
        model.addAttribute("skills", skillObjs);
        model.addAttribute("jobId", newJob.getId());
        model.addAttribute("employerId", employerId);

        return "view";

    }

    @GetMapping("view/{jobId}")
    public String displayViewJob(Model model, @PathVariable int jobId) {
        model.addAttribute("job", jobRepository.findById(jobId));
        return "view";
    }


    @GetMapping("list-jobs")
    public String displayListJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "list-jobs";
    }

}
