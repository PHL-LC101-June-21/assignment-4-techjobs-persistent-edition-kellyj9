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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
        //model.addAttribute("employers", employerRepository.findAll());

        // get all the skill objects from the repository
        List<Skill> allSkills = (List <Skill>) skillRepository.findAll();
       // get all the skill IDs from the skills objects
//        List<Integer> allSkills = new ArrayList<>();
//        for (Skill obj : allObjs) {
//            allSkills.add(obj.getId());
//        }
        // add allSkills to the model.  (allSkills contains all the skills IDs)
        model.addAttribute("allSkills", allSkills);

        List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
        model.addAttribute("allEmployers", allEmployers);

//        model.addAttribute("skills", new ArrayList<Skill>());

        //List<Integer> checkedSkills = new ArrayList<>();
        //model.addAttribute("checkedSkills", checkedSkills);
        return "add";
    }

    @PostMapping("add")
    public String processAddJobForm(
                    @ModelAttribute @Valid Job newJob,
                    Errors errors,
                    Model model,
                    @RequestParam int employerId,
                    @RequestParam List<Integer> skills) {

        boolean isInvalid = false;

        if (skills.isEmpty())  {
                isInvalid=true;
                errors.rejectValue("skills", "skills.invalidskills",
                        "At least one skill must be chosen.");
        }

        if (employerId == 0) {
            isInvalid=true;
            errors.rejectValue("employer", "employer.invalidemployer",
                    "An employer must be chosen.");
        }

        //may be able to take out this
//        if (isInvalid)  {
//            model.addAttribute("title", "Add Job");
//            model.addAttribute("job", newJob);
//            model.addAttribute("employers", employerRepository.findAll());
//            model.addAttribute("skills", skillRepository.findAll());
//
//            return "add";
//        }

        // NOTE: may be able to combine this code with above
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Job");
           // model.addAttribute("job", new Job());
            //model.addAttribute("employers", employerRepository.findAll());
            List<Skill> allSkills = (List<Skill>) skillRepository.findAll();
            model.addAttribute("allSkills", allSkills);
            model.addAttribute("skills", new ArrayList<Skill>());

            List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
            model.addAttribute("allEmployers", allEmployers);
            model.addAttribute("employer", new ArrayList<Employer>());

            model.addAttribute("employerId", employerId);

            //model.addAttribute("checkedSkills", new ArrayList<>());
            return "add";
        }

        Optional<Employer> employerOptional = employerRepository.findById(employerId);

        if (employerOptional.isEmpty()) {
            model.addAttribute("title", "Add Job");
           // model.addAttribute("job", new Job());
//            model.addAttribute("employers", employerRepository.findAll());
//
//            List<Skill> allSkills = (List <Skill>) skillRepository.findAll();
//            model.addAttribute("allSkills", allSkills);
//            model.addAttribute("skills", new ArrayList<Skill>());



            List<Skill> allSkills = (List<Skill>) skillRepository.findAll();
            model.addAttribute("allSkills", allSkills);
            model.addAttribute("skills", new ArrayList<Skill>());

            List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
            model.addAttribute("allEmployers", allEmployers);
            model.addAttribute("employer", new ArrayList<Employer>());

            model.addAttribute("employerId", employerId);
            //model.addAttribute("checkedSkills", new ArrayList<>());
            return "add";
//            model.addAttribute("title", "My Jobs");
//            model.addAttribute("jobs", jobRepository.findAll());
//            return "redirect:";
        }

//
//        List <Integer> checkedSkillsIntegerList = new ArrayList<>();
//        for (String skill : checkedSkills) {
//                    checkedSkillsIntegerList.add(Integer.parseInt(skill));
//        }

       // Iterable<Skill> skillObjs = skillRepository.findAllById(checkedSkillsIntegerList);

//        List<Integer> skillIds = new ArrayList<>();
//        for (Skill skill : checkedSkills) {
//            skillIds.add();
//        }

        Iterable<Skill> skillObjs = skillRepository.findAllById(skills);

        if (!(skillObjs.iterator().hasNext())) {
            model.addAttribute("title", "Add Job");
//            model.addAttribute("job", new Job());
//            model.addAttribute("employers", employerRepository.findAll());
//
//            List<Skill> allSkills = (List <Skill>) skillRepository.findAll();
//            model.addAttribute("allSkills", allSkills);
//            model.addAttribute("skills", new ArrayList<Skill>());
            //model.addAttribute("checkedSkills", new ArrayList<>());
            List<Skill> allSkills = (List<Skill>) skillRepository.findAll();
            model.addAttribute("allSkills", allSkills);
            model.addAttribute("skills", new ArrayList<Skill>());

            List<Employer> allEmployers = (List<Employer>) employerRepository.findAll();
            model.addAttribute("allEmployers", allEmployers);
            model.addAttribute("employer", new ArrayList<Employer>());

            model.addAttribute("employerId", employerId);
            return "add";
//            model.addAttribute("title", "My Jobs");
//            model.addAttribute("jobs", jobRepository.findAll());
//            return "redirect:";
        }

        newJob.setSkills((List<Skill>) skillObjs);
        Employer employer = (Employer) employerOptional.get();
        newJob.setEmployer(employer);

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
