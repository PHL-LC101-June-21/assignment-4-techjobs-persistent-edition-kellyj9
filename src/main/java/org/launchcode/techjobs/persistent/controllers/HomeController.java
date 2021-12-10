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
import java.util.List;

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

        // add the title of the page to the model
        model.addAttribute("title", "Add Job");

        // add a new job object to the model
        model.addAttribute("job", new Job());

        // get all the skill objects from the skill repository
        List<Skill> allSkills = (List <Skill>) skillRepository.findAll();
        // add allSkills to the model.  (allSkills contains all the skills IDs)
        model.addAttribute("allSkills", allSkills);

        // get all the employer objects from the employer repository
        List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
        // add allEmployers to the model.  (allEmployers contains all the employer IDs)
        model.addAttribute("allEmployers", allEmployers);

        return "add";
    }

    @PostMapping("add")
    public String processAddJobForm(
                    @ModelAttribute @Valid Job newJob,
                    Errors errors,
                    Model model,
                    @RequestParam int employerId,
                    @RequestParam List<Integer> skills) {

        // if the user didn't select any skills...
        if (skills.isEmpty())  {
                errors.rejectValue("skills", "skills.invalidskills",
                        "At least one skill must be chosen.");
        }

        // if the user didn't select an employer
        if (employerId == 0) {
            errors.rejectValue("employer", "employer.invalidemployer",
                    "An employer must be chosen.");
        }

        // if the form wasn't filled out properly...
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Job");

            List<Skill> allSkills = (List<Skill>) skillRepository.findAll();
            model.addAttribute("allSkills", allSkills);

            List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
            model.addAttribute("allEmployers", allEmployers);

            model.addAttribute("employerId", employerId);

            return "add";
        }

        // find the employer in the employer repository with the employer Id that was
        // chosen in the form
        Employer employer = employerRepository.findById(employerId).orElse(new Employer());

        // find the skill(s) in the skill respository with the skill Id(s) chosen in the form
        List<Skill> skillObjs = (List<Skill>) skillRepository.findAllById(skills);

        // we passed all the validation, so set the skills and employer for the job
        // and save it to the job repository
        newJob.setSkills((List<Skill>) skillObjs);
        newJob.setEmployer(employer);
        jobRepository.save(newJob);

        // now that we have the new job,
        // we can show the job and page title on the view page
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
